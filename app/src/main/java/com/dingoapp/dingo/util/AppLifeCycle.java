package com.dingoapp.dingo.util;

/**
 * Created by guestguest on 19/03/16.
 */

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import java.util.HashMap;

/**
 * Check if the application is in the foreground or background.
 * *
 * Register this callbacks for an application
 * Application application = (Application) context.getApplicationContext();
 * application.registerActivityLifecycleCallbacks(new BaseLifeCycleCallbacks());
 * *
 * Note: These callbacks can be registered at any level of the application lifecycle.
 * Previous methods to get the application lifecycle forced the lifecycle callbacks to be registered
 * at the start of the application in a dedicated Application class.
 */
public class AppLifeCycle implements Application.ActivityLifecycleCallbacks {

    HashMap<String, Integer> mForegroundActivities;
    HashMap<String, Integer> mVisibleActivities;
    private static AppLifeCycle mInstance;

    public static AppLifeCycle getInstance(){
        if(mInstance == null){
            mInstance = new AppLifeCycle();
        }
        return mInstance;
    }

    private AppLifeCycle() {
        mVisibleActivities = new HashMap<>();
        mForegroundActivities = new HashMap<>();
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    }

    @Override
    public void onActivityStarted(Activity activity) {
        //map Activity unique class name with 1 on foreground
        mVisibleActivities.put(activity.getClass().getName(), 1);
        applicationStatus();
    }

    @Override
    public void onActivityResumed(Activity activity) {
        mForegroundActivities.put(activity.getClass().getName(), 1);
        applicationStatus();
    }

    @Override
    public void onActivityPaused(Activity activity) {
        mForegroundActivities.put(activity.getClass().getName(), 0);
        applicationStatus();
    }

    @Override
    public void onActivityStopped(Activity activity) {
        //map Activity unique class name with 0 on foreground
        mVisibleActivities.put(activity.getClass().getName(), 0);
        applicationStatus();
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
    }

    /**
     * Check if any activity is in the foreground
     */
    private boolean isBackGround() {
        for (String s : mForegroundActivities.keySet()) {
            if (mForegroundActivities.get(s) == 1) {
                return false;
            }
        }
        return true;
    }

    public boolean isActivityVisible(Class<?> cls){
        int visible = mVisibleActivities.get(cls.getName());
        return visible == 1;
    }

    /**
     * Log application status.
     */
    private void applicationStatus() {
        Log.d("ApplicationStatus", "Is application background" + isBackGround());
        if (isBackGround()) {
            //Do something if the application is in background
        }
    }
}