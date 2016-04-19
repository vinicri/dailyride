package com.lightgraylabs.dailyrides.slaveofferreply;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lightgraylabs.dailyrides.BaseMapActivity;
import com.lightgraylabs.dailyrides.OfferDetailsActivity;
import com.lightgraylabs.dailyrides.R;
import com.lightgraylabs.dailyrides.api.ApiCallback;
import com.lightgraylabs.dailyrides.api.DingoApiService;
import com.lightgraylabs.dailyrides.api.Response;
import com.lightgraylabs.dailyrides.api.model.RideMasterRequest;
import com.lightgraylabs.dailyrides.api.model.RideOffer;
import com.lightgraylabs.dailyrides.api.model.RideOfferSlave;
import com.lightgraylabs.dailyrides.google.maps.api.directions.model.DirectionsResponse;
import com.lightgraylabs.dailyrides.util.CircleTransform;
import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.lightgraylabs.dailyrides.api.AddressUtils.getOneLineAddress;

/**
 * Created by guestguest on 08/03/16.
 */
public class SlaveOfferReplyActivity extends BaseMapActivity{

    //when it's opened from main screen
    public static final String EXTRA_OFFER = "EXTRA_OFFER";

    //when it's opened from notification
    public static final String EXTRA_SLAVE_OFFER_ID = "EXTRA_SLAVE_OFFER_ID";

    @Bind(R.id.header_user)TextView mHeaderUser;
    @Bind(R.id.header_date_time)TextView mDateTime;

    @Bind(R.id.header_picture)ImageView mHeaderPicture;

    @Bind(R.id.route_text1_top)TextView mRouteText1Top;
    @Bind(R.id.route_text2_top)TextView mRouteText2Top;
    @Bind(R.id.route_text3_top)TextView mRouteText3Top;

    @Bind(R.id.route_text1)TextView mRouteText1;
    @Bind(R.id.route_text2)TextView mRouteText2;
    @Bind(R.id.route_text3)TextView mRouteText3;
    @Bind(R.id.route_text4)TextView mRouteText4;

    @Bind(R.id.offer)Button mOfferButton;
    @Bind(R.id.decline)Button mDeclineButton;

    LatLng PE_ANTONIO = new LatLng(-23.605671, -46.692275);
    LatLng LEOPOLDO = new LatLng(-23.587741, -46.679778);
    //LatLng PAULISTA = new LatLng(-23.560541, -46.657462);
    //LatLng ROD = new LatLng(-23.515551, -46.624975);

    private RideOffer mOffer;
    private RideOfferSlave mSlaveOffer;
    private SimpleDateFormat mTimeFormat;

