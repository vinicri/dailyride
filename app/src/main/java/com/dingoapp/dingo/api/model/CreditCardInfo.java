package com.dingoapp.dingo.api.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by guestguest on 16/02/16.
 */
public class CreditCardInfo {

    String number;

    @SerializedName("expiration_month")
    int expirationMonth;

    @SerializedName("expiration_year")
    int expirationYear;
    int cvv;

    String name;
    String cpf;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public int getExpirationMonth() {
        return expirationMonth;
    }

    public void setExpirationMonth(int expirationMonth) {
        this.expirationMonth = expirationMonth;
    }

    public int getExpirationYear() {
        return expirationYear;
    }

    public void setExpirationYear(int expirationYear) {
        this.expirationYear = expirationYear;
    }

    public int getCvv() {
        return cvv;
    }

    public void setCvv(int cvv) {
        this.cvv = cvv;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }
}
