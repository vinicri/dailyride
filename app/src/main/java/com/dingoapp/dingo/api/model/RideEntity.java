package com.dingoapp.dingo.api.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by guestguest on 13/02/16.
 */
public class RideEntity {

    int id;

    boolean monday;
    boolean tuesday;
    boolean wednesday;
    boolean thursday;
    boolean friday;
    boolean saturday;
    boolean sunday;

    @SerializedName("leaving_time")
    Date leavingTime;

    @SerializedName("leaving_address")
    Address leavingAddress;

    @SerializedName("arriving_address")
    Address arrivingAddress;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isMonday() {
        return monday;
    }

    public void setMonday(boolean monday) {
        this.monday = monday;
    }

    public boolean isTuesday() {
        return tuesday;
    }

    public void setTuesday(boolean tuesday) {
        this.tuesday = tuesday;
    }

    public boolean isWednesday() {
        return wednesday;
    }

    public void setWednesday(boolean wednesday) {
        this.wednesday = wednesday;
    }

    public boolean isThursday() {
        return thursday;
    }

    public void setThursday(boolean thursday) {
        this.thursday = thursday;
    }

    public boolean isFriday() {
        return friday;
    }

    public void setFriday(boolean friday) {
        this.friday = friday;
    }

    public boolean isSaturday() {
        return saturday;
    }

    public void setSaturday(boolean saturday) {
        this.saturday = saturday;
    }

    public boolean isSunday() {
        return sunday;
    }

    public void setSunday(boolean sunday) {
        this.sunday = sunday;
    }

    public Date getLeavingTime() {
        return leavingTime;
    }

    public void setLeavingTime(Date leavingTime) {
        this.leavingTime = leavingTime;
    }

    public Address getLeavingAddress() {
        return leavingAddress;
    }

    public void setLeavingAddress(Address leavingAddress) {
        this.leavingAddress = leavingAddress;
    }

    public Address getArrivingAddress() {
        return arrivingAddress;
    }

    public void setArrivingAddress(Address arrivingAddress) {
        this.arrivingAddress = arrivingAddress;
    }
}
