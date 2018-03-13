package com.ardeapps.menomesta.utils;

import android.content.Context;
import android.location.Location;
import android.text.format.DateUtils;

import com.ardeapps.menomesta.AppRes;
import com.ardeapps.menomesta.R;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Arttu on 4.5.2017.
 */
public class StringUtils {

    public static String getRatingText(double rating) {
        DecimalFormatSymbols decimalSymbol = new DecimalFormatSymbols(Locale.getDefault());
        decimalSymbol.setDecimalSeparator('.');
        DecimalFormat df = new DecimalFormat("#.#", decimalSymbol);
        return df.format(rating);
    }

    public static String getAgeText(long birthTime) {
        Calendar birthday = Calendar.getInstance();
        birthday.setTimeInMillis(birthTime);
        Calendar today = Calendar.getInstance();
        int age = today.get(Calendar.YEAR) - birthday.get(Calendar.YEAR);
        birthday.add(Calendar.YEAR, age);
        if (today.before(birthday)) {
            age--;
        }
        return AppRes.getContext().getString(R.string.age, String.valueOf(age));
    }

    public static boolean isEmptyString(String text) {
        return text == null || text.equals("");
    }

    public static boolean areSame(String value1, String value2) {
        return value1 != null && value2 != null && value1.equals(value2);
    }

    public static String getDateTimeText(long milliseconds, boolean dateNumeric, boolean toLowerCase) {
        Context context = AppRes.getContext();
        String dateTimeString = getDateText(milliseconds, dateNumeric, toLowerCase);
        dateTimeString += " " + context.getString(R.string.clock) + " " + getTimeText(milliseconds);
        return toLowerCase ? dateTimeString.toLowerCase() : dateTimeString;
    }

    public static String getTimeText(long milliseconds) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(milliseconds);

        int minutes = c.get(Calendar.MINUTE);
        String minutesString = minutes < 10 ? "0" + minutes : minutes + "";

        int hours = c.get(Calendar.HOUR_OF_DAY);
        String hoursString = hours < 10 ? "0" + hours : hours + "";

