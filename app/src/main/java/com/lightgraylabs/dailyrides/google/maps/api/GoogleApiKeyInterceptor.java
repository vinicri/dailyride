package com.lightgraylabs.dailyrides.google.maps.api;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.lightgraylabs.dailyrides.DingoApplication;
import com.lightgraylabs.dailyrides.util.CurrentUser;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by guestguest on 03/02/16.
 */
public class GoogleApiKeyInterceptor implements Interceptor {

    private String mGoogleApiKey;

    public GoogleApiKeyInterceptor(){
        Context appContext = DingoApplication.getAppContext();
        try {
            ApplicationInfo app = appContext.getPackageManager().getApplicationInfo(appContext.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = app.metaData;
            mGoogleApiKey = bundle.getString("com.google.android.geo.API_KEY");
        } catch (PackageManager.NameNotFoundException e) {
            //TODO
            e.printStackTrace();
        }
    }

    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        Request request = chain.request();
        if(CurrentUser.getInstance().isLoggedIn()){
            HttpUrl url = request.url().newBuilder()
                    //FIXME
                    .addQueryParameter("key", "AIzaSyAdPwypNyNj2yTFld7abSBEAxZmm5gd_ws")
                    .build();
            request = request.newBuilder().url(url).build();
        }
        return chain.proceed(request);
    }
}