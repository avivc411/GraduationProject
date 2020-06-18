package com.Project.project.Report.Utilities;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;

public final class DateUtils {

    /**
     * Converting String to Date.
     *
     * @param stringDate - String of date, format type is: MM-dd-yyyy.
     * @return
     */
    public static Date convertStringToDate(String stringDate) {
        Date date = new Date();
        try {
            date = new SimpleDateFormat("MM-dd-yyyy").parse(stringDate);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * Converting Date to String.
     * String date format: MM-dd-yyyy.
     */
    public static String convertDateToString(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
        String strDate = dateFormat.format(date);
        return strDate;
    }

    /**
     * @return date in format
     */
    public static String convertDateToString(Date date, String format){
        return new SimpleDateFormat(format, Locale.getDefault()).format(date);
    }
}
