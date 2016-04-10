package com.dingoapp.dingo.util;

import com.dingoapp.dingo.api.Callback;
import com.dingoapp.dingo.api.Response;
import com.dingoapp.dingo.util.ErrorHandlingCallAdapter.Call;

import java.io.IOException;


/**
 * Created by guestguest on 12/03/16.
 */
public abstract class DingoService {

    public <T> void enqueueCall(final Call<T> call, final Callback<T> callback){
        call.enqueue(
                new ErrorHandlingCallAdapter.Callback<T>() {
                    @Override
                    public void success(retrofit2.Response<T> response) {
                        Response<T> dResponse = new Response<T>(response.code(), response.body());
                        callback.success(dResponse);
                    }

                    @Override
                    public void unauthenticated(retrofit2.Response<?> response) {
                        Response dResponse = new Response(response.code(), response.body());
                        callback.success(dResponse);
                    }

                    @Override
                    public void clientError(retrofit2.Response<?> response) {
                        Response dResponse = new Response(response.code(), response.body());
                        callback.success(dResponse);
                    }

                    @Override
                    public void serverError(retrofit2.Response<?> response) {
                        Response dResponse = new Response(response.code(), response.body());
                        callback.success(dResponse);

                    }

                    @Override
                    public void networkError(IOException e) {
                        callback.networkError(e);
                    }

                    @Override
                    public void unexpectedError(Throwable t) {
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
