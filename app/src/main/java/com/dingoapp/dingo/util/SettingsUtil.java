package com.dingoapp.dingo.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.dingoapp.dingo.api.model.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import static com.dingoapp.dingo.util.LogUtil.makeLogTag;

/**
 * Created by guestguest on 30/01/16.
 */
public class SettingsUtil {

    private static final String TAG = makeLogTag(SettingsUtil.TAG);

    private static final String PREF_USER = "pref_user";

    private static Gson gson;

    static {
        gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .create();
    }

    public static User getCurrentUser(Context context){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String user = sp.getString(PREF_USER, null);
        return user == null ? null : gson.fromJson(user, User.class);
    }

    public static void setCurrentUser(Context context, User user){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putString(PREF_USER, gson.toJson(user)).commit();
    }
}
