package com.dingoapp.dingo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by guestguest on 30/01/16.
 */
public class RidesActivity extends AppCompatActivity {

    @Bind(R.id.request) Button mRequestButton;
    @Bind(R.id.offer) Button mOfferButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0, 0);
        setContentView(R.layout.activity_rides);
        ButterKnife.bind(this);

        mOfferButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(RidesActivity.this, OfferActivity.class);
                        startActivity(intent);
                    }
                }

        );
    }
}
