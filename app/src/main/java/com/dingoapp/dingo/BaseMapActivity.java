package com.dingoapp.dingo;

import android.os.Bundle;

import com.dingoapp.dingo.api.Callback;
import com.dingoapp.dingo.api.DingoApiService;
import com.dingoapp.dingo.api.Response;
import com.dingoapp.dingo.api.RideUtils;
import com.dingoapp.dingo.api.model.RideMasterRequest;
import com.dingoapp.dingo.api.model.RideOffer;
import com.dingoapp.dingo.google.maps.api.GoogleMapsApiService;
import com.dingoapp.dingo.google.maps.api.directions.Utils;
import com.dingoapp.dingo.google.maps.api.directions.model.DirectionsResponse;
import com.dingoapp.dingo.util.PhotosDownloader;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

import static com.dingoapp.dingo.api.AddressUtils.getLatLng;

/**
 * Created by guestguest on 24/03/16.
 */
public abstract class BaseMapActivity extends BaseActivity implements OnMapReadyCallback, GoogleMap.OnMapLoadedCallback {

    private LatLng mOrigin;
    private LatLng mDestination;
    private List<RideMasterRequest> mOrderedRequests;
    private PolylineOptions mPolylineOptions;
    private boolean mMapDidLoad;
    private GoogleMap mMap;


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);

        if(mapFragment == null) {
            return;
        }

        mOrigin = getLatLng(getOffer().getLeavingAddress());
        mDestination = getLatLng(getOffer().getArrivingAddress());

        LatLng[] waypoints = null;

        if(!getRequests().isEmpty()){
            if(getRequests().size() > 1){
                mOrderedRequests = RideUtils.orderRequestsByLeavingDistanceFromOffer(getOffer(), getRequests());
            }
            else{
                mOrderedRequests = new ArrayList<>();
                mOrderedRequests.addAll(getRequests());
            }

            waypoints = new LatLng[mOrderedRequests.size() * 2];

            for(int i = 0; i < mOrderedRequests.size(); i++){
                RideMasterRequest request = mOrderedRequests.get(i);
                waypoints[i] = getLatLng(request.getLeavingAddress());
                waypoints[i * 2] = getLatLng(request.getArrivingAddress());
            }
        }

        GoogleMapsApiService.getInstance().getDirections(mOrigin, mDestination, waypoints,
                new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Response<DirectionsResponse> response) {
                        String encodedPoly = response.body().getRoutes().get(0).getOverviewPolyline().getPoints();
                        List<LatLng> polyLatLgn = Utils.decodePoly(encodedPoly);

                        mPolylineOptions = new PolylineOptions()
                                .addAll(polyLatLgn)
                                .width(14).color(getResources().getColor(R.color.turquoise)).geodesic(true);

                        showPolyline();

                        gotDirections(response.body());

                    }

                    @Override
                    public void onFailure(Throwable t) {

                    }
                });

        mapFragment.getMapAsync(this);
    }

    protected abstract RideOffer getOffer();

    protected abstract List<RideMasterRequest> getRequests();

    protected void gotDirections(DirectionsResponse directions){

    }

    private synchronized void showPolyline(){

        if(!mMapDidLoad || mPolylineOptions == null){
            return;
        }

        Polyline polyline = mMap.addPolyline(mPolylineOptions);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        new PhotosDownloader(this, googleMap, mOrigin,   DingoApiService.getPhotoUrl(getOffer().getUser())).execute();
        new PhotosDownloader(this, googleMap, mDestination, DingoApiService.getPhotoUrl(getOffer().getUser())).execute();

        for(RideMasterRequest request: mOrderedRequests){
            new PhotosDownloader(this, googleMap, getLatLng(request.getLeavingAddress()),
                    DingoApiService.getPhotoUrl(request.getUser())).execute();
            new PhotosDownloader(this, googleMap, getLatLng(request.getArrivingAddress()),
                    DingoApiService.getPhotoUrl(request.getUser())).execute();
        }
        mMap = googleMap;
        mMap.setOnMapLoadedCallback(this);

    }

    @Override
    public void onMapLoaded() {

        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        builder.include(mOrigin);
        for(RideMasterRequest request: mOrderedRequests) {
            builder.include(getLatLng(request.getLeavingAddress()));
        }

        LatLngBounds latLngBounds = builder.build();

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(latLngBounds, 150);

        mMap.moveCamera(cameraUpdate);

        mMapDidLoad = true;

        showPolyline();

    }
}
