package com.munye.utils;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Akash on 1/25/2017.
 */

public class Formatter {

    public static String getDoubleDigits(int number){
        return String.format("%02d",number);
    }


    public static String getDateInFormate(String date){

        Date myDate = null;
        try {
            SimpleDateFormat oldFormate = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
            myDate = oldFormate.parse(date);

        } catch (ParseException e) {
            AndyUtils.generateLog("Exception while formate date"+e);
        }
        SimpleDateFormat newFormate = new SimpleDateFormat("dd/MM/yy, hh:mm a");
        return newFormate.format(myDate);
    }


    public static String formateDigitAfterPoint(String data){
        DecimalFormat decimalFormat = new DecimalFormat("0.##");
        return String.valueOf(decimalFormat.format(Double.parseDouble(data)));
    }

    public static String invoiceDigitFormater(String data){
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        return String.valueOf(decimalFormat.format(Double.parseDouble(data)));
    }

}
