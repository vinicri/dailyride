package com.dingoapp.dingo.addworkemail;

import android.os.Bundle;

import com.dingoapp.dingo.R;
import com.dingoapp.dingo.addemail.AddEmailActivity;

/**
 * Created by guestguest on 01/04/16.
 */
public class AddWorkEmailActivity extends AddEmailActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle(R.string.activity_add_work_email);
    }

    @Override
    protected void addEmail(String email, Callback<Void> callback) {
        callback.onResponse(null);
    }

    @Override
    protected void sendDocument() {

    }

    @Override
    protected void resendEmail() {

    }

    @Override
    protected void confirmCode(String code, Callback<Void> callback) {
        callback.onResponse(null);
    }


}
