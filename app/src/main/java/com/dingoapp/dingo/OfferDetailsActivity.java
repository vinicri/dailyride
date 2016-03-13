package com.dingoapp.dingo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dingoapp.dingo.api.Callback;
import com.dingoapp.dingo.api.Response;
import com.dingoapp.dingo.chat.ChatActivity;
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

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by guestguest on 09/03/16.
 */
public class OfferDetailsActivity extends BaseActivity implements OnMapReadyCallback, GoogleMap.OnMapLoadedCallback {

    @Bind(R.id.ride_date)TextView mRideDateTime;
    @Bind(R.id.rider1_picture)ImageView mRider1Picture;
    @Bind(R.id.rider_text)TextView mRiderText;
    @Bind(R.id.chat_button)ImageView mChatButton;

    @Bind(R.id.match1_picture)ImageView mMatch1Picture;
    @Bind(R.id.match2_picture)ImageView mMatch2Picture;
    @Bind(R.id.match_text)TextView mMatchText;

    @Bind(R.id.start)Button mStartButton;

    LatLng PE_ANTONIO = new LatLng(-23.605671, -46.692275);
    LatLng OLIMPIADAS = new LatLng(-23.595331, -46.687868);
    LatLng PAULISTA = new LatLng(-23.568586, -46.647527);
    LatLng ROD = new LatLng(-23.515551, -46.624975);
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_details);
        getSupportActionBar().setTitle(R.string.activity_offer_details);
        ButterKnife.bind(this);

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Glide.with(this).load("http://imguol.com/2012/09/14/o-carioca-jeferson-monteiro-22-e-o-responsavel-pelo-perfil-falso-dilma-bolada-no-twitter-e-no-facebook-1347654373519_300x280.jpg")
                .bitmapTransform(new CircleTransform(this))
                .into(mRider1Picture);


        Glide.with(this).load("http://ndl.mgccw.com/mu3/000/390/055/sss/fbe75c22e38349b587df7de1cfa842b2_small.png")
                .bitmapTransform(new CircleTransform(this))
                .into(mMatch1Picture);


        Glide.with(this).load("http://www.bctr.cornell.edu/wp-content/uploads/2012/02/studentprofile-sheasmall.jpg")
                .bitmapTransform(new CircleTransform(this))
                .into(mMatch2Picture);


        mChatButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(OfferDetailsActivity.this, ChatActivity.class);
                        startActivity(intent);
                    }
                }
        );

        mStartButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(OfferDetailsActivity.this, "O horário da carona ainda não está próximo.", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        new PhotosDownloader(this, googleMap, PE_ANTONIO, "http://36.media.tumblr.com/0495e65a4f15696b4f3cc0dcff59a9e0/tumblr_mtqdx1uILV1r93kc1o1_r1_1280.jpg").execute();
        new PhotosDownloader(this, googleMap, OLIMPIADAS, "http://imguol.com/2012/09/14/o-carioca-jeferson-monteiro-22-e-o-responsavel-pelo-perfil-falso-dilma-bolada-no-twitter-e-no-facebook-1347654373519_300x280.jpg").execute();
        new PhotosDownloader(this, googleMap, PAULISTA, "http://imguol.com/2012/09/14/o-carioca-jeferson-monteiro-22-e-o-responsavel-pelo-perfil-falso-dilma-bolada-no-twitter-e-no-facebook-1347654373519_300x280.jpg").execute();
        new PhotosDownloader(this, googleMap, ROD, "http://36.media.tumblr.com/0495e65a4f15696b4f3cc0dcff59a9e0/tumblr_mtqdx1uILV1r93kc1o1_r1_1280.jpg").execute();

        mMap = googleMap;
        mMap.setOnMapLoadedCallback(this);

    }


    @Override
    public void onMapLoaded() {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        builder.include(PE_ANTONIO);
        builder.include(OLIMPIADAS);
        //builder.include(PAULISTA);
        //builder.include(ROD);

        LatLngBounds latLngBounds = builder.build();

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(latLngBounds, 150);

        mMap.moveCamera(cameraUpdate);

        GoogleMapsApiService.getInstance().getDirections(PE_ANTONIO, ROD, new LatLng[]{OLIMPIADAS, PAULISTA},
                new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Response<DirectionsResponse> response) {
                        if (response.code() == Response.HTTP_200_OK) {
                            String encodedPoly = response.body().getRoutes().get(0).getOverviewPolyline().getPoints();
                            List<LatLng> polyLatLgn = Utils.decodePoly(encodedPoly);

                            Polyline polyline = mMap.addPolyline(
                                    new PolylineOptions()
                                            .addAll(polyLatLgn)
                                            .width(14).color(ContextCompat.getColor(OfferDetailsActivity.this, R.color.turquoise)).geodesic(true)
                            );
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {

                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_offer_details, menu);
        return true;
    }
}
