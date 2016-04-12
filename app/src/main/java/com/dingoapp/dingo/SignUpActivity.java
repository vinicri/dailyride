package com.dingoapp.dingo;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.dingoapp.dingo.api.ApiCallback;
import com.dingoapp.dingo.api.DingoApiService;
import com.dingoapp.dingo.api.Response;
import com.dingoapp.dingo.api.model.DingoError;
import com.dingoapp.dingo.api.model.User;
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

    @Bind(R.id.sign_up)Button mSignUp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getSupportActionBar().setTitle(R.string.activity_sign_up);

        ButterKnife.bind(this);

        mPhoneEdit.addTextChangedListener(new TextWatcherPhoneBR());

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

                            DingoApiService.getInstance().userRegister(user,
                                    new ApiCallback<Void>(SignUpActivity.this){
                                        @Override
                                        public void success(Response<Void> response) {
                                            if(response.code() == Response.HTTP_201_CREATED){

                                            }
                                            else{
                                                //todo
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
                                                default:
                                                    super.clientError(response, error);
                                            }
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