        return hoursString + ":" + minutesString;
    }

    public static String getCommentTimeText(long milliseconds) {
        Date date = new Date();
        long time_difference = date.getTime() - milliseconds;
        int seconds = (int) (time_difference / 1000) % 60;
        int minutes = (int) ((time_difference / (1000 * 60)) % 60);
        int hours = (int) ((time_difference / (1000 * 60 * 60)) % 24);
        int days = (int) (time_difference / (1000 * 60 * 60 * 24));

        String text;
        if (days == 0) {
            if (hours == 0) {
                if (minutes == 0) {
                    text = seconds + "s";
                } else text = minutes + "m";
            } else text = hours + "t";
        } else text = days + "vrk";
        return text;
    }

    public static String getDateTimeText(long milliseconds) {
        Context context = AppRes.getContext();
        return getDateText(milliseconds, false, false) + " " + context.getString(R.string.clock) + " " + getTimeText(milliseconds);
    }

    public static String getDateText(long milliseconds) {
        return new SimpleDateFormat("d.M.yyyy", Locale.getDefault()).format(new Date(milliseconds));
    }

    public static String getDateText(long milliseconds, boolean numeric, boolean lowerCase) {
        if (numeric) {
            Date date = new Date(milliseconds);
            SimpleDateFormat sdf = new SimpleDateFormat("d.M.yyyy", Locale.ENGLISH);
            return sdf.format(date);
        } else {
            Context context = AppRes.getContext();
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(milliseconds);
            String dateString = "";
            String extra = "na";
            if (DateUtils.isToday(milliseconds)) {
                dateString = context.getString(R.string.today);
            } else if (DateUtil.isOnThisWeek(milliseconds)) {
                switch (c.get(Calendar.DAY_OF_WEEK)) {
                    case Calendar.MONDAY:
                        dateString = context.getString(R.string.monday) + extra;
                        break;
                    case Calendar.TUESDAY:
                        dateString = context.getString(R.string.tuesday) + extra;
                        break;
                    case Calendar.WEDNESDAY:
                        dateString = context.getString(R.string.wednesday) + extra;
                        break;
                    case Calendar.THURSDAY:
                        dateString = context.getString(R.string.thursday) + extra;
                        break;
                    case Calendar.FRIDAY:
                        dateString = context.getString(R.string.friday) + extra;
                        break;
                    case Calendar.SATURDAY:
                        dateString = context.getString(R.string.saturday) + extra;
                        break;
                    case Calendar.SUNDAY:
                        dateString = context.getString(R.string.sunday) + extra;
                        break;
                }
            } else {
                dateString = c.get(Calendar.DAY_OF_MONTH) + ". ";
                switch (c.get(Calendar.MONTH)) {
                    case Calendar.JANUARY:
                        dateString += context.getString(R.string.january);
                        break;
                    case Calendar.FEBRUARY:
                        dateString += context.getString(R.string.february);
                        break;
                    case Calendar.MARCH:
                        dateString += context.getString(R.string.march);
                        break;
                    case Calendar.APRIL:
                        dateString += context.getString(R.string.april);
                        break;
                    case Calendar.MAY:
                        dateString += context.getString(R.string.may);
                        break;
                    case Calendar.JUNE:
                        dateString += context.getString(R.string.june);
                        break;
                    case Calendar.JULY:
                        dateString += context.getString(R.string.july);
                        break;
                    case Calendar.AUGUST:
                        dateString += context.getString(R.string.august);
                        break;
                    case Calendar.SEPTEMBER:
                        dateString += context.getString(R.string.september);
                        break;
                    case Calendar.OCTOBER:
                        dateString += context.getString(R.string.october);
                        break;
                    case Calendar.NOVEMBER:
                        dateString += context.getString(R.string.november);
                        break;
                    case Calendar.DECEMBER:
                        dateString += context.getString(R.string.december);
                        break;
                }
            }
            return lowerCase ? dateString.toLowerCase() : dateString;
        }
    }

    public static String getWeekDayText(long milliseconds) {
        Context context = AppRes.getContext();
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(milliseconds);
        String dateText = "";
        int date = cal.get(Calendar.DAY_OF_WEEK);
        if (cal.get(Calendar.HOUR_OF_DAY) < 6) {
            if (date == 1) {
                date = 7;
            } else date--;
        }
        switch (date) {
            case 1:
                dateText = context.getString(R.string.sunday);
                break;
            case 2:
                dateText = context.getString(R.string.monday);
                break;
            case 3:
                dateText = context.getString(R.string.tuesday);
                break;
            case 4:
                dateText = context.getString(R.string.wednesday);
                break;
            case 5:
                dateText = context.getString(R.string.thursday);
                break;
            case 6:
                dateText = context.getString(R.string.friday);
                break;
            case 7:
                dateText = context.getString(R.string.saturday);
                break;
        }
        if (cal.get(Calendar.HOUR_OF_DAY) < 6) {
            dateText += " " + context.getString(R.string.night);
        }
        return dateText;
    }

    public static String getDistanceToText(double targetLat, double targetLng) {
        AppRes appRes = (AppRes) AppRes.getContext();
        Location location = appRes.getLocation();
        String distanceText = "";
        if (location != null) {
            float[] results = new float[1];
            Location.distanceBetween(location.getLatitude(), location.getLongitude(), targetLat, targetLng, results);
            long meters = 50 * Math.round(results[0] / 50);
            if (meters < 1000) {
                if (meters < 50)
                    distanceText = "50m";
                else
                    distanceText = meters + "m";
            } else {
                distanceText = String.format(Locale.ENGLISH, "%.1f", (float) meters / 1000) + "km";
            }
        }
        return distanceText;
    }
}
