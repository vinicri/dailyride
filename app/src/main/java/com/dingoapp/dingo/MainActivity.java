package com.dingoapp.dingo;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.crashlytics.android.Crashlytics;
import com.dingoapp.dingo.api.ApiCallback;
import com.dingoapp.dingo.api.DingoApiService;
import com.dingoapp.dingo.api.Response;
import com.dingoapp.dingo.api.model.User;
import com.dingoapp.dingo.gcm.RegistrationIntentService;
import com.dingoapp.dingo.rides.RidesActivity;
import com.dingoapp.dingo.util.CurrentUser;
import com.dingoapp.dingo.util.Installation;
import com.dingoapp.dingo.util.SettingsUtil;
import com.dingoapp.dingo.welcome.WelcomeActivity;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.util.Arrays;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.dingoapp.dingo.util.LogUtil.makeLogTag;

public class MainActivity extends AppCompatActivity {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    @Bind(R.id.background) ImageView mBackground;
    @Bind(R.id.buttons_box)View mButtonsBox;
    @Bind(R.id.login_box)View mLoginBox;
    @Bind(R.id.sign_up)Button mSignUpButton;
    @Bind(R.id.login1)Button mLogin1Button;
    private Button facebookLogin;
    private CallbackManager fbCallbackManager;
    private final String TAG = makeLogTag("MainActivity");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //PreferenceManager.getDefaultSharedPreferences(this).edit().clear().commit();
        if (!SettingsUtil.getSentTokenToServer(this) && checkPlayServices()) {

            // Start IntentService to register this application with GCM.
            registerGCM();
        }

        if(CurrentUser.getInstance().isLoggedIn()){
            Crashlytics.setUserIdentifier(String.valueOf(CurrentUser.getUser().getId()));
            Crashlytics.setUserEmail(CurrentUser.getUser().getEmail());
            Intent intent = new Intent(this, RidesActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        //user not logged, set installation id as identifier for Crashlytics
        Crashlytics.setUserIdentifier(Installation.id(this));

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Glide.with(this).load(R.drawable.friends_green).transform(new CenterCrop(this)).into(mBackground);

        //int height = mLoginBox.getMeasuredHeight();

        /*
         * Facebook login
         */

        FacebookSdk.sdkInitialize(getApplicationContext());
        fbCallbackManager = CallbackManager.Factory.create();
        facebookLogin = (Button)findViewById(R.id.facebook_login);

        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        int initialY = size.y;
        final int loginBoxHeight = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 215, getResources().getDisplayMetrics());
        mLoginBox.setTranslationY(loginBoxHeight);
        final int finalY  = initialY - loginBoxHeight;


        mLogin1Button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                      //  ObjectAnimator.ofInt(mButtonsBox, "translationY", 0, -loginBoxHeight).setDuration(250).start();
                        mButtonsBox.animate().translationYBy(-loginBoxHeight).alpha(0).setDuration(250).start();
                        mLoginBox.animate().translationYBy(-loginBoxHeight).setDuration(250).start();
                        mLoginBox.setVisibility(View.VISIBLE);

                        // translateY.setDuration(400);
                       // translateY.start();
                        /*ValueAnimator translateYAnimator = ValueAnimator.ofInt(0, -loginBoxHeight);
                        translateYAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
                        translateYAnimator.setDuration(750);
                        translateYAnimator.addUpdateListener(
                                new ValueAnimator.AnimatorUpdateListener() {
                                    @Override
                                    public void onAnimationUpdate(ValueAnimator animation) {
                                        int value = (Integer) animation.getAnimatedValue();
                                        mButtonsBox.setTranslationY(value);
                                        mLoginBox.setTranslationY(value);

                                        //mButtonsBox.requestLayout();
                                    }
                                }
                        );

                        translateYAnimator.addListener(
                                new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationStart(Animator animation) {
                                        mLoginBox.setVisibility(View.VISIBLE);
                                    }
                                }
                        );
                        translateYAnimator.start();*/
                    }
                }
        );



        facebookLogin.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(AccessToken.getCurrentAccessToken() != null){
                            LoginManager.getInstance().logOut();
                        }

                        LoginManager.getInstance()
                                .logInWithReadPermissions(MainActivity.this,
                                        Arrays.asList(
                                                "public_profile",
                                                "email",
                                                "user_friends"));
//                                                "user_location",
//                                                "user_birthday",
//                                                "user_about_me",
//                                                "user_education_history",
//                                                "user_hometown",
//                                                "user_work_history",
//                                                "user_website"


                        LoginManager.getInstance()
                                .registerCallback(fbCallbackManager,
                                        new FacebookCallback<LoginResult>() {
                                            @Override
                                            public void onSuccess(LoginResult loginResult) {
                                                Log.d(TAG, loginResult.toString());
                                                User user = new User();
                                               // user.setFirstName("Vinicius");
                                               // user.setLastName("Silva");
                                                user.setFbAccessToken(AccessToken.getCurrentAccessToken().getToken());
                                               // user.setEmail("vinicri@gmail.com");
                                                DingoApiService.getInstance().registerWithFacebook(
                                                        user,
                                                        new ApiCallback<User>(MainActivity.this) {
                                                            @Override
                                                            public void success(Response<User> response) {
                                                                Log.d(TAG, response.toString());


                                                                if(response.code() == Response.HTTP_200_OK){
                                                                    User user = response.body();
                                                                    SettingsUtil.setCurrentUser(MainActivity.this, user);
                                                                    //register gcm after set user so the http request will contain user bearer
                                                                    registerGCM();
                                                                    if(user.getAcceptedTerms()){
                                                                        Intent intent = new Intent(MainActivity.this, RidesActivity.class);
                                                                        startActivity(intent);
                                                                    }
                                                                    else{
                                                                        Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
                                                                        startActivity(intent);
                                                                    }

                                                                }

                                                                else if(response.code() == Response.HTTP_201_CREATED){
                                                                    SettingsUtil.setCurrentUser(MainActivity.this, response.body());
                                                                    //register gcm after set user so the http request will contain user bearer
                                                                    registerGCM();
                                                                    Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
                                                                    startActivity(intent);
                                                                }
                                                            }
                                                        });
                                            }

                                            @Override
                                            public void onCancel() {

                                            }

                                            @Override
                                            public void onError(FacebookException error) {
                                                Log.d(TAG, error.toString());
                                            }
                                        });
                    }

                }
        );

        mSignUpButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
                        startActivity(intent);
                    }
                }
        );
    }

    private void registerGCM(){
        Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        fbCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }
}
