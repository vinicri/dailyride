package com.dingoapp.dingo.addemail;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import com.dingoapp.dingo.BaseActivity;
import com.dingoapp.dingo.R;
import com.dingoapp.dingo.api.Response;
import com.dingoapp.dingo.api.model.Institution;
import com.dingoapp.dingo.api.model.User;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

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
        bundle.putString(EmailFragment.EXTRA_USER_FIRST_NAME, mUser.getFirstName());

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
        mUserHasNoEmail = false;
        addEmail(email,
                new Callback<Institution>() {
                    @Override
                    public void onResponse(Response<Institution> response) {
                        callback.onFinished();
                        if (response.code() == Response.HTTP_200_OK) {
                            mEmail = email;
                            Institution institution = response.body();
                            if (institution.getStatus().equals(Institution.STATUS_REGISTERED)) {
                                mEntityName = null; // institution is already registered
                                Bundle bundle = new Bundle();
                                bundle.putString(CodeFragment.EXTRA_EMAIL, email);
                                CodeFragment codeFragment = new CodeFragment();
                                codeFragment.setArguments(bundle);
                                getSupportFragmentManager().beginTransaction()
                                        .add(R.id.container, codeFragment)
                                        .addToBackStack("code_fragment")
                                        .commit();
                            } else if (institution.getStatus().equals(Institution.STATUS_NOT_REGISTERED)) {
                                NameFragment nameFragment = new NameFragment();
                                getSupportFragmentManager().beginTransaction()
                                        .add(R.id.container, nameFragment)
                                        .addToBackStack("name_fragment")
                                        .commit();
                            } else {
                                //todo to server unexpected state
                            }
                        }


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
        mEntityName = name;
        if(mUserHasNoEmail){
            CredentialsFragment credentialsFragment = new CredentialsFragment();
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
                    new Callback<Institution>() {
                        @Override
                        public void onResponse(Response<Institution> response) {
                            imageFile.delete();
                            callback.onFinished();

                        }

                        @Override
                        public void onFailure(Throwable t) {
                        }
                    });

        }
        catch (IOException e){
            //todo
        }

    }

    @Override
    public void onResend(){
        resendEmail();
    }

    @Override
    public void onConfirm(String code, final FragmentCallback callback){
        confirmPin(code, mEntityName,
                new Callback<Institution>() {
                    @Override
                    public void onResponse(Response<Institution> response) {
                        callback.onFinished();
                        if (response.code() == Response.HTTP_200_OK) {
                            Institution institution = response.body();
                            if (institution.getStatus().equals(Institution.STATUS_DENIED)) {
                                showWrongPinDialog();
                            } else {
                                onConfirmedPin(response.body(), mEntityName);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        callback.onFinished();
                    }
                });
    }

    private void showWrongPinDialog(){
        new AlertDialog.Builder(this)
                .setMessage(R.string.email_confirmation_pin_wrong)
                .setPositiveButton(R.string.ok, null)
                .show();
    }

    protected abstract void addEmail(String email, Callback<Institution> callback);
    protected abstract void sendDocument(String institutionName, Uri fileUri, Callback<Institution> callback);
    protected abstract void resendEmail();
    protected abstract void confirmPin(String pin, String institutionName, Callback<Institution> callback);
    protected abstract void onConfirmedPin(Institution institution, String institutionName);

    public interface Callback<T> {

        public void onResponse(Response<T> response);
        public void onFailure(Throwable t);
    }


}
