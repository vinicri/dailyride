package com.dingoapp.dingo;

import android.content.Intent;

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
    public RideEntity getRecurrentEntity() {
        return mRequest;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            if(requestCode == RESULT_LEAVING_ADDRESS) {
                Address address = (Address) data.getSerializableExtra("address");
                mRequest.setLeavingAddress(address);
                //populateAddressBox(address, mLeavingAddressLine1, mLeavingAddressLine2);
            }
            else if(requestCode == RESULT_ARRIVING_ADDRESS){
                Address address = (Address) data.getSerializableExtra("address");
                mRequest.setArrivingAddress(address);
                //populateAddressBox(address, mArrivingAddressLine1, mArrivingAddressLine2);
            }
            else if(requestCode == RESULT_TIME){
                Date time = (Date) data.getSerializableExtra("date");
                mRequest.setLeavingTime(time);
                //mTimeText.setText(sdf.format(time));
            }
        }

    }

    @Override
    void create() {

           //FIXME
            mRequest.setLeavingTime(new Date());

            Callback<RideMasterRequest> callback = new Callback<RideMasterRequest>() {
                @Override
                public void onResponse(Response<RideMasterRequest> response) {
                    if (response.code() == Response.HTTP_201_CREATED) {
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
