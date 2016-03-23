package com.dingoapp.dingo.api.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by guestguest on 19/03/16.
 */
public class RideOfferSlave implements Serializable {

    public static final char STATE_PENDING = 'O';
    public static final char STATE_ACCEPTED = 'C';
    public static final char STATE_REFUSED = 'R';

    RideOffer master;

    @SerializedName("to_ride_request")
    RideMasterRequest toRideRequest;

    double price;

    char state;


    public RideOffer getMaster() {
        return master;
    }

    public void setMaster(RideOffer master) {
        this.master = master;
    }

    public RideMasterRequest getToRideRequest() {
        return toRideRequest;
    }

    public void setToRideRequest(RideMasterRequest toRideRequest) {
        this.toRideRequest = toRideRequest;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public char getState() {
        return state;
    }

    public void setState(char state) {
        this.state = state;
    }
}
