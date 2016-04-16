package com.dingoapp.dingo.addemail;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import com.dingoapp.dingo.BaseActivity;
import com.dingoapp.dingo.R;
import com.dingoapp.dingo.api.ApiCallback;
import com.dingoapp.dingo.api.Callback;
import com.dingoapp.dingo.api.Response;
import com.dingoapp.dingo.api.model.DingoError;
import com.dingoapp.dingo.api.model.Institution;
import com.dingoapp.dingo.api.model.User;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.dingoapp.dingo.util.ViewUtils.showOkDialog;

/**
 * Created by guestguest on 01/04/16.
 */
public abstract class AddEmailActivity extends BaseActivity implements EmailFragment.OnEmailFragmentActionListener,
        NameFragment.OnNameFragmentActionListener,
        CredentialsFragment.OnNoEmailFragmentListener,
        CodeFragment.OnCodeFragmentListener{

    public static final String EXTRA_USER = "EXTRA_USER";
    boolean mUserHasNoEmail = false;
    private String mEmail;
    private String mEntityName;
    protected User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_email);

        mUser = (User)getIntent().getSerializableExtra(EXTRA_USER);

        Bundle bundle = new Bundle();
        bundle.putString(EmailFragment.EXTRA_HEADER, getEmailFragmentHeader());
        bundle.putString(EmailFragment.EXTRA_EMAIL_HINT, getEmailFragmentEmailHint());

        EmailFragment emailFragment = new EmailFragment();
        emailFragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, emailFragment)
                .commit();
    }

    protected abstract String getEmailFragmentEmailHint();

    protected abstract String getEmailFragmentHeader();

    /*
      OnEmailFragmentActionListener
     */
    @Override
    public void onSaveButtonClick(final String email, final FragmentCallback callback) {
        mUserHasNoEmail = false;
        addEmail(email,
                new ApiCallback<Institution>(this) {
                    @Override
                    public void success(Response<Institution> response) {
                        if (response.code() == Response.HTTP_200_OK) {
                            mEmail = email;
                            mEntityName = null; // institution is already registered
                            Bundle bundle = new Bundle();
                            bundle.putString(CodeFragment.EXTRA_EMAIL, email);
                            CodeFragment codeFragment = new CodeFragment();
                            codeFragment.setArguments(bundle);
                            getSupportFragmentManager().beginTransaction()
                                    .add(R.id.container, codeFragment)
                                    .addToBackStack("code_fragment")
                                    .commit();
                        }
                        else{
                            super.success(response);
                        }
                    }

                    @Override
                    public void clientError(Response<?> response, DingoError error) {
                        switch (error.code()){
                            case DingoError.ERR_ENTITY_NOT_REGISTERED:
                                mEmail = email;
                                Bundle bundle = new Bundle();
                                bundle.putString(NameFragment.EXTRA_HEADER, getNameFragmentHeader());
                                NameFragment nameFragment = new NameFragment();
                                nameFragment.setArguments(bundle);
                                getSupportFragmentManager().beginTransaction()
                                        .add(R.id.container, nameFragment)
                                        .addToBackStack("name_fragment")
                                        .commit();
                                break;
                            case DingoError.ERR_ENTITY_EMAIL_ALREADY_BEING_USED:
                                showOkDialog(AddEmailActivity.this, R.string.add_email_already_being_used);
                                break;
                            default:
                                super.clientError(response, error);
                        }

                    }

                    @Override
                    public void onFinish() {
                        callback.onFinish();
                    }
                });
    }

    protected abstract String getNameFragmentHeader();

    @Override
    public void onNoEmailButtonClick() {
        mUserHasNoEmail = true;
        Bundle bundle = new Bundle();
        bundle.putString(NameFragment.EXTRA_HEADER, getNameFragmentHeader());
        NameFragment nameFragment = new NameFragment();
        nameFragment.setArguments(bundle);
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
        mEntityName = name;
        if(mUserHasNoEmail){
            Bundle bundle = new Bundle();
            bundle.putString(CredentialsFragment.EXTRA_HEADER, getCredentialsFragmentHeader());
            CredentialsFragment credentialsFragment = new CredentialsFragment();
            credentialsFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, credentialsFragment)
                    .addToBackStack("no_email_fragment")
                    .commit();
        }
        else{
            CodeFragment codeFragment = new CodeFragment();
            Bundle bundle = new Bundle();
            bundle.putString(CodeFragment.EXTRA_EMAIL, mEmail);
            codeFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, codeFragment)
                    .addToBackStack("code_fragment")
                    .commit();
        }
    }

    protected abstract String getCredentialsFragmentHeader();

    /*
      OnNoEmailFragmentListener
     */

    @Override
    public void onSend(String filePath, final FragmentCallback callback) {
        // Get the dimensions of the View
        int targetW = 800;
        int targetH = 800;

        //File file = new File(filePath);
        String path = filePath.split(":")[1];
        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.max(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        //bmOptions.inPurgeable = true; //deprecated

        Bitmap bitmap = BitmapFactory.decodeFile(path, bmOptions);

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        try {
            final File imageFile = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    getExternalFilesDir(null)    /* directory */
            );

            OutputStream outputStream = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);

            Uri fileUri = Uri.fromFile(imageFile);
            sendDocument(mEntityName, fileUri,
                    new ApiCallback<Institution>(this) {
                        @Override
                        public void success(Response<Institution> response) {
                            imageFile.delete();

                        }

                        @Override
                        public void clientError(Response<?> response, DingoError error) {
                            super.clientError(response, error);

                        }

                        @Override
                        public void onFinish() {
                            callback.onFinish();
                        }
                    });

        }
        catch (IOException e){
            //todo
        }

    }

    @Override
    public void onResend(FragmentCallback callback){
        resendEmail();
    }

    @Override
    public void onConfirm(String code, final FragmentCallback callback){
        confirmPin(code, mEntityName,
                new ApiCallback<Institution>(this) {
                    @Override
                    public void clientError(Response<?> response, DingoError error) {
                        switch (error.code()){
                            case DingoError.ERR_ENTITY_EMAIL_CODE_WRONG:
                                showOkDialog(AddEmailActivity.this, R.string.email_confirmation_pin_wrong);
                                break;
                            default:
                                super.clientError(response, error);
                        }
                    }

                    @Override
                    public void success(Response<Institution> response) {
                        if (response.code() == Response.HTTP_200_OK) {
                            Institution institution = response.body();
                            if (institution.getStatus() == User.EntityStatus.C ||
                                    institution.getStatus() == User.EntityStatus.P) {
                                onConfirmedPin(response.body(), mEntityName);
                            }
                            else{
                                //not expected
                                super.success(response);
                            }
                        }
                        else{
                            super.success(response);
                        }
                    }

                    @Override
                    public void onFinish() {
                        callback.onFinish();
                    }
                });
    }

    protected abstract void addEmail(String email, Callback<Institution> callback);
    protected abstract void sendDocument(String institutionName, Uri fileUri, Callback<Institution> callback);
    protected abstract void resendEmail();
    protected abstract void confirmPin(String pin, String institutionName, Callback<Institution> callback);
    protected abstract void onConfirmedPin(Institution institution, String institutionName);


    protected final User getUser(){
        return mUser;
    }
    /*public interface Callback<T> {

        //public void onResponse(Response<T> response);
        //public void onFailure(Throwable t);

        void success(Response<T> response);
        void clientError(Response<?> response, DingoError error);
        void onFinish();
    }*/


}
