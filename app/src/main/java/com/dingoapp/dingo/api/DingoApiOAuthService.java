package com.dingoapp.dingo.api;

import com.dingoapp.dingo.api.model.User;
import com.dingoapp.dingo.util.ErrorHandlingCallAdapter;
import com.dingoapp.dingo.util.RetrofitLogInterceptor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;

/**
 * Created by guestguest on 17/04/16.
 */
public class DingoApiOAuthService extends DingoService {


    private static DingoApiOAuthService instance;
    private final DingoApi apiService;

    public static DingoApiOAuthService getInstance(){
        if(instance == null){
            instance = new DingoApiOAuthService();
        }
        return instance;
    }

    private DingoApiOAuthService(){
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .create();

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new RetrofitLogInterceptor())
                //todo review timeouts
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(DingoApi.BASE_URL)
                .addCallAdapterFactory(new ErrorHandlingCallAdapter.ErrorHandlingCallAdapterFactory())
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        apiService = retrofit.create(DingoApi.class);
    }

    public Response<User.OAuthToken> refreshToken(String refreshToken) throws IOException {
        //RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), refreshToken);
        retrofit2.Response<User.OAuthToken> response = apiService.refreshToken(refreshToken).execute();
        return new Response<>(response.code(), response.body());
    }
}
