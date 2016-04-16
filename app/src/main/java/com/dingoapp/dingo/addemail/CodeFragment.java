package com.dingoapp.dingo.addemail;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dingoapp.dingo.R;

import static com.dingoapp.dingo.util.ViewUtils.showOkDialog;

/**
 * Created by guestguest on 01/04/16.
 */
public class CodeFragment extends Fragment {

    public final static String EXTRA_EMAIL = "EXTRA_EMAIL";

    TextView mEmailText;
    EditText mCodeEdit;
    Button mResendButton;
    View mConfirmButton;
    private OnCodeFragmentListener mListener;
    TextView mConfirmButtonText;
    ProgressBar mConfirmButtonSpin;

    boolean mProcessingCode;
    boolean mProcessingResend;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_email_code, container, false);

        mEmailText = (TextView)v.findViewById(R.id.email_text);
        mCodeEdit = (EditText)v.findViewById(R.id.code_edit);
        mResendButton = (Button)v.findViewById(R.id.resend_code_button);
        mConfirmButton = v.findViewById(R.id.confirm_code_button);
        mConfirmButtonText = (TextView)v.findViewById(R.id.confirm_code_text);
        mConfirmButtonSpin = (ProgressBar)v.findViewById(R.id.confirm_code_spin);

        mConfirmButtonSpin.getIndeterminateDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);

        String email = getArguments().getString(EXTRA_EMAIL);

        if(email != null){
            mEmailText.setText(email);
        }

        mConfirmButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String code = mCodeEdit.getText().toString();
                        if(code.length() < 6){
                            showInvalidDialog();
                            return;
                        }

                        if(mProcessingCode){
                            return;
                        }

                        mProcessingCode = true;
                        mConfirmButtonText.setVisibility(View.GONE);
                        mConfirmButtonSpin.setVisibility(View.VISIBLE);

                        mListener.onConfirm(code,
                                new FragmentCallback() {
                                    @Override
                                    public void onFinish() {
                                        mProcessingCode = false;
                                        mConfirmButtonText.setVisibility(View.VISIBLE);
                                        mConfirmButtonSpin.setVisibility(View.GONE);
                                    }
                                });
                    }
                }
        );

        mResendButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.onResend(
                                new FragmentCallback() {
                                    @Override
                                    public void onFinish() {
                                        if(mProcessingResend){
                                            return;
                                        }
                                        //todo - disabled for now
                                    }
                                }
                        );
                    }
                }
        );

        mCodeEdit.requestFocus();
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        return v;

    }

    private void showInvalidDialog(){
        showOkDialog(getActivity(), R.string.code_fragment_pin_too_short);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            mListener = (OnCodeFragmentListener)context;
        }
        catch (ClassCastException e){
            throw new ClassCastException(context.toString() + " must implement OnCodeFragmentListener");
        }
    }

    interface OnCodeFragmentListener{
        void onResend(FragmentCallback callback);
        void onConfirm(String code, FragmentCallback callback);
    }
}
