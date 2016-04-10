package com.dingoapp.dingo.api;

import android.app.AlertDialog;
import android.content.Context;

import com.dingoapp.dingo.R;

import java.io.IOException;

/**
 * Created by guestguest on 09/04/16.
 */
public abstract class ApiCallback<T> implements Callback<T>{

    private final Context mContext;

    public ApiCallback(Context context){
        mContext = context;
    }

    @Override
    public abstract void success(Response<T> response);

    @Override
    public void unauthenticated(Response<?> response) {

    }

    @Override
    public void clientError(Response<?> response) {

    }

    @Override
    public void serverError(Response<?> response) {

    }

    @Override
    public void networkError(IOException e) {
        new AlertDialog.Builder(mContext)
                .setMessage(R.string.no_internet_error)
                .setPositiveButton(R.string.ok, null)
                .show();
    }

    @Override
    public void unexpectedError(Throwable t) {

    }
}
