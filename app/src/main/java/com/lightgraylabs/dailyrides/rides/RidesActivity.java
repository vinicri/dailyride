package com.lightgraylabs.dailyrides.rides;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lightgraylabs.dailyrides.BaseActivity;
import com.lightgraylabs.dailyrides.BroadcastExtras;
import com.lightgraylabs.dailyrides.OfferActivity;
import com.lightgraylabs.dailyrides.OfferDetailsActivity;
import com.lightgraylabs.dailyrides.OfferInviteActivity;
import com.lightgraylabs.dailyrides.R;
import com.lightgraylabs.dailyrides.RequestActivity;
import com.lightgraylabs.dailyrides.analytics.Analytics;
import com.lightgraylabs.dailyrides.api.ApiCallback;
import com.lightgraylabs.dailyrides.api.DingoApiService;
import com.lightgraylabs.dailyrides.api.Response;
import com.lightgraylabs.dailyrides.api.model.RideEntity;
import com.lightgraylabs.dailyrides.api.model.RideMasterRequest;
import com.lightgraylabs.dailyrides.api.model.RideOffer;
import com.lightgraylabs.dailyrides.api.model.RideOfferSlave;
import com.lightgraylabs.dailyrides.api.model.User;
import com.lightgraylabs.dailyrides.api.model.UserRides;
import com.lightgraylabs.dailyrides.slaveofferreply.SlaveOfferReplyActivity;
import com.lightgraylabs.dailyrides.util.CurrentUser;
import com.lightgraylabs.dailyrides.view.QuickNoticeScrollListener;

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

    private static final String name = Analytics.SCREEN_RIDES;

    private static final int REQUEST_CODE_NEW_OFFER = 1;
    private static final int REQUEST_CODE_NEW_REQUEST = 2;
    private static final int REQUEST_CODE_EDIT_OFFER = 3;
    private static final int REQUEST_CODE_EDIT_REQUEST = 4;

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
    private BroadcastReceiver mRequestInviteToConfirm;
    private BroadcastReceiver mOfferInviteAccepted;
    private BroadcastReceiver mOfferInviteDeclined;

    Paint mNotificationTextPaint = new Paint();

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

        mNotificationTextPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 14 , getResources().getDisplayMetrics()));

        mAdapter = new RidesAdapter(this, mRideEntities);
        mAdapter.setOnRideClickListener(
                new RidesAdapter.OnRideClickListener() {
                    @Override
                    public void onOfferClick(RideOffer offer) {
                        if (offer.getInvitesToAccept().isEmpty()) {
                            Intent intent = new Intent(RidesActivity.this, OfferDetailsActivity.class);
                            intent.putExtra(OfferDetailsActivity.EXTRA_OFFER, offer);
                            startActivityForResult(intent, REQUEST_CODE_EDIT_OFFER);
                        } else {
                            Intent intent = new Intent(RidesActivity.this, SlaveOfferReplyActivity.class);
                            intent.putExtra(SlaveOfferReplyActivity.EXTRA_OFFER, offer);
                            startActivityForResult(intent, REQUEST_CODE_EDIT_OFFER);
                        }

                    }

                    @Override
                    public void onRequestClick(RideMasterRequest request) {
                        if(request.getInvitesToConfirm().isEmpty()){

                            Toast.makeText(RidesActivity.this, R.string.finding_rides, Toast.LENGTH_LONG);
//                            Intent intent = new Intent(RidesActivity.this, RequestDetailsActivity.class);
//                            intent.putExtra(RequestDetailsActivity.EXTRA_REQUEST, request);
//                            startActivityForResult(intent, REQUEST_CODE_EDIT_REQUEST);
                        }
                        else{
                            Intent intent = new Intent(RidesActivity.this, OfferInviteActivity.class);
                            intent.putExtra(OfferInviteActivity.EXTRA_REQUEST, request);
                            startActivityForResult(intent, REQUEST_CODE_EDIT_REQUEST);
                        }
                    }
                }
        );

        DingoApiService.getInstance().getUserRides(
                new ApiCallback<UserRides>(this) {
                    @Override
                    public void success(Response<UserRides> response) {
                        if (response.code() == Response.HTTP_200_OK) {
                            UserRides userRides = response.body();
                            mRideEntities.addAll(userRides.getRequests());
                            mRideEntities.addAll(userRides.getOffers());
                            orderRideEntities();
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                });

        View.OnClickListener requestListener =
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(RidesActivity.this, RequestActivity.class);
                        startActivityForResult(intent, REQUEST_CODE_NEW_REQUEST);
                        Analytics.getInstance().event(Analytics.CATEGORY_CREATE_OFFER,
                                                        Analytics.ACTION_ENTER_SCREEN,
                                                        null,
                                                        null);
                    }
                };

        mOfferButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(RidesActivity.this, OfferActivity.class);
                        startActivityForResult(intent, REQUEST_CODE_NEW_OFFER);
                        Analytics.getInstance().event(Analytics.CATEGORY_CREATE_REQUEST,
                                Analytics.ACTION_ENTER_SCREEN,
                                null,
                                null);
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
                RideOfferSlave offerSlave = (RideOfferSlave)intent.getSerializableExtra(BroadcastExtras.EXTRA_RIDE_INVITE);

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

        mRequestInviteToConfirm = new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent) {
                RideOfferSlave invite = (RideOfferSlave)intent.getSerializableExtra(BroadcastExtras.EXTRA_RIDE_INVITE);
                RideMasterRequest request = findRideForInvite(RideMasterRequest.class, invite);
                if(request != null) {
                    request.getInvitesToConfirm().add(invite);
                    int index = mRideEntities.indexOf(request);
                    mAdapter.notifyItemChanged(index);
                    showNotification(getString(R.string.notification_driver_is_offering_a_ride, invite.getMaster().getUser().getFirstName()), null);
                }
                else{
                    //todo not expected:report
                }
            }
        };


        mOfferInviteAccepted = new BroadcastReceiver(){

            @Override
            public void onReceive(Context context, Intent intent) {
                RideOfferSlave invite = (RideOfferSlave)intent.getSerializableExtra(BroadcastExtras.EXTRA_RIDE_INVITE);
                RideOffer offer = findRideForInvite(RideOffer.class, invite);
                if(offer != null) {
                    offer.getInvitesAccepted().add(invite);
                    removeInvite(invite, offer.getInvitesWaitingConfirmation());
                    int index = mRideEntities.indexOf(offer);
                    mAdapter.notifyItemChanged(index);
                    showNotification(getString(R.string.notification_rider_accepted_invite, invite.getToRideRequest().getUser().getFirstName()), null);
                }
                else{
                    //todo not expected:report
                }
            }
        };


        mOfferInviteDeclined = new BroadcastReceiver(){

            @Override
            public void onReceive(Context context, Intent intent) {
                RideOfferSlave invite = (RideOfferSlave)intent.getSerializableExtra(BroadcastExtras.EXTRA_RIDE_INVITE);
                RideOffer offer = findRideForInvite(RideOffer.class, invite);
                if(offer != null) {
                    offer.getInvitesRefused().add(invite);
                    int index = mRideEntities.indexOf(offer);
                    mAdapter.notifyItemChanged(index);
                }
                else{
                    //todo not expected:report
                }
            }
        };

    }

    private <T> T findRideForInvite(Class<T> rideClass, RideOfferSlave invite){
        long rideId = rideClass.isAssignableFrom(RideOffer.class) ? invite.getMaster().getId() : invite.getToRideRequest().getId();
        for(RideEntity ride: mRideEntities) {
            if (rideClass.isInstance(ride)){
                if(ride.getId() == rideId){
                    return (T)ride;
                }
            }
        }
        return null;
    }

    //remove the invite with same id
    private void removeInvite(RideOfferSlave target, List<RideOfferSlave> inviteList){
        RideOfferSlave existingInvite = null;
        for(RideOfferSlave invite: inviteList){
            if(invite.getId() == target.getId()){
                existingInvite = invite;
            }
        }
        if(existingInvite != null){
            inviteList.remove(existingInvite);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRequestInviteToConfirm, new IntentFilter(BroadcastExtras.NOTIFICATION_REQUEST_INVITE_TO_CONFIRM));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRideOfferSlaveReceiver, new IntentFilter(BroadcastExtras.NOTIFICATION_RIDE_OFFER_SLAVE));
        LocalBroadcastManager.getInstance(this).registerReceiver(mOfferInviteAccepted, new IntentFilter(BroadcastExtras.NOTIFICATION_OFFER_INVITE_ACCEPTED));
        LocalBroadcastManager.getInstance(this).registerReceiver(mOfferInviteDeclined, new IntentFilter(BroadcastExtras.NOTIFICATION_OFFER_INVITE_DECLINED));
        Analytics.getInstance().screenViewed(name);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRideOfferSlaveReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRequestInviteToConfirm);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mOfferInviteAccepted);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mOfferInviteDeclined);
    }

    private void showNotification(CharSequence text, View.OnClickListener action){
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        //float textWidth = mNotificationTextPaint.measureText(text.toString());
        //textWidth += TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, getResources().getDisplayMetrics());

        final int initialWidth = Math.round(280 * getResources().getDisplayMetrics().density);
        final int finalWidth = Math.round(40 * getResources().getDisplayMetrics().density);

        mNotificationText.setText(text);
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
            if(requestCode == REQUEST_CODE_NEW_OFFER){
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
            else if(requestCode == REQUEST_CODE_NEW_REQUEST){
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
            else if(requestCode == REQUEST_CODE_EDIT_OFFER){
                RideOffer editedOffer = (RideOffer)data.getSerializableExtra("offer");
                replaceRide(editedOffer);
            }
            else if(requestCode == REQUEST_CODE_EDIT_REQUEST){
                RideMasterRequest editedRequest = (RideMasterRequest)data.getSerializableExtra("request");
                replaceRide(editedRequest);
            }
        }
    }


    private void replaceRide(RideEntity editedRide){
        long editedRideId = editedRide.getId();
        RideEntity existingRide = null;
        for(RideEntity ride: mRideEntities){
                if(ride.getId() == editedRideId){
                    existingRide = ride;
                    break;
                }
        }
        if(existingRide != null) { //safeguard, never expected
            int index = mRideEntities.indexOf(existingRide);
            mRideEntities.remove(index);
            mRideEntities.add(index, editedRide);
            mAdapter.notifyItemChanged(index);
        }
        else{
            //todo notify server?
        }
    }


   /* private void replaceOffer(RideOffer editedOffer){
        long editedOfferId = editedOffer.getId();
        RideOffer existingOffer = null;
        for(RideEntity ride: mRideEntities){
            if(ride instanceof RideOffer){
                RideOffer offer = (RideOffer)ride;
                if(offer.getId() == editedOfferId){
                    existingOffer = offer;
                    break;
                }
            }
        }
        if(existingOffer != null) { //safeguard, never expected
            int index = mRideEntities.indexOf(existingOffer);
            mRideEntities.remove(index);
            mRideEntities.add(index, editedOffer);
            mAdapter.notifyItemChanged(index);

        }
        else{
            //todo notify server?
        }
    }*/

    private class LeavingDateComparator implements Comparator<RideEntity> {

        @Override
        public int compare(RideEntity lhs, RideEntity rhs) {
            return lhs.getLeavingTime().compareTo(rhs.getLeavingTime());
        }

    }
}
