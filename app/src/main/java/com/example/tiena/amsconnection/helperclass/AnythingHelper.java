package com.example.tiena.amsconnection.helperclass;

import android.app.Activity;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by tiena on 12/09/2017.
 */

public class AnythingHelper {
    public static String convertTimestampToDate(Long timestamp, String format){
        Date date =  new Date(timestamp);
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.ENGLISH);
        return sdf.format(date);
    }

    public static String convertTimestampToDate(Long timestamp){
        Date date =  new Date(timestamp);
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, HH:mm", Locale.ENGLISH);
        return sdf.format(date);
    }

    public static  void hideKeyboard(Activity activity){
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }
}
