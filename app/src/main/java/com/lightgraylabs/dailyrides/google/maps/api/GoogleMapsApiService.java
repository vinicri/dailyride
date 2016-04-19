package com.lightgraylabs.dailyrides.google.maps.api;

import com.lightgraylabs.dailyrides.api.AddressUtils;
import com.lightgraylabs.dailyrides.api.Callback;
import com.lightgraylabs.dailyrides.api.Response;
import com.lightgraylabs.dailyrides.api.model.Address;
import com.lightgraylabs.dailyrides.google.maps.api.directions.model.DirectionsResponse;
import com.lightgraylabs.dailyrides.google.maps.api.geocoding.model.GeocodingResponse;
import com.lightgraylabs.dailyrides.google.maps.api.places.model.AddressComponent;
import com.lightgraylabs.dailyrides.google.maps.api.places.model.PlaceResponse;
import com.lightgraylabs.dailyrides.api.DingoService;
import com.lightgraylabs.dailyrides.util.ErrorHandlingCallAdapter;
import com.lightgraylabs.dailyrides.util.ErrorHandlingCallAdapter.Call;
import com.lightgraylabs.dailyrides.util.RetrofitLogInterceptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;

/**
 * Created by guestguest on 03/02/16.
 */
public class GoogleMapsApiService extends DingoService{

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
                .addCallAdapterFactory(new ErrorHandlingCallAdapter.ErrorHandlingCallAdapterFactory())
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
        enqueueCall(call, callback);
    }

    public void getDirections(LatLng origin, LatLng destination, LatLng[] waypoints, Callback<DirectionsResponse> callback){
        String waypointsString;
        if(waypoints != null) {
            waypointsString = latLgnText(waypoints[0]);
            for (int i = 1; i < waypoints.length; i++) {
                waypointsString += "|" + latLgnText(waypoints[1]);
            }
        }
        else{
            waypointsString = null;
        }

        Call<DirectionsResponse> call = apiService.getDirections(latLgnText(origin), latLgnText(destination), waypointsString);

        enqueueCall(call, callback);
    }

    public void getPlaceById(String placeId, final Callback<Address> callback){
        final Call<PlaceResponse> call = apiService.getPlaceById(placeId, GoogleMapsApi.LANGUAGE_PT_BR, false);
       // call.enqueue();
        call.enqueue(new ErrorHandlingCallAdapter.Callback<PlaceResponse>() {
            @Override
            public void success(retrofit2.Response<PlaceResponse> response) {
                Response<Address> dResponse = new Response<>(response.code(),
                        convertPlaceToAddress(response.body()));
                callback.success(dResponse);
            }

            @Override
            public void unauthenticated(retrofit2.Response<?> response) {

            }

            @Override
            public void clientError(retrofit2.Response<?> response) {

            }

            @Override
            public void serverError(retrofit2.Response<?> response) {

            }

            @Override
            public void networkError(IOException e) {

            }

            @Override
            public void unexpectedError(Throwable t) {

            }
        });
    }


    private Address convertPlaceToAddress(PlaceResponse place){
        Address address = new Address();

        com.lightgraylabs.dailyrides.google.maps.api.places.model.Result result = place.getResult();
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

        if(result.getTypes() != null && result.getTypes().contains("establishment")){
            address.setEstablishmentType(true);
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

    private String latLgnText(LatLng latLng){
        return latLng.latitude + "," + latLng.longitude;
    }
}
