package com.dingoapp.dingo;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.dingoapp.dingo.api.model.Address;
import com.dingoapp.dingo.api.model.RideEntity;
import com.dingoapp.dingo.searchaddress.AddressSearchActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by guestguest on 13/02/16.
 */
public abstract class RideCreateActivity extends AppCompatActivity {

    protected static final int RESULT_LEAVING_ADDRESS = 10;
    protected static final int RESULT_ARRIVING_ADDRESS = 11;
    protected static final int RESULT_TIME = 12;

    //private RideOffer mRideOffer = RideOffer.getWeekdaysCheckedInstance();
    private SimpleDateFormat sdf;// = new SimpleDateFormat("EEEE, d MMM yyyy", getResources().getConfiguration().locale);

    @Bind(R.id.leaving_address_box)
    LinearLayout mLeavingAddressBox;
    @Bind(R.id.leaving_address_line1)
    TextView mLeavingAddressLine1;
    @Bind(R.id.leaving_address_line2)
    TextView mLeavingAddressLine2;
    @Bind(R.id.arriving_address_line1)
    TextView mArrivingAddressLine1;
    @Bind(R.id.arriving_address_line2)
    TextView mArrivingAddressLine2;
    @Bind(R.id.arriving_address_box)
    LinearLayout mArrivingAddressBox;
    @Bind(R.id.time_box)
    LinearLayout mTimeBox;
    @Bind(R.id.time_text)
    TextView mTimeText;
    @Bind(R.id.recurrence_box)
    LinearLayout mRecurrenceBox;
    @Bind(R.id.recurrence_radio_group)
    RadioGroup mRecurrenceGroup;
    @Bind(R.id.recurrence_yes)
    RadioButton mRecurrenceYes;
    @Bind(R.id.recurrence_no)
    RadioButton mRecurrenceNo;
    @Bind(R.id.days_box)
    LinearLayout mDaysBox;

    @Bind(R.id.day_sunday)
    Button mSundayButton;
    @Bind(R.id.day_monday)
    Button mMondayButton;
    @Bind(R.id.day_tuesday)
    Button mTuesdayButton;
    @Bind(R.id.day_wednesday)
    Button mWednesdayButton;
    @Bind(R.id.day_thursday)
    Button mThursdayButton;
    @Bind(R.id.day_friday)
    Button mFridayButton;
    @Bind(R.id.day_saturday)
    Button mSaturdayButton;

    @Bind(R.id.offer_button)
    Button mOfferButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sdf = new SimpleDateFormat("EEEE, d MMM yyyy", getResources().getConfiguration().locale);
        setContentView(R.layout.activity_offer);
        ButterKnife.bind(this);

