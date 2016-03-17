package com.dingoapp.dingo;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.dingoapp.dingo.chat.ChatActivity;
import com.dingoapp.dingo.util.CircleTransform;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by guestguest on 13/03/16.
 */
public class RequestDetailsActivity extends BaseActivity {

    @Bind(R.id.chat_button) ImageView mChatButton;
    @Bind(R.id.driver_picture) ImageView mDriverPicture;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_details);
        ButterKnife.bind(this);
        getSupportActionBar().setTitle(R.string.activity_request_details);

        mChatButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(RequestDetailsActivity.this, ChatActivity.class);
                        startActivity(intent);
                    }
                }
        );

        Glide.with(this).load("http://36.media.tumblr.com/0495e65a4f15696b4f3cc0dcff59a9e0/tumblr_mtqdx1uILV1r93kc1o1_r1_1280.jpg")
                .bitmapTransform(new CircleTransform(this))
                .into(mDriverPicture);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_ride_details, menu);
        return true;
    }
}
