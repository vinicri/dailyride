package com.dingoapp.dingo.welcome;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.dingoapp.dingo.R;
import com.dingoapp.dingo.rides.RidesActivity;
import com.dingoapp.dingo.api.Callback;
import com.dingoapp.dingo.api.DingoApiService;
import com.dingoapp.dingo.api.Response;
import com.dingoapp.dingo.api.model.User;
import com.dingoapp.dingo.util.CurrentUser;

/**
 * Created by guestguest on 30/01/16.
 */
public class WelcomeActivity extends AppCompatActivity{


    private LinearLayout mAcceptTermsBox;
    private Button mAcceptedButton;

    private LinearLayout mRiderModeBox;
    private Button mYesButton;
    private Button mNoButton;

    private boolean mDidAcceptTerms;
    private User.RiderMode mSelectedRiderMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0, 0);
        setContentView(R.layout.activity_welcome);

        mAcceptTermsBox = (LinearLayout)findViewById(R.id.accept_terms_box);
        mAcceptedButton = (Button)findViewById(R.id.accepted);

        mRiderModeBox = (LinearLayout)findViewById(R.id.rider_mode_box);
        mYesButton = (Button)findViewById(R.id.yes);
        mNoButton = (Button)findViewById(R.id.no);

        mAcceptedButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDidAcceptTerms = true;
                        mRiderModeBox.setVisibility(View.VISIBLE);
                    }
                }
        );

        //rider mode selected
        mYesButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mSelectedRiderMode = User.RiderMode.D;
                        sendToServer();
                    }
                }
        );

        mNoButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mSelectedRiderMode = User.RiderMode.R;
                        sendToServer();
                    }
                }
        );
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
