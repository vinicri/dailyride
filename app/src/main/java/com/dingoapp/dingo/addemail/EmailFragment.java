package com.dingoapp.dingo.addemail;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.dingoapp.dingo.R;

/**
 * Created by guestguest on 01/04/16.
 */
public class EmailFragment extends Fragment {

    public final static String EXTRA_USER_FIRST_NAME = "EXTRA_USER_FIRST_NAME";

    EditText mEmailEdit;
    Button mNoEmailButton;
    View mSaveButton;
    View mSaveButtonText;
    ProgressBar mSaveButtonSpin;

    boolean mProcessing;

    OnEmailFragmentActionListener mListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_email_email, container, false);

        mEmailEdit = (EditText)v.findViewById(R.id.email_edit);
        mNoEmailButton = (Button)v.findViewById(R.id.no_email_button);
        mSaveButton = v.findViewById(R.id.save_button);
        mSaveButtonText = v.findViewById(R.id.save_button_text);
        mSaveButtonSpin = (ProgressBar)v.findViewById(R.id.save_button_spin);

        String userFirstName = getArguments().getString(EXTRA_USER_FIRST_NAME);

        if(userFirstName != null){
            mEmailEdit.setHint(getString(R.string.fragment_email_hint, userFirstName.toLowerCase()));
        }

        mSaveButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String email = mEmailEdit.getText().toString();
                        if(mProcessing){
                            return;
                        }

                        mProcessing = true;

                        mSaveButtonText.setVisibility(View.GONE);
                        mSaveButtonSpin.setVisibility(View.VISIBLE);

                        if (email == null || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                            showInvalidEmailDialog();
                            return;
                        }

                        mListener.onSaveButtonClick(email, new FragmentCallback() {
                            @Override
                            public void onFinished() {
                                mSaveButtonText.setVisibility(View.VISIBLE);
                                mSaveButtonSpin.setVisibility(View.GONE);
                                mProcessing = false;
                            }
                        });
                    }
                }
        );

        mNoEmailButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.onNoEmailButtonClick();
                    }
                }
        );

        mEmailEdit.requestFocus();
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        Drawable[] drawables = mNoEmailButton.getCompoundDrawables();
        mNoEmailButton.setCompoundDrawablesWithIntrinsicBounds(drawables[0], drawables[1], drawables[2], drawables[3]);

        mSaveButtonSpin.getIndeterminateDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);

        return v;
    }

    private void showInvalidEmailDialog(){

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            mListener = (OnEmailFragmentActionListener)context;
        }
        catch (ClassCastException e){
            throw new ClassCastException(context.toString() + " must implement OnEmailFragmentActionListener");
        }

    }

    interface OnEmailFragmentActionListener{
        void onSaveButtonClick(String email, FragmentCallback callback);
        void onNoEmailButtonClick();
    }
}