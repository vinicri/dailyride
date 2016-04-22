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
import com.lightgraylabs.dailyrides.api.model.RideMasterRequest;

import java.util.Date;

import static com.lightgraylabs.dailyrides.util.ViewUtils.showOkDialog;

/**
 * Created by guestguest on 13/02/16.
 */
public class RequestActivity extends RideCreateActivity {

    public static final String EXTRA_REQUEST = "EXTRA_REQUEST";

    private static final String name = Analytics.SCREEN_CREATE_REQUEST;
    RideMasterRequest mRequest = RideMasterRequest.getWeekdaysCheckedInstance();
    private boolean mIsProcessing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle(R.string.activity_title_request);
        getCreateButtonText().setText(R.string.activity_ride_create_request);
        if(isEditMode()) {
            mRequest = (RideMasterRequest) getIntent().getSerializableExtra(EXTRA_REQUEST);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Analytics.getInstance().screenViewed(name);
    }

    @Override
    public RideEntity getRideEntity() {
        return mRequest;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            if(requestCode == RESULT_LEAVING_ADDRESS) {
                Address address = (Address) data.getSerializableExtra("address");
                mRequest.setLeavingAddress(address);
            }
            else if(requestCode == RESULT_ARRIVING_ADDRESS){
                Address address = (Address) data.getSerializableExtra("address");
                mRequest.setArrivingAddress(address);
            }
            else if(requestCode == RESULT_TIME){
                Date time = (Date) data.getSerializableExtra("date");
                mRequest.setLeavingTime(time);
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

        Callback<RideMasterRequest> callback = new ApiCallback<RideMasterRequest>(this){

            @Override
            public void success(Response<RideMasterRequest> response) {
                if (response.code() == Response.HTTP_201_CREATED) {
                    RideMasterRequest request = response.body();
                    Intent intent = new Intent();
                    intent.putExtra("request", request);
                    setResult(RESULT_OK, intent);
                    finish();

                    String label;
                    if (mRecurrenceGroup.getCheckedRadioButtonId() == R.id.recurrence_yes) {
                        label = Analytics.LABEL_RECURRENT;
                    }
                    else{
                        label = Analytics.LABEL_ONCE;
                    }
                    Analytics.getInstance().event(Analytics.CATEGORY_CREATE_REQUEST,
                            Analytics.ACTION_DID_CREATE,
                            label,
                            (long) request.getId());
                }
                else{
                    super.success(response);
                }
            }

            @Override
            public void onFinish() {
                mIsProcessing = false;
                showCreateButtonText();
            }
        };

        if (mRecurrenceGroup.getCheckedRadioButtonId() == R.id.recurrence_yes) {
            DingoApiService.getInstance().createRideMasterRequestRecurrent(mRequest, callback);
        } else {
            DingoApiService.getInstance().createRideMasterRequest(mRequest, callback);
        }


    }

    @Override
    void cancel() {
        int messageString;
        if(isRecurrent()){
            messageString = R.string.ride_request_cancel_warning_recurrent;
        }
        else{
            messageString = R.string.ride_request_cancel_warning;
        }
        new AlertDialog.Builder(RequestActivity.this)
                .setMessage(messageString)
                .setPositiveButton(R.string.yes,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(isRecurrent()){
                                    DingoApiService.getInstance().deleteRideMasterRequestRecurrent(
                                            mRequest.getId(),
                                            new ApiCallback<Void>(RequestActivity.this) {
                                                @Override
                                                public void success(Response<Void> response) {
                                                    if(response.code() == Response.HTTP_204_NO_CONTENT){
                                                        Intent intent = new Intent();
                                                        intent.putExtra(RESULT_EXTRA_CANCELLED_FLAG, true);
                                                        intent.putExtra(RESULT_EXTRA_CANCELLED_ID, mRequest.getId());
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
        showOkDialog(RequestActivity.this, R.string.ride_request_cant_edit);
    }
}
