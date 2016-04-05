package com.dingoapp.dingo.welcome;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dingoapp.dingo.R;

/**
 * Created by guestguest on 04/04/16.
 */
public class IntroPagerAdapter extends FragmentPagerAdapter {

    private static final int NUM_ITEMS = 3;

    public IntroPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new Intro0Fragment();
            case 1:
                return new Intro1RiderFragment();
            case 2:
                return new Intro2RiderFragment();
            default:
                return new Intro0Fragment();
        }
    }

    @Override
    public int getCount() {
        return NUM_ITEMS;
    }

    public static class Intro0Fragment extends Fragment{
        private Typeface mTypeface;
        private TextView mHowItWorksText;
        private TextView mToStartText;
        private TextView mIfDriverText;
        private TextView mYouAlsoCanText;

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_intro_0, container, false);
            mTypeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/GloriaHallelujah.ttf");

            mHowItWorksText = (TextView)v.findViewById(R.id.see_how_it_works);
            mToStartText = (TextView)v.findViewById(R.id.to_start);
            mIfDriverText = (TextView)v.findViewById(R.id.if_driver);
            mYouAlsoCanText = (TextView)v.findViewById(R.id.you_also_can);

            mHowItWorksText.setTypeface(mTypeface);
            mToStartText.setTypeface(mTypeface);
            mIfDriverText.setTypeface(mTypeface);
            mYouAlsoCanText.setTypeface(mTypeface);

            return v;
        }
    }


    public static class Intro1RiderFragment extends Fragment{
        private Typeface mTypeface;
        private TextView mRegisterYourRouteText;
        private TextView mJustOnceText;
        private TextView mRepeatDaysText;
        private TextView mRepeatText;

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_intro_1_rider, container, false);
            mTypeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/GloriaHallelujah.ttf");

            mRegisterYourRouteText = (TextView)v.findViewById(R.id.register_your_route);
            mJustOnceText = (TextView)v.findViewById(R.id.just_once);
            mRepeatDaysText = (TextView)v.findViewById(R.id.repeat_days);
            mRepeatText = (TextView)v.findViewById(R.id.repeat_route);

            mRegisterYourRouteText.setTypeface(mTypeface);
            mJustOnceText.setTypeface(mTypeface);
            mRepeatDaysText.setTypeface(mTypeface);
            mRepeatText.setTypeface(mTypeface);

            return v;
        }
    }


    public static class Intro2RiderFragment extends Fragment{
        private Typeface mTypeface;
        private TextView mRegisterYourRouteText;
        private TextView mJustOnceText;
        private TextView mRepeatDaysText;
        private TextView mRepeatText;

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_intro_2_rider, container, false);
            mTypeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/GloriaHallelujah.ttf");

            mRegisterYourRouteText = (TextView)v.findViewById(R.id.register_your_route);
            mJustOnceText = (TextView)v.findViewById(R.id.just_once);
            mRepeatDaysText = (TextView)v.findViewById(R.id.repeat_days);
            mRepeatText = (TextView)v.findViewById(R.id.repeat_route);

            mRegisterYourRouteText.setTypeface(mTypeface);
            mJustOnceText.setTypeface(mTypeface);
            mRepeatDaysText.setTypeface(mTypeface);
            mRepeatText.setTypeface(mTypeface);

            return v;
        }
    }
}
