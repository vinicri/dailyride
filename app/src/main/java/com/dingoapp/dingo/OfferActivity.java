package com.dingoapp.dingo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;

import com.dingoapp.dingo.searchaddress.AddressSearchActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by guestguest on 31/01/16.
 */
public class OfferActivity extends AppCompatActivity{

    private static final int RESULT_LEAVING_ADDRESS = 10;
    private static final int RESULT_ARRIVING_ADDRESS = 11;
    private static final int RESULT_TIME = 11;

    @Bind(R.id.leaving_address_box) LinearLayout mLeavingAddressBox;
    @Bind(R.id.arriving_address_box) LinearLayout mArrivingAddressBox;
    @Bind(R.id.time_box) LinearLayout mTimeBox;
    @Bind(R.id.recurrence_box) LinearLayout mRecurrenceBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer);
        ButterKnife.bind(this);

        mLeavingAddressBox.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(OfferActivity.this, AddressSearchActivity.class);
                        startActivityForResult(intent, RESULT_LEAVING_ADDRESS);
                    }
                }
        );

        mTimeBox.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(OfferActivity.this, TimeActivity.class);
                        startActivityForResult(intent, RESULT_TIME);
                    }
                }
        );

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }
}
