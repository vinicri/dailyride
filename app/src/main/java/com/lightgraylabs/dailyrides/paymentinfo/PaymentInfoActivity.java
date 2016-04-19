package com.lightgraylabs.dailyrides.paymentinfo;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.lightgraylabs.dailyrides.BaseActivity;
import com.lightgraylabs.dailyrides.R;
import com.lightgraylabs.dailyrides.api.ApiCallback;
import com.lightgraylabs.dailyrides.api.DingoApiService;
import com.lightgraylabs.dailyrides.api.Response;
import com.lightgraylabs.dailyrides.api.model.CreditCardInfo;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by guestguest on 16/02/16.
 */
public class PaymentInfoActivity extends BaseActivity{

    CreditCardInfo mCreditCardInfo;

    @Bind(R.id.number) EditText mNumber;
    @Bind(R.id.expiration_month) EditText mExpireMonth;
    @Bind(R.id.expiration_year) EditText mExpireYear;
    @Bind(R.id.cvv) EditText mCvv;
    @Bind(R.id.name) EditText mName;
    @Bind(R.id.cpf) EditText mCpf;

    @Bind(R.id.save) Button mSaveButton;
    @Bind(R.id.cancel) Button mCancelButton;

    boolean mEditMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_info);
        ButterKnife.bind(this);


        DingoApiService.getInstance().getCreditCardInfo(
                new ApiCallback<CreditCardInfo>(PaymentInfoActivity.this) {
                    @Override
                    public void success(Response<CreditCardInfo> response) {
                        if (response.code() == Response.HTTP_204_NO_CONTENT) {
                            mEditMode = true;

                        } else if (response.code() == Response.HTTP_200_OK) {
                            mCreditCardInfo = response.body();
                            populateFields(mCreditCardInfo);
                            enableFields(false);
                            mSaveButton.setText(getString(R.string.credit_card_edit));
                        }
                    }
                });

        mSaveButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setEdit(!mEditMode);
                    }
                }
        );

        mCancelButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mEditMode = false;
                        populateFields(mCreditCardInfo);
                        enableFields(false);
                        mCancelButton.setVisibility(View.GONE);
                        mSaveButton.setText(getString(R.string.credit_card_edit));
                    }
                }
        );
    }

    private void populateFields(CreditCardInfo info){
        mNumber.setText(info.getNumber());
        mExpireMonth.setText(String.valueOf(info.getExpirationMonth()));
        mExpireYear.setText(String.valueOf(info.getExpirationYear()));
        mCvv.setText(String.valueOf(info.getCvv()));
        mName.setText(info.getName());
        mCpf.setText(info.getCpf());
    }

    private void clearFields(){
        mNumber.setText("");
        mExpireMonth.setText("");
        mExpireYear.setText("");
        mCvv.setText("");
        mName.setText("");
        mCpf.setText("");
    }

    private void enableFields(boolean enable){
        mNumber.setEnabled(enable);
        mExpireMonth.setEnabled(enable);
        mExpireYear.setEnabled(enable);
        mCvv.setEnabled(enable);
        mName.setEnabled(enable);
        mCpf.setEnabled(enable);
    }

    private CreditCardInfo validateFields(){
        //todo validation
        CreditCardInfo info = new CreditCardInfo();
        info.setNumber(mNumber.getText().toString());
        info.setExpirationMonth(Integer.parseInt(mExpireMonth.getText().toString()));
        info.setExpirationYear(Integer.parseInt(mExpireYear.getText().toString()));
        info.setCvv(Integer.parseInt(mCvv.getText().toString()));
        info.setName(mName.getText().toString());
        info.setCpf(mCpf.getText().toString());
        return info;
    }

    private void setEdit(boolean enable){
        if(enable){
            clearFields();
            enableFields(true);
            mNumber.requestFocus();
            mSaveButton.setText(getString(R.string.credit_card_save));
            mCancelButton.setVisibility(View.VISIBLE);
            mEditMode = enable;
        }
        else{
            CreditCardInfo info = validateFields();
            if(validateFields() != null){
                mCancelButton.setVisibility(View.GONE);
                save(info);
            }
        }
    }

    public void save(CreditCardInfo info){
        if(mCreditCardInfo == null){
            DingoApiService.getInstance().createCreditCardInfo(info,
                    new ApiCallback<CreditCardInfo>(PaymentInfoActivity.this) {
                        @Override
                        public void success(Response<CreditCardInfo> response) {
                            if(response.code() == Response.HTTP_201_CREATED){
                                mCreditCardInfo = response.body();
                                populateFields(mCreditCardInfo);
                                enableFields(false);
                                mSaveButton.setText(getString(R.string.credit_card_edit));
                            }
                        }
                    });
        }
        else{
            DingoApiService.getInstance().updateCreditCardInfo(info,
                    new ApiCallback<CreditCardInfo>(PaymentInfoActivity.this) {
                        @Override
                        public void success(Response<CreditCardInfo> response) {
                            if(response.code() == Response.HTTP_200_OK){
                                mCreditCardInfo = response.body();
                                populateFields(mCreditCardInfo);
                                enableFields(false);
                                mSaveButton.setText(getString(R.string.credit_card_edit));
                            }
                        }
                    });
        }
    }
}
