package com.lightgraylabs.dailyrides.api.model;

/**
 * Created by guestguest on 03/04/16.
 */
public class EmailConfirmationPin {

    public static final String STATUS_PENDING_APPROVAL = "PENDING_APPROVAL";
    public static final String STATUS_ACCEPTED = "ACCEPTED";
    public static final String STATUS_DENIED = "DENIED";

    String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
