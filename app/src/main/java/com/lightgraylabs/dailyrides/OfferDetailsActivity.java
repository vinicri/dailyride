package com.lightgraylabs.dailyrides;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.lightgraylabs.dailyrides.api.DingoApiService;
import com.lightgraylabs.dailyrides.api.model.RideMasterRequest;
import com.lightgraylabs.dailyrides.api.model.RideOffer;
import com.lightgraylabs.dailyrides.api.model.RideOfferSlave;
import com.lightgraylabs.dailyrides.api.model.User;
import com.lightgraylabs.dailyrides.chat.ChatActivity;
import com.lightgraylabs.dailyrides.google.maps.api.directions.model.DirectionsResponse;
import com.lightgraylabs.dailyrides.util.CircleTransform;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by guestguest on 09/03/16.
 */
public class OfferDetailsActivity extends BaseMapActivity implements OnMapReadyCallback, GoogleMap.OnMapLoadedCallback {

    public static final String EXTRA_OFFER = "EXTRA_OFFER";

    @Bind(R.id.ride_date)TextView mRideDateTime;

    @Bind(R.id.invites_box)View mInvitesBox;
    @Bind(R.id.invites_accepted)View mInvitesAccepted;
    @Bind(R.id.no_invites_accepted)View mInvitesAcceptedNone;
    @Bind(R.id.no_invites_accepted_text)TextView mNoInvitesAcceptedText;
    @Bind(R.id.invites_waiting_confirmation)View mInvitesWaitingConfirmation;
    @Bind(R.id.no_invites_waiting_confirmation)View mInvitesWaitingConfirmationNone;
    @Bind(R.id.finding_riders)View mFindindRiders;

    @Bind(R.id.accepted_picture1)ImageView mAcceptedPicture1;
    @Bind(R.id.accepted_picture2)ImageView mAcceptedPicture2;
    @Bind(R.id.accepted_text)TextView mAcceptedText;
    @Bind(R.id.chat_button)ImageView mChatButton;

    @Bind(R.id.to_confirm_picture1)ImageView mToConfirmPicture1;
    @Bind(R.id.to_confirm_picture2)ImageView mToConfirmPicture2;
    @Bind(R.id.to_confirm_text)TextView mToConfirmText;

    @Bind(R.id.start)Button mStartButton;

    LatLng PE_ANTONIO = new LatLng(-23.605671, -46.692275);
    LatLng OLIMPIADAS = new LatLng(-23.595331, -46.687868);
    LatLng PAULISTA = new LatLng(-23.568586, -46.647527);
    LatLng ROD = new LatLng(-23.515551, -46.624975);
    private GoogleMap mMap;
    private RideOffer mOffer;
    private boolean mMapDidLoad;

