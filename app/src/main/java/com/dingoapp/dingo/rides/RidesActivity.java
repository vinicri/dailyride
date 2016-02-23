package com.dingoapp.dingo.rides;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.dingoapp.dingo.BaseActivity;
import com.dingoapp.dingo.OfferActivity;
import com.dingoapp.dingo.R;
import com.dingoapp.dingo.RequestActivity;
import com.dingoapp.dingo.api.Callback;
import com.dingoapp.dingo.api.DingoApiService;
import com.dingoapp.dingo.api.Response;
import com.dingoapp.dingo.api.model.RideEntity;
import com.dingoapp.dingo.api.model.User;
import com.dingoapp.dingo.api.model.UserRides;
import com.dingoapp.dingo.util.CurrentUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by guestguest on 30/01/16.
 */
public class RidesActivity extends BaseActivity {

    @Bind(R.id.request) Button mRequestButton;
    @Bind(R.id.request2) Button mRequestButton2;
    @Bind(R.id.rider_mode_buttons) LinearLayout mRiderModeButtons;
    @Bind(R.id.driver_mode_buttons) LinearLayout mDriverModeButtons;
    @Bind(R.id.offer) Button mOfferButton;
    @Bind(R.id.rides_list) RecyclerView mRecyclerView;

    List<RideEntity> mRideEntities = new ArrayList<>();

    protected int getSelfNavDrawerItem() {
        return NAVDRAWER_ITEM_DEFAULT;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0, 0);
        setContentView(R.layout.activity_rides);
        getSupportActionBar().setTitle(R.string.activity_ride_title);
        ButterKnife.bind(this);

        DingoApiService.getInstance().getUserRides(
                new Callback<UserRides>() {
                    @Override
                    public void onResponse(Response<UserRides> response) {
                        if (response.code() == Response.HTTP_200_OK) {
                            UserRides userRides = response.body();
                            mRideEntities.addAll(userRides.getRequests());
                            mRideEntities.addAll(userRides.getOffers());
                        }

                    }

                    @Override
                    public void onFailure(Throwable t) {

                    }
                }
        );


        View.OnClickListener requestListener =
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(RidesActivity.this, RequestActivity.class);
                        startActivity(intent);
                    }
                };

        mOfferButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(RidesActivity.this, OfferActivity.class);
                        startActivity(intent);
                    }
                }

        );

        mRequestButton.setOnClickListener(requestListener);
        mRequestButton2.setOnClickListener(requestListener);

        if(CurrentUser.getUser().getRiderMode() == User.RiderMode.D){
            mRiderModeButtons.setVisibility(View.GONE);
            mDriverModeButtons.setVisibility(View.VISIBLE);
        }
        else{
            mDriverModeButtons.setVisibility(View.GONE);
            mRiderModeButtons.setVisibility(View.VISIBLE);
        }

    }

    private void refreshList(){
        if(mRideEntities.size() == 0){
            //show empty screen
        }
        else{
            //adapter notify
        }
    }
}
