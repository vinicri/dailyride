package com.dingoapp.dingo;

import android.app.Application;
import android.content.Context;

/**
 * Created by guestguest on 30/01/16.
 */
public class DingoApplication extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getAppContext(){
        return context;
    }
}
