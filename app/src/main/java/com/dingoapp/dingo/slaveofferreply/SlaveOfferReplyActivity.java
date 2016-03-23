package com.dingoapp.dingo.slaveofferreply;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dingoapp.dingo.BaseActivity;
import com.dingoapp.dingo.OfferDetailsActivity;
import com.dingoapp.dingo.R;
import com.dingoapp.dingo.api.Callback;
import com.dingoapp.dingo.api.DingoApiService;
import com.dingoapp.dingo.api.Response;
import com.dingoapp.dingo.api.RideUtils;
import com.dingoapp.dingo.api.model.RideMasterRequest;
import com.dingoapp.dingo.api.model.RideOffer;
import com.dingoapp.dingo.api.model.RideOfferSlave;
import com.dingoapp.dingo.google.maps.api.GoogleMapsApiService;
import com.dingoapp.dingo.google.maps.api.directions.Utils;
import com.dingoapp.dingo.google.maps.api.directions.model.DirectionsResponse;
import com.dingoapp.dingo.util.CircleTransform;
import com.dingoapp.dingo.util.PhotosDownloader;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.Polyline;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.dingoapp.dingo.api.AddressUtils.getLatLng;
import static com.dingoapp.dingo.api.AddressUtils.getOneLineAddress;

/**
 * Created by guestguest on 08/03/16.
 */
public class SlaveOfferReplyActivity extends BaseActivity implements OnMapReadyCallback, GoogleMap.OnMapLoadedCallback{

    //when it's opened from main screen
    public static final String EXTRA_OFFER = "EXTRA_OFFER";

    //when it's opened from notification
    public static final String EXTRA_SLAVE_OFFER_ID = "EXTRA_SLAVE_OFFER_ID";

    @Bind(R.id.header_user)TextView mHeaderUser;
    @Bind(R.id.header_date_time)TextView mDateTime;

    @Bind(R.id.header_picture)ImageView mHeaderPicture;

    @Bind(R.id.route_text1_top)TextView mRouteText1Top;
    @Bind(R.id.route_text2_top)TextView mRouteText2Top;
    @Bind(R.id.route_text3_top)TextView mRouteText3Top;

    @Bind(R.id.route_text1)TextView mRouteText1;
    @Bind(R.id.route_text2)TextView mRouteText2;
    @Bind(R.id.route_text3)TextView mRouteText3;
    @Bind(R.id.route_text4)TextView mRouteText4;

    @Bind(R.id.decline)Button mDeclineButton;
    @Bind(R.id.offer)Button mOfferButton;

    LatLng PE_ANTONIO = new LatLng(-23.605671, -46.692275);
    LatLng LEOPOLDO = new LatLng(-23.587741, -46.679778);
    //LatLng PAULISTA = new LatLng(-23.560541, -46.657462);
    //LatLng ROD = new LatLng(-23.515551, -46.624975);