        mLeavingAddressBox.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(RideCreateActivity.this, AddressSearchActivity.class);
                        startActivityForResult(intent, RESULT_LEAVING_ADDRESS);
                    }
                }
        );

        mArrivingAddressBox.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(RideCreateActivity.this, AddressSearchActivity.class);
                        startActivityForResult(intent, RESULT_ARRIVING_ADDRESS);
                    }
                }
        );

        mTimeBox.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(RideCreateActivity.this, TimeActivity.class);
                        startActivityForResult(intent, RESULT_TIME);
                    }
                }
        );

        mRecurrenceGroup.setOnCheckedChangeListener(
                new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        if (checkedId == R.id.recurrence_yes) {
                            mDaysBox.setVisibility(View.VISIBLE);
                        } else if (checkedId == R.id.recurrence_no) {
                            mDaysBox.setVisibility(View.GONE);
                        }
                    }
                }
        );

        refreshAllDayButtons();

        mSundayButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getRecurrentEntity().setSunday(!getRecurrentEntity().isSunday());
                        refreshDayButton(mSundayButton, getRecurrentEntity().isSunday());
                    }
                }
        );


        mMondayButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getRecurrentEntity().setMonday(!getRecurrentEntity().isMonday());
                        refreshDayButton(mMondayButton, getRecurrentEntity().isMonday());
                    }
                }
        );

        mTuesdayButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getRecurrentEntity().setTuesday(!getRecurrentEntity().isTuesday());
                        refreshDayButton(mTuesdayButton, getRecurrentEntity().isTuesday());
                    }
                }
        );

        mWednesdayButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getRecurrentEntity().setWednesday(!getRecurrentEntity().isWednesday());
                        refreshDayButton(mWednesdayButton, getRecurrentEntity().isWednesday());
                    }
                }
        );

        mThursdayButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getRecurrentEntity().setThursday(!getRecurrentEntity().isThursday());
                        refreshDayButton(mThursdayButton, getRecurrentEntity().isThursday());
                    }
                }
        );

        mFridayButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getRecurrentEntity().setFriday(!getRecurrentEntity().isFriday());
                        refreshDayButton(mFridayButton, getRecurrentEntity().isFriday());
                    }
                }
        );

        mSaturdayButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getRecurrentEntity().setSaturday(!getRecurrentEntity().isSaturday());
                        refreshDayButton(mSaturdayButton, getRecurrentEntity().isSaturday());
                    }
                }
        );

        mOfferButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        create();
                    }
                }
        );

    }

    public abstract RideEntity getRecurrentEntity();

    private void refreshAllDayButtons() {
        refreshDayButton(mSundayButton, getRecurrentEntity().isSunday());
        refreshDayButton(mMondayButton, getRecurrentEntity().isMonday());
        refreshDayButton(mTuesdayButton, getRecurrentEntity().isTuesday());
        refreshDayButton(mWednesdayButton, getRecurrentEntity().isWednesday());
        refreshDayButton(mThursdayButton, getRecurrentEntity().isThursday());
        refreshDayButton(mFridayButton, getRecurrentEntity().isFriday());
        refreshDayButton(mSaturdayButton, getRecurrentEntity().isSaturday());
    }

    private void refreshDayButton(Button dayButton, boolean checked) {

        if (checked) {
            dayButton.setBackgroundResource(R.drawable.shape_day_button_enabled);
        } else {
            dayButton.setBackgroundResource(R.drawable.shape_day_button_disabled);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == RESULT_LEAVING_ADDRESS) {
                Address address = (Address) data.getSerializableExtra("address");
                populateAddressBox(address, mLeavingAddressLine1, mLeavingAddressLine2);
            } else if (requestCode == RESULT_ARRIVING_ADDRESS) {
                Address address = (Address) data.getSerializableExtra("address");
                populateAddressBox(address, mArrivingAddressLine1, mArrivingAddressLine2);
            } else if (requestCode == RESULT_TIME) {
                Date time = (Date) data.getSerializableExtra("date");
                mTimeText.setText(sdf.format(time));
            }
        }

    }

    private void populateAddressBox(Address address, TextView line1, TextView line2) {
        if (address.isRouteType()) {
            SpannableString route = new SpannableString(address.getRouteLong() + ", ");
            SpannableString number = new SpannableString(address.getNumber());
            number.setSpan(new StyleSpan(Typeface.BOLD), 0, number.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            line1.setText(route);
            line1.append(number);
            String line2Text = new String();
            if (address.getDistrict() != null) {
                line2Text = line2Text.concat(address.getDistrict());
            }
            if (address.getCity() != null) {
                if (line2Text.length() > 0) {
                    line2Text = line2Text.concat(", ");
                }
                line2Text = line2Text.concat(address.getCity());
            }
            if (address.getState() != null) {
                if (line2Text.length() > 0) {
                    line2Text = line2Text.concat(" - ");
                }
                line2Text = line2Text.concat(address.getState());
            }
            line2.setText(line2Text);
        } else if (address.isEstablishmentType()) {
            line1.setText(address.getName());
            if (address.getRouteLong() != null) {
                String line2Text = new String();
                line2Text = address.getRouteLong();
                if (address.getNumber() != null) {
                    line2Text = line2Text.concat(", " + address.getNumber());
                } else {
                    line2Text = line2Text.concat(", " + getString(R.string.no_number_symbol));
                }
                line2.setText(line2Text);
            } else {
                line2.setText("");
            }
        } else {
            //FIXME not expectected state
        }
    }

    private void validateOffer() {

    }

    abstract void create();

}