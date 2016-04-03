package com.dingoapp.dingo.addemail;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;

import com.dingoapp.dingo.R;

/**
 * Created by guestguest on 01/04/16.
 */
public class CredentialsFragment extends Fragment {

    View mDocumentBox;
    ImageView mDocumentPreview;
    Button mSendButton;

    OnNoEmailFragmentListener mListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_email_no_email, container, false);

        mDocumentBox = v.findViewById(R.id.document_box);
        mDocumentPreview = (ImageView)v.findViewById(R.id.document_preview);
        mSendButton = (Button)v.findViewById(R.id.send_button);

        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            mListener = (OnNoEmailFragmentListener)context;
        }
        catch (ClassCastException e){
            throw new ClassCastException(context.toString() + " must implement OnNoEmailFragmentListener");
        }
    }

    interface OnNoEmailFragmentListener{
        void onSend();
    }
}
