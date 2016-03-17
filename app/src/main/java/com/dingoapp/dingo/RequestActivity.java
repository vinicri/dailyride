package com.dingoapp.dingo;

import android.content.Intent;
import android.os.Bundle;

import com.dingoapp.dingo.api.Callback;
import com.dingoapp.dingo.api.DingoApiService;
import com.dingoapp.dingo.api.Response;
import com.dingoapp.dingo.api.model.Address;
import com.dingoapp.dingo.api.model.RideEntity;
import com.dingoapp.dingo.api.model.RideMasterRequest;

import java.util.Date;

/**
 * Created by guestguest on 13/02/16.
 */
public class RequestActivity extends RideCreateActivity {

    RideMasterRequest mRequest = RideMasterRequest.getWeekdaysCheckedInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle(R.string.activity_title_request);
        getCreateButton().setText(R.string.activity_ride_create_request);
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

            Callback<RideMasterRequest> callback = new Callback<RideMasterRequest>() {
                @Override
                public void onResponse(Response<RideMasterRequest> response) {
                    if (response.code() == Response.HTTP_201_CREATED) {
                        RideMasterRequest request = response.body();
                        Intent intent = new Intent();
                        intent.putExtra("request", request);
                        setResult(RESULT_OK, intent);
                        finish();
//                        if (response.body() == null || response.body().size() == 0) {
//                            finish();
//                        } else {
//                            //ArrayList<RideMasterRequest> requests = new ArrayList<>(response.body());
//                            //Intent intent = new Intent(OfferActivity.this, null);
//                            //intent.putExtra("requests", requests);
//                            //startActivity(intent);
//                        }
                    }
                }

                @Override
                public void onFailure(Throwable t) {

                }
            };

            if (mRecurrenceGroup.getCheckedRadioButtonId() == R.id.recurrence_yes) {
                DingoApiService.getInstance().createRideMasterRequestRecurrent(mRequest, callback);
            } else {
                DingoApiService.getInstance().createRideMasterRequest(mRequest, callback);
            }


    }


}
