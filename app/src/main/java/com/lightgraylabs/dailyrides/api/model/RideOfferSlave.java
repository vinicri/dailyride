package com.lightgraylabs.dailyrides.api.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by guestguest on 19/03/16.
 */
public class RideOfferSlave implements Serializable {

    public static final char STATE_PENDING = 'O';
    public static final char STATE_ACCEPTED = 'C';
    public static final char STATE_REFUSED = 'R';

    long id;

    RideOffer master;

    @SerializedName("to_ride_request")
    RideMasterRequest toRideRequest;

    @SerializedName("estimated_pickup_time")
    int estimatedPickupTime;

    double price;

    char state;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

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

    public int getEstimatedPickupTime() {
        return estimatedPickupTime;
    }

    public void setEstimatedPickupTime(int estimatedPickupTime) {
        this.estimatedPickupTime = estimatedPickupTime;
    }
}
