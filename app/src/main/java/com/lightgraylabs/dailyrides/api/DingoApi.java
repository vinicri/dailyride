package com.lightgraylabs.dailyrides.api;

import com.lightgraylabs.dailyrides.api.model.CreditCardInfo;
import com.lightgraylabs.dailyrides.api.model.GcmToken;
import com.lightgraylabs.dailyrides.api.model.Institution;
import com.lightgraylabs.dailyrides.api.model.RideMasterRequest;
import com.lightgraylabs.dailyrides.api.model.RideOffer;
import com.lightgraylabs.dailyrides.api.model.RideOfferSlave;
import com.lightgraylabs.dailyrides.api.model.RideRating;
import com.lightgraylabs.dailyrides.api.model.Token;
import com.lightgraylabs.dailyrides.api.model.User;
import com.lightgraylabs.dailyrides.api.model.UserRides;
import com.lightgraylabs.dailyrides.util.ErrorHandlingCallAdapter.Call;
import com.squareup.okhttp.RequestBody;

import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by guestguest on 13/01/16.
 */
public interface DingoApi {

    String BASE_URL = "https://192.168.0.102:8000/api/";
    String STATIC_URL = "https://192.168.0.102:8000/static/";

    @POST("/users/login_fb/")
    Call<User> registerWithFacebook(@Body User user);

    @FormUrlEncoded
    @POST("/users/login/")
    Call<User> userLogin(@Field("email") String email, @Field("password") String password);

    @POST("/users/logout/")
    Call<Void> userLogout(@Query("uuid")String installationUuid);

    @POST("/users/accept_terms/")
    Call<Void> acceptTerms(@Query("rider_mode")User.RiderMode riderMode);

    @POST("/refreshtoken/")
    Call<User.OAuthToken> refreshToken(@Query("refresh_token")String refreshToken);

    @POST("/users/addphone/")
    Call<Void> userAddPhone(@Query("phone")String phone);

    @POST("/users/work/")
    Call<Institution> userAddWork(@Query("email") String email);

    @POST("/users/school/")
    Call<Institution> userAddSchool(@Query("email") String email);

    @POST("/users/work/confirm/")
    Call<Institution> userConfirmWork(@Query("pin") String pin, @Query("name") String company);

    @POST("/users/school/confirm/")
    Call<Institution> userConfirmSchool(@Query("pin") String pin, @Query("name") String school);

    @POST("/users/firebasetoken/")
    Call<Token> getFirebaseToken();

    @POST("/users/signup/")
    Call<User> userRegister(@Body User user);

    @POST("/users/signup/confirm/")
    Call<Void> userSignupConfirm(@Query("pin")String pin);

    @Multipart
    @POST("/users/work/credentials/")
    Call<Institution> addWorkCredential(@Part("credential\"; filename=\"credential.png\"") RequestBody credential);

    @GET("/users/{id}/comments/")
    Call<List<RideRating>> userGetComments(@Path("id") long id);

    @POST("/ridemasterrequest/findoffers/")
    Call<List<RideOffer>> findOffersforRequest(@Body RideMasterRequest request);

    @POST("/ridemasterrecurrentrequest/findoffers/")
    Call<List<RideOffer>> findOffersforRecurrentRequest(@Body RideMasterRequest request);

    @POST("/rideoffers/")
    Call<RideOffer> createOffer(@Body RideOffer offer);

    @POST("/rideoffersrecurrent/")
    Call<RideOffer> createRecurrentOffer(@Body RideOffer offer);

    @POST("/ridemasterrequests/")
    Call<RideMasterRequest> createRideMasterRequest(@Body RideMasterRequest request);

    @POST("/ridemasterrequestsrecurrent/")
    Call<RideMasterRequest> createRideMasterRequestRecurrent(@Body RideMasterRequest request);

    @GET("/userrides/")
    Call<UserRides> getUserRides();

    @GET("/recurrentrides/")
    Call<UserRides> getUserRecurrentRides();

    @GET("/creditcardinfo/")
    Call<CreditCardInfo> getCreditCardInfo();

    @POST("/creditcardinfo/")
    Call<CreditCardInfo> createCreditCardInfo(@Body CreditCardInfo creditCardInfo);

    @PUT("/creditcardinfo/update/")
    Call<CreditCardInfo> updateCreditCardInfo(@Body CreditCardInfo creditCardInfo);

    @DELETE("/creditcardinfo/")
    Call<Void> deleteCreditCardInfo(@Body CreditCardInfo creditCardInfo);

    @POST("/gcmtoken/")
    Call<GcmToken> createGcmToken(@Body GcmToken token);

    //@DELETE("/gcmtoken/delete")
    //Call<Void> deleteGmcToken(@Query("uuid")String installationUuid);

    @GET("/rideofferslave/{id}/")
    Call<RideOfferSlave> getRideOfferSlave(@Path("id") long id);

    @POST("/rideofferslave/{id}/accept/")
    Call<RideOfferSlave> acceptRideOfferSlave(@Path("id") long id, @Query("estimated_pickup_time") Integer estimatedPickupTime);

    @POST("/rideofferslave/{id}/decline/")
    Call<RideOfferSlave> declineRideOfferSlave(@Path("id") long id, @Query("estimated_pickup_time") Integer estimatedPickupTime);



}
