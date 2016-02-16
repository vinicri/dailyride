package com.dingoapp.dingo.api.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by guestguest on 12/02/16.
 */
public class RideMasterRequest extends RideEntity implements Serializable {
    public static final char STATE_NEW = 'N';
    public static final char STATE_OPEN = 'O';
    public static final char STATE_ACCEPTED = 'A';
    public static final char STATE_CANCELLED = 'C';

    @SerializedName("leaving_time_end")
    Date leavingTimeEnd;

    List<RideOffer> offers;

    char state;

    @SerializedName("waiting_requests")
    int waitingRequests;

    @SerializedName("rejected_requests")
    int rejectedRequests;

    @SerializedName("new_matches")
    int newMatches;

    //when a user is offering the ride
    @SerializedName("offering_user")
    User offeringUser;

    User driver;

    public static RideMasterRequest getWeekdaysCheckedInstance() {
        RideMasterRequest request = new RideMasterRequest();
        request.monday = true;
        request.tuesday = true;
        request.wednesday = true;
        request.thursday = true;
        request.friday = true;
        return request;
    }

    public Date getLeavingTimeEnd() {
        return leavingTimeEnd;
    }

    public void setLeavingTimeEnd(Date leavingTimeEnd) {
        this.leavingTimeEnd = leavingTimeEnd;
    }

    public char getState() {
        return state;
    }

    public void setState(char state) {
        this.state = state;
    }

    public User getOfferingUser() {
        return offeringUser;
    }

    public void setOfferingUser(User offeringUser) {
        this.offeringUser = offeringUser;
    }

    public int getWaitingRequests() {
        return waitingRequests;
    }

    public void setWaitingRequests(int waitingRequests) {
        this.waitingRequests = waitingRequests;
    }

    public int getRejectedRequests() {
        return rejectedRequests;
    }

    public void setRejectedRequests(int rejectedRequests) {
        this.rejectedRequests = rejectedRequests;
    }

    public int getNewMatches() {
        return newMatches;
    }

    public void setNewMatches(int newMatches) {
        this.newMatches = newMatches;
    }

    public User getDriver() {
        return driver;
    }

    public void setDriver(User driver) {
        this.driver = driver;
    }
}
