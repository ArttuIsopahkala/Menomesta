package com.ardeapps.menomesta.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Arttu on 6.5.2017.
 */
public class DateUtil {

    public static int getLatestValidYear() {
        return Calendar.getInstance().get(Calendar.YEAR) - 18;
    }

    public static int getEarliestValidYear() {
        return Calendar.getInstance().get(Calendar.YEAR) - 65;
    }

    public static Calendar getDefaultBirthday() {
        Calendar cal = Calendar.getInstance();
        int latestYear = Calendar.getInstance().get(Calendar.YEAR) - 18;
        cal.set(Calendar.YEAR, latestYear);
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal;
    }

    public static boolean isOnThisWeek(long milliseconds) {
        Calendar c = Calendar.getInstance();
        c.setFirstDayOfWeek(Calendar.MONDAY);

        // contextet hour, minutes, seconds and millis
        c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        Date monday = c.getTime();

        Date nextMonday = new Date(monday.getTime() + 7 * 24 * 60 * 60 * 1000);

        Date reference = new Date(milliseconds);

        return reference.after(monday) && reference.before(nextMonday);
    }

    public static boolean isAfterToday(long milliseconds) {
        Calendar cal = new GregorianCalendar();
        // reset hour, minutes, seconds and millis
        if (cal.get(Calendar.HOUR_OF_DAY) < 6) {
            cal.set(Calendar.DATE, cal.get(Calendar.DATE) - 1);
        }
        cal.set(Calendar.HOUR_OF_DAY, 6);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Date start = cal.getTime();
        Date end = new Date(start.getTime() + 24 * 60 * 60 * 1000);
        Date reference = new Date(milliseconds);

        return reference.after(end);
    }

    public static boolean isToday(long milliseconds) {
        Calendar cal = new GregorianCalendar();
        // reset hour, minutes, seconds and millis
        if (cal.get(Calendar.HOUR_OF_DAY) < 6) {
            cal.set(Calendar.DATE, cal.get(Calendar.DATE) - 1);
        }
        cal.set(Calendar.HOUR_OF_DAY, 6);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Date start = cal.getTime();
        Date end = new Date(start.getTime() + 24 * 60 * 60 * 1000);
        Date reference = new Date(milliseconds);

        return reference.after(start) && reference.before(end);
    }

    public static boolean isAfterYesterday(long milliseconds) {
        Calendar date = new GregorianCalendar();
        // reset hour, minutes, seconds and millis
        if (date.get(Calendar.HOUR_OF_DAY) < 6) {
            date.set(Calendar.DATE, date.get(Calendar.DATE) - 1);
        }
        date.set(Calendar.HOUR_OF_DAY, 6);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);
        return milliseconds > date.getTimeInMillis();
    }

    public static long getStartOfWeekInMillis() {
        Calendar cal = new GregorianCalendar();
        // reset hour, minutes, seconds and millis
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        cal.set(Calendar.HOUR_OF_DAY, 6);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return cal.getTimeInMillis();
    }

    public static long getEndOfWeekInMillis() {
        Calendar cal = new GregorianCalendar();
        // reset hour, minutes, seconds and millis
        cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 23);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);

        return cal.getTimeInMillis();
    }

    public static int getAgeInYears(long birthday) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(birthday);
        Calendar today = Calendar.getInstance();
        int age = today.get(Calendar.YEAR) - cal.get(Calendar.YEAR);
        cal.add(Calendar.YEAR, age);
        if (today.before(cal)) {
            age--;
        }
        return age;
    }
}
