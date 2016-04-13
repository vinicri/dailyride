package com.dingoapp.dingo.api;

import com.crashlytics.android.Crashlytics;
import com.dingoapp.dingo.api.model.DingoError;
import com.dingoapp.dingo.util.ErrorHandlingCallAdapter;
import com.dingoapp.dingo.util.ErrorHandlingCallAdapter.Call;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.StringReader;


/**
 * Created by guestguest on 12/03/16.
 */
public abstract class DingoService {

    public static TypeAdapter<DingoError> mErrorAdapter;

    static{
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .create();

        mErrorAdapter = gson.getAdapter(TypeToken.get(DingoError.class));
    }

    public <T> void enqueueCall(final Call<T> call, final Callback<T> callback){
        call.enqueue(
                new ErrorHandlingCallAdapter.Callback<T>() {
                    @Override
                    public void success(retrofit2.Response<T> response) {
                        Response<T> dResponse = new Response<T>(response.code(), response.body());
                        callback.onFinish();
                        callback.success(dResponse);
                    }

                    @Override
                    public void unauthenticated(retrofit2.Response<?> response) {
                        Response dResponse = new Response(response.code(), response.body());
                        callback.onFinish();
                        callback.unauthenticated(dResponse);
                    }

                    @Override
                    public void clientError(retrofit2.Response<?> response) {
                        String errorBody = "{\"empty\": \"empty\"}";
                        try{
                            errorBody = response.errorBody().string();
                        } catch (IOException e) {
                            Crashlytics.logException(e);
                        }
                        Response dResponse = new Response(response.code(), response.body(), errorBody);

                        DingoError dingoError = new DingoError();
                        try {
                            dingoError = mErrorAdapter.fromJson(new StringReader(errorBody));
                        } catch (IOException e) {
                            Crashlytics.logException(e);
                        }
                        callback.onFinish();
                        callback.clientError(dResponse, dingoError);
                    }

                    @Override
                    public void serverError(retrofit2.Response<?> response) {
                        String errorBody = "{\"empty\": \"empty\"}";
                        try{
                            errorBody = response.errorBody().string();
                        } catch (IOException e) {
                            Crashlytics.logException(e);
                        }
                        Response dResponse = new Response(response.code(), response.body(), errorBody);
                        callback.onFinish();
                        callback.serverError(dResponse);
                    }

                    @Override
                    public void networkError(IOException e) {
                        callback.onFinish();
                        callback.networkError(e);
                    }

                    @Override
                    public void unexpectedError(Throwable t) {
                        callback.onFinish();
                        callback.unexpectedError(t);
                    }
                }
        );
        /*call.enqueue(new retrofit2.Callback<T>() {
            @Override
            public void onResponse(retrofit2.Response<T> response) {
                Response<T> dResponse = new Response<T>(response.code(), response.body());
                callback.onResponse(dResponse);
            }

            @Override
            public void onFailure(Throwable t) {
                callback.onFailure(t);
            }
        });*/

    }
}
