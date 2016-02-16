package com.dingoapp.dingo.offertouser;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.dingoapp.dingo.api.model.RideMasterRequest;

import java.util.ArrayList;

/**
 * Created by guestguest on 13/02/16.
 */
public class OfferToUserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ArrayList<RideMasterRequest> requests = (ArrayList<RideMasterRequest>) getIntent().getSerializableExtra("requests");
        if(requests == null){

        }


    }
}
