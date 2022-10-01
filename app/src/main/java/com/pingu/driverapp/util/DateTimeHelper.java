package com.pingu.driverapp.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DateTimeHelper {
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    public static List<String> getPast3DaysDate() {

        try {
            List<String> list = new ArrayList<>();

            final DateFormat dateFormat = new SimpleDateFormat("d MMM yyyy");
            final Date todayDate = Calendar.getInstance().getTime();

            Calendar calMinusOneDay = Calendar.getInstance();
            calMinusOneDay.setTime(todayDate);
            calMinusOneDay.add(Calendar.DATE, -1);
            Date dateMinusOneDay = calMinusOneDay.getTime();
            String dateMinusOneDayStr = dateFormat.format(dateMinusOneDay);
            list.add(dateMinusOneDayStr);

            Calendar calMinusTwoDay = Calendar.getInstance();
            calMinusTwoDay.setTime(todayDate);
            calMinusTwoDay.add(Calendar.DATE, -2);
            Date dateMinusTwoDay = calMinusTwoDay.getTime();
            String dateMinusTwoDayStr = dateFormat.format(dateMinusTwoDay);
            list.add(dateMinusTwoDayStr);

            Calendar calMinusThreeDay = Calendar.getInstance();
            calMinusThreeDay.setTime(todayDate);
            calMinusThreeDay.add(Calendar.DATE, -3);
            Date dateMinusThreeDay = calMinusThreeDay.getTime();
            String dateMinusThreeDayStr = dateFormat.format(dateMinusThreeDay);
            list.add(dateMinusThreeDayStr);

            return list;

        } catch (Exception ex) {
            return null;
        }
    }

    public static String getTodayDateForQuery() {
        try {
            DateFormat df = new SimpleDateFormat("d MMM yyyy");
            String dateTime = df.format(Calendar.getInstance().getTime());
            return dateTime;

        } catch (Exception ex) {
            return "";
        }
    }

    public static String getCurrentDate() {
        try {
            DateFormat df = new SimpleDateFormat("d MMM yyyy");
            String dateTime = df.format(Calendar.getInstance().getTime());
            return dateTime;

        } catch (Exception ex) {
            return "";
        }
    }

    public static String getCurrentDateTime() {
        try {
            DateFormat df = new SimpleDateFormat("d MMM yyyy • HH:mm");
            String dateTime = df.format(Calendar.getInstance().getTime());
            return dateTime;

        } catch (Exception ex) {
            return "";
        }
    }

    public static String formatDateTime(final String dateTimeInput) {
        try {
            SimpleDateFormat originalDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date originalDate = originalDateFormat.parse(dateTimeInput);

            DateFormat df = new SimpleDateFormat("d MMM yyyy • HH:mm");
            String dateTime = df.format(originalDate);
            return dateTime;

        } catch (Exception ex) {
            return dateTimeInput;
        }
    }

    public static String getNowDateTime() {
        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dateTime = df.format(new Date());
            return dateTime;

        } catch (Exception ex) {
            return "";
        }
    }

    public static String getDateOnly(final String dateTimeInput) {
        try {
            SimpleDateFormat originalDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date originalDate = originalDateFormat.parse(dateTimeInput);

            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            String dateTime = df.format(originalDate);
            return dateTime;

        } catch (Exception ex) {
            return dateTimeInput;
        }
    }

    public static String getTimeOnly(final String dateTimeInput) {
        try {
            SimpleDateFormat originalDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date originalDate = originalDateFormat.parse(dateTimeInput);

            DateFormat df = new SimpleDateFormat("HH:mm");
            String dateTime = df.format(originalDate);
            return dateTime;

        } catch (Exception ex) {
            return dateTimeInput;
        }
    }

    public static String getTimeAgo(final String datetimeStr) {
        long time = milliseconds(datetimeStr);
        if (time < 1000000000000L) {
            time *= 1000;
        }

        long now = System.currentTimeMillis();
        if (time > now || time <= 0) {
            return null;
        }

        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "just now";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "a minute ago";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " minutes ago";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "an hour ago";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " hours ago";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "yesterday";
        } else {
            return diff / DAY_MILLIS + " days ago";
        }
    }

    private static long milliseconds(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date mDate = sdf.parse(date);
            long timeInMilliseconds = mDate.getTime();
            return timeInMilliseconds;
        } catch (ParseException e) {
        }

        return 0;
    }
}
