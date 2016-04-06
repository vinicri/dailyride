package com.dingoapp.dingo.chat.api;

import android.content.Context;
import android.net.Uri;

import com.dingoapp.dingo.DingoApplication;
import com.dingoapp.dingo.api.Callback;
import com.dingoapp.dingo.api.DingoApiService;
import com.dingoapp.dingo.api.Response;
import com.dingoapp.dingo.api.model.Token;
import com.dingoapp.dingo.util.SettingsUtil;
import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;


/**
 * Created by guestguest on 05/04/16.
 */
public class FirebaseApi {

    public static final String BASE_URL = "https://dailyride.firebaseio.com/";
    public static final String CHATS_ENDPOINT =  "/dingo/chats/";
    public static final String MESSAGES_ENDPOINT =  "/messages/";

    public static Uri getChatUrl(long offerId){
        Uri uri = Uri.parse(BASE_URL)
                .buildUpon()
                .appendPath("dingo")
                .appendPath("chats")
                .appendPath(String.valueOf(offerId))
                .appendPath("messages")
                .build();
        return uri;
    }

    //todo can context be removed?
    //todo review essa salada de fruta
    public static void getAuthenticatedSession(final Context context, final Uri uri, final AuthCallback callback, final boolean retry){
        final Firebase ref = new Firebase(uri.toString());
        final String token = SettingsUtil.getFirebaseToken(DingoApplication.getAppContext());

        final Firebase.AuthResultHandler authResultHandler = new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
                callback.onAuthenticated(ref);
            }

            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                if(!retry){
                    callback.onAuthenticationError();
                }
                else{
                    //retry
                    SettingsUtil.setFirebaseToken(context, null);
                    getAuthenticatedSession(context, uri, callback, false);
                }

            }
        };

        if (token == null){
            DingoApiService.getInstance().getFirebaseToken(
                    new Callback<Token>() {
                        @Override
                        public void onResponse(Response<Token> response) {
                            String token = response.body().getToken();
                            SettingsUtil.setFirebaseToken(context, token);
                            ref.authWithCustomToken(token, authResultHandler);
                        }

                        @Override
                        public void onFailure(Throwable t) {

                        }
                    }
            );
        }
        else{
            ref.authWithCustomToken(token, authResultHandler);
        }

    }


    public interface AuthCallback{
        public void onAuthenticated(Firebase ref);
        public void onAuthenticationError();
    }


}
