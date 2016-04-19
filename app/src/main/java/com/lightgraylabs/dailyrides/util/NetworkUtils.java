package com.lightgraylabs.dailyrides.util;

import com.crashlytics.android.Crashlytics;

import java.io.IOException;

/**
 * Created by guestguest on 17/04/16.
 */
public class NetworkUtils {

    public static boolean isOnline() {

        Runtime runtime = Runtime.getRuntime();
        try {

            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);

        } catch (IOException e){
            Crashlytics.logException(e);
        }
        catch (InterruptedException e){
            Crashlytics.logException(e);
        }

        return false;
    }

}
