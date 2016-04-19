package com.lightgraylabs.dailyrides.chat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by guestguest on 10/03/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChatUser {

    @JsonProperty("first_name")
    String firstName;

    @JsonProperty("last_name")
    String lastName;

    //Date timestamp;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFullName(){
        String fullName = firstName;
        if(lastName != null && lastName.length() > 0){
            fullName += " " + lastName;
        }
        return fullName;
    }

}
