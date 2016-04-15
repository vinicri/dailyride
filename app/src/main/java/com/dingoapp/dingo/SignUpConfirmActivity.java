package com.dingoapp.dingo;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.view.MenuItem;
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
import com.dingoapp.dingo.util.SettingsUtil;
import com.dingoapp.dingo.welcome.WelcomeActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.dingoapp.dingo.util.ViewUtils.showOkDialog;

/**
 * Created by guestguest on 12/04/16.
 */
public class SignUpConfirmActivity extends BaseActivity {

    public static final String EXTRA_SHOULD_GO_BACK_TO_PARENT = "EXTRA_SHOULD_GO_BACK_TO_PARENT";

    @Bind(R.id.phone_text)TextView mPhoneText;
    @Bind(R.id.code_edit)EditText mCodeEdit;
    @Bind(R.id.confirm_code_button)View mConfirmButton;
    @Bind(R.id.confirm_code_text)TextView mConfirmText;
    @Bind(R.id.confirm_code_spin)ProgressBar mConfirmSpin;

    private boolean mShouldGoBackToParent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_confirm);
        getSupportActionBar().setTitle(R.string.activity_confirm_registration);

        ButterKnife.bind(this);

        mShouldGoBackToParent = getIntent().getBooleanExtra(EXTRA_SHOULD_GO_BACK_TO_PARENT, false);

        mPhoneText.setText(CurrentUser.getUser().getPhone());

        mCodeEdit.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        mConfirmSpin.getIndeterminateDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);

        mConfirmButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mConfirmText.setVisibility(View.GONE);
                        mConfirmSpin.setVisibility(View.VISIBLE);
                        if(mCodeEdit.getText() == null || mCodeEdit.getText().length() == 0){
                            showOkDialog(SignUpConfirmActivity.this, R.string.sign_up_confirmation_code_enter);
                            return;
                        }

                        DingoApiService.getInstance().userSignUpConfirm(mCodeEdit.getText().toString(),
                                new ApiCallback<Void>(SignUpConfirmActivity.this) {
                                    @Override
                                    public void success(Response<Void> response) {
                                        if(response.code() == Response.HTTP_200_OK){
                                            CurrentUser.getInstance().setPhoneConfirmed(true);
                                            CurrentUser.getInstance().setRegistrationConfirmed(true);
                                            Intent intent = new Intent(SignUpConfirmActivity.this, WelcomeActivity.class);
                                            startActivity(intent);
                                        }
                                        else{
                                            //todo
                                        }
                                    }

                                    @Override
                                    public void clientError(Response<?> response, DingoError error) {
                                        switch (error.code()) {
                                            case DingoError.ERR_PHONE_CONFIRMATION_CODE_WRONG:
                                                showOkDialog(SignUpConfirmActivity.this, R.string.dingo_err_20);
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


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                if(mShouldGoBackToParent) {
                    new AlertDialog.Builder(this)
                            .setMessage(R.string.sign_up_confirm_back)
                            .setPositiveButton(R.string.yes,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            SettingsUtil.setCurrentUser(SignUpConfirmActivity.this, null);
                                            SettingsUtil.setSentTokenToServer(SignUpConfirmActivity.this, false);
                                            NavUtils.navigateUpFromSameTask(SignUpConfirmActivity.this);
                                        }
                                    })
                            .setNegativeButton(R.string.no, null)
                            .show();
                }
                else{
                    SettingsUtil.setCurrentUser(SignUpConfirmActivity.this, null);
                    SettingsUtil.setSentTokenToServer(SignUpConfirmActivity.this, false);
                    finish();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
