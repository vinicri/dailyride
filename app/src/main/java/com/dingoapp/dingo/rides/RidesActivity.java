package com.dingoapp.dingo.rides;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.dingoapp.dingo.OfferActivity;
import com.dingoapp.dingo.R;
import com.dingoapp.dingo.RequestActivity;
import com.dingoapp.dingo.api.Callback;
import com.dingoapp.dingo.api.DingoApiService;
import com.dingoapp.dingo.api.Response;
import com.dingoapp.dingo.api.model.RideEntity;
import com.dingoapp.dingo.api.model.UserRides;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by guestguest on 30/01/16.
 */
public class RidesActivity extends AppCompatActivity {

    @Bind(R.id.request) Button mRequestButton;
    @Bind(R.id.offer) Button mOfferButton;
    @Bind(R.id.rides_list) RecyclerView mRecyclerView;

    List<RideEntity> mRideEntities = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0, 0);
        setContentView(R.layout.activity_rides);
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

        mOfferButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(RidesActivity.this, OfferActivity.class);
                        startActivity(intent);
                    }
                }

        );

        mRequestButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(RidesActivity.this, RequestActivity.class);
                        startActivity(intent);
                    }
                }
        );
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
