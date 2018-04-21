package com.munye.utils;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Akash on 1/16/2017.
 */

public class Validation {

    public static boolean isPasswordMatch(String password , String confirmpassword){

        if(TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmpassword)){
            return false;
        }
        if(password.equals(confirmpassword))
        {
            return true;
        }
        return false;
    }

    public static boolean isEmailValid(String email) {
        if (null == email || email.length() == 0) {
            return false;
        }
        Pattern emailPattern = Pattern
                .compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                        + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
        Matcher emailMatcher = emailPattern.matcher(email);
        return emailMatcher.matches();
    }

    public static boolean isContactNoValid(String number){
        if(TextUtils.isEmpty(number) || number.length() > 10 || number.length()<10){
            return false;
        }
        else {
            return true;
        }
    }

    public static boolean isEmpty(String content){
        if(TextUtils.isEmpty(content)){
            return true;
        }
        return false;
    }

    public static boolean isValidPasswordLength(String password){
        if(password.length() < 6){
            return true;
        }
        return false;
    }
}
