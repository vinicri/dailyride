package com.lightgraylabs.dailyrides;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lightgraylabs.dailyrides.addworkemail.AddWorkEmailActivity;
import com.lightgraylabs.dailyrides.api.DingoApiService;
import com.lightgraylabs.dailyrides.api.model.User;
import com.lightgraylabs.dailyrides.util.CircleTransform;
import com.lightgraylabs.dailyrides.util.CurrentUser;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by guestguest on 28/03/16.
 */
public class OwnProfileActivity extends BaseActivity {

    private static final int REQUEST_CODE_ADD_PHONE = 1;
    private static final int REQUEST_CODE_ADD_WORK = 2;

    @Bind(R.id.back_button)ImageView mBackButton;
    @Bind(R.id.picture)ImageView mPicture;
    @Bind(R.id.name)TextView mName;

    @Bind(R.id.fb_box)View mFbBox;
    @Bind(R.id.fb_text)TextView mFbText;
    @Bind(R.id.fb_check)ImageView mFbCheck;

    @Bind(R.id.school_box)View mSchoolBox;
    @Bind(R.id.school_text)TextView mSchoolText;
    @Bind(R.id.school_check)ImageView mSchoolCheck;
    @Bind(R.id.school_alert)TextView mSchoolAlert;

    @Bind(R.id.work_box)View mWorkBox;
    @Bind(R.id.work_text)TextView mWorkText;
    @Bind(R.id.work_check)ImageView mWorkCheck;
    @Bind(R.id.work_alert)TextView mWorkAlert;

    @Bind(R.id.phone_box)View mPhoneBox;
    @Bind(R.id.phone_text)TextView mPhoneText;
    @Bind(R.id.phone_check)ImageView mPhoneCheck;
    @Bind(R.id.phone_alert)TextView mPhoneAlert;

    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_own);
        ButterKnife.bind(this);

        mUser = CurrentUser.getUser();

        displayData();

        mBackButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        OwnProfileActivity.this.onBackPressed();
                    }
                }
        );
    }

    private void displayData(){

        Glide.with(this).load(DingoApiService.getPhotoUrl(mUser))
                .transform(new CircleTransform(this))
                .into(mPicture);

        mName.setText(mUser.getFullName());

        if(mUser.getFbUserId() == null){
            //todo: add connect your facebook account
            mFbBox.setVisibility(View.GONE);
        }
        else{
            mFbBox.setVisibility(View.VISIBLE);
            mFbText.setText(getResources().getQuantityString(R.plurals.profile_fb_friends, mUser.getFbTotalFriends()));
        }

        if(mUser.getCompany() == null && mUser.getWorkSpecifiedName() == null){
            mWorkText.setText(R.string.own_profile_add_work);
            mWorkText.setTextColor(ContextCompat.getColor(this, R.color.gray));
            mWorkCheck.setVisibility(View.GONE);
            mWorkAlert.setVisibility(View.GONE);
            mWorkBox.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(OwnProfileActivity.this, AddWorkEmailActivity.class);
                            intent.putExtra(AddPhoneActivity.EXTRA_USER, mUser);
                            startActivityForResult(intent, REQUEST_CODE_ADD_WORK);
                        }
                    }
            );
        }
        else{
            String name = mUser.getCompany() != null ? mUser.getCompany().getShortName() : mUser.getWorkSpecifiedName();
            mWorkText.setText(name);
            mWorkText.setTextColor(ContextCompat.getColor(this, R.color.gray_dark));
            mWorkCheck.setVisibility(View.VISIBLE);
            if(mUser.isWorkConfirmed()){
                mWorkCheck.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.check_24px));
                mWorkAlert.setVisibility(View.GONE);
            }
            else{
                mWorkCheck.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.alert_orange_24px));
                mWorkAlert.setVisibility(View.VISIBLE);
            }
            //todo review duplicated
            mWorkBox.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(OwnProfileActivity.this, AddWorkEmailActivity.class);
                            intent.putExtra(AddPhoneActivity.EXTRA_USER, mUser);
                            startActivityForResult(intent, REQUEST_CODE_ADD_WORK);
                        }
                    }
            );
        }

        if(mUser.getSchool() == null){
            mSchoolText.setText(R.string.own_profile_add_school);
            mSchoolText.setTextColor(ContextCompat.getColor(this, R.color.gray));
            mSchoolCheck.setVisibility(View.GONE);
            mSchoolAlert.setVisibility(View.GONE);
        }
        else{
            mSchoolText.setText(mUser.getSchool().getShortName());
            if(mUser.isSchoolConfirmed()){
                mSchoolCheck.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.check_24px));
                mSchoolAlert.setVisibility(View.GONE);
            }
            else{
                mSchoolCheck.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.alert_orange_24px));
                mSchoolAlert.setVisibility(View.VISIBLE);
            }
        }

        if(mUser.getPhone() == null){
            mPhoneText.setText(R.string.own_profile_add_phone);
            mPhoneAlert.setVisibility(View.GONE);
            mPhoneText.setTextColor(ContextCompat.getColor(this, R.color.gray));
            mPhoneBox.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(OwnProfileActivity.this, AddPhoneActivity.class);
                            intent.putExtra(AddPhoneActivity.EXTRA_USER, mUser);
                            startActivityForResult(intent, REQUEST_CODE_ADD_PHONE);
                        }
                    }
            );
        }
        else{
            mPhoneText.setText(mUser.getPhone());
            if(mUser.isPhoneConfirmed()){
                mPhoneCheck.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.check_24px));
                mPhoneAlert.setVisibility(View.GONE);
            }
            else{
                mPhoneCheck.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.alert_orange_24px));
                mPhoneAlert.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == REQUEST_CODE_ADD_WORK){
                mUser = (User)data.getSerializableExtra(AddWorkEmailActivity.EXTRA_USER);
                displayData();
            }
        }
    }
}
