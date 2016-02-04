package com.dingoapp.dingo.api;

import com.dingoapp.dingo.api.model.User;
import com.dingoapp.dingo.util.OAuthInterceptor;
import com.dingoapp.dingo.util.RetrofitLogInterceptor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;

/**
 * Created by guestguest on 13/01/16.
 */
public class DingoApiService{

    private static DingoApiService instance;
    private final DingoApi apiService;

    private DingoApiService(){
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .create();

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new RetrofitLogInterceptor())
                .addInterceptor(new OAuthInterceptor())
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(DingoApi.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        apiService = retrofit.create(DingoApi.class);
    }

    public static DingoApiService getInstance(){
        if(instance == null){
            instance = new DingoApiService();
        }
        return instance;
    }

    public void registerWithFacebook(User user, final Callback<User> callback){
        final Call<User> call = apiService.registerWithFacebook(user);
        call.enqueue(new retrofit2.Callback<User>() {
            @Override
            public void onResponse(retrofit2.Response<User> response) {
                Response<User> dResponse = new Response<>(response.code(),
                        response.body());
                callback.onResponse(dResponse);
            }

            @Override
            public void onFailure(Throwable t) {
                callback.onFailure(t);
            }
        });

    }

    public void acceptTerms(User.RiderMode riderMode, final Callback callback){
        final Call<Void> call = apiService.acceptTerms(riderMode);
        call.enqueue(new retrofit2.Callback<Void>() {
            @Override
            public void onResponse(retrofit2.Response<Void> response) {
                Response dResponse = new Response(response.code(), response.body());
                callback.onResponse(dResponse);
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });

    }



}
