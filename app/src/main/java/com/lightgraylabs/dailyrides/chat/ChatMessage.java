package com.lightgraylabs.dailyrides.chat;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by guestguest on 10/03/16.
 */
public class ChatMessage {

    public final static char TYPE_SYSTEM = 'S';
    public final static char TYPE_USER = 'U';

    public final static String SUBTYPE_USER_ADDED = "USER_ADDED";

    //'S' - system
    //'U' - user
    private char type;

    private String subtype;

    private String message;

    @SerializedName("user_id")
    @JsonProperty("user_id")
    private Long userId;

    private Date timestamp;

    @SerializedName("related_user")
    @JsonProperty("related_user")
    private Long relatedUser;

    public char getType() {
        return type;
    }

    public void setType(char type) {
        this.type = type;
    }

    public String getSubtype() {
        return subtype;
    }

    public void setSubtype(String subtype) {
        this.subtype = subtype;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Long getRelatedUser() {
        return relatedUser;
    }

    public void setRelatedUser(Long relatedUser) {
        this.relatedUser = relatedUser;
    }
}
