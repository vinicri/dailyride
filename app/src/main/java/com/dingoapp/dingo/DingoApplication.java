package com.dingoapp.dingo;

import android.app.Application;
import android.content.Context;

import com.dingoapp.dingo.util.AppLifeCycle;

/**
 * Created by guestguest on 30/01/16.
 */
public class DingoApplication extends Application {

    public static String APP_VERSION = "1";
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        Application application = (Application) context.getApplicationContext();
        application.registerActivityLifecycleCallbacks(AppLifeCycle.getInstance());
    }

    public static Context getAppContext(){
        return context;
    }
}
