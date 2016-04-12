package com.dingoapp.dingo.api;

import android.app.AlertDialog;
import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.dingoapp.dingo.R;
import com.dingoapp.dingo.api.model.DingoError;
import com.dingoapp.dingo.util.CurrentUser;
import com.dingoapp.dingo.util.Installation;
import com.rollbar.android.Rollbar;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
    public void clientError(Response<?> response, DingoError error) {
        if(error.code() == DingoError.ERR_EMPTY){
            Map<String, String> data = new HashMap<>();
            data.put("http_code", String.valueOf(response.code()));
            data.put("body", response.error());
            addUserData(data);
            Rollbar.reportMessage("error_client", "unexpected", data);
        }
        else{
            Map<String, String> data = new HashMap<>();
            data.put("http_code", String.valueOf(response.code()));
            data.put("code", String.valueOf(error.code()));
            data.put("message", error.getMessage());
            data.put("body", response.error());
            addUserData(data);
            Rollbar.reportMessage("error_client", "ignored", data);
        }
    }

    @Override
    public void serverError(Response<?> response) {
        new AlertDialog.Builder(mContext)
                .setMessage(R.string.request_server_error)
                .setPositiveButton(R.string.ok, null)
                .show();
        Map<String, String> data = new HashMap<>();
        data.put("http_code", String.valueOf(response.code()));
        data.put("body", response.error());
        //todo add endpoint
        addUserData(data);
        Rollbar.reportMessage("error_server", "", data);
    }

    @Override
    public void networkError(IOException e) {
        new AlertDialog.Builder(mContext)
                .setMessage(R.string.no_internet_error)
                .setPositiveButton(R.string.ok, null)
                .show();
        Map<String, String> data = new HashMap<>();
        addUserData(data);
        Rollbar.reportMessage("error_network", "", data);
    }

    @Override
    public void unexpectedError(Throwable t) {
        new AlertDialog.Builder(mContext)
                .setMessage(R.string.request_unexpected_error)
                .setPositiveButton(R.string.ok, null)
                .show();
        Crashlytics.logException(t);
    }

    private void addUserData(Map<String, String> data){
        if(CurrentUser.getInstance().isLoggedIn()) {
            data.put("user", String.valueOf(CurrentUser.getUser().getId()));
        }
        else{
            data.put("installation", Installation.id());
        }
    }
}
