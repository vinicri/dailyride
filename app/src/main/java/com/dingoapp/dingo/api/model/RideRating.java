package com.dingoapp.dingo.api.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by guestguest on 14/04/16.
 */
public class RideRating implements Serializable{

    public static final char STATE_RATED = 'R';
    private static final char STATE_UNRATED = 'U';

    public static final char ROLE_DRIVER = 'D';
    public static final char ROLE_RIDER = 'R';

    User user;

    @SerializedName("target_user")
    User targetUser;

    RideOffer offer;

    double rating;

    char state;
    char role;

    String comments;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getTargetUser() {
        return targetUser;
    }

    public void setTargetUser(User targetUser) {
        this.targetUser = targetUser;
    }

    public RideOffer getOffer() {
        return offer;
    }

    public void setOffer(RideOffer offer) {
        this.offer = offer;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public char getState() {
        return state;
    }

    public void setState(char state) {
        this.state = state;
    }

    public char getRole() {
        return role;
    }

    public void setRole(char role) {
        this.role = role;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
