package com.dingoapp.dingo.addemail;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.dingoapp.dingo.R;

/**
 * Created by guestguest on 01/04/16.
 */
public class CodeFragment extends Fragment {

    public final static String EXTRA_EMAIL = "EXTRA_EMAIL";

    TextView mEmailText;
    EditText mCodeEdit;
    Button mResendButton;
    Button mConfirmButton;
    private OnCodeFragmentListener mListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_email_code, container, false);

        mEmailText = (TextView)v.findViewById(R.id.email_text);
        mCodeEdit = (EditText)v.findViewById(R.id.code_edit);
        mResendButton = (Button)v.findViewById(R.id.resend_code_button);
        mConfirmButton = (Button)v.findViewById(R.id.confirm_code_button);

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

                        mListener.onConfirm(code);
                    }
                }
        );

        mResendButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.onResend();
                    }
                }
        );

        mCodeEdit.requestFocus();
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        return v;

    }

    private void showInvalidDialog(){

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
        void onResend();
        void onConfirm(String code);
    }
}
