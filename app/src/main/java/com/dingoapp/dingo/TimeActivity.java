package com.dingoapp.dingo;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TimePicker;

import com.dingoapp.dingo.view.HorizontalPicker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by guestguest on 31/01/16.
 */
public class TimeActivity extends BaseActivity {

    @Bind(R.id.date_picker) HorizontalPicker mDatePicker;
    @Bind(R.id.previous_day) ImageView mPreviousDayButton;
    @Bind(R.id.next_day) ImageView mNextDayButton;
    @Bind(R.id.time_picker) TimePicker mTimePicker;
    @Bind(R.id.set)
    Button mSetButton;
    ArrayList<Date> mDates = new ArrayList<>();
    ArrayList<CharSequence> mDateStrings = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time);
        ButterKnife.bind(this);

        String title = (String)getIntent().getSerializableExtra("title");
        if(title != null){
            getSupportActionBar().setTitle(title);
        }

        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM, yyyy");

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        int saturdaysCounter = 0;
        int iterationsCounter = 0;
        while(saturdaysCounter < 2){
            Date date = cal.getTime();
            mDates.add(date);

            switch (iterationsCounter){
                case 0:
                    mDateStrings.add(getString(R.string.today));
                    break;
                case 1:
                    mDateStrings.add(getString(R.string.tomorrow));
                    break;
                default:
                    mDateStrings.add(sdf.format(date));
            }

            cal.add(Calendar.DAY_OF_MONTH, 1);

            if(cal.get(Calendar.DAY_OF_WEEK) == 7){
                saturdaysCounter++;
            }
            iterationsCounter++;
        }

        Date lastSatuday = cal.getTime();
        mDates.add(lastSatuday);
        mDateStrings.add(sdf.format(lastSatuday));

        CharSequence[] datesArray = new CharSequence[mDateStrings.size()];
        mDatePicker.setValues(mDateStrings.toArray(datesArray));

        mPreviousDayButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDatePicker.scrollToPrevious();
                    }
                }
        );

        mNextDayButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDatePicker.scrollToNext();
                    }
                }
        );

        mTimePicker.setIs24HourView(true);

        mSetButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setTime();
                    }
                }
        );
    }

    private void setTime() {
        Date selectedDate = mDates.get(mDatePicker.getSelectedItem());

        Calendar cal = Calendar.getInstance();
        cal.setTime(selectedDate);
        cal.set(Calendar.HOUR_OF_DAY, mTimePicker.getCurrentHour());
        cal.set(Calendar.MINUTE, mTimePicker.getCurrentMinute());
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Calendar now = Calendar.getInstance();
        now.set(Calendar.SECOND, 0);
        now.set(Calendar.MILLISECOND, 0);

        Date setDate = cal.getTime();
        Date nowDate = now.getTime();

        if(setDate.compareTo(nowDate) < 0){
            new AlertDialog.Builder(this)
                    .setMessage(R.string.time_earlier_than_now)
                    .setPositiveButton(R.string.ok, null)
                    .show();
            return;
        }

        Intent intent = new Intent();
        intent.putExtra("date", setDate);
        setResult(RESULT_OK, intent);
        finish();
    }


}
