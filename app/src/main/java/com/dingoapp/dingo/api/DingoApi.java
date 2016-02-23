package com.dingoapp.dingo.api;

import com.dingoapp.dingo.api.model.CreditCardInfo;
import com.dingoapp.dingo.api.model.RideMasterRequest;
import com.dingoapp.dingo.api.model.RideOffer;
import com.dingoapp.dingo.api.model.User;
import com.dingoapp.dingo.api.model.UserRides;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

/**
 * Created by guestguest on 13/01/16.
 */
public interface DingoApi {

    String BASE_URL = "http://192.168.0.100:8000/api/";

    @POST("/users/login_fb/")
    Call<User> registerWithFacebook(@Body User user);

    @POST("/users/accept_terms/")
    Call<Void> acceptTerms(@Query("rider_mode")User.RiderMode riderMode);

    @POST("/ridemasterrequest/findoffers/")
    Call<List<RideOffer>> findOffersforRequest(@Body RideMasterRequest request);

    @POST("/ridemasterrecurrentrequest/findoffers/")
    Call<List<RideOffer>> findOffersforRecurrentRequest(@Body RideMasterRequest request);

    @POST("/rideoffers/")
    Call<List<RideMasterRequest>> createOffer(@Body RideOffer offer);

    @POST("/rideoffersrecurrent/")
    Call<List<RideMasterRequest>> createRecurrentOffer(@Body RideOffer offer);

    @POST("/ridemasterrequests/")
    Call<RideMasterRequest> createRideMasterRequest(@Body RideMasterRequest request);

    @POST("/ridemasterrequestsrecurrent/")
    Call<RideMasterRequest> createRideMasterRequestRecurrent(@Body RideMasterRequest request);

    @GET("/userrides/")
    Call<UserRides> getUserRides();

    @GET("/creditcardinfo/")
    Call<CreditCardInfo> getCreditCardInfo();

    @POST("/creditcardinfo/")
    Call<CreditCardInfo> createCreditCardInfo(@Body CreditCardInfo creditCardInfo);

    @PUT("/creditcardinfo/update/")
    Call<CreditCardInfo> updateCreditCardInfo(@Body CreditCardInfo creditCardInfo);

    @DELETE("/creditcardinfo/")
    Call<Void> deleteCreditCardInfo(@Body CreditCardInfo creditCardInfo);

}
