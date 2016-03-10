package com.dingoapp.dingo.slaveofferreply;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dingoapp.dingo.BaseActivity;
import com.dingoapp.dingo.R;
import com.dingoapp.dingo.util.CircleTransform;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by guestguest on 08/03/16.
 */
public class SlaveOfferReplyActivity extends BaseActivity implements OnMapReadyCallback, GoogleMap.OnMapLoadedCallback{

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
        ButterKnife.bind(this);

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Glide.with(this).load("http://ndl.mgccw.com/mu3/000/390/055/sss/fbe75c22e38349b587df7de1cfa842b2_small.png")
                .bitmapTransform(new CircleTransform(this))
                .into(mHeaderPicture);


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

        new PhotosDownloader(googleMap, PE_ANTONIO, "http://36.media.tumblr.com/0495e65a4f15696b4f3cc0dcff59a9e0/tumblr_mtqdx1uILV1r93kc1o1_r1_1280.jpg").execute();
        new PhotosDownloader(googleMap, LEOPOLDO, "http://ndl.mgccw.com/mu3/000/390/055/sss/fbe75c22e38349b587df7de1cfa842b2_small.png").execute();
        new PhotosDownloader(googleMap, PAULISTA, "http://ndl.mgccw.com/mu3/000/390/055/sss/fbe75c22e38349b587df7de1cfa842b2_small.png").execute();
        new PhotosDownloader(googleMap, ROD, "http://36.media.tumblr.com/0495e65a4f15696b4f3cc0dcff59a9e0/tumblr_mtqdx1uILV1r93kc1o1_r1_1280.jpg").execute();

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
    }


    private class PhotosDownloader extends AsyncTask<Void, Void, Bitmap>{

        private final GoogleMap mMap;
        private final LatLng mLatLng;
        private final String mPhotoUrl;

        public PhotosDownloader(GoogleMap map, LatLng latLng, String photoUrl){
            mMap = map;
            mLatLng = latLng;
            mPhotoUrl = photoUrl;


        };

        @Override
        protected Bitmap doInBackground(Void... params) {
            URL url = null;
            try {
                url = new URL(mPhotoUrl);

                return BitmapFactory.decodeStream(url.openConnection().getInputStream());


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return  null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);

            float radius = 44.0f;//markerBitmap.getWidth() / 2.0f - 4.0f// ;
            float newWidth;
            float newHeight;
            if(bitmap.getWidth() < bitmap.getHeight() ){
                newWidth = radius * 2.0f;
                newHeight = bitmap.getHeight() * newWidth / bitmap.getWidth();
            }
            else{
                newHeight = radius * 2.0f;
                newWidth = bitmap.getWidth() * newHeight / bitmap.getHeight();
            }
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, (int)newWidth, (int)newHeight, true);
            Bitmap markerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.marker);
            Bitmap mutableBitmap = markerBitmap.copy(Bitmap.Config.ARGB_8888, true);

            float cx = markerBitmap.getWidth() / 2.0f;
            float cy = radius + 14f;//markerBitmap.getWidth() / 2.0f;
            Canvas canvas = new Canvas(mutableBitmap);
            Paint paint = new Paint();
            paint.setShader(new BitmapShader(scaledBitmap, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
            paint.setAntiAlias(true);
            //float r = size / 2f;
            canvas.drawCircle(cx, cy, radius, paint);

            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(mLatLng)
                            //.title("Marker")
                            //.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));
                    .icon(BitmapDescriptorFactory.fromBitmap(mutableBitmap)));


        }
    }
}
