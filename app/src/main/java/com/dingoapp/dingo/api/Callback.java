package com.dingoapp.dingo.api;

import com.dingoapp.dingo.api.model.DingoError;

import java.io.IOException;

/**
 * Created by guestguest on 13/01/16.
 */
public interface Callback<T> {

    //public void onResponse(Response<T> response);
    //public void onFailure(Throwable t);

    void success(Response<T> response);
    /** Called for 401 responses. */
    void unauthenticated(Response<?> response);
    /** Called for [400, 500) responses, except 401. */
    void clientError(Response<?> response, DingoError error);
    /** Called for [500, 600) response. */
    void serverError(Response<?> response);
    /** Called for network errors while making the call. */
    void networkError(IOException e);
    /** Called for unexpected errors while making the call. */
    void unexpectedError(Throwable t);
}
