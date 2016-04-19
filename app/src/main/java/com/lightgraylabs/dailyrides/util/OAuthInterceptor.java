package com.lightgraylabs.dailyrides.util;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by guestguest on 31/01/16.
 */
public class OAuthInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        if(CurrentUser.getInstance().isLoggedIn()){
            request = request.newBuilder()
                    .addHeader("Authorization", "Bearer " + CurrentUser.getInstance().getAccessToken())
                    .build();
        }
        return chain.proceed(request);
    }
}