    private GoogleMap mMap;
    private List<RideMasterRequest> mOrderedRequests;
    private RideOffer mOffer;
    private SimpleDateFormat mTimeFormat;
    private PolylineOptions mPolylineOptions;
    private LatLng mOrigin;
    private LatLng mDestination;
    private boolean mMapDidLoad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slave_offer_reply);
        getSupportActionBar().setTitle(R.string.activity_slave_offer);
        getActionBarToolbar().showOverflowMenu();
        ButterKnife.bind(this);

        mOffer = (RideOffer)getIntent().getSerializableExtra(EXTRA_OFFER);

        if(mOffer != null){
            final RideOfferSlave slaveOffer = mOffer.getSlave();
            String riderName = slaveOffer.getToRideRequest().getUser().getFirstName();
            mHeaderUser.setText(getString(R.string.slave_offer_user_wants_a_ride, riderName));

            SimpleDateFormat mDayFormat = new SimpleDateFormat("EEEE");
            mTimeFormat = new SimpleDateFormat("HH:mm");

            mDateTime.setText(getString(R.string.slave_offer_datetime,
                    mDayFormat.format(mOffer.getLeavingTime()),
                    mTimeFormat.format(mOffer.getLeavingTime())));

            Glide.with(this).load(DingoApiService.getPhotoUrl(mOffer.getSlave().getToRideRequest().getUser()))
                    .bitmapTransform(new CircleTransform(this))
                    .into(mHeaderPicture);

            mRouteText2Top.setText(getString(R.string.slave_offer_meets_at, riderName));
            mRouteText3Top.setText(getString(R.string.slave_offer_drops_at, riderName));

            mOrigin = getLatLng(mOffer.getLeavingAddress());
            mDestination = getLatLng(mOffer.getArrivingAddress());
            LatLng[] waypoints;
            if(mOffer.getRequests().isEmpty()){
                waypoints = new LatLng[2];
                waypoints[0] = getLatLng(slaveOffer.getToRideRequest().getLeavingAddress());
                waypoints[1] = getLatLng(slaveOffer.getToRideRequest().getArrivingAddress());
                mOrderedRequests = new ArrayList<>();
                mOrderedRequests.add(slaveOffer.getToRideRequest());
            }
            else{
                List<RideMasterRequest> requestsWithSlaveOfferRequest = new ArrayList<>(mOffer.getRequests());
                requestsWithSlaveOfferRequest.add(slaveOffer.getToRideRequest());

                mOrderedRequests = RideUtils.orderRequestsByLeavingDistanceFromOffer(mOffer, requestsWithSlaveOfferRequest);

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
                            if (response.code() == Response.HTTP_200_OK) {
                                String encodedPoly = response.body().getRoutes().get(0).getOverviewPolyline().getPoints();
                                List<LatLng> polyLatLgn = Utils.decodePoly(encodedPoly);

                                mPolylineOptions = new PolylineOptions()
                                        .addAll(polyLatLgn)
                                        .width(14).color(getResources().getColor(R.color.turquoise)).geodesic(true);

                                showPolyline();

                                //todo: get duration with traffic
                                int duration = 0;
                                //sum up durations of legs up to the slave offer request
                                for (int i = 0; i < mOrderedRequests.size(); i++) {
                                    RideMasterRequest request = mOrderedRequests.get(i);
                                    duration += response.body().getRoutes().get(0).getLegs().get(i).getDuration().getValue();
                                    if (request == slaveOffer.getToRideRequest()) {
                                        break;
                                    }
                                }

                                //fill up address using duration
                                showAddresses(duration);
                            } else {
                                showAddresses(0);
                            }
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            //todo show retry option over the map
                            showAddresses(0);
                        }
                    });

            MapFragment mapFragment = (MapFragment) getFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);

        }

        mOfferButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(SlaveOfferReplyActivity.this, OfferDetailsActivity.class);
                        startActivity(intent);
                    }
                }

        );


    }

    private void showAddresses(int duration){
       mRouteText1.setText(
               getString(R.string.slave_offer_leaving_address,
                       getOneLineAddress(mOffer.getLeavingAddress(), true),
                       mTimeFormat.format(mOffer.getLeavingTime())));

        if(duration > 0){
            Calendar cal = Calendar.getInstance();
            cal.setTime(mOffer.getLeavingTime());
            cal.add(Calendar.SECOND, duration);

            mRouteText2.setText(
                    getString(R.string.slave_offer_meets_address,
                            getOneLineAddress(mOffer.getSlave().getToRideRequest().getLeavingAddress(), true),
                            mTimeFormat.format(cal.getTime())));

        }
        else{
            mRouteText2.setText(getOneLineAddress(mOffer.getSlave().getToRideRequest().getLeavingAddress(), true));
        }

        mRouteText3.setText(getOneLineAddress(mOffer.getSlave().getToRideRequest().getArrivingAddress(), true));

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        new PhotosDownloader(this, googleMap, mOrigin, "http://36.media.tumblr.com/0495e65a4f15696b4f3cc0dcff59a9e0/tumblr_mtqdx1uILV1r93kc1o1_r1_1280.jpg").execute();
        //new PhotosDownloader(this, googleMap, LEOPOLDO, mOffer.getSlave().getToRideRequest().getUser().getProfilePhotoOriginal()).execute();
        //new PhotosDownloader(this, googleMap, PAULISTA, mOffer.getSlave().getToRideRequest().getUser().getProfilePhotoOriginal()).execute();
        new PhotosDownloader(this, googleMap, mDestination, "http://36.media.tumblr.com/0495e65a4f15696b4f3cc0dcff59a9e0/tumblr_mtqdx1uILV1r93kc1o1_r1_1280.jpg").execute();

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

        /*GoogleMapsApiService.getInstance().getDirections(PE_ANTONIO, ROD, new LatLng[]{LEOPOLDO, PAULISTA},
                new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Response<DirectionsResponse> response) {
                        if (response.code() == Response.HTTP_200_OK) {
                            String encodedPoly = response.body().getRoutes().get(0).getOverviewPolyline().getPoints();
                            List<LatLng> polyLatLgn = Utils.decodePoly(encodedPoly);

                            Polyline polyline = mMap.addPolyline(
                                    new PolylineOptions()
                                            .addAll(polyLatLgn)
                                            .width(14).color(getResources().getColor(R.color.turquoise)).geodesic(true)
                            );
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {

                    }
                });*/
    }

    private synchronized void showPolyline(){

        if(!mMapDidLoad || mPolylineOptions == null){
            return;
        }

        Polyline polyline = mMap.addPolyline(mPolylineOptions);

    }
}
