package com.lightgraylabs.dailyrides;

import android.os.Bundle;

import com.lightgraylabs.dailyrides.api.ApiCallback;
import com.lightgraylabs.dailyrides.api.DingoApiService;
import com.lightgraylabs.dailyrides.api.Response;
import com.lightgraylabs.dailyrides.api.RideUtils;
import com.lightgraylabs.dailyrides.api.model.RideMasterRequest;
import com.lightgraylabs.dailyrides.api.model.RideOffer;
import com.lightgraylabs.dailyrides.google.maps.api.GoogleMapsApiService;
import com.lightgraylabs.dailyrides.google.maps.api.directions.Utils;
import com.lightgraylabs.dailyrides.google.maps.api.directions.model.DirectionsResponse;
import com.lightgraylabs.dailyrides.util.CurrentUser;
import com.lightgraylabs.dailyrides.util.PhotosDownloader;
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

import static com.lightgraylabs.dailyrides.api.AddressUtils.getLatLng;

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
        mOrderedRequests = new ArrayList<>();

        if(!getRequests().isEmpty()){
            if(getRequests().size() > 1){
                mOrderedRequests = RideUtils.orderRequestsByLeavingDistanceFromOffer(getOffer(), getRequests());
            }
            else{
                mOrderedRequests.addAll(getRequests());
            }

            waypoints = new LatLng[mOrderedRequests.size() * 2];

            for(int i = 0; i < mOrderedRequests.size(); i++){
                RideMasterRequest request = mOrderedRequests.get(i);
                waypoints[i] = getLatLng(request.getLeavingAddress());
                waypoints[i * 2 + 1] = getLatLng(request.getArrivingAddress());
            }
        }

        GoogleMapsApiService.getInstance().getDirections(mOrigin, mDestination, waypoints,
                new ApiCallback<DirectionsResponse>(this) {
                    @Override
                    public void success(Response<DirectionsResponse> response) {
                        if(response.code() == Response.HTTP_200_OK) {
                            String encodedPoly = response.body().getRoutes().get(0).getOverviewPolyline().getPoints();
                            List<LatLng> polyLatLgn = Utils.decodePoly(encodedPoly);

                            mPolylineOptions = new PolylineOptions()
                                    .addAll(polyLatLgn)
                                    .width(14).color(getResources().getColor(R.color.turquoise)).geodesic(true);

                            showPolyline();

                            gotDirections(response.body(), mOrderedRequests);
                        }
                        else{
                            directionsFailed(mOrderedRequests);
                        }
                    }
                });

        mapFragment.getMapAsync(this);
    }

    protected abstract RideOffer getOffer();

    protected abstract List<RideMasterRequest> getRequests();

    protected void gotDirections(DirectionsResponse directions, List<RideMasterRequest> orderedRequests){

    }


    protected void directionsFailed(List<RideMasterRequest> orderedRequests){

    };

    private synchronized void showPolyline(){

        if(!mMapDidLoad || mPolylineOptions == null){
            return;
        }

        Polyline polyline = mMap.addPolyline(mPolylineOptions);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        //fixme remove
        new PhotosDownloader(this, googleMap, mOrigin, DingoApiService.getPhotoUrl(CurrentUser.getUser())).execute();
        new PhotosDownloader(this, googleMap, mDestination, DingoApiService.getPhotoUrl(CurrentUser.getUser())).execute();

        // new PhotosDownloader(this, googleMap, mOrigin,   DingoApiService.getPhotoUrl(getOffer().getUser())).execute();
       // new PhotosDownloader(this, googleMap, mDestination, DingoApiService.getPhotoUrl(getOffer().getUser())).execute();

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
        if(mOrderedRequests.size() == 0){
            //no request, include also destination, show all user route
            builder.include(mDestination);
        }
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
