package com.dingoapp.dingo.api;

import com.dingoapp.dingo.api.model.CreditCardInfo;
import com.dingoapp.dingo.api.model.GcmToken;
import com.dingoapp.dingo.api.model.RideMasterRequest;
import com.dingoapp.dingo.api.model.RideOffer;
import com.dingoapp.dingo.api.model.User;
import com.dingoapp.dingo.api.model.UserRides;
import com.dingoapp.dingo.util.DingoService;
import com.dingoapp.dingo.util.OAuthInterceptor;
import com.dingoapp.dingo.util.RetrofitLogInterceptor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
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
        enqueueCall(call, callback);
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

    public void createRecurrentRideOffer(RideOffer offer, final Callback<RideOffer> callback){
        Call<RideOffer> call = apiService.createRecurrentOffer(offer);
        enqueueCall(call, callback);
    }

    public void createRideMasterRequest(RideMasterRequest request, Callback<RideMasterRequest> callback){
        Call<RideMasterRequest> call = apiService.createRideMasterRequest(request);
        enqueueCall(call, callback);
    }

    public void createRideMasterRequestRecurrent(RideMasterRequest request, Callback<RideMasterRequest> callback){
        Call<RideMasterRequest> call = apiService.createRideMasterRequestRecurrent(request);
        enqueueCall(call, callback);
    }

    public void getUserRides(Callback<UserRides> callback){
        Call<UserRides> call = apiService.getUserRides();
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

    public void createGcmToken(GcmToken token, Callback<GcmToken> callback){
        Call<GcmToken> call = apiService.createGcmToken(token);
        enqueueCall(call, callback);
    }

}
