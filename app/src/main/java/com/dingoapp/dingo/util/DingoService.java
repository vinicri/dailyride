package com.dingoapp.dingo.util;

import com.dingoapp.dingo.api.Callback;
import com.dingoapp.dingo.api.Response;

import retrofit2.Call;

/**
 * Created by guestguest on 12/03/16.
 */
public abstract class DingoService {

    public <T> void enqueueCall(final Call<T> call, final Callback<T> callback){
        call.enqueue(new retrofit2.Callback<T>() {
            @Override
            public void onResponse(retrofit2.Response<T> response) {
                Response<T> dResponse = new Response<T>(response.code(), response.body());
                callback.onResponse(dResponse);
            }

            @Override
            public void onFailure(Throwable t) {
                callback.onFailure(t);
            }
        });

    }
}
