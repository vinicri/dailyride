package com.lightgraylabs.dailyrides.profile;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.lightgraylabs.dailyrides.BaseActivity;
import com.lightgraylabs.dailyrides.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by guestguest on 16/02/16.
 */
public class ProfileActivity extends BaseActivity {

    public static final String EXTRA_USER = "EXTRA_USER";

    @Bind(R.id.back_button)ImageView mBackButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        mBackButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ProfileActivity.this.onBackPressed();
                    }
                }
        );
    }
}
