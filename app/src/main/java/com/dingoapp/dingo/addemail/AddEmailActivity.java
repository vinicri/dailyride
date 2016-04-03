package com.dingoapp.dingo.addemail;

import android.os.Bundle;

import com.dingoapp.dingo.BaseActivity;
import com.dingoapp.dingo.R;
import com.dingoapp.dingo.api.Response;
import com.dingoapp.dingo.api.model.User;

/**
 * Created by guestguest on 01/04/16.
 */
public abstract class AddEmailActivity extends BaseActivity implements EmailFragment.OnEmailFragmentActionListener,
        NameFragment.OnNameFragmentActionListener,
        NoEmailFragment.OnNoEmailFragmentListener,
        CodeFragment.OnCodeFragmentListener{

    public static final String EXTRA_USER = "EXTRA_USER";
    boolean mUserHasNoEmail = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_email);

        User user = (User)getIntent().getSerializableExtra(EXTRA_USER);

        Bundle bundle = new Bundle();
        bundle.putString(EmailFragment.EXTRA_USER_FIRST_NAME, user.getFirstName());

        EmailFragment emailFragment = new EmailFragment();
        emailFragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, emailFragment)
                .commit();
    }

    /*
      OnEmailFragmentActionListener
     */
    @Override
    public void onSaveButtonClick(final String email, final FragmentCallback callback) {
        addEmail(email,
                new Callback<Void>() {
                    @Override
                    public void onResponse(Response<Void> response) {
                        callback.onFinished();
                        Bundle bundle = new Bundle();
                        bundle.putString(CodeFragment.EXTRA_EMAIL, email);
                        CodeFragment codeFragment = new CodeFragment();
                        codeFragment.setArguments(bundle);
                        getSupportFragmentManager().beginTransaction()
                                .add(R.id.container, codeFragment)
                                .addToBackStack("code_fragment")
                                .commit();


                    }

                    @Override
                    public void onFailure(Throwable t) {
                        callback.onFinished();
                    }
                });
    }

    @Override
    public void onNoEmailButtonClick() {
        mUserHasNoEmail = true;
        NameFragment nameFragment = new NameFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, nameFragment)
                .addToBackStack("name_fragment")
                .commit();
    }

    /*
      OnNameFragmentActionListener
     */

    @Override
    public void onNext(String name) {
        if(mUserHasNoEmail){
            NoEmailFragment noEmailFragment = new NoEmailFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, noEmailFragment)
                    .addToBackStack("no_email_fragment")
                    .commit();
        }
        else{
            CodeFragment codeFragment = new CodeFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, codeFragment)
                    .addToBackStack("no_email_fragment")
                    .commit();
        }
    }

    /*
      OnNoEmailFragmentListener
     */

    @Override
    public void onSend() {
        sendDocument();
    }

    @Override
    public void onResend(){
        resendEmail();
    }

    @Override
    public void onConfirm(String code){
        confirmCode(code,
                new Callback<Void>() {
                    @Override
                    public void onResponse(Response<Void> response) {

                    }

                    @Override
                    public void onFailure(Throwable t) {

                    }
                });
    }

    protected abstract void addEmail(String email, Callback<Void> callback);
    protected abstract void sendDocument();
    protected abstract void resendEmail();
    protected abstract void confirmCode(String code, Callback<Void> callback);

    public interface Callback<T> {

        public void onResponse(Response<T> response);
        public void onFailure(Throwable t);
    }


}
