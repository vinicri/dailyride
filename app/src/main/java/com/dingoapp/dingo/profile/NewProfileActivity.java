package com.dingoapp.dingo.profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dingoapp.dingo.AddPhoneActivity;
import com.dingoapp.dingo.MainActivity;
import com.dingoapp.dingo.R;
import com.dingoapp.dingo.addworkemail.AddSchoolEmailActivity;
import com.dingoapp.dingo.addworkemail.AddWorkEmailActivity;
import com.dingoapp.dingo.api.ApiCallback;
import com.dingoapp.dingo.api.DingoApiService;
import com.dingoapp.dingo.api.Response;
import com.dingoapp.dingo.api.model.RideRating;
import com.dingoapp.dingo.api.model.User;
import com.dingoapp.dingo.util.CircleTransform;
import com.dingoapp.dingo.util.CurrentUser;
import com.dingoapp.dingo.util.SettingsUtil;
import com.rollbar.android.Rollbar;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by guestguest on 08/04/16.
 */

public class NewProfileActivity extends AppCompatActivity
        implements AppBarLayout.OnOffsetChangedListener {

//    private static final float PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR  = 0.9f;
//    private static final float PERCENTAGE_TO_HIDE_TITLE_DETAILS     = 0.3f;
//    private static final int ALPHA_ANIMATIONS_DURATION              = 200;

//    private boolean mIsTheTitleVisible          = false;
//    private boolean mIsTheTitleContainerVisible = true;

    public static final String EXTRA_USER = "EXTRA_USER";
    public static final String EXTRA_CURRENT_USER_MODE = "EXTRA_CURRENT_USER_MODE";

    private static final int REQUEST_CODE_ADD_PHONE = 1;
    private static final int REQUEST_CODE_ADD_WORK = 2;
    private static final int REQUEST_CODE_ADD_SCHOOL = 3;

    private LinearLayout mTitleContainer;
    private TextView mTitle;
    private AppBarLayout mAppBarLayout;
    private Toolbar mToolbar;
    private Toolbar mActionBarToolbar;

    @Bind(R.id.picture)ImageView mPicture;
    @Bind(R.id.recycler_view)RecyclerView mRecyclerView;

    @Bind(R.id.ratings_box)View mRatingsView;
    @Bind(R.id.ratings_text)TextView mRatingsText;
    private User mUser;
    private ProfileAdapter mAdapter;
    private boolean mCurrentUserMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_new);
        ButterKnife.bind(this);

        mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mActionBarToolbar != null) {
            setSupportActionBar(mActionBarToolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mCurrentUserMode = getIntent().getBooleanExtra(EXTRA_CURRENT_USER_MODE, false);
        if(mCurrentUserMode) {
            mUser = CurrentUser.getUser();
        }
        else{
            mUser = (User)getIntent().getSerializableExtra(EXTRA_USER);
        }

        Glide.with(this).load(DingoApiService.getPhotoUrl(mUser))
                .bitmapTransform(new CircleTransform(this))
                .into(mPicture);

        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(mUser.getFullName());

        if(mUser.getStarGrade() > 0) {
            mRatingsText.setText(new DecimalFormat("#.0").format(mUser.getStarGrade()));
        }
        else{
            mRatingsView.setVisibility(View.GONE);
        }

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(llm);

        mAdapter = new ProfileAdapter(this, mUser, mCurrentUserMode);

        mAdapter.setEditListener(
                new ProfileAdapter.EditListener() {
                    @Override
                    public void onSchoolAdd() {
                        Intent intent = new Intent(NewProfileActivity.this, AddSchoolEmailActivity.class);
                        intent.putExtra(AddSchoolEmailActivity.EXTRA_USER, mUser);
                        startActivityForResult(intent, REQUEST_CODE_ADD_SCHOOL);
                    }

                    @Override
                    public void onWorkAdd() {
                        Intent intent = new Intent(NewProfileActivity.this, AddWorkEmailActivity.class);
                        intent.putExtra(AddWorkEmailActivity.EXTRA_USER, mUser);
                        startActivityForResult(intent, REQUEST_CODE_ADD_WORK);
                    }

                    @Override
                    public void onPhoneAdd() {
                        Intent intent = new Intent(NewProfileActivity.this, AddPhoneActivity.class);
                        intent.putExtra(AddPhoneActivity.EXTRA_USER, mUser);
                        startActivityForResult(intent, REQUEST_CODE_ADD_PHONE);

                    }
                }
        );

        mRecyclerView.setAdapter(mAdapter);


        DingoApiService.getInstance().userGetComments(mUser.getId(),
                new ApiCallback<List<RideRating>>(NewProfileActivity.this) {
                    @Override
                    public void success(Response<List<RideRating>> response) {
                        if (response.code() == Response.HTTP_204_NO_CONTENT) {
                            mAdapter.setRideRatings(new ArrayList<RideRating>());
                        } else if (response.code() == Response.HTTP_200_OK) {
                            mAdapter.setRideRatings(response.body());
                        } else {
                            super.success(response);
                            mAdapter.setRideRatings(new ArrayList<RideRating>());
                        }
                    }

                    @Override
                    public void serverError(Response<?> response) {
                        //ignore server error and show no comments
                        mAdapter.setRideRatings(new ArrayList<RideRating>());

                        Map<String, String> data = new HashMap<>();
                        data.put("http_code", String.valueOf(response.code()));
                        data.put("body", response.error());
                        data.put("user", String.valueOf(CurrentUser.getUser().getId()));
                        //todo add endpoint
                        Rollbar.reportMessage("error_server", "", data);
                    }
                });



        //collapsingToolbar

        //mAppBarLayout.addOnOffsetChangedListener(this);

        //mToolbar.inflateMenu(R.menu.menu_main);
        //startAlphaAnimation(mTitle, 0, View.INVISIBLE);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
            case R.id.logoff:
                SettingsUtil.setCurrentUser(this, null);
                SettingsUtil.setFirebaseToken(this, null);
                SettingsUtil.setSentTokenToServer(this, false);
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int offset) {
        /*int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(offset) / (float) maxScroll;

        handleAlphaOnTitle(percentage);
        handleToolbarTitleVisibility(percentage);*/
    }

    private void handleToolbarTitleVisibility(float percentage) {
       /* if (percentage >= PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR) {

            if(!mIsTheTitleVisible) {
                startAlphaAnimation(mTitle, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheTitleVisible = true;
            }

        } else {

            if (mIsTheTitleVisible) {
                startAlphaAnimation(mTitle, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                mIsTheTitleVisible = false;
            }
        }*/
    }

    /*private void handleAlphaOnTitle(float percentage) {
        if (percentage >= PERCENTAGE_TO_HIDE_TITLE_DETAILS) {
            if(mIsTheTitleContainerVisible) {
                startAlphaAnimation(mTitleContainer, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                mIsTheTitleContainerVisible = false;
            }

        } else {

            if (!mIsTheTitleContainerVisible) {
                startAlphaAnimation(mTitleContainer, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheTitleContainerVisible = true;
            }
        }
    }

    public static void startAlphaAnimation (View v, long duration, int visibility) {
        AlphaAnimation alphaAnimation = (visibility == View.VISIBLE)
                ? new AlphaAnimation(0f, 1f)
                : new AlphaAnimation(1f, 0f);

        alphaAnimation.setDuration(duration);
        alphaAnimation.setFillAfter(true);
        v.startAnimation(alphaAnimation);
    }*/


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == REQUEST_CODE_ADD_WORK || requestCode == REQUEST_CODE_ADD_SCHOOL){
                mUser = (User)data.getSerializableExtra(AddWorkEmailActivity.EXTRA_USER);
                CurrentUser.getInstance().setUser(mUser);
                mAdapter.setUser(mUser);
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(mCurrentUserMode) {
            getMenuInflater().inflate(R.menu.own_profile_menu, menu);
            return true;
        }
        else{
            return super.onCreateOptionsMenu(menu);
        }
    }


}