    private int mDuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slave_offer_reply);
        getSupportActionBar().setTitle(R.string.activity_slave_offer);
        getActionBarToolbar().showOverflowMenu();
        ButterKnife.bind(this);

        mOffer = (RideOffer)getIntent().getSerializableExtra(EXTRA_OFFER);

        if(mOffer != null){
            mSlaveOffer = mOffer.getInvitesToAccept().get(0);
            String riderName = mSlaveOffer.getToRideRequest().getUser().getFirstName();
            mHeaderUser.setText(getString(R.string.slave_offer_user_wants_a_ride, riderName));

            SimpleDateFormat mDayFormat = new SimpleDateFormat("EEEE");
            mTimeFormat = new SimpleDateFormat("HH:mm");

            mDateTime.setText(getString(R.string.slave_offer_datetime,
                    mDayFormat.format(mOffer.getLeavingTime()),
                    mTimeFormat.format(mOffer.getLeavingTime())));

            Glide.with(this).load(DingoApiService.getPhotoUrl(mSlaveOffer.getToRideRequest().getUser()))
                    .bitmapTransform(new CircleTransform(this))
                    .into(mHeaderPicture);

            mRouteText2Top.setText(getString(R.string.slave_offer_meets_at, riderName));
            mRouteText3Top.setText(getString(R.string.slave_offer_drops_at, riderName));

        }

        mOfferButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DingoApiService.getInstance().acceptRideOfferSlave(mSlaveOffer.getId(), mDuration,
                                new ApiCallback<RideOfferSlave>(SlaveOfferReplyActivity.this) {
                                    @Override
                                    public void success(Response<RideOfferSlave> response) {
                                        if(response.code() == Response.HTTP_200_OK){
                                            mOffer.getInvitesToAccept().clear();
                                            mOffer.getInvitesWaitingConfirmation().add(response.body());
                                            Intent intent = new Intent(SlaveOfferReplyActivity.this, OfferDetailsActivity.class);
                                            intent.putExtra(SlaveOfferReplyActivity.EXTRA_OFFER, mOffer);
                                            startActivity(intent);
                                            Intent resultData = new Intent();
                                            resultData.putExtra("offer", mOffer);
                                            setResult(RESULT_OK, resultData);
                                            finish();
                                        }
                                    }
                                });
                    }
                }

        );

        mDeclineButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DingoApiService.getInstance().declineRideOfferSlave(mSlaveOffer.getId(), mDuration,
                                new ApiCallback<RideOfferSlave>(SlaveOfferReplyActivity.this) {
                                    @Override
                                    public void success(Response<RideOfferSlave> response) {
                                        mOffer.getInvitesToAccept().clear();
                                        Intent intent = new Intent(SlaveOfferReplyActivity.this, OfferDetailsActivity.class);
                                        intent.putExtra(SlaveOfferReplyActivity.EXTRA_OFFER, mOffer);
                                        startActivity(intent);
                                    }
                                });
                    }
                }
        );
    }

    @Override
    protected RideOffer getOffer() {
        return mOffer;
    }

    @Override
    protected List<RideMasterRequest> getRequests() {
        List<RideMasterRequest> requestsWithSlaveOfferRequest = new ArrayList<>();
        if(mOffer.getRequests() != null){
            requestsWithSlaveOfferRequest.addAll(mOffer.getRequests());
        }
        requestsWithSlaveOfferRequest.add(mSlaveOffer.getToRideRequest());
        return requestsWithSlaveOfferRequest;
    }

    @Override
    protected void gotDirections(DirectionsResponse directions, List<RideMasterRequest> orderedRequests){
        int duration = 0;
        //sum up durations of legs up to the slave offer request
        for (int i = 0; i < orderedRequests.size(); i++) {
            RideMasterRequest request = orderedRequests.get(i);
            duration += directions.getRoutes().get(0).getLegs().get(i).getDuration().getValue();
            if (request == mSlaveOffer.getToRideRequest()) {
                break;
            }
        }

        mDuration = duration;
        //fill up address using duration
        showAddresses();

    }

    @Override
    protected void directionsFailed(List<RideMasterRequest> orderedRequests) {
        showAddresses();
    }

    private void showAddresses(){
       mRouteText1.setText(
               getString(R.string.slave_offer_leaving_address,
                       getOneLineAddress(mOffer.getLeavingAddress(), true),
                       mTimeFormat.format(mOffer.getLeavingTime())));

        if(mDuration > 0){
            Calendar cal = Calendar.getInstance();
            cal.setTime(mOffer.getLeavingTime());
            cal.add(Calendar.SECOND, mDuration);

            mRouteText2.setText(
                    getString(R.string.slave_offer_meets_address,
                            getOneLineAddress(mSlaveOffer.getToRideRequest().getLeavingAddress(), true),
                            mTimeFormat.format(cal.getTime())));

        }
        else{
            mRouteText2.setText(getOneLineAddress(mSlaveOffer.getToRideRequest().getLeavingAddress(), true));
        }

        mRouteText3.setText(getOneLineAddress(mSlaveOffer.getToRideRequest().getArrivingAddress(), true));

    }

}
