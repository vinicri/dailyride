package com.dingoapp.dingo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.dingoapp.dingo.api.Callback;
import com.dingoapp.dingo.api.DingoApiService;
import com.dingoapp.dingo.api.Response;
import com.dingoapp.dingo.api.model.User;
import com.dingoapp.dingo.rides.RidesActivity;
import com.dingoapp.dingo.util.CurrentUser;
import com.dingoapp.dingo.util.SettingsUtil;
import com.dingoapp.dingo.welcome.WelcomeActivity;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import java.util.Arrays;

import static com.dingoapp.dingo.util.LogUtil.makeLogTag;

public class MainActivity extends AppCompatActivity {

    private Button facebookLogin;
    private CallbackManager fbCallbackManager;
    private final String TAG = makeLogTag("MainActivity");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //PreferenceManager.getDefaultSharedPreferences(this).edit().clear().commit();
        if(CurrentUser.getInstance().isLoggedIn()){
            Intent intent = new Intent(this, RidesActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        setContentView(R.layout.activity_main);

        /*
         * Facebook login
         */
        FacebookSdk.sdkInitialize(getApplicationContext());
        fbCallbackManager = CallbackManager.Factory.create();
        facebookLogin = (Button)findViewById(R.id.facebook_login);

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
                                                        new Callback<User>() {
                                                            @Override
                                                            public void onResponse(Response<User> response) {
                                                                Log.d(TAG, response.toString());


                                                                if(response.code() == Response.HTTP_200_OK){
                                                                }

                                                                else if(response.code() == Response.HTTP_201_CREATED){
                                                                    SettingsUtil.setCurrentUser(MainActivity.this, response.body());
                                                                    Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
                                                                    startActivity(intent);
                                                                }
                                                            }

                                                            @Override
                                                            public void onFailure(Throwable t) {
                                                                Log.d(TAG, t.toString());
                                                            }
                                                        }
                                                );
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        fbCallbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
