package com.dingoapp.dingo.api.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by guestguest on 03/04/16.
 */
public class Institution implements Serializable {

    public static final String STATUS_REGISTERED = "REGISTERED";
    public static final String STATUS_NOT_REGISTERED = "NOT_REGISTERED";

    public static final String STATUS_ACCEPTED = "ACCEPTED";
    public static final String STATUS_PENDING_APPROVAL = "PENDING_APPROVAL";
    public static final String STATUS_DENIED = "DENIED";

    String status;

    long id;

    @SerializedName("name")
    String name;

    @SerializedName("short_name")
    String shortName;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
