package com.lightgraylabs.dailyrides.api.model;

/**
 * Created by guestguest on 10/04/16.
 */
public class DingoError {

    public static final int ERR_EMPTY = -1;
    public static final int ERR_SIGN_UP_EXISTING_USER = 10;
    public static final int ERR_SIGN_UP_EXISTING_FACEBOOK_USER = 11;
    public static final int ERR_SIGN_UP_EXISTING_PHONE = 12;

    public static final int ERR_PHONE_CONFIRMATION_CODE_WRONG = 20;

    public static final int ERR_LOGIN_WRONG_CREDENTIALS = 30;

    public static final int ERR_ENTITY_EMAIL_ALREADY_BEING_USED = 40;
    public static final int ERR_ENTITY_EMAIL_CODE_WRONG = 41;
    public static final int ERR_ENTITY_NOT_REGISTERED = 42;

    int code = ERR_EMPTY;
    String message = "__empty__";

    public int code() {
        return code;
    }

    public String message() {
        return message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
