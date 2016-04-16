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

import static com.dingoapp.dingo.util.ViewUtils.showOkDialog;

/**
 * Created by guestguest on 01/04/16.
 */
public class NameFragment extends Fragment{

    public final static String EXTRA_HEADER = "EXTRA_HEADER";

    EditText mNameEdit;
    Button mNextButton;
    TextView mHeader;
    private OnNameFragmentActionListener mListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_email_name, container, false);

        mHeader = (TextView)v.findViewById(R.id.header);
        mNameEdit = (EditText)v.findViewById(R.id.name_edit);
        mNextButton = (Button)v.findViewById(R.id.next_button);

        String header = getArguments().getString(EXTRA_HEADER);

        mHeader.setText(header);

        mNextButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String name = mNameEdit.getText().toString();
                        if(isValid(name)){
                            mListener.onNext(name);
                        }
                        else{
                            showOkDialog(getActivity(), R.string.add_email_entity_name_too_short);
                        }
                    }
                }
        );

        mNameEdit.requestFocus();
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        return v;
    }

    private boolean isValid(String name){
        return name != null && name.length() > 3;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            mListener = (OnNameFragmentActionListener)context;
        }
        catch (ClassCastException e){
            throw new ClassCastException(context.toString() + " must implement OnNameFragmentActionListener");
        }
    }

    interface OnNameFragmentActionListener{
        void onNext(String name);
    }
}
