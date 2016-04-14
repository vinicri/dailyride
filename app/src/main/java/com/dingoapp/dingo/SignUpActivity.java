package com.dingoapp.dingo;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.dingoapp.dingo.api.ApiCallback;
import com.dingoapp.dingo.api.DingoApiService;
import com.dingoapp.dingo.api.GCMUtils;
import com.dingoapp.dingo.api.Response;
import com.dingoapp.dingo.api.model.DingoError;
import com.dingoapp.dingo.api.model.User;
import com.dingoapp.dingo.util.SettingsUtil;
import com.dingoapp.dingo.util.TextWatcherPhoneBR;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.dingoapp.dingo.util.ViewUtils.showOkDialog;

/**
 * Created by guestguest on 10/04/16.
 */
public class SignUpActivity extends BaseActivity{

    @Bind(R.id.first_name)EditText mFirstNameEdit;
    @Bind(R.id.last_name)EditText mLastNameEdit;
    @Bind(R.id.gender_radio_group) RadioGroup mGenderGroup;
    @Bind(R.id.email)EditText mEmailEdit;
    @Bind(R.id.password)EditText mPasswordEdit;
    @Bind(R.id.phone)EditText mPhoneEdit;

    @Bind(R.id.sign_up_button)View mSignUp;
    @Bind(R.id.sign_up_text)TextView mSignUpText;
    @Bind(R.id.sign_up_spin)ProgressBar mSignUpSpin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getSupportActionBar().setTitle(R.string.activity_sign_up);

        ButterKnife.bind(this);

        mPhoneEdit.addTextChangedListener(new TextWatcherPhoneBR());

        mSignUpSpin.getIndeterminateDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);

        mSignUp.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(validate()){
                            User user = new User();
                            user.setFirstName(mFirstNameEdit.getText().toString());
                            user.setLastName(mLastNameEdit.getText().toString());
                            if(mGenderGroup.getCheckedRadioButtonId() == R.id.gender_male) {
                                user.setGender(User.Gender.M);
                            }
                            else{
                                user.setGender(User.Gender.F);
                            }
                            user.setEmail(mEmailEdit.getText().toString());
                            user.setPassword(mPasswordEdit.getText().toString());
                            user.setPhone(mPhoneEdit.getText().toString().replaceAll("[^0-9]*", ""));

                            mSignUpText.setVisibility(View.GONE);
                            mSignUpSpin.setVisibility(View.VISIBLE);
                            DingoApiService.getInstance().userRegister(user,
                                    new ApiCallback<User>(SignUpActivity.this){
                                        @Override
                                        public void success(Response<User> response) {
                                            if(response.code() == Response.HTTP_201_CREATED){
                                                SettingsUtil.setCurrentUser(SignUpActivity.this, response.body());

                                                GCMUtils.registerGCM(SignUpActivity.this);

                                                Intent intent = new Intent(SignUpActivity.this, SignUpConfirmActivity.class);
                                                startActivity(intent);
                                            }
                                            else{
                                                //todo
                                                //Rollbar.
                                            }
                                        }

                                        @Override
                                        public void clientError(Response<?> response, DingoError error) {

                                            switch (error.code()){
                                                case DingoError.ERR_SIGN_UP_EXISTING_USER:
                                                    showOkDialog(SignUpActivity.this, R.string.dingo_err_10);
                                                    break;
                                                case DingoError.ERR_SIGN_UP_EXISTING_FACEBOOK_USER:
                                                    showOkDialog(SignUpActivity.this, R.string.dingo_err_11);
                                                    break;
                                                case DingoError.ERR_SIGN_UP_EXISTING_PHONE:
                                                    showOkDialog(SignUpActivity.this, R.string.dingo_err_12);
                                                    break;
                                                default:
                                                    super.clientError(response, error);
                                            }
                                        }

                                        @Override
                                        public void onFinish() {
                                            mSignUpText.setVisibility(View.VISIBLE);
                                            mSignUpSpin.setVisibility(View.GONE);
                                        }
                                    }
                            );
                        }
                    }
                }
        );
    }

    private boolean validate(){

        boolean isValid = true;

        if(TextUtils.isEmpty(mFirstNameEdit.getText())){
            mFirstNameEdit.setError(getString(R.string.sign_up_first_name_error));
            isValid = false;
        }

        if(TextUtils.isEmpty(mLastNameEdit.getText())){
            mLastNameEdit.setError(getString(R.string.sign_up_last_name_error));
            isValid = false;
        }

        if(TextUtils.isEmpty(mEmailEdit.getText())){
            mEmailEdit.setError(getString(R.string.sign_up_email_error_empty));
            isValid = false;
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(mEmailEdit.getText()).matches()){
            mEmailEdit.setError(getString(R.string.sign_up_email_error_invalid));
            isValid = false;
        }

        if(mPasswordEdit.getText() == null || mPasswordEdit.getText().length() < 6){
            mPasswordEdit.setError(getString(R.string.sign_up_password_error));
            isValid = false;
        }

        if (TextUtils.isEmpty(mPhoneEdit.getText())) {
            mPhoneEdit.setError(getString(R.string.sign_up_phone_error_empty));
            isValid = false;
        }
        else{
            String number =  mPhoneEdit.getText().toString().replaceAll("[^0-9]*", "");
            if(number.length() < 10 || (number.length() == 11 && number.charAt(2) != '9')){
                mPhoneEdit.setError(getString(R.string.sign_up_number_error_invalid));
                isValid = false;
            }
        }

        return isValid;
    }
}
