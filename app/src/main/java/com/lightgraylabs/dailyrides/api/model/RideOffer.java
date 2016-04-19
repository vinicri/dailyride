package com.lightgraylabs.dailyrides.api.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by guestguest on 12/02/16.
 */
public class RideOffer extends RideEntity {
    public static final char STATE_OPEN = 'O';
    public static final char STATE_CANCELLED = 'C';
    private static final char STATE_CLOSED = 'X';

    //user who is offering
    User user;

    double price;
    @SerializedName("total_seats")
    int totalSeats;

    char state;

    @SerializedName("open_requests")
    int openRequests;

    @SerializedName("accepted_requests")
    int acceptedRequests;

    @SerializedName("accepted_users")
    List<User> acceptedUsers;

    @SerializedName("new_matches")
    int newMatches;

    @SerializedName("invites_to_accept")
    List<RideOfferSlave> invitesToAccept = new ArrayList<>();

    @SerializedName("invites_accepted")
    List<RideOfferSlave> invitesAccepted = new ArrayList<>();

    @SerializedName("invites_refused")
    List<RideOfferSlave> invitesRefused = new ArrayList<>();

    @SerializedName("invites_waiting_confirmation")
    List<RideOfferSlave> invitesWaitingConfirmation = new ArrayList<>();

    //RideOfferSlave slave;

    List<RideMasterRequest> requests = new ArrayList<>();

    public static RideOffer getWeekdaysCheckedInstance(){
        RideOffer offer = new RideOffer();
        offer.monday = true;
        offer.tuesday = true;
        offer.wednesday = true;
        offer.thursday = true;
        offer.friday = true;
        return offer;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(int totalSeats) {
        this.totalSeats = totalSeats;
    }

    public Date getLeavingTime() {
        return leavingTime;
    }

    public void setLeavingTime(Date leavingTime) {
        this.leavingTime = leavingTime;
    }

    public char getState() {
        return state;
    }

    public void setState(char state) {
        this.state = state;
    }

    public int getOpenRequests() {
        return openRequests;
    }

    public void setOpenRequests(int openRequests) {
        this.openRequests = openRequests;
    }

    public int getAcceptedRequests() {
        return acceptedRequests;
    }

    public void setAcceptedRequests(int acceptedRequests) {
        this.acceptedRequests = acceptedRequests;
    }

    public int getNewMatches() {
        return newMatches;
    }

    public void setNewMatches(int newMatches) {
        this.newMatches = newMatches;
    }

    public List<User> getAcceptedUsers() {
        return acceptedUsers;
    }

    public void setAcceptedUsers(List<User> acceptedUsers) {
        this.acceptedUsers = acceptedUsers;
    }

    public List<RideMasterRequest> getRequests() {
        return requests;
    }

    public void setRequests(List<RideMasterRequest> requests) {
        this.requests = requests;
    }

    public List<RideOfferSlave> getInvitesToAccept() {
        return invitesToAccept;
    }

    public void setInvitesToAccept(List<RideOfferSlave> invitesToAccept) {
        this.invitesToAccept = invitesToAccept;
    }

    public List<RideOfferSlave> getInvitesAccepted() {
        return invitesAccepted;
    }

    public void setInvitesAccepted(List<RideOfferSlave> invitesAccepted) {
        this.invitesAccepted = invitesAccepted;
    }

    public List<RideOfferSlave> getInvitesRefused() {
        return invitesRefused;
    }

    public void setInvitesRefused(List<RideOfferSlave> invitesRefused) {
        this.invitesRefused = invitesRefused;
    }

    public List<RideOfferSlave> getInvitesWaitingConfirmation() {
        return invitesWaitingConfirmation;
    }

    public void setInvitesWaitingConfirmation(List<RideOfferSlave> invitesWaitingConfirmation) {
        this.invitesWaitingConfirmation = invitesWaitingConfirmation;
    }

    /*public RideOfferSlave getSlave() {
        return slave;
    }

    public void setSlave(RideOfferSlave slave) {
        this.slave = slave;
    }*/



}


