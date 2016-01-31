package com.dingoapp.dingo.api;

import com.dingoapp.dingo.api.model.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by guestguest on 13/01/16.
 */
public interface DingoApi {

    String BASE_URL = "http://192.168.0.102:8000/api/";

    @POST("/users/login_fb/")
    Call<User> registerWithFacebook(@Body User user);

    @POST("/users/accept_terms")
    Call acceptTerms(@Query("rider_mode")User.RiderMode riderMode);

}
