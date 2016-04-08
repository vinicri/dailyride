package com.dingoapp.dingo.addemail;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.dingoapp.dingo.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by guestguest on 01/04/16.
 */
public class CredentialsFragment extends Fragment {


    private static final int REQUEST_IMAGE_CAPTURE = 1;

    View mDocumentBox;
    ImageView mDocumentPreview;
    Button mSendButton;

    OnNoEmailFragmentListener mListener;
    private String mCurrentPhotoPath;

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

        mDocumentPreview.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dispatchTakePictureIntent();
                    }
                }
        );
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
        void onSend(String filePath, FragmentCallback callback);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            Bundle extras = data.getExtras();
            Glide.with(getActivity())
                    .load(new File(mCurrentPhotoPath))
                    .into(mDocumentPreview);
            //Bitmap imageBitmap = (Bitmap) extras.get("data");
            //mDocumentPreview.setImageBitmap(imageBitmap);
            mListener.onSend(mCurrentPhotoPath, new FragmentCallback() {
                @Override
                public void onFinished() {
                    File file = new File(mCurrentPhotoPath);
                    file.delete();
                }
            });
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(null);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }
}
