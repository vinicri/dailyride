package com.dingoapp.dingo.util;


import com.google.common.reflect.TypeToken;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by guestguest on 09/04/16.
 */
public class ErrorHandlingCallAdapter {

    public interface Callback<T> {
        /** Called for [200, 300) responses. */
        void success(Response<T> response);
        /** Called for 401 responses. */
        void unauthenticated(Response<?> response);
        /** Called for [400, 500) responses, except 401. */
        void clientError(Response<?> response);
        /** Called for [500, 600) response. */
        void serverError(Response<?> response);
        /** Called for network errors while making the call. */
        void networkError(IOException e);
        /** Called for unexpected errors while making the call. */
        void unexpectedError(Throwable t);
    }

    public interface Call<T>{
        void cancel();
        void enqueue(Callback<T> callback);
        Call<T> clone();
        Response<T> execute() throws IOException;

        // Left as an exercise for the reader...
        // TODO MyResponse<T> execute() throws MyHttpException;
    }

    public static class ErrorHandlingCallAdapterFactory implements retrofit2.CallAdapter.Factory {
        @Override public retrofit2.CallAdapter get(Type returnType, Annotation[] annotations,
                                                    Retrofit retrofit) {
            TypeToken<?> token = TypeToken.of(returnType);
            if (token.getRawType() != Call.class) {
                return null;
            }
            if (!(returnType instanceof ParameterizedType)) {
                throw new IllegalStateException(
                        "Call must have generic type (e.g., Call<ResponseBody>)");
            }
            final Type responseType = ((ParameterizedType) returnType).getActualTypeArguments()[0];
            return new retrofit2.CallAdapter<Call<?>>() {
                @Override public Type responseType() {
                    return responseType;
                }

                @Override public <R> Call<R> adapt(retrofit2.Call<R> call) {
                    return new CallAdapter<>(call);
                }
            };
        }
    }

    /** Adapts a {@link retrofit2.Call} to {@link Call}. */
    public static class CallAdapter<T> implements Call<T> {
        private final retrofit2.Call call;

        CallAdapter(retrofit2.Call call) {
            this.call = call;
        }

        @Override public void cancel() {
            call.cancel();
        }

        @Override public void enqueue(final Callback<T> callback) {
            call.enqueue(new retrofit2.Callback<T>() {
                @Override public void onResponse(Response<T> response) {
                    int code = response.code();
                    if (code >= 200 && code < 300) {
                        callback.success(response);
                    } else if (code == 401) {
                        callback.unauthenticated(response);
                    } else if (code >= 400 && code < 500) {
                        callback.clientError(response);
                    } else if (code >= 500 && code < 600) {
                        callback.serverError(response);
                    } else {
                        callback.unexpectedError(new RuntimeException("Unexpected response " + response));
                    }
                }

                @Override public void onFailure(Throwable t) {
                    if (t instanceof IOException) {
                        callback.networkError((IOException) t);
                    } else {
                        callback.unexpectedError(t);
                    }
                }
            });
        }

        @Override public Call<T> clone() {
            return new CallAdapter<>(call.clone());
        }

        @Override
        public Response<T> execute() throws IOException {
            return call.execute();
        }
    }
}
