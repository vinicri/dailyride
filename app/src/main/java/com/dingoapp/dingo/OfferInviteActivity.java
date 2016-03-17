package com.dingoapp.dingo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.dingoapp.dingo.util.CircleTransform;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by guestguest on 13/03/16.
 */
public class OfferInviteActivity extends BaseActivity{

    @Bind(R.id.accept) Button mAcceptButton;
    @Bind(R.id.driver_picture)ImageView mDriverPicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_invite);
        getActionBarToolbar().setTitle(R.string.activity_offer_invite);

        ButterKnife.bind(this);

        mAcceptButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(OfferInviteActivity.this, RequestDetailsActivity.class);
                        startActivity(intent);
                    }
                }
        );

        Glide.with(this).load("http://36.media.tumblr.com/0495e65a4f15696b4f3cc0dcff59a9e0/tumblr_mtqdx1uILV1r93kc1o1_r1_1280.jpg")
                .bitmapTransform(new CircleTransform(this))
                .into(mDriverPicture);

        Glide.with(this).load("http://36.media.tumblr.com/0495e65a4f15696b4f3cc0dcff59a9e0/tumblr_mtqdx1uILV1r93kc1o1_r1_1280.jpg")
                .bitmapTransform(new CircleTransform(this))
                .into(mDriverPicture);

    }
}
