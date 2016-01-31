package com.dingoapp.dingo.welcome;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.dingoapp.dingo.R;

/**
 * Created by guestguest on 30/01/16.
 */
public class RiderModeFragment extends Fragment{
    Button mYesButton;
    Button mNoButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_rider_mode, container, false);

        mYesButton = (Button)v.findViewById(R.id.yes);
        mNoButton = (Button)v.findViewById(R.id.no);


        return v;
        //return super.onCreateView(inflater, container, savedInstanceState);
    }
}
