package com.dingoapp.dingo.google.maps.api;

import com.dingoapp.dingo.api.AddressUtils;
import com.dingoapp.dingo.api.Callback;
import com.dingoapp.dingo.api.Response;
import com.dingoapp.dingo.api.model.Address;
import com.dingoapp.dingo.google.maps.api.geocoding.model.GeocodingResponse;
import com.dingoapp.dingo.google.maps.api.places.model.AddressComponent;
import com.dingoapp.dingo.google.maps.api.places.model.PlaceResponse;
import com.dingoapp.dingo.util.RetrofitLogInterceptor;
import com.google.gson.Gson;

import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;

/**
 * Created by guestguest on 03/02/16.
 */
public class GoogleMapsApiService {

    private static GoogleMapsApiService instance;
    private final GoogleMapsApi apiService;

    private GoogleMapsApiService(){
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new RetrofitLogInterceptor())
                .addInterceptor(new GoogleApiKeyInterceptor())
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GoogleMapsApi.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .build();

        apiService = retrofit.create(GoogleMapsApi.class);

    }

    public static GoogleMapsApiService getInstance(){
        if(instance == null){
            instance = new GoogleMapsApiService();
        }
        return instance;
    }

    public void getGeocodedAddresses(String address, final Callback<GeocodingResponse> callback){
        address = address.replace(" ", "+");
        final Call<GeocodingResponse> call = apiService.getGeocodedAddresses(address);
        call.enqueue(new retrofit2.Callback<GeocodingResponse>() {
            @Override
            public void onResponse(retrofit2.Response<GeocodingResponse> response) {
                Response<GeocodingResponse> dResponse = new Response<GeocodingResponse>(response.code(),
                        response.body());
                callback.onResponse(dResponse);
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });

    }
    public void getPlaceById(String placeId, final Callback<Address> callback){
        final Call<PlaceResponse> call = apiService.getPlaceById(placeId, GoogleMapsApi.LANGUAGE_PT_BR, false);
        call.enqueue(new retrofit2.Callback<PlaceResponse>() {
            @Override
            public void onResponse(retrofit2.Response<PlaceResponse> response) {
                Response<Address> dResponse = new Response<>(response.code(),
                        convertPlaceToAddress(response.body()));
                callback.onResponse(dResponse);
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }

    private Address convertPlaceToAddress(PlaceResponse place){
        Address address = new Address();

        com.dingoapp.dingo.google.maps.api.places.model.Result result = place.getResult();
        address.setPlaceId(result.getPlaceId());
        address.setName(result.getName());
        address.setLatitude(result.getGeometry().getLocation().getLat());
        address.setLongitude(result.getGeometry().getLocation().getLng());

        for(AddressComponent addressComponent: result.getAddressComponents()){
            List<String> types = addressComponent.getTypes();
            if(types.contains("street_number")){
                address.setNumber(addressComponent.getLongName());
            }
            else if(types.contains("route")){
                address.setRouteLong(addressComponent.getLongName());
                address.setRouteShort(addressComponent.getShortName());
            }
            else if(types.contains("sublocality") || types.contains("neighborhood")){
                address.setDistrict(addressComponent.getLongName());
            }
            else if(types.contains("locality")){
                address.setCity(addressComponent.getLongName());
            }
            else if(types.contains("administrative_area_level_1")){
                address.setState(addressComponent.getLongName());
            }
            else if(types.contains("administrative_area_level_2")){
                address.setRegion(addressComponent.getLongName());
            }
            else if(types.contains("country")){
                address.setCountry(addressComponent.getLongName());
            }
            else if(types.contains("postal_code")){
                address.setPostalCode(addressComponent.getLongName());
            }
            else if(types.contains("establishment")){
                address.setEstablishmentType(true);
            }
        }

        //sometimes place has establishment type but is a route name
        if(AddressUtils.hasRoutePattern(address.getName())){
            address.setRouteType(true);

            String[] routeComponents = AddressUtils.extractRouteAndPreciseNumber(address.getName());
            if(routeComponents != null){
                address.setRouteLong(routeComponents[0]);
                if(routeComponents[1] != null){
                    address.setNumber(routeComponents[1]);
                }
            }
            else{
                address.setRouteLong(address.getName());
                //FIXME ? short
            }
        }

        return address;

    }
}
