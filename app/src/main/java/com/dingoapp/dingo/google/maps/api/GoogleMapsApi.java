package com.dingoapp.dingo.google.maps.api;

import com.dingoapp.dingo.google.maps.api.directions.model.DirectionsResponse;
import com.dingoapp.dingo.google.maps.api.geocoding.model.GeocodingResponse;
import com.dingoapp.dingo.google.maps.api.places.model.PlaceResponse;

import com.dingoapp.dingo.util.ErrorHandlingCallAdapter.Call;

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

    //https://maps.googleapis.com/maps/api/directions/json?origin=-23.605671,-46.692275&destination=-23.515551,-46.624975&mode=driving&waypoints=-23.587741,-46.679778|-23.560541,-46.657462
    @GET("directions/json")
    Call<DirectionsResponse> getDirections(@Query("origin")String origin, @Query("destination")String destination, @Query("waypoints")String waypoints);

}
