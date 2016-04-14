package com.dingoapp.dingo.api;

import android.content.Context;
import android.content.Intent;

import com.dingoapp.dingo.gcm.RegistrationIntentService;

/**
 * Created by guestguest on 13/04/16.
 */
public class GCMUtils {

    public static void registerGCM(Context context){
        Intent intent = new Intent(context, RegistrationIntentService.class);
        context.startService(intent);
    }
}
