package com.dingoapp.dingo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.dingoapp.dingo.view.HorizontalPicker;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by guestguest on 31/01/16.
 */
public class TimeActivity extends AppCompatActivity {

    @Bind(R.id.date_picker) HorizontalPicker mDatePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time);
        ButterKnife.bind(this);


        ArrayList<CharSequence> dates = new ArrayList<>();
        dates.add("Hoje");
        dates.add("Amanha");
        CharSequence[] datesArray = new CharSequence[dates.size()];
        mDatePicker.setValues(dates.toArray(datesArray));
    }
}
