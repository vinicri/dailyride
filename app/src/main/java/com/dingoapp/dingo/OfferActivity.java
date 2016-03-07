package com.dingoapp.dingo;

import android.content.Intent;
import android.os.Bundle;

import com.dingoapp.dingo.api.Callback;
import com.dingoapp.dingo.api.DingoApiService;
import com.dingoapp.dingo.api.Response;
import com.dingoapp.dingo.api.model.Address;
import com.dingoapp.dingo.api.model.RideEntity;
import com.dingoapp.dingo.api.model.RideOffer;

import java.util.Date;

/**
 * Created by guestguest on 31/01/16.
 */
public class OfferActivity extends RideCreateActivity{

    private RideOffer mRideOffer = RideOffer.getWeekdaysCheckedInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle(R.string.activity_title_offer);
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

    private void validateOffer(){

    }

    @Override
    void create() {
        //FIXME
        mRideOffer.setLeavingTime(new Date());

        Callback<RideOffer> callback = new Callback<RideOffer>() {
            @Override
            public void onResponse(Response<RideOffer> response) {
                if(response.code() == Response.HTTP_201_CREATED){
                    RideOffer offer = response.body();
                    if(offer.getRequests() == null || offer.getRequests().isEmpty()){
                        Intent intent = new Intent();
                        intent.putExtra("offer", offer);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                    else{
                        //todo
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {

            }
        };

        if(mRecurrenceGroup.getCheckedRadioButtonId() == R.id.recurrence_yes){
            DingoApiService.getInstance().createRecurrentRideOffer(mRideOffer, callback);
        }
        else{
            DingoApiService.getInstance().createRideOffer(mRideOffer, callback);
        }

    }

}
