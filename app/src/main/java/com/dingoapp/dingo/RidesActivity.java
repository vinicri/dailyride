package com.dingoapp.dingo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by guestguest on 30/01/16.
 */
public class RidesActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0, 0);
        setContentView(R.layout.activity_rides);
    }
}
