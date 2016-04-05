package com.dingoapp.dingo.welcome;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.dingoapp.dingo.R;
import com.dingoapp.dingo.api.Callback;
import com.dingoapp.dingo.api.DingoApiService;
import com.dingoapp.dingo.api.Response;
import com.dingoapp.dingo.api.model.User;
import com.dingoapp.dingo.rides.RidesActivity;
import com.dingoapp.dingo.util.CurrentUser;
import com.dingoapp.dingo.view.NonSwipebleViewPager;
import com.viewpagerindicator.CirclePageIndicator;

/**
 * Created by guestguest on 30/01/16.
 */
public class WelcomeActivity extends AppCompatActivity{


    private static final String TAG = "WelcomeActivity";
    private LinearLayout mAcceptTermsBox;
    private Button mAcceptedButton;

    private View mRiderModeBox;
    private Button mYesButton;
    private Button mNoButton;

    private boolean mDidAcceptTerms;
    private User.RiderMode mSelectedRiderMode;
    private NonSwipebleViewPager mViewPager;
    private CirclePageIndicator mPagerIndicator;

    private int previousIdlePagerPosition = 0;
    private IntroPagerAdapter mPagerAdapter;
    private boolean mEnterEnabled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0, 0);
        setContentView(R.layout.activity_welcome);

       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.gray_dark));
        }
*/

        mAcceptTermsBox = (LinearLayout)findViewById(R.id.accept_terms_box);
        mAcceptedButton = (Button)findViewById(R.id.accepted);

        mRiderModeBox = findViewById(R.id.rider_mode_box);
        mYesButton = (Button)findViewById(R.id.yes);
        mNoButton = (Button)findViewById(R.id.no);

        mViewPager = (NonSwipebleViewPager)findViewById(R.id.pager);
        mViewPager.setAllowSwipe(false);
        mPagerAdapter = new IntroPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);
        mPagerIndicator = (CirclePageIndicator)findViewById(R.id.pager_indicator);
        mPagerIndicator.setViewPager(mViewPager);
        mPagerIndicator.setOnPageChangeListener(
                new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                        Log.d(TAG, String.format("onPageScrolled %d", position));
                        if(position != mPagerAdapter.getCount() - 1){
                            if(mEnterEnabled) {
                                mAcceptedButton.setText(getString(R.string.welcome_next));
                                mEnterEnabled = false;
                            }
                        }
                    }

                    @Override
                    public void onPageSelected(int position) {
                        Log.d(TAG, "onPageSelected");

                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {
                        Log.d(TAG, String.format("onPageScrollStateChanged %d", state));
                        if (state == ViewPager.SCROLL_STATE_IDLE) {
                            int currentPosition = mViewPager.getCurrentItem();
                            if(currentPosition == 0 &&
                                    previousIdlePagerPosition != currentPosition){
                                hideAndShowView(mAcceptTermsBox, mRiderModeBox);
                                mViewPager.setAllowSwipe(false);
                            }
                            else if(currentPosition == mPagerAdapter.getCount() - 1){
                                mAcceptedButton.setText(getString(R.string.welcome_enter));
                                mEnterEnabled = true;
                            }
                            previousIdlePagerPosition = currentPosition;
                        }

                    }
                }
        );

        mAcceptedButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mEnterEnabled){
                            mDidAcceptTerms = true;
                            //sendToServer();
                        }
                        else{
                            mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
                        }

                       //
                       // mRiderModeBox.setVisibility(View.VISIBLE);
                    }
                }
        );

        //rider mode selected
        mYesButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mSelectedRiderMode = User.RiderMode.D;
                        hideAndShowView(mRiderModeBox, mAcceptTermsBox);
                        mViewPager.setAllowSwipe(true);
                        mViewPager.setCurrentItem(1);
                    }
                }
        );

        mNoButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mSelectedRiderMode = User.RiderMode.R;
                        hideAndShowView(mRiderModeBox, mAcceptTermsBox);
                        mViewPager.setAllowSwipe(true);
                        mViewPager.setCurrentItem(1);
                    }
                }
        );
    }

    private void hideAndShowView(final View toHide, final View toShow){
        ValueAnimator hideAnimator = ValueAnimator.ofFloat(1, 0);
        ValueAnimator showAnimator = ValueAnimator.ofFloat(0, 1);

        hideAnimator.setDuration(150);
        showAnimator.setDuration(150);

        hideAnimator.addUpdateListener(
                new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float value = (Float) animation.getAnimatedValue();
                        toHide.setAlpha(value);
                    }
                }
        );

        showAnimator.addUpdateListener(
                new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float value = (Float) animation.getAnimatedValue();
                        toShow.setAlpha(value);
                    }
                }
        );

        hideAnimator.addListener(
                new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        toHide.setVisibility(View.GONE);
                    }
                }
        );

        showAnimator.addListener(
                new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                        toShow.setVisibility(View.VISIBLE);
                        toShow.setAlpha(0);
                    }
                }
        );

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(hideAnimator).before(showAnimator);
        animatorSet.start();

    }

    private void sendToServer() {
        DingoApiService.getInstance().acceptTerms(mSelectedRiderMode,
                new Callback() {
                    @Override
                    public void onResponse(Response response) {
                        if(response.code() == Response.HTTP_200_OK){
                            CurrentUser.getInstance().setRiderMode(mSelectedRiderMode);
                            Intent intent = new Intent(WelcomeActivity.this, RidesActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {

                    }
                });

    }
}
