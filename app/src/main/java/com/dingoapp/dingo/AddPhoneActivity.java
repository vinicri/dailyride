package com.dingoapp.dingo;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.dingoapp.dingo.api.ApiCallback;
import com.dingoapp.dingo.api.DingoApiService;
import com.dingoapp.dingo.api.Response;
import com.dingoapp.dingo.api.model.User;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by guestguest on 28/03/16.
 */
public class AddPhoneActivity extends BaseActivity {

    public static final String EXTRA_USER = "EXTRA_USER";
    User mUser;

    @Bind(R.id.phone_box)View mPhoneBox;
    @Bind(R.id.confirmation_box)View mConfirmationBox;

    @Bind(R.id.phone_edit) EditText mPhoneEdit;

    @Bind(R.id.send_button)Button mSendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_phone);
        ButterKnife.bind(this);

        mUser = (User)getIntent().getSerializableExtra(EXTRA_USER);

        mSendButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //todo validation
                        String phone = mPhoneEdit.getText().toString();
                        DingoApiService.getInstance().userAddPhone(phone,
                                new ApiCallback<Void>(AddPhoneActivity.this) {
                                    @Override
                                    public void success(Response<Void> response) {
                                        if(response.code() == Response.HTTP_200_OK){
                                            mPhoneBox.setVisibility(View.GONE);
                                            mConfirmationBox.setVisibility(View.VISIBLE);
                                        }
                                    }
                                });
                    }
                }
        );

        if(mUser.getPhone() == null){

        }
        else if(mUser.isPhoneConfirmed()){
            //adding a new phone
        }
        else{
            mPhoneBox.setVisibility(View.GONE);
            mConfirmationBox.setVisibility(View.VISIBLE);
        }


    }
}
