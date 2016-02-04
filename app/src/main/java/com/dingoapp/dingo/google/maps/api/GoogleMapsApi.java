package com.dingoapp.dingo.google.maps.api;

import com.dingoapp.dingo.google.maps.api.geocoding.model.GeocodingResponse;
import com.dingoapp.dingo.google.maps.api.places.model.PlaceResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by guestguest on 03/02/16.
 */
public interface GoogleMapsApi {

    String BASE_URL = "https://maps.googleapis.com/maps/api/";

    String LANGUAGE_PT_BR = "pt-BR";

    @GET("place/details/json")
    Call<PlaceResponse> getPlaceById(@Query("placeid")String placeId, @Query("language")String language, @Query("sensor")boolean sensor);

    //FIXME key is being added to query, should it?
    @GET("geocode/json")
    Call<GeocodingResponse> getGeocodedAddresses(@Query("address") String address);

}
