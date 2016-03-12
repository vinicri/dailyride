package com.dingoapp.dingo.chat;

/**
 * Created by guestguest on 10/03/16.
 */
public class ChatMessage {

    public final static char TYPE_SYSTEM = 'S';
    public final static char TYPE_USER = 'U';

    //'S' - system
    //'U' - user
    private char type;

    private String message;
    private Long userId;

    public char getType() {
        return type;
    }

    public void setType(char type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
