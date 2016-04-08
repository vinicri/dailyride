package com.dingoapp.dingo.addworkemail;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.dingoapp.dingo.R;
import com.dingoapp.dingo.addemail.AddEmailActivity;
import com.dingoapp.dingo.api.DingoApiService;
import com.dingoapp.dingo.api.Response;
import com.dingoapp.dingo.api.model.Institution;

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
    protected void addEmail(String email, final Callback<Institution> callback) {
        DingoApiService.getInstance().userAddWork(email,
                new com.dingoapp.dingo.api.Callback<Institution>() {
                    @Override
                    public void onResponse(Response<Institution> response) {
                        callback.onResponse(response);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        callback.onFailure(t);
                    }
                });
    }

    @Override
    protected void sendDocument(String institutionName, Uri fileUri, final Callback<Institution> callback){
        DingoApiService.getInstance().uploadWorkCredential(institutionName, fileUri,
                new com.dingoapp.dingo.api.Callback<Institution>() {
                    @Override
                    public void onResponse(Response<Institution> response) {
                        callback.onResponse(response);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        callback.onFailure(t);
                    }
                });
    }

    @Override
    protected void resendEmail() {

    }

    @Override
    protected void confirmPin(String pin, String entityName, final Callback<Institution> callback) {
        DingoApiService.getInstance().userConfirmWork(pin, entityName,
                new com.dingoapp.dingo.api.Callback<Institution>() {
                    @Override
                    public void onResponse(Response<Institution> response) {
                        callback.onResponse(response);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        callback.onFailure(t);
                    }
                });
    }

    @Override
    protected void onConfirmedPin(Institution institution, String institutionName) {
        if (institution.getStatus().equals(Institution.STATUS_ACCEPTED)) {
            mUser.setCompany(institution);
            mUser.setWorkSpecifiedName(null);
            mUser.setWorkConfirmed(true);

        } else if (institution.getStatus().equals(Institution.STATUS_PENDING_APPROVAL)) {
            mUser.setCompany(null);
            mUser.setWorkSpecifiedName(institutionName);
            mUser.setWorkConfirmed(false);
        }
        else{
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(EXTRA_USER, mUser);
        setResult(RESULT_OK, intent);
        finish();
    }


}
