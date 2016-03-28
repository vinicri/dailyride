package com.dingoapp.dingo.util;

import android.content.Context;
import android.content.Intent;

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
}
