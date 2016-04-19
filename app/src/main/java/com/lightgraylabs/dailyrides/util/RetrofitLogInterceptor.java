package com.lightgraylabs.dailyrides.util;

import android.util.Log;

import java.io.IOException;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.ResponseBody;
import okio.Buffer;

import static com.lightgraylabs.dailyrides.util.LogUtil.makeLogTag;

/**
 * Created by guestguest on 20/01/16.
 */
public class RetrofitLogInterceptor implements Interceptor {


    private static final String TAG = makeLogTag("RetrofitLogInterceptor");

    @Override
    public okhttp3.Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        long t1 = System.nanoTime();
        Log.d(TAG, String.format("Sending request %s on %s%n%s", request.url(), chain.connection(), request.headers()));
        Log.d(TAG, String.format("HEADER BEGIN\n%s\nHEADER END", headerToString(request)));
        if(request.body() != null) {
            Log.d(TAG, String.format("REQUEST BODY BEGIN\n%s\nREQUEST BODY END", bodyToString(request)));
        }
        okhttp3.Response response = chain.proceed(request);

        ResponseBody responseBody = response.body();
        String responseBodyString = response.body().string();

        // now we have extracted the response body but in the process
        // we have consumed the original reponse and can't read it again
        // so we need to build a new one to return from this method

        okhttp3.Response newResponse = response.newBuilder().body(ResponseBody.create(responseBody.contentType(), responseBodyString.getBytes())).build();

        long t2 = System.nanoTime();
        Log.d(TAG, String.format("Received response for %s in %.1fms%n%s", response.request().url(), (t2 - t1) / 1e6d, response.headers()));
        Log.d(TAG, String.format("RESPONSE BODY BEGIN:\n%s\nRESPONSE BODY END", responseBodyString));

        return newResponse;
    }

    private static String bodyToString(final Request request){

        try {
            final Request copy = request.newBuilder().build();
            final Buffer buffer = new Buffer();
            copy.body().writeTo(buffer);
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "did not work";
        }
    }

    private static String headerToString(Request request){
        Request copy = request.newBuilder().build();
        Headers headers = copy.headers();
        String string = new String();
        for(int i = 0; i < headers.size(); i++){
            string += "\n" + headers.name(i) + ":" + headers.value(i);
        }
        return string;
    }
}