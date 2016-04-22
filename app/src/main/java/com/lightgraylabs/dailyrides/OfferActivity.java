package com.lightgraylabs.dailyrides;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import com.lightgraylabs.dailyrides.analytics.Analytics;
import com.lightgraylabs.dailyrides.api.ApiCallback;
import com.lightgraylabs.dailyrides.api.Callback;
import com.lightgraylabs.dailyrides.api.DingoApiService;
import com.lightgraylabs.dailyrides.api.Response;
import com.lightgraylabs.dailyrides.api.model.Address;
import com.lightgraylabs.dailyrides.api.model.RideEntity;
import com.lightgraylabs.dailyrides.api.model.RideOffer;

import java.util.Date;

import static com.lightgraylabs.dailyrides.util.ViewUtils.showOkDialog;

/**
 * Created by guestguest on 31/01/16.
 */
public class OfferActivity extends RideCreateActivity{

    public static final String EXTRA_OFFER = "EXTRA_OFFER";

    private static final String name = Analytics.SCREEN_CREATE_OFFER;

    private RideOffer mRideOffer = RideOffer.getWeekdaysCheckedInstance();
    private boolean mIsProcessing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle(R.string.activity_title_offer);
        if(isEditMode()) {
            mRideOffer = (RideOffer) getIntent().getSerializableExtra(EXTRA_OFFER);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Analytics.getInstance().screenViewed(name);
    }

    @Override
    public RideEntity getRideEntity() {
        return mRideOffer;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            if(requestCode == RESULT_LEAVING_ADDRESS) {
                Address address = (Address) data.getSerializableExtra("address");
                mRideOffer.setLeavingAddress(address);
            }
            else if(requestCode == RESULT_ARRIVING_ADDRESS){
                Address address = (Address) data.getSerializableExtra("address");
                mRideOffer.setArrivingAddress(address);
            }
            else if(requestCode == RESULT_TIME){
                Date time = (Date) data.getSerializableExtra("date");
                mRideOffer.setLeavingTime(time);
            }
        }

    }

    @Override
    void create() {

        if(!validateRide()){
            return;
        }

        if(mIsProcessing){
            return;
        }

        mIsProcessing = true;
        showCreateButtonSpin();

        Callback<RideOffer> callback = new ApiCallback<RideOffer>(this) {
            @Override
            public void success(Response<RideOffer> response) {
                if(response.code() == Response.HTTP_201_CREATED){
                    RideOffer offer = response.body();
                    if(offer.getRequests() == null || offer.getRequests().isEmpty()){
                        Intent intent = new Intent();
                        intent.putExtra("offer", offer);
                        setResult(RESULT_OK, intent);
                        finish();

                        String label;
                        if (mRecurrenceGroup.getCheckedRadioButtonId() == R.id.recurrence_yes) {
                            label = Analytics.LABEL_RECURRENT;
                        }
                        else{
                            label = Analytics.LABEL_ONCE;
                        }

                        Analytics.getInstance().event(Analytics.CATEGORY_CREATE_OFFER,
                                Analytics.ACTION_DID_CREATE,
                                label,
                                (long)offer.getId());
                    }
                    else{
                        super.success(response);
                    }
                }
            }

            @Override
            public void onFinish() {
                mIsProcessing = false;
                showCreateButtonText();
            }
        };

        if(mRecurrenceGroup.getCheckedRadioButtonId() == R.id.recurrence_yes){
            DingoApiService.getInstance().createRecurrentRideOffer(mRideOffer, callback);
        }
        else{
            DingoApiService.getInstance().createRideOffer(mRideOffer, callback);
        }

    }

    @Override
    void cancel() {
        int messageString;
        if(isRecurrent()){
            messageString = R.string.ride_offer_cancel_warning_recurrent;
        }
        else{
            messageString = R.string.ride_offer_cancel_warning;
        }
        new AlertDialog.Builder(OfferActivity.this)
                .setMessage(messageString)
                .setPositiveButton(R.string.yes,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(isRecurrent()){
                                    DingoApiService.getInstance().deleteRecurrentRideOffer(
                                            mRideOffer.getId(),
                                            new ApiCallback<Void>(OfferActivity.this) {
                                                @Override
                                                public void success(Response<Void> response) {
                                                    if(response.code() == Response.HTTP_204_NO_CONTENT){
                                                        Intent intent = new Intent();
                                                        intent.putExtra(RESULT_EXTRA_CANCELLED_FLAG, true);
                                                        intent.putExtra(RESULT_EXTRA_CANCELLED_ID, mRideOffer.getId());
                                                        setResult(RESULT_OK, intent);
                                                        finish();
                                                    }
                                                    else{
                                                        super.success(response);
                                                    }
                                                }


                                            }
                                    );
                                }
                                else{
                                    //todo
                                    //not recurrent
                                }

                            }
                        })
                .setNegativeButton(R.string.no, null)
                .show();
    }

    @Override
    void showEditNotAllowedDialog() {
        showOkDialog(OfferActivity.this, R.string.ride_offer_cant_edit);
    }


}

