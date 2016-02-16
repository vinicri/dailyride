package com.dingoapp.dingo.api.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by guestguest on 12/02/16.
 */
public class RideOffer extends RideEntity {

    public enum State{
        OPEN,
        CANCELLED,
        CLOSED
    }

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

    @SerializedName("new_matches")
    int newMatches;

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
}


