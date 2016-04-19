package com.lightgraylabs.dailyrides.api;

/**
 * Created by guestguest on 13/01/16.
 */
public class Response<T> {

    public static final int HTTP_200_OK = 200;
    public static final int HTTP_201_CREATED = 201;
    public static final int HTTP_204_NO_CONTENT = 204;

    public static final int HTTP_401_UNAUTHORIZED = 401;


    int statusCode;
    T body;
    String error;

    public Response(int statusCode, T body){
        this.statusCode = statusCode;
        this.body = body;
    }

    public Response(int statusCode, T body, String error){
        this.statusCode = statusCode;
        this.body = body;
        this.error = error;
    }


    public int code(){
        return statusCode;
    }

    public T body(){
        return body;
    }

    public String error(){
        return error;
    }


}
