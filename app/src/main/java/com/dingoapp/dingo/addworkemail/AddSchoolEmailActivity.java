package com.dingoapp.dingo.addworkemail;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.dingoapp.dingo.R;
import com.dingoapp.dingo.addemail.AddEmailActivity;
import com.dingoapp.dingo.api.Callback;
import com.dingoapp.dingo.api.DingoApiService;
import com.dingoapp.dingo.api.model.Institution;
import com.dingoapp.dingo.api.model.User;

/**
 * Created by guestguest on 16/04/16.
 */
public class AddSchoolEmailActivity extends AddEmailActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle(R.string.activity_add_school_email);
    }

    @Override
    protected void addEmail(String email, final Callback<Institution> callback) {
        DingoApiService.getInstance().userAddSchool(email, callback);
    }

    @Override
    protected void sendDocument(String institutionName, Uri fileUri, final Callback<Institution> callback){
        //DingoApiService.getInstance().uploadWorkCredential(institutionName, fileUri, callback);
        //todo on failure
    }

    @Override
    protected void resendEmail() {

    }

    @Override
    protected void confirmPin(String pin, String entityName, final Callback<Institution> callback) {
        DingoApiService.getInstance().userConfirmSchool(pin, entityName, callback);
    }

    @Override
    protected void onConfirmedPin(Institution institution, String institutionName) {
        if (institution.getStatus() == User.EntityStatus.C) {
            mUser.setSchool(institution);
            mUser.setSchoolSpecifiedName(null);
            mUser.setSchoolConfirmed(true);
            mUser.setSchoolStatus(User.EntityStatus.C);

        } else if (institution.getStatus() == User.EntityStatus.P) {
            mUser.setSchool(null);
            mUser.setSchoolSpecifiedName(institutionName);
            mUser.setSchoolConfirmed(false);
            mUser.setSchoolStatus(User.EntityStatus.P);
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
        return getString(R.string.add_school_fragment_email_hint, getUser().getFirstName().toLowerCase());
    }

    @Override
    protected String getEmailFragmentHeader() {
        return getString(R.string.add_school_fragment_email_header);
    }

    @Override
    protected String getNameFragmentHeader() {
        return getString(R.string.add_school_fragment_name_header);
    }

    @Override
    protected String getCredentialsFragmentHeader() {
        return getString(R.string.add_school_fragment_credentials_header);
    }
}
