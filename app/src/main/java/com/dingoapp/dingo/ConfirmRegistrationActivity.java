package com.dingoapp.dingo;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dingoapp.dingo.api.ApiCallback;
import com.dingoapp.dingo.api.DingoApiService;
import com.dingoapp.dingo.api.Response;
import com.dingoapp.dingo.api.model.DingoError;
import com.dingoapp.dingo.util.CurrentUser;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.dingoapp.dingo.util.ViewUtils.showOkDialog;

/**
 * Created by guestguest on 12/04/16.
 */
public class ConfirmRegistrationActivity extends BaseActivity {

    @Bind(R.id.phone_text)TextView mPhoneText;
    @Bind(R.id.code_edit)EditText mCodeEdit;
    @Bind(R.id.confirm_code_button)View mConfirmButton;
    @Bind(R.id.confirm_code_text)TextView mConfirmText;
    @Bind(R.id.confirm_code_spin)ProgressBar mConfirmSpin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_registration);
        getSupportActionBar().setTitle(R.string.activity_confirm_registration);

        ButterKnife.bind(this);

        mPhoneText.setText(CurrentUser.getUser().getPhone());

        mCodeEdit.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        mConfirmButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mConfirmText.setVisibility(View.GONE);
                        mConfirmSpin.setVisibility(View.VISIBLE);
                        DingoApiService.getInstance().userSignUpConfirm(mConfirmText.getText().toString(),
                                new ApiCallback<Void>(ConfirmRegistrationActivity.this) {
                                    @Override
                                    public void success(Response<Void> response) {
                                        if(response.code() == Response.HTTP_200_OK){
                                            //

                                            //Intent intent = new Intent(ConfirmRegistrationActivity.this, WelcomeActivity.class);
                                            //startActivity(intent);
                                        }
                                        else{
                                            //todo
                                        }
                                    }

                                    @Override
                                    public void clientError(Response<?> response, DingoError error) {
                                        switch (error.code()) {
                                            case DingoError.ERR_PHONE_CONFIRMATION_CODE_WRONG:
                                                showOkDialog(ConfirmRegistrationActivity.this, R.string.dingo_err_20);
                                                break;
                                            default:
                                                super.clientError(response, error);
                                        }
                                    }

                                    @Override
                                    public void onFinish() {
                                        mConfirmText.setVisibility(View.VISIBLE);
                                        mConfirmSpin.setVisibility(View.GONE);
                                    }
                                });

                    }
                }
        );


    }
}
