package com.lightgraylabs.dailyrides.api.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
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

    User user;

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

    @SerializedName("invites_to_confirm")
    List<RideOfferSlave> invitesToConfirm = new ArrayList<>();

    @SerializedName("invites_accepted")
    List<RideOfferSlave> invitesAccepted = new ArrayList<>();

    @SerializedName("invites_others")
    List<RideOfferSlave> invitesOthers = new ArrayList<>();

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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public List<RideOfferSlave> getInvitesOthers() {
        return invitesOthers;
    }

    public void setInvitesOthers(List<RideOfferSlave> invitesOthers) {
        this.invitesOthers = invitesOthers;
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

    public List<RideOfferSlave> getInvitesToConfirm() {
        return invitesToConfirm;
    }

    public void setInvitesToConfirm(List<RideOfferSlave> invitesToConfirm) {
        this.invitesToConfirm = invitesToConfirm;
    }

    public List<RideOfferSlave> getInvitesAccepted() {
        return invitesAccepted;
    }

    public void setInvitesAccepted(List<RideOfferSlave> invitesAccepted) {
        this.invitesAccepted = invitesAccepted;
    }
}
