package com.dingoapp.dingo.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.AsyncTask;

import com.dingoapp.dingo.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class PhotosDownloader extends AsyncTask<Void, Void, Bitmap> {

    private final GoogleMap mMap;
    private final LatLng mLatLng;
    private final String mPhotoUrl;
    private final Context mContext;

    public PhotosDownloader(Context context, GoogleMap map, LatLng latLng, String photoUrl){
        mMap = map;
        mLatLng = latLng;
        mPhotoUrl = photoUrl;
        mContext = context;


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

        float radius = 55.0f;//markerBitmap.getWidth() / 2.0f - 4.0f// ;
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
        Bitmap markerBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.marker);
        Bitmap mutableBitmap = markerBitmap.copy(Bitmap.Config.ARGB_8888, true);

        float cx = scaledBitmap.getWidth() / 2.0f + 8;
        float cy = radius + 8f;//markerBitmap.getWidth() / 2.0f;
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