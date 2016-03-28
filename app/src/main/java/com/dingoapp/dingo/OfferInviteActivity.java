package com.dingoapp.dingo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dingoapp.dingo.api.AddressUtils;
import com.dingoapp.dingo.api.Callback;
import com.dingoapp.dingo.api.DingoApiService;
import com.dingoapp.dingo.api.Response;
import com.dingoapp.dingo.api.model.RideMasterRequest;
import com.dingoapp.dingo.api.model.RideOffer;
import com.dingoapp.dingo.api.model.RideOfferSlave;
import com.dingoapp.dingo.util.CircleTransform;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by guestguest on 13/03/16.
 */
public class OfferInviteActivity extends BaseActivity{

    public static final String EXTRA_REQUEST = "EXTRA_REQUEST";

    @Bind(R.id.accept) Button mAcceptButton;
    @Bind(R.id.decline) Button mDeclineButton;

    @Bind(R.id.driver_picture)ImageView mDriverPicture;
    @Bind(R.id.presentation)TextView mPresentation;
    @Bind(R.id.month_day)TextView mMonthDay;
    @Bind(R.id.weekday)TextView mWeekday;
    @Bind(R.id.time)TextView mTime;
    @Bind(R.id.leaving_address)TextView mLeavingAddress;
    @Bind(R.id.arriving_address)TextView mArrivingAddress;
    @Bind(R.id.price)TextView mPrice;

    @Bind(R.id.driver_name) TextView mDriverName;

    private RideMasterRequest mRequest;

    SimpleDateFormat mMonthDayFormat = new SimpleDateFormat("dd");
    SimpleDateFormat mMonthNameFormat = new SimpleDateFormat("MMMM");
    SimpleDateFormat mWeekdayFormat = new SimpleDateFormat("EEEE");
    SimpleDateFormat mTimeFormat = new SimpleDateFormat("HH:mm");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_invite);
        getActionBarToolbar().setTitle(R.string.activity_offer_invite);

        ButterKnife.bind(this);

        mRequest = (RideMasterRequest)getIntent().getSerializableExtra(EXTRA_REQUEST);

        final RideOffer offer = mRequest.getInvitesToConfirm().get(0).getMaster();

        final RideOfferSlave invite = mRequest.getInvitesToConfirm().get(0);
        mPresentation.setText(getString(R.string.request_invite_confirm_presentation, offer.getUser().getFirstName()));

        mMonthDay.setText(getString(R.string.request_invite_confirm_month_day,
                mMonthDayFormat.format(offer.getLeavingTime()),
                mMonthNameFormat.format(offer.getLeavingTime())));

        mWeekday.setText(mWeekdayFormat.format(offer.getLeavingTime()));

        int estimatedPickupTime = invite.getEstimatedPickupTime();
        if(estimatedPickupTime > 0){
            Calendar cal = Calendar.getInstance();
            cal.setTime(offer.getLeavingTime());
            cal.add(Calendar.SECOND, estimatedPickupTime);
            mTime.setText(mTimeFormat.format(cal.getTime()));
        }
        else{
            mTime.setText(mTimeFormat.format(offer.getLeavingTime()));
            //todo estimate here - query to google service
        }

        mLeavingAddress.setText(AddressUtils.getOneLineAddress(mRequest.getLeavingAddress(), false, true));
        mArrivingAddress.setText(AddressUtils.getOneLineAddress(mRequest.getArrivingAddress(), false, true));


        Glide.with(this).load(DingoApiService.getPhotoUrl(offer.getUser()))
                .bitmapTransform(new CircleTransform(this))
                .into(mDriverPicture);

        mDriverName.setText(offer.getUser().getFullName());

        mAcceptButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DingoApiService.getInstance().acceptRideOfferSlave(invite.getId(), null,
                                new Callback<RideOfferSlave>() {
                                    @Override
                                    public void onResponse(Response<RideOfferSlave> response) {
                                        if (response.code() == Response.HTTP_200_OK) {
                                            mRequest.getInvitesToConfirm().clear();
                                            mRequest.getInvitesAccepted().add(response.body());
                                            showDetails();
                                        }

                                    }

                                    @Override
                                    public void onFailure(Throwable t) {
                                        Log.d("e", "e");
                                    }
                                });

                    }
                }
        );

        mDeclineButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DingoApiService.getInstance().declineRideOfferSlave(invite.getId(), null,
                                new Callback<RideOfferSlave>() {
                                    @Override
                                    public void onResponse(Response<RideOfferSlave> response) {
                                        if(response.code() == Response.HTTP_200_OK){
                                            mRequest.getInvitesToConfirm().clear();
                                            showDetails();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Throwable t) {

                                    }
                                });
                    }
                }
        );

    }

    private void showDetails(){
        Intent intent = new Intent(OfferInviteActivity.this, RequestDetailsActivity.class);
        intent.putExtra(RequestDetailsActivity.EXTRA_REQUEST, mRequest);
        startActivity(intent);
        Intent resultData = new Intent();
        resultData.putExtra("request", mRequest);
        setResult(RESULT_OK, resultData);
        finish();
    }
}
