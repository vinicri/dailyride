package com.dingoapp.dingo.api;

/**
 * Created by guestguest on 13/01/16.
 */
public interface Callback<T> {

    public void onResponse(Response<T> response);
    public void onFailure(Throwable t);
}