    List<RideMasterRequest> mAcceptedRequests;
    private ImageView[] mAcceptedPictures;
    private ImageView[] mToConfirmPictures;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_details);
        getSupportActionBar().setTitle(R.string.activity_offer_details);
        ButterKnife.bind(this);

        mAcceptedPictures = new ImageView[] {mAcceptedPicture1, mAcceptedPicture2};
        mToConfirmPictures = new ImageView[] {mToConfirmPicture1, mToConfirmPicture2};


        mOffer = (RideOffer)getIntent().getSerializableExtra(EXTRA_OFFER);
        mAcceptedRequests = new ArrayList<>();
        for(RideOfferSlave invite: mOffer.getInvitesAccepted()){
            mAcceptedRequests.add(invite.getToRideRequest());
        }

        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

        mRideDateTime.setText(getString(R.string.offer_details_ride_date_time,
                dayFormat.format(mOffer.getLeavingTime()),
                timeFormat.format(mOffer.getLeavingTime())));

        /*MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);*/

        mChatButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(OfferDetailsActivity.this, ChatActivity.class);
                        intent.putExtra(ChatActivity.EXTRA_OFFER_ID, mOffer.getId());
                        ArrayList<User> acceptedUsers = new ArrayList<User>();
                        for(RideOfferSlave invite: mOffer.getInvitesAccepted()){
                            acceptedUsers.add(invite.getToRideRequest().getUser());
                        }
                        intent.putExtra(ChatActivity.EXTRA_USERS, acceptedUsers);
                        startActivity(intent);
                    }
                }
        );

        mStartButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(OfferDetailsActivity.this, "O horário da carona ainda não está próximo.", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }


    private void showInvites(List<RideMasterRequest> orderedAcceptedRequests){

        if(mOffer.getInvitesAccepted().isEmpty() &&
                mOffer.getInvitesWaitingConfirmation().isEmpty() &&
                mOffer.getInvitesRefused().isEmpty()){
            //nothing had happened in this offer yet
            mInvitesBox.setVisibility(View.GONE);
            mFindindRiders.setVisibility(View.VISIBLE);
            mStartButton.setVisibility(View.GONE);
        }
        else{
            mInvitesBox.setVisibility(View.VISIBLE);
            mFindindRiders.setVisibility(View.GONE);
            mStartButton.setVisibility(View.VISIBLE);

            if(!mOffer.getInvitesAccepted().isEmpty()){
                mInvitesAccepted.setVisibility(View.VISIBLE);
                mInvitesAcceptedNone.setVisibility(View.GONE);
                showUsersForRequests(orderedAcceptedRequests, mAcceptedPictures, mAcceptedText, R.plurals.offer_details_go_with_you);
            }
            else{
                //make it invisible so layout won't mess #needs-fix
                mInvitesAccepted.setVisibility(View.INVISIBLE);
                mInvitesAcceptedNone.setVisibility(View.VISIBLE);

                if(mOffer.getInvitesWaitingConfirmation().isEmpty()){
                    mNoInvitesAcceptedText.setText(getString(R.string.offer_details_no_invites_accepted));
                }
                else{
                    mNoInvitesAcceptedText.setText(getString(R.string.offer_details_waiting_acceptance));
                }
            }

            //waiting confirmation box
            if(!mOffer.getInvitesWaitingConfirmation().isEmpty()){

                mInvitesWaitingConfirmation.setVisibility(View.VISIBLE);
                mInvitesWaitingConfirmationNone.setVisibility(View.GONE);

                List<RideMasterRequest> requestsToConfirm = new ArrayList<>();
                for(RideOfferSlave invite: mOffer.getInvitesWaitingConfirmation()){
                    requestsToConfirm.add(invite.getToRideRequest());
                }
                showUsersForRequests(requestsToConfirm, mToConfirmPictures, mToConfirmText, R.plurals.offer_details_needs_to_confirm);
            }
            else{
                //make it invisible so layout won't mess #needs-fix
                mInvitesWaitingConfirmation.setVisibility(View.INVISIBLE);
                mInvitesWaitingConfirmationNone.setVisibility(View.VISIBLE);
            }
        }
    }


    private void showUsersForRequests(List<RideMasterRequest> requests, ImageView[] imageViews, TextView namesTextView, int pluralForNames){
        String names = null;
        for(int i = 0; i < imageViews.length; i++){
            imageViews[i].setVisibility(View.GONE);
        }
        for(int i = 0; i < requests.size() && i < imageViews.length; i++){
            RideMasterRequest request = requests.get(i);

            imageViews[i].setVisibility(View.VISIBLE);
            Glide.with(this).load(DingoApiService.getPhotoUrl(request.getUser()))
                    .bitmapTransform(new CircleTransform(this))
                    .into(imageViews[i]);

            if(i == 0){
                names = request.getUser().getFirstName();
            }
            else if(i == requests.size() - 1 || i == imageViews.length - 1){
                //last user in list
                names += " " + getString(R.string.offer_details_and_conjuction) + " " + request.getUser().getFirstName();
            }
            else{
                names += ", " + request.getUser().getFirstName();
            }
        }

        String acceptedText = names + "\n" + getResources().getQuantityString(pluralForNames, requests.size());

        namesTextView.setText(acceptedText);

    }

    @Override
    protected RideOffer getOffer() {
        return mOffer;
    }

    @Override
    protected List<RideMasterRequest> getRequests() {
        return mAcceptedRequests;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_ride_details, menu);
        return true;
    }

    @Override
    protected void gotDirections(DirectionsResponse directions, List<RideMasterRequest> orderedRequests) {
        showInvites(orderedRequests);
    }

    @Override
    protected void directionsFailed(List<RideMasterRequest> orderedRequests) {
        showInvites(orderedRequests);
    }
}
