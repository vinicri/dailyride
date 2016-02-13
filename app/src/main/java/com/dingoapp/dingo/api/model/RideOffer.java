package com.dingoapp.dingo.api.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by guestguest on 12/02/16.
 */
public class RideOffer {

    public enum State{
        OPEN,
        CANCELLED,
        CLOSED
    }

    User user;
    State state;
    double price;

    @SerializedName("total_seats")
    int totalSeats;

    @SerializedName("leaving_address")
    Address leavingAddress;

    @SerializedName("arriving_address")
    Address arrivingAddress;

    @SerializedName("leaving_time")
    Date leavingTime;

    boolean monday;
    boolean tuesday;
    boolean wednesday;
    boolean thursday;
    boolean friday;
    boolean saturday;
    boolean sunday;

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

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
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

    public Date getLeavingTime() {
        return leavingTime;
    }

    public void setLeavingTime(Date leavingTime) {
        this.leavingTime = leavingTime;
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
}


