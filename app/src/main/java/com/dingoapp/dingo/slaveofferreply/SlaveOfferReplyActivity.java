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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.text.SimpleDateFormat;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

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
    LatLng PAULISTA = new LatLng(-23.560541, -46.657462);
    LatLng ROD = new LatLng(-23.515551, -46.624975);
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slave_offer_reply);
        getSupportActionBar().setTitle(R.string.activity_slave_offer);
        getActionBarToolbar().showOverflowMenu();
        ButterKnife.bind(this);

        RideOffer offer = (RideOffer)getIntent().getSerializableExtra(EXTRA_OFFER);

        if(offer != null){
            RideOfferSlave slaveOffer = offer.getSlave();
            String riderName = slaveOffer.getToRideRequest().getUser().getFirstName();
            mHeaderUser.setText(getString(R.string.slave_offer_user_wants_a_ride, riderName));

            SimpleDateFormat mDayFormat = new SimpleDateFormat("EEEE");
            SimpleDateFormat mTimeFormat = new SimpleDateFormat("HH:mm");

            mDateTime.setText(getString(R.string.slave_offer_datetime,
                    mDayFormat.format(offer.getLeavingTime()),
                    mTimeFormat.format(offer.getLeavingTime())));

            Glide.with(this).load(DingoApiService.getPhotoUrl(offer.getSlave().getToRideRequest().getUser()))
                    .bitmapTransform(new CircleTransform(this))
                    .into(mHeaderPicture);

            mRouteText2Top.setText(getString(R.string.slave_offer_meets_at, riderName));
            mRouteText3Top.setText(getString(R.string.slave_offer_drops_at, riderName));



        }

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

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

    @Override
    public void onMapReady(GoogleMap googleMap) {

        //CameraUpdate cameraUpdate =
//        LatLng PERTH = new LatLng(-31.90, 115.86);
//        Marker perth = googleMap.addMarker(new MarkerOptions()
//                .position(PERTH)
//                .anchor(0.5f, 0.5f)
//                .rotation(90.0f));


//        int size = Math.min(source.getWidth(), source.getHeight());
//        int x = (source.getWidth() - size) / 2;
//        int y = (source.getHeight() - size) / 2;
//
//        // TODO this could be acquired from the pool too
       //Bitmap squared = Bitmap.createBitmap(source, x, y, size, size);
//
//        Bitmap result = pool.get(size, size, Bitmap.Config.ARGB_8888);
//        if (result == null) {
//            result = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
//        }

        new PhotosDownloader(this, googleMap, PE_ANTONIO, "http://36.media.tumblr.com/0495e65a4f15696b4f3cc0dcff59a9e0/tumblr_mtqdx1uILV1r93kc1o1_r1_1280.jpg").execute();
        new PhotosDownloader(this, googleMap, LEOPOLDO, "http://ndl.mgccw.com/mu3/000/390/055/sss/fbe75c22e38349b587df7de1cfa842b2_small.png").execute();
        new PhotosDownloader(this, googleMap, PAULISTA, "http://ndl.mgccw.com/mu3/000/390/055/sss/fbe75c22e38349b587df7de1cfa842b2_small.png").execute();
        new PhotosDownloader(this, googleMap, ROD, "http://36.media.tumblr.com/0495e65a4f15696b4f3cc0dcff59a9e0/tumblr_mtqdx1uILV1r93kc1o1_r1_1280.jpg").execute();

        mMap = googleMap;
        mMap.setOnMapLoadedCallback(this);

    }

    @Override
    public void onMapLoaded() {

        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        builder.include(PE_ANTONIO);
        builder.include(LEOPOLDO);

        LatLngBounds latLngBounds = builder.build();

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(latLngBounds, 150);

        mMap.moveCamera(cameraUpdate);

        GoogleMapsApiService.getInstance().getDirections(PE_ANTONIO, ROD, new LatLng[]{LEOPOLDO, PAULISTA},
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
                });
    }
}
