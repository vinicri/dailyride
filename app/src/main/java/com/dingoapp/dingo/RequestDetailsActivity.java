package com.dingoapp.dingo;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dingoapp.dingo.api.AddressUtils;
import com.dingoapp.dingo.api.DingoApiService;
import com.dingoapp.dingo.api.model.RideMasterRequest;
import com.dingoapp.dingo.api.model.RideOffer;
import com.dingoapp.dingo.api.model.RideOfferSlave;
import com.dingoapp.dingo.api.model.User;
import com.dingoapp.dingo.chat.ChatActivity;
import com.dingoapp.dingo.util.CircleTransform;
import com.dingoapp.dingo.util.ViewUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by guestguest on 13/03/16.
 */
public class RequestDetailsActivity extends BaseActivity {

    public static final String EXTRA_REQUEST = "EXTRA_REQUEST";

    @Bind(R.id.chat_button) ImageView mChatButton;
    @Bind(R.id.driver_picture) ImageView mDriverPicture;

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
        setContentView(R.layout.activity_request_details);
        ButterKnife.bind(this);



        mRequest = (RideMasterRequest)getIntent().getSerializableExtra(EXTRA_REQUEST);


        if(mRequest.getInvitesAccepted().isEmpty()){
            //procurando caronas
            getSupportActionBar().setTitle(R.string.activity_request_details_looking_for_driver);

        }
        else{
            getSupportActionBar().setTitle(R.string.activity_request_details_accepted);

            final RideOffer offer = mRequest.getInvitesAccepted().get(0).getMaster();

            mPresentation.setText(getString(R.string.request_details_will_give_you_a_ride, offer.getUser().getFirstName()));

            Glide.with(this).load(DingoApiService.getPhotoUrl(offer.getUser()))
                    .bitmapTransform(new CircleTransform(this))
                    .into(mDriverPicture);

            mDriverPicture.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ViewUtils.showProfileActivity(RequestDetailsActivity.this, offer.getUser());
                        }
                    }
            );

            mDriverName.setText(offer.getUser().getFullName());

            mMonthDay.setText(getString(R.string.request_invite_confirm_month_day,
                    mMonthDayFormat.format(offer.getLeavingTime()),
                    mMonthNameFormat.format(offer.getLeavingTime())));

            mWeekday.setText(mWeekdayFormat.format(offer.getLeavingTime()));

            int estimatedPickupTime = mRequest.getInvitesAccepted().get(0).getEstimatedPickupTime();
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

        }

        mChatButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!mRequest.getInvitesAccepted().isEmpty()) {
                            RideOffer offer = mRequest.getInvitesAccepted().get(0).getMaster();
                            Intent intent = new Intent(RequestDetailsActivity.this, ChatActivity.class);
                            intent.putExtra(ChatActivity.EXTRA_OFFER_ID, offer.getId());
                            ArrayList<User> acceptedUsers = new ArrayList<User>();
                            for(RideOfferSlave invite: mRequest.getInvitesOthers()){
                                acceptedUsers.add(invite.getToRideRequest().getUser());
                            }
                            //add driver
                            acceptedUsers.add(mRequest.getInvitesAccepted().get(0).getMaster().getUser());
                            intent.putExtra(ChatActivity.EXTRA_USERS, acceptedUsers);
                            startActivity(intent);
                        }
                    }
                }
        );

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_ride_details, menu);
        return true;
    }
}
