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

    @Bind(R.id.offer)Button mOfferButton;
    @Bind(R.id.decline)Button mDeclineButton;

    LatLng PE_ANTONIO = new LatLng(-23.605671, -46.692275);
    LatLng LEOPOLDO = new LatLng(-23.587741, -46.679778);
    //LatLng PAULISTA = new LatLng(-23.560541, -46.657462);
    //LatLng ROD = new LatLng(-23.515551, -46.624975);

    private GoogleMap mMap;
    private List<RideMasterRequest> mOrderedRequests;
    private RideOffer mOffer;
    private RideOfferSlave mSlaveOffer;
    private SimpleDateFormat mTimeFormat;
    private PolylineOptions mPolylineOptions;
    private LatLng mOrigin;
    private LatLng mDestination;
    private boolean mMapDidLoad;
    private int mDuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slave_offer_reply);
        getSupportActionBar().setTitle(R.string.activity_slave_offer);
        getActionBarToolbar().showOverflowMenu();
        ButterKnife.bind(this);

        mOffer = (RideOffer)getIntent().getSerializableExtra(EXTRA_OFFER);

        if(mOffer != null){
            mSlaveOffer = mOffer.getInvitesToAccept().get(0);
            String riderName = mSlaveOffer.getToRideRequest().getUser().getFirstName();
            mHeaderUser.setText(getString(R.string.slave_offer_user_wants_a_ride, riderName));

            SimpleDateFormat mDayFormat = new SimpleDateFormat("EEEE");
            mTimeFormat = new SimpleDateFormat("HH:mm");

            mDateTime.setText(getString(R.string.slave_offer_datetime,
                    mDayFormat.format(mOffer.getLeavingTime()),
                    mTimeFormat.format(mOffer.getLeavingTime())));

            Glide.with(this).load(DingoApiService.getPhotoUrl(mSlaveOffer.getToRideRequest().getUser()))
                    .bitmapTransform(new CircleTransform(this))
                    .into(mHeaderPicture);

            mRouteText2Top.setText(getString(R.string.slave_offer_meets_at, riderName));
            mRouteText3Top.setText(getString(R.string.slave_offer_drops_at, riderName));

            mOrigin = getLatLng(mOffer.getLeavingAddress());
            mDestination = getLatLng(mOffer.getArrivingAddress());
            LatLng[] waypoints;
            if(mOffer.getRequests().isEmpty()){
                waypoints = new LatLng[2];
                waypoints[0] = getLatLng(mSlaveOffer.getToRideRequest().getLeavingAddress());
                waypoints[1] = getLatLng(mSlaveOffer.getToRideRequest().getArrivingAddress());
                mOrderedRequests = new ArrayList<>();
                mOrderedRequests.add(mSlaveOffer.getToRideRequest());
            }
            else{
                List<RideMasterRequest> requestsWithSlaveOfferRequest = new ArrayList<>(mOffer.getRequests());
                requestsWithSlaveOfferRequest.add(mSlaveOffer.getToRideRequest());

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
                                    if (request == mSlaveOffer.getToRideRequest()) {
                                        break;
                                    }
                                }

                                mDuration = duration;
                                //fill up address using duration
                                showAddresses();
                            } else {
                                showAddresses();
                            }
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            //todo show retry option over the map
                            showAddresses();
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
                        DingoApiService.getInstance().acceptRideOfferSlave(mSlaveOffer.getId(), mDuration,
                                new Callback<RideOfferSlave>() {
                                    @Override
                                    public void onResponse(Response<RideOfferSlave> response) {
                                        if(response.code() == Response.HTTP_200_OK){
                                            mOffer.getInvitesToAccept().clear();
                                            mOffer.getInvitesWaitingConfirmation().add(response.body());
                                            Intent intent = new Intent(SlaveOfferReplyActivity.this, OfferDetailsActivity.class);
                                            intent.putExtra(SlaveOfferReplyActivity.EXTRA_OFFER, mOffer);
                                            startActivity(intent);
                                        }

                                    }

                                    @Override
                                    public void onFailure(Throwable t) {

                                    }
                                });
                    }
                }

        );

        mDeclineButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DingoApiService.getInstance().declineRideOfferSlave(mSlaveOffer.getId(), mDuration,
                                new Callback<RideOfferSlave>() {
                                    @Override
                                    public void onResponse(Response<RideOfferSlave> response) {
                                        mOffer.getInvitesToAccept().clear();
                                        Intent intent = new Intent(SlaveOfferReplyActivity.this, OfferDetailsActivity.class);
                                        intent.putExtra(SlaveOfferReplyActivity.EXTRA_OFFER, mOffer);
                                        startActivity(intent);
                                    }

                                    @Override
                                    public void onFailure(Throwable t) {

                                    }
                                });
                    }
                }
        );
    }

    private void showAddresses(){
       mRouteText1.setText(
               getString(R.string.slave_offer_leaving_address,
                       getOneLineAddress(mOffer.getLeavingAddress(), true),
                       mTimeFormat.format(mOffer.getLeavingTime())));

        if(mDuration > 0){
            Calendar cal = Calendar.getInstance();
            cal.setTime(mOffer.getLeavingTime());
            cal.add(Calendar.SECOND, mDuration);

            mRouteText2.setText(
                    getString(R.string.slave_offer_meets_address,
                            getOneLineAddress(mSlaveOffer.getToRideRequest().getLeavingAddress(), true),
                            mTimeFormat.format(cal.getTime())));

        }
        else{
            mRouteText2.setText(getOneLineAddress(mSlaveOffer.getToRideRequest().getLeavingAddress(), true));
        }

        mRouteText3.setText(getOneLineAddress(mSlaveOffer.getToRideRequest().getArrivingAddress(), true));

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

    }

    private synchronized void showPolyline(){

        if(!mMapDidLoad || mPolylineOptions == null){
            return;
        }

        Polyline polyline = mMap.addPolyline(mPolylineOptions);

    }
}
