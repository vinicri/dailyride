package com.lightgraylabs.dailyrides.api;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

import com.lightgraylabs.dailyrides.DingoApplication;
import com.lightgraylabs.dailyrides.MainActivity;
import com.lightgraylabs.dailyrides.api.model.User;
import com.lightgraylabs.dailyrides.util.CurrentUser;

import java.io.IOException;

import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

/**
 * Created by guestguest on 12/04/16.
 */
public class DingoAuthenticator implements Authenticator {
    @Override
    public Request authenticate(Route route, Response response) throws IOException {

        /*//todo review
        if(response.request().url().pathSegments() != null &&
                response.request().url().pathSegments().size() > 0 &&
                response.request().url().pathSegments().get(0).equals("refreshtoken")){
            //logoff user

        }*/

        //user is not logged in, so 401 is as it should be
        if(!CurrentUser.getInstance().isLoggedIn()){
            return null;
            //never expected to happen
        }

        /*if(responseCount(response) > 1){

        }*/


        com.lightgraylabs.dailyrides.api.Response<User.OAuthToken> refreshTokenResponse = DingoApiOAuthService.getInstance().refreshToken(CurrentUser.getUser().getAuthToken().getRefreshToken());
        if(refreshTokenResponse.code() == com.lightgraylabs.dailyrides.api.Response.HTTP_200_OK){
            CurrentUser.getInstance().updateOAuthToken(refreshTokenResponse.body());
        }
        else if (refreshTokenResponse.code() == com.lightgraylabs.dailyrides.api.Response.HTTP_401_UNAUTHORIZED){
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(
                    new Runnable() {
                        @Override
                        public void run() {
                            Context context = DingoApplication.getAppContext();
                            CurrentUser.getInstance().logout();
                            Intent intent = new Intent(context, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            context.startActivity(intent);
                        }
                    }
            );
            return null;
        }
        else{
            return null; //// FIXME: 17/04/16 todo report unexpected HTTP code
        }

        return response.request().newBuilder()
                .header("Authorization", "Bearer " + CurrentUser.getInstance().getAccessToken())
                .build();

    }

    private int responseCount(Response response) {
        int result = 1;
        while ((response = response.priorResponse()) != null) {
            result++;
        }
        return result;
    }
}
