package com.dingoapp.dingo.api;

import com.dingoapp.dingo.api.model.DingoError;

import java.io.IOException;

/**
 * Created by guestguest on 09/04/16.
 */
public class CallbackAdapter<T> implements Callback<T> {
    @Override
    public void success(Response<T> response) {

    }

    @Override
    public void unauthenticated(Response<?> response) {

    }

    @Override
    public void clientError(Response<?> response, DingoError error) {

    }

    @Override
    public void serverError(Response<?> response) {

    }

    @Override
    public void networkError(IOException e) {

    }

    @Override
    public void unexpectedError(Throwable t) {

    }
}
