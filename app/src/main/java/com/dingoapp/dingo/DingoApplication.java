package com.dingoapp.dingo;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.crashlytics.android.Crashlytics;
import com.dingoapp.dingo.util.AppLifeCycle;
import com.firebase.client.Firebase;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import io.fabric.sdk.android.Fabric;

/**
 * Created by guestguest on 30/01/16.
 */
public class DingoApplication extends Application {

    public static String APP_VERSION = "1";
    private static Context context;
    private static Tracker mTracker;

    @Override
    public void onCreate() {
        super.onCreate();

        context = getApplicationContext();
        Application application = (Application) context.getApplicationContext();
        application.registerActivityLifecycleCallbacks(AppLifeCycle.getInstance());
        Firebase.setAndroidContext(context);
        Fabric.with(this, new Crashlytics());

        GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
        // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
        mTracker = analytics.newTracker(R.xml.global_tracker);

    }

    public static Context getAppContext(){
        return context;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }


    /**
     * Gets the default {@link Tracker} for this {@link Application}.
     * @return tracker
     */
    public static Tracker getDefaultTracker() {
        return mTracker;
    }
}
