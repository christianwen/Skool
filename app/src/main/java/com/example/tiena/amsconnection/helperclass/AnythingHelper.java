package com.example.tiena.amsconnection.helperclass;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by tiena on 12/09/2017.
 */

public class AnythingHelper {
    public static String convertTimestampToDate(Long timestamp){
        Date date =  new Date(timestamp);
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, HH:mm", Locale.ENGLISH);
        return sdf.format(date);
    }
}
