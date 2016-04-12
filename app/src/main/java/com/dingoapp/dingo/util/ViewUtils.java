package com.dingoapp.dingo.util;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AlertDialog;

import com.dingoapp.dingo.R;
import com.dingoapp.dingo.api.model.User;
import com.dingoapp.dingo.profile.ProfileActivity;

/**
 * Created by guestguest on 26/03/16.
 */
public class ViewUtils {

    public static void showProfileActivity(Context context, User user){
        Intent intent = new Intent(context, ProfileActivity.class);
        intent.putExtra(ProfileActivity.EXTRA_USER, user);
        context.startActivity(intent);
    }

    public static void showOkDialog(Context context, int string){
        String message = context.getString(string);
        showOkDialog(context, message);
    }

    public static void showOkDialog(Context context, int string, Object... formatArgs){
        String message = context.getString(string, formatArgs);
        showOkDialog(context, message);
    }

    public static void showOkDialog(Context context, CharSequence message){
        new AlertDialog.Builder(context)
                .setMessage(message)
                .setPositiveButton(R.string.ok, null)
                .show();
    }
}
