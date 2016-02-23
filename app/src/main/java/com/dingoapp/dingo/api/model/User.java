package com.dingoapp.dingo.api.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by guestguest on 13/01/16.
 */
public class User {

    public enum RiderMode{
        D, //Driver
        R //Rider
    }

    @SerializedName("first_name")
    String firstName;

    @SerializedName("last_name")
    String lastName;

    @SerializedName("email")
    String email;

    @SerializedName("photo_url")
    String photoUrl;

    @SerializedName("fb_access_token")
    String fbAccessToken;

    @SerializedName("fb_user_id")
    String fbUserId;

    @SerializedName("auth_token")
    OAuthToken authToken;

    @SerializedName("rider_mode")
    RiderMode riderMode;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFbAccessToken() {
        return fbAccessToken;
    }

    public void setFbAccessToken(String fbAccessToken) {
        this.fbAccessToken = fbAccessToken;
    }

    public String getFbUserId() {
        return fbUserId;
    }

    public void setFbUserId(String fbUserId) {
        this.fbUserId = fbUserId;
    }

    public OAuthToken getAuthToken() {
        return authToken;
    }

    public void setAuthToken(OAuthToken authToken) {
        this.authToken = authToken;
    }

    public RiderMode getRiderMode() {
        return riderMode;
    }

    public void setRiderMode(RiderMode riderMode) {
        this.riderMode = riderMode;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public boolean hasPhoto(){
        return photoUrl != null;
    }

    public class OAuthToken{

        @SerializedName("expires_in")
        int expiresIn;

        @SerializedName("access_token")
        String accessToken;

        @SerializedName("refresh_token")
        String refreshToken;

        @SerializedName("scope")
        String scope;

        @SerializedName("token_type")
        String tokenType;

        public int getExpiresIn() {
            return expiresIn;
        }

        public void setExpiresIn(int expiresIn) {
            this.expiresIn = expiresIn;
        }

        public String getAccessToken() {
            return accessToken;
        }

        public void setAccessToken(String accessToken) {
            this.accessToken = accessToken;
        }

        public String getRefreshToken() {
            return refreshToken;
        }

        public void setRefreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
        }

        public String getScope() {
            return scope;
        }

        public void setScope(String scope) {
            this.scope = scope;
        }

        public String getTokenType() {
            return tokenType;
        }

        public void setTokenType(String tokenType) {
            this.tokenType = tokenType;
        }
    }
}
