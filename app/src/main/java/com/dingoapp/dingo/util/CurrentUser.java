package com.dingoapp.dingo.util;

import com.dingoapp.dingo.DingoApplication;
import com.dingoapp.dingo.api.model.User;

/**
 * Created by guestguest on 30/01/16.
 */
public class CurrentUser {

    private static CurrentUser instance;
    private User user;

    private CurrentUser(){
        user = SettingsUtil.getCurrentUser(DingoApplication.getAppContext());
    }

    public static CurrentUser getInstance(){
        if(instance == null){
            instance = new CurrentUser();
        }
        return instance;
    }

    public String getAccessToken(){
        return user.getAuthToken().getAccessToken();
    }

    public void setRiderMode(User.RiderMode riderMode){
        user.setRiderMode(riderMode);
        save();
    }

    public void setPhoneConfirmed(boolean confirmed){
        user.setPhoneConfirmed(confirmed);
        save();
    }

    public void setRegistrationConfirmed(boolean confirmed){
        user.setRegistrationConfirmed(confirmed);
        save();
    }


    public static User getUser(){
        return getInstance().user;
    }

    public boolean isLoggedIn(){
        return user != null;
    }

    public void reload(){
        user = SettingsUtil.getCurrentUser(DingoApplication.getAppContext());
    }

    private void save(){
        SettingsUtil.setCurrentUser(DingoApplication.getAppContext(), user);
    }
}
