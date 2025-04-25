package com.motocoredb.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat DATETIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static Date parseDate(String date) {
        try {
            return DATE_FORMAT.parse(date);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid date format");
        }
    }

    public static Date parseDateTime(String datetime) {
        try {
            return DATETIME_FORMAT.parse(datetime);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid datetime format");
        }
    }

    public static String formatDate(Date date) {
        return DATE_FORMAT.format(date);
    }

    public static String formatDateTime(Date date) {
        return DATETIME_FORMAT.format(date);
    }
}