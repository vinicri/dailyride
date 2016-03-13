package com.dingoapp.dingo.rides;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
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
import com.dingoapp.dingo.api.model.RideMasterRequest;
import com.dingoapp.dingo.api.model.RideOffer;
import com.dingoapp.dingo.api.model.User;
import com.dingoapp.dingo.api.model.UserRides;
import com.dingoapp.dingo.util.CurrentUser;
import com.dingoapp.dingo.view.QuickNoticeScrollListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by guestguest on 30/01/16.
 */
public class RidesActivity extends BaseActivity {

    private static final int REQUEST_CODE_OFFER = 1;
    private static final int REQUEST_CODE_REQUEST = 2;


    @Bind(R.id.request) Button mRequestButton;
    @Bind(R.id.request2) Button mRequestButton2;
    @Bind(R.id.rider_mode_buttons) LinearLayout mRiderModeButtons;
    @Bind(R.id.driver_mode_buttons) LinearLayout mDriverModeButtons;
    @Bind(R.id.offer) Button mOfferButton;
    @Bind(R.id.rides_list) RecyclerView mRecyclerView;
    @Bind(R.id.header) LinearLayout mHeaderView;

    List<RideEntity> mRideEntities = new ArrayList<>();
    private RidesAdapter mAdapter;
    private LeavingDateComparator mLeavingDateComparator;
    private LinearLayoutManager mLayoutManager;
    private float mHeaderHeight;

    private static final ScheduledExecutorService worker = Executors.newSingleThreadScheduledExecutor();
    private QuickNoticeScrollListener mNoticeScrollListener;


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
        mAdapter = new RidesAdapter(this, mRideEntities);

        DingoApiService.getInstance().getUserRides(
                new Callback<UserRides>() {
                    @Override
                    public void onResponse(Response<UserRides> response) {
                        if (response.code() == Response.HTTP_200_OK) {
                            UserRides userRides = response.body();
                            mRideEntities.addAll(userRides.getRequests());
                            mRideEntities.addAll(userRides.getOffers());
                            mAdapter.notifyDataSetChanged();
                        }

                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Log.d("text", "text");
                    }
                }
        );


        View.OnClickListener requestListener =
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(RidesActivity.this, RequestActivity.class);
                        startActivityForResult(intent, REQUEST_CODE_REQUEST);
                    }
                };

        mOfferButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(RidesActivity.this, OfferActivity.class);
                        startActivityForResult(intent, REQUEST_CODE_OFFER);
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


        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mHeaderHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics());

       /* QuickReturnRecyclerViewOnScrollListener scrollListener = new QuickReturnRecyclerViewOnScrollListener.Builder(QuickReturnViewType.HEADER)
                .header(mHeaderView)
                .minHeaderTranslation(-(int)height)
                .isSnappable(false)
                .build();*/

        QuickNoticeScrollListener.NoticeListener noticeListener = new QuickNoticeScrollListener.NoticeListener() {
            @Override
            public void wasShown() {
                mAdapter.setHeaderEnabled(true);
            }

            @Override
            public void wasHidden() {
                mAdapter.setHeaderEnabled(false);
            }
        };

        mNoticeScrollListener = new QuickNoticeScrollListener.Builder(mHeaderView, mRecyclerView)
                .maxTranslation((int)mHeaderHeight)
                .listener(noticeListener)
                .build();

        //mRecyclerView.addOnScrollListener(mNoticeScrollListener);
        mLeavingDateComparator = new LeavingDateComparator();

        final Handler mainHandler = new Handler(Looper.getMainLooper());
        Runnable task = new Runnable() {
            @Override
            public void run() {
                mNoticeScrollListener.showNotice("teste");
               // mAdapter.setHeaderEnabled(true);
                //mHeaderView.setTranslationY(mHeaderHeight);
                //mNoticeScrollListener.scrollItDown();
               // mNoticeScrollListener.showNotice("teste");
                //mRecyclerView.addOnScrollListener(mNoticeScrollListener);
                //mNoticeScrollListener.setEnabled(true);
            }
        };

        mainHandler.postDelayed(task, 5000);



       // worker.schedule(task, 5, TimeUnit.SECONDS);
    }

    private void refreshList(){
        if(mRideEntities.size() == 0){
            //show empty screen
        }
        else{
            //adapter notify
        }
    }

    private void orderRideEntities(){
        Collections.sort(mRideEntities, mLeavingDateComparator);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == REQUEST_CODE_OFFER){
                RideOffer offer = (RideOffer)data.getSerializableExtra("offer");
                mRideEntities.add(offer);
                orderRideEntities();
                int index = mAdapter.itemIndex(offer);
                mAdapter.notifyItemInserted(index);
                //mLayoutManager.getPosition(mLayoutManager.getChildAt(index);
                //mLayoutManager.scrollToPosition(index);
                mLayoutManager.scrollToPositionWithOffset(index, -(int)mHeaderHeight);
               // int childLayoutPosY = mRecyclerView.getChildLayoutPosition(mRecyclerView.getChildAt(index));

               //mRecyclerView.scrollToPosition();
            }
            else if(requestCode == REQUEST_CODE_REQUEST){
                RideMasterRequest request = (RideMasterRequest)data.getSerializableExtra("request");
                mRideEntities.add(request);
                orderRideEntities();
                int index = mAdapter.itemIndex(request);
                mAdapter.notifyItemInserted(index);
                mLayoutManager.scrollToPositionWithOffset(index, -(int)mHeaderHeight);
            }
        }
    }




    private class LeavingDateComparator implements Comparator<RideEntity> {

        @Override
        public int compare(RideEntity lhs, RideEntity rhs) {
            return lhs.getLeavingTime().compareTo(rhs.getLeavingTime());
        }

    }
}
