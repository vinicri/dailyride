package com.lightgraylabs.dailyrides.ridesrecurrent;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.lightgraylabs.dailyrides.BaseActivity;
import com.lightgraylabs.dailyrides.OfferActivity;
import com.lightgraylabs.dailyrides.R;
import com.lightgraylabs.dailyrides.RequestActivity;
import com.lightgraylabs.dailyrides.api.ApiCallback;
import com.lightgraylabs.dailyrides.api.DingoApiService;
import com.lightgraylabs.dailyrides.api.Response;
import com.lightgraylabs.dailyrides.api.model.RideEntity;
import com.lightgraylabs.dailyrides.api.model.RideOffer;
import com.lightgraylabs.dailyrides.api.model.UserRides;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by guestguest on 16/04/16.
 */
public class RecurrentRidesActivity extends BaseActivity {

    private static final int REQUEST_RIDE_EDIT = 1;

    private RecyclerView mRecyclerView;
    List<RideEntity> mRides = new ArrayList<>();
    private RecurrentRidesAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rides_recurrent);
        getSupportActionBar().setTitle(R.string.activity_ride_recurrent_title);

        mRecyclerView = (RecyclerView)findViewById(R.id.rides_list);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);

        mRecyclerView.setLayoutManager(llm);

        mAdapter = new RecurrentRidesAdapter(this, mRides);
        mAdapter.setListener(
                new RecurrentRidesAdapter.RideSelectedListener() {
                    @Override
                    public void onRideSelected(RideEntity ride) {
                        Intent intent;
                        if(ride instanceof RideOffer){
                            intent = new Intent(RecurrentRidesActivity.this, OfferActivity.class);
                            intent.putExtra(OfferActivity.EXTRA_OFFER, ride);
                            intent.putExtra(OfferActivity.EXTRA_EDIT_MODE, true);
                            intent.putExtra(OfferActivity.EXTRA_RECURRENT_FLAG, true);
                        }
                        else{
                            intent = new Intent(RecurrentRidesActivity.this, RequestActivity.class);
                            intent.putExtra(RequestActivity.EXTRA_REQUEST, ride);
                            intent.putExtra(RequestActivity.EXTRA_EDIT_MODE, true);
                            intent.putExtra(RequestActivity.EXTRA_RECURRENT_FLAG, true);
                        }
                        startActivityForResult(intent, REQUEST_RIDE_EDIT);
                    }
                }
        );
        mRecyclerView.setAdapter(mAdapter);

        DingoApiService.getInstance().getUserRecurrentRides(
                new ApiCallback<UserRides>(this) {
                    @Override
                    public void success(Response<UserRides> response) {
                        if(response.code() == Response.HTTP_204_NO_CONTENT){

                        }
                        else if(response.code() == Response.HTTP_200_OK){
                            mRides.addAll(response.body().getOffers());
                            mRides.addAll(response.body().getRequests());
                            mAdapter.notifyDataSetChanged();
                        }
                        else {
                            super.success(response);
                        }
                    }
                }
        );
    }
}
