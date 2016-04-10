package com.dingoapp.dingo.util;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * Created by guestguest on 10/04/16.
 */
public class TextWatcherPhoneBR implements TextWatcher {

    private boolean isUpdating;

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        String current = s.toString();

				/*
				 * Ok, here is the trick... calling setText below will recurse
				 * to this function, so we set a flag that we are actually
				 * updating the text, so we don't need to reprocess it...
				 */
        if (isUpdating) {
            isUpdating = false;
            return;

        }

				/* Strip any non numeric digit from the String... */
        String number = current.replaceAll("[^0-9]*", "");
        if (number.length() > 11)
            number = number.substring(0, 11);

        int length = number.length();

        /* Pad the number to 10 characters... */
        String paddedNumber = padNumber(number, 11);

				/* Split phone number into parts... */
        String ddd = paddedNumber.substring(0, 2);
        String part1;
        String part2;
        if(length > 10){
            part1 = paddedNumber.substring(2, 7);
            part2 = paddedNumber.substring(7, 11).trim();
        }
        else {
            part1 = paddedNumber.substring(2, 6);
            part2 = paddedNumber.substring(6, 11).trim();
        }

				/* build the masked phone number... */
        String phone;
        if(length > 6) {
            phone = "(" + ddd + ") " + part1 + "-" + part2;
        }
        else{
            phone = "(" + ddd + ") " + part1;
        }

				/*
				 * Set the update flag, so the recurring call to
				 * afterTextChanged won't do nothing...
				 */
        isUpdating = true;
        //s.clear();
        s.replace(0, s.length(), phone);
        //DRPhoneEditText.this.setText(phone);

        //DRPhoneEditText.this.setSelection(positioning[length]);
    }

    public String getCleanText(String text) {
        text.replaceAll("[^0-9]*", "");
        return text;

    }

    protected String padNumber(String number, int maxLength) {
        String padded = new String(number);
        for (int i = 0; i < maxLength - number.length(); i++)
            padded += " ";
        return padded;

    }
}
