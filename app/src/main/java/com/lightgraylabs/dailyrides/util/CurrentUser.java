package com.lightgraylabs.dailyrides.util;

import com.lightgraylabs.dailyrides.DingoApplication;
import com.lightgraylabs.dailyrides.api.model.User;

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

    public void setAcceptedTerms(boolean accepted){
        user.setAcceptedTerms(accepted);
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

    public void updateOAuthToken(User.OAuthToken token){
        user.setAuthToken(token);
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

    public void save(){
        SettingsUtil.setCurrentUser(DingoApplication.getAppContext(), user);
    }

    //never replace oauth_token as it might have been refreshed
    // by http interceptors
    public void setUser(User user){
        user.setAuthToken(this.user.getAuthToken());
        this.user = user;
        save();
    }

    public void logout(){
        user = null;
        SettingsUtil.setCurrentUser(DingoApplication.getAppContext(), null);
        SettingsUtil.setSentTokenToServer(DingoApplication.getAppContext(), false);
    }

}
