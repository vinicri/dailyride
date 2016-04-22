package com.lightgraylabs.dailyrides.api;

import android.net.Uri;

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
import com.lightgraylabs.dailyrides.util.ErrorHandlingCallAdapter;
import com.lightgraylabs.dailyrides.util.ErrorHandlingCallAdapter.Call;
import com.lightgraylabs.dailyrides.util.OAuthInterceptor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;

/**
 * Created by guestguest on 13/01/16.
 */
public class DingoApiService extends DingoService{

    private static DingoApiService instance;
    private final DingoApi apiService;

    private DingoApiService(){
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .create();

        OkHttpClient client = new OkHttpClient.Builder()
                //.addInterceptor(new RetrofitLogInterceptor())
                .addInterceptor(new OAuthInterceptor())
                .authenticator(new DingoAuthenticator())
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

    public static DingoApiService getInstance(){
        if(instance == null){
            instance = new DingoApiService();
        }
        return instance;
    }

    public void registerWithFacebook(User user, final Callback<User> callback){
        final Call<User> call = apiService.registerWithFacebook(user);
        enqueueCall(call, callback);
    }

    public void userLogin(String email, String password, Callback<User> callback){
        final Call<User> call = apiService.userLogin(email, password);
        enqueueCall(call, callback);
    }

    public void userLogout(String installationUuid, Callback<Void> callback){
        Call<Void> call = apiService.userLogout(installationUuid);
        enqueueCall(call, callback);
    }

    public Response<Void> userLogoutSync(String installationUuid) throws IOException {
        retrofit2.Response<Void> response = apiService.userLogout(installationUuid).execute();
        return new Response<>(response.code(), response.body());
    }

    public Response<User.OAuthToken> refreshToken(String refreshToken) throws IOException{
        //RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), refreshToken);
        retrofit2.Response<User.OAuthToken> response = apiService.refreshToken(refreshToken).execute();
        return new Response<>(response.code(), response.body());
    }

    public void userRegister(User user, final Callback<User> callback){
        Call<User> call = apiService.userRegister(user);
        enqueueCall(call, callback);
    }

    public void userSignUpConfirm(String pin, Callback<Void> callback){
        Call<Void> call = apiService.userSignupConfirm(pin);
        enqueueCall(call, callback);
    }

    public void acceptTerms(User.RiderMode riderMode, final Callback<Void> callback){
        final Call<Void> call = apiService.acceptTerms(riderMode);
        enqueueCall(call, callback);
        /*call.enqueue(new retrofit2.Callback<Void>() {
            @Override
            public void onResponse(retrofit2.Response<Void> response) {
                Response dResponse = new Response(response.code(), response.body());
                callback.onResponse(dResponse);
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });*/

    }


    public void userAddPhone(String phone, Callback<Void> callback){
        Call<Void> call = apiService.userAddPhone(phone);
        enqueueCall(call, callback);
    }

    public void userAddWork(String email, Callback<Institution> callback){
        Call<Institution> call = apiService.userAddWork(email);
        enqueueCall(call, callback);
    }


    public void userAddSchool(String email, Callback<Institution> callback){
        Call<Institution> call = apiService.userAddSchool(email);
        enqueueCall(call, callback);
    }

    public void userConfirmWork(String pin, String company, Callback<Institution> callback){
        Call<Institution> call = apiService.userConfirmWork(pin, company);
        enqueueCall(call, callback);
    }

    public void userConfirmSchool(String pin, String school, Callback<Institution> callback){
        Call<Institution> call = apiService.userConfirmSchool(pin, school);
        enqueueCall(call, callback);
    }

    public void uploadWorkCredential(String company, Uri imageUri, Callback<Institution> callback ){
        File file = new File(imageUri.getPath());
        RequestBody imageBody = RequestBody.create(MediaType.parse("image/*"), file);
        Call<Institution> call = apiService.addWorkCredential(imageBody);
        enqueueCall(call, callback);
    }

    public void getFirebaseToken(Callback<Token> callback){
        Call<Token> call = apiService.getFirebaseToken();
        enqueueCall(call, callback);
    }

    public void userGetComments(long id, Callback<List<RideRating>> callback){
        Call<List<RideRating>> call = apiService.userGetComments(id);
        enqueueCall(call, callback);
    }

    public void findOffersforRequest(RideMasterRequest request, final Callback<List<RideOffer>> callback){
        Call<List<RideOffer>> call = apiService.findOffersforRequest(request);
        enqueueCall(call, callback);
    };

    public void findOffersforRecurrentRequest(RideMasterRequest request, final Callback<List<RideOffer>>  callback){
        Call<List<RideOffer>> call = apiService.findOffersforRecurrentRequest(request);
        enqueueCall(call, callback);
    }

    public void createRideOffer(RideOffer offer, final Callback<RideOffer> callback){
        Call<RideOffer> call = apiService.createOffer(offer);
        enqueueCall(call, callback);
    }

    public void cancelRideOffer(long id, final Callback<Void> callback){
        Call<Void> call = apiService.cancelOffer(id);
        enqueueCall(call, callback);
    }

    public void createRecurrentRideOffer(RideOffer offer, final Callback<RideOffer> callback){
        Call<RideOffer> call = apiService.createRecurrentOffer(offer);
        enqueueCall(call, callback);
    }

    public void deleteRecurrentRideOffer(long id, final Callback<Void> callback){
        Call<Void> call = apiService.deleteRecurrentOffer(id);
        enqueueCall(call, callback);
    }

    public void createRideMasterRequest(RideMasterRequest request, Callback<RideMasterRequest> callback){
        Call<RideMasterRequest> call = apiService.createRideMasterRequest(request);
        enqueueCall(call, callback);
    }

    public void cancelRideMasterRequest(long id, final Callback<Void> callback){
        Call<Void> call = apiService.cancelRideMasterRequest(id);
        enqueueCall(call, callback);
    }

    public void createRideMasterRequestRecurrent(RideMasterRequest request, Callback<RideMasterRequest> callback){
        Call<RideMasterRequest> call = apiService.createRideMasterRequestRecurrent(request);
        enqueueCall(call, callback);
    }

    public void deleteRideMasterRequestRecurrent(long id, final Callback<Void> callback){
        Call<Void> call = apiService.deleteRideMasterRequestRecurrent(id);
        enqueueCall(call, callback);
    }

    public void getUserRides(Callback<UserRides> callback){
        Call<UserRides> call = apiService.getUserRides();
        enqueueCall(call, callback);
    }

    public void getUserRecurrentRides(Callback<UserRides> callback){
        Call<UserRides> call = apiService.getUserRecurrentRides();
        enqueueCall(call, callback);
    }

    public void getCreditCardInfo(Callback<CreditCardInfo> callback){
        Call<CreditCardInfo> call = apiService.getCreditCardInfo();
        enqueueCall(call, callback);
    }

    public void createCreditCardInfo(CreditCardInfo creditCardInfo, Callback<CreditCardInfo> callback){
        Call<CreditCardInfo> call = apiService.createCreditCardInfo(creditCardInfo);
        enqueueCall(call, callback);
    }

    public void updateCreditCardInfo(CreditCardInfo creditCardInfo, Callback<CreditCardInfo> callback){
        Call<CreditCardInfo> call = apiService.updateCreditCardInfo(creditCardInfo);
        enqueueCall(call, callback);
    }

    public void deleteCreditCardInfo(CreditCardInfo creditCardInfo, Callback<Void> callback){
        Call<Void> call = apiService.deleteCreditCardInfo(creditCardInfo);
        enqueueCall(call, callback);
    }

    public void getRideOfferSlave(long id, Callback<RideOfferSlave> callback){
        Call<RideOfferSlave> call = apiService.getRideOfferSlave(id);
        enqueueCall(call, callback);
    }

    public void acceptRideOfferSlave(long id, Integer estimatedPickupTime, Callback<RideOfferSlave> callback){
        Call<RideOfferSlave> call = apiService.acceptRideOfferSlave(id, estimatedPickupTime);
        enqueueCall(call, callback);
    }

    public void declineRideOfferSlave(long id, Integer estimatedPickupTime, Callback<RideOfferSlave> callback){
        Call<RideOfferSlave> call = apiService.declineRideOfferSlave(id, estimatedPickupTime);
        enqueueCall(call, callback);
    }

    public static String getPhotoUrl(User user){
       return DingoApi.MEDIA_URL + user.getProfilePhotoOriginal();
    }

    //syncronous method
    public Response<GcmToken> createGcmToken(GcmToken token) throws IOException {
        retrofit2.Response<GcmToken> response = apiService.createGcmToken(token).execute();
        return new Response<>(response.code(), response.body());
    }




}
