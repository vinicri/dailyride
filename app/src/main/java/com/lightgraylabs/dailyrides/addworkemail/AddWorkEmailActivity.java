package com.lightgraylabs.dailyrides.addworkemail;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.lightgraylabs.dailyrides.R;
import com.lightgraylabs.dailyrides.addemail.AddEmailActivity;
import com.lightgraylabs.dailyrides.api.Callback;
import com.lightgraylabs.dailyrides.api.DingoApiService;
import com.lightgraylabs.dailyrides.api.model.Institution;
import com.lightgraylabs.dailyrides.api.model.User;

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
        DingoApiService.getInstance().userAddWork(email, callback);
    }

    @Override
    protected void sendDocument(String institutionName, Uri fileUri, final Callback<Institution> callback){
        DingoApiService.getInstance().uploadWorkCredential(institutionName, fileUri, callback);
        //todo on failure
    }

    @Override
    protected void resendEmail() {

    }

    @Override
    protected void confirmPin(String pin, String entityName, final Callback<Institution> callback) {
        DingoApiService.getInstance().userConfirmWork(pin, entityName, callback);
    }

    @Override
    protected void onConfirmedPin(Institution institution, String institutionName) {
        if (institution.getStatus() == User.EntityStatus.C) {
            mUser.setCompany(institution);
            mUser.setWorkSpecifiedName(null);
            mUser.setWorkConfirmed(true);
            mUser.setWorkStatus(User.EntityStatus.C);

        } else if (institution.getStatus() == User.EntityStatus.P) {
            mUser.setCompany(null);
            mUser.setWorkSpecifiedName(institutionName);
            mUser.setWorkConfirmed(false);
            mUser.setWorkStatus(User.EntityStatus.P);
        }
        else{
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(EXTRA_USER, mUser);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected String getEmailFragmentEmailHint() {
        return getString(R.string.add_work_fragment_email_hint, getUser().getFirstName().toLowerCase());
    }

    @Override
    protected String getEmailFragmentHeader() {
        return getString(R.string.add_work_fragment_email_header);
    }

    @Override
    protected String getNameFragmentHeader() {
        return getString(R.string.add_work_fragment_name_header);
    }

    @Override
    protected String getCredentialsFragmentHeader() {
        return getString(R.string.add_work_fragment_credentials_header);
    }

}
