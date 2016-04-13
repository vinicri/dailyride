package com.dingoapp.dingo.api;

import java.io.IOException;

import okhttp3.*;
import okhttp3.Response;

/**
 * Created by guestguest on 12/04/16.
 */
public class DingoAuthenticator implements Authenticator {
    @Override
    public Request authenticate(Route route, Response response) throws IOException {

        if (responseCount(response) > 1) {
            return null;
        }

        return response.request().newBuilder()
                .header("Authorization", "")
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
