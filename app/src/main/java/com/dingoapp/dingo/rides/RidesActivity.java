package com.dingoapp.dingo.rides;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dingoapp.dingo.BaseActivity;
import com.dingoapp.dingo.BroadcastExtras;
import com.dingoapp.dingo.OfferActivity;
import com.dingoapp.dingo.R;
import com.dingoapp.dingo.RequestActivity;
import com.dingoapp.dingo.api.Callback;
import com.dingoapp.dingo.api.DingoApiService;
import com.dingoapp.dingo.api.Response;
import com.dingoapp.dingo.api.model.RideEntity;
import com.dingoapp.dingo.api.model.RideMasterRequest;
import com.dingoapp.dingo.api.model.RideOffer;
import com.dingoapp.dingo.api.model.RideOfferSlave;
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
    @Bind(R.id.notification_box) RelativeLayout mNotificationBox;

    @Bind(R.id.notification_text) TextView mNotificationText;

    List<RideEntity> mRideEntities = new ArrayList<>();
    private RidesAdapter mAdapter;
    private LeavingDateComparator mLeavingDateComparator;
    private LinearLayoutManager mLayoutManager;
    private float mHeaderHeight;

    //private RidesAdapter.RideViewHolder.PreAnimationRunnable mPreAnimationRunnable;

    private static final ScheduledExecutorService worker = Executors.newSingleThreadScheduledExecutor();
    private QuickNoticeScrollListener mNoticeScrollListener;
    private BroadcastReceiver mRideOfferSlaveReceiver;


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

        //mPreAnimationRunnable = new RidesAdapter.RideViewHolder.PreAnimationRunnable();

        /*mRecyclerView.setItemAnimator(new RecyclerViewItemAnimatorAdapter() {
            @Override
            public boolean animateAppearance(RecyclerView.ViewHolder viewHolder, ItemHolderInfo preLayoutInfo, ItemHolderInfo postLayoutInfo) {
                if (viewHolder instanceof RidesAdapter.RideViewHolder) {
                    RidesAdapter.RideViewHolder rideViewHolder = (RidesAdapter.RideViewHolder) viewHolder;
                    rideViewHolder.mAlertBox.setVisibility(View.VISIBLE);
                    rideViewHolder.itemView.requestLayout();
                    rideViewHolder.showAlertAndShrinkUpAfter(RidesActivity.this, 4000, new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            super.onAnimationStart(animation);
                        }
                    });
                }
                return false;
            }
        });*/



        mHeaderHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics());

       /* QuickReturnRecyclerViewOnScrollListener scrollListener = new QuickReturnRecyclerViewOnScrollListener.Builder(QuickReturnViewType.HEADER)
                .header(mHeaderView)
                .minHeaderTranslation(-(int)height)
                .isSnappable(false)
                .build();*/

       /* QuickNoticeScrollListener.NoticeListener noticeListener = new QuickNoticeScrollListener.NoticeListener() {
            @Override
            public void wasShown() {
                mAdapter.setHeaderEnabled(true);
            }

            @Override
            public void wasHidden() {
                mAdapter.setHeaderEnabled(false);
            }
        };*/

        /*mNoticeScrollListener = new QuickNoticeScrollListener.Builder(mHeaderView, mRecyclerView)
                .maxTranslation((int)mHeaderHeight)
                .listener(noticeListener)
                .build();*/

        //mRecyclerView.addOnScrollListener(mNoticeScrollListener);
        mLeavingDateComparator = new LeavingDateComparator();

        final Handler mainHandler = new Handler(Looper.getMainLooper());
        Runnable task = new Runnable() {
            @Override
            public void run() {
                //mNoticeScrollListener.showNotice("teste");
               // mAdapter.setHeaderEnabled(true);
                //mHeaderView.setTranslationY(mHeaderHeight);
                //mNoticeScrollListener.scrollItDown();
               // mNoticeScrollListener.showNotice("teste");
                //mRecyclerView.addOnScrollListener(mNoticeScrollListener);
                //mNoticeScrollListener.setEnabled(true);
            }
        };

        //mainHandler.postDelayed(task, 5000);

        //TSnackbar.make(findViewById(android.R.id.content), "Hello from TSnackBar.", TSnackbar.LENGTH_LONG).show();


        /*Runnable task2 = new Runnable() {
            @Override
            public void run() {
                showNotification("teste", null);
            }
        };

        mainHandler.postDelayed(task2, 1000);*/

       // worker.schedule(task, 5, TimeUnit.SECONDS);

        mRideOfferSlaveReceiver = new BroadcastReceiver(){

            @Override
            public void onReceive(Context context, Intent intent) {
                RideOfferSlave offerSlave = (RideOfferSlave)intent.getSerializableExtra(BroadcastExtras.EXTRA_RIDE_OFFER_SLAVE);

                long offerSlaveId = offerSlave.getMaster().getId();
                for(RideEntity ride: mRideEntities){
                    if(ride instanceof RideOffer){
                        RideOffer offer = (RideOffer)ride;
                        if(offer.getId() == offerSlaveId){
                            offer.getInvitesToAccept().add(offerSlave);
                            int index = mRideEntities.indexOf(offer);
                            showNotification(getString(R.string.notification_user_wants_a_ride, offerSlave.getToRideRequest().getUser().getFirstName()), null);
                            mAdapter.notifyItemChanged(index);
                        }
                    }
                }


            }
        };



    }


    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRideOfferSlaveReceiver, new IntentFilter(BroadcastExtras.NOTIFICATION_RIDE_OFFER_SLAVE));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRideOfferSlaveReceiver);
    }

    private void showNotification(CharSequence text, View.OnClickListener action){
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        final int initialWidth = Math.round(250 * getResources().getDisplayMetrics().density);
        final int finalWidth = Math.round(40 * getResources().getDisplayMetrics().density);
        ValueAnimator alphaAnimationBox = ValueAnimator.ofFloat(0f, 1f);
        ValueAnimator alphaAnimationText = ValueAnimator.ofFloat(1f, 0f);
        ValueAnimator widthAnimation  = ValueAnimator.ofInt(initialWidth, finalWidth);

        alphaAnimationBox.addUpdateListener(
                new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float value = (Float) animation.getAnimatedValue();
                        mNotificationBox.setAlpha(value);
                    }
                }
        );

        widthAnimation.addUpdateListener(
                new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        int value = (Integer) animation.getAnimatedValue();
                        mNotificationBox.getLayoutParams().width = value;
                        mNotificationBox.requestLayout();
                            /*layoutParams.height = value;
                            alertView.setLayoutParams(layoutParams);
                            alertView.invalidate();*/
                    }
                }
        );

        alphaAnimationText.addUpdateListener(
                new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float value = (Float) animation.getAnimatedValue();
                        mNotificationText.setAlpha(value);
                    }
                }
        );


        alphaAnimationBox.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                mNotificationBox.setAlpha(0);
                mNotificationText.setAlpha(1);
                mNotificationBox.getLayoutParams().width = initialWidth;
                mNotificationBox.setVisibility(View.VISIBLE);
                mNotificationBox.requestLayout();

            }
        });


        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(alphaAnimationBox).before(alphaAnimationText);
        animatorSet.play(alphaAnimationText).before(widthAnimation);

        alphaAnimationBox.setDuration(200);
        alphaAnimationText.setDuration(100);
        widthAnimation.setDuration(400);

        alphaAnimationText.setInterpolator(new AccelerateDecelerateInterpolator());
        widthAnimation.setInterpolator(new AccelerateDecelerateInterpolator());

        alphaAnimationText.setStartDelay(3000);

        animatorSet.start();

    }

    private void hideNotification(){
        ValueAnimator alphaAnimationBox = ValueAnimator.ofFloat(1f, 0f);
        alphaAnimationBox.addUpdateListener(
                new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float value = (Float) animation.getAnimatedValue();
                        mNotificationText.setAlpha(value);
                    }
                }
        );

        alphaAnimationBox.addListener(
                new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        mNotificationText.setVisibility(View.GONE);
                    }
                }
        );

        alphaAnimationBox.setDuration(300);
        alphaAnimationBox.start();
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
                offer.justCreated = true;
                final int index = mRideEntities.indexOf(offer);
                /*mPreAnimationRunnable.setRunnable(new Runnable() {
                    @Override
                    public void run() {
                        mLayoutManager.scrollToPositionWithOffset(index, 0);
                    }
                });*/
                mAdapter.notifyItemInserted(index);
                mLayoutManager.scrollToPositionWithOffset(index, 0);

                //mLayoutManager.getPosition(mLayoutManager.getChildAt(index);
                //mLayoutManager.scrollToPosition(index);
                //mLayoutManager.scrollToPositionWithOffset(index, -(int)mHeaderHeight);
               // int childLayoutPosY = mRecyclerView.getChildLayoutPosition(mRecyclerView.getChildAt(index));

               //mRecyclerView.scrollToPosition();
            }
            else if(requestCode == REQUEST_CODE_REQUEST){
                RideMasterRequest request = (RideMasterRequest)data.getSerializableExtra("request");
                mRideEntities.add(request);
                orderRideEntities();
                final int index = mRideEntities.indexOf(request);
                request.justCreated = true;
                /*mPreAnimationRunnable.setRunnable(new Runnable() {
                    @Override
                    public void run() {
                        mLayoutManager.scrollToPositionWithOffset(index, 0);
                    }
                });*/
                mAdapter.notifyItemInserted(index);
                mLayoutManager.scrollToPositionWithOffset(index, 0);
                //mLayoutManager.scrollToPositionWithOffset(index, -(int)mHeaderHeight);
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
