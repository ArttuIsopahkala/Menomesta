package com.ardeapps.menomesta.utils;

import android.content.Context;
import android.location.Location;
import android.text.format.DateUtils;
import android.view.View;

import com.ardeapps.menomesta.AppRes;
import com.ardeapps.menomesta.PrefRes;
import com.ardeapps.menomesta.R;
import com.ardeapps.menomesta.objects.BarLocation;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import static com.ardeapps.menomesta.PrefRes.FACEBOOK_PERMISSION_DENY_CITIES;

/**
 * Created by Arttu on 4.5.2017.
 */
public class StringUtils {

    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("d.M.yyyy", Locale.getDefault());

    public static String getUnsupportedBarsText(ArrayList<String> unsupportedBarsList) {
        Context context = AppRes.getContext();
        int size = unsupportedBarsList.size();
        String unsupportedBarsText = "";
        if (size == 1) {
            unsupportedBarsText += context.getString(R.string.facebook_description_single, unsupportedBarsList.get(0));
        } else {
            String barsString = "";
            for (int i = 0; i < unsupportedBarsList.size(); i++) {
                if(i < 3 && unsupportedBarsList.get(i) != null) {
                    barsString += unsupportedBarsList.get(i);
                    if (i < size - 1) {
                        if (i == size - 2) {
                            barsString += " ja ";
                        } else {
                            barsString += ", ";
                        }
                    }
                } else {
                    break;
                }
            }
            if (size > 3) {
                barsString += "...";
            }
            unsupportedBarsText += context.getString(R.string.facebook_description_multiple, barsString);
        }
        return unsupportedBarsText;
    }

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
        return text == null || text.trim().equals("");
    }

    public static boolean areSame(String value1, String value2) {
        return value1 != null && value2 != null && value1.equals(value2);
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
        return simpleDateFormat.format(new Date(milliseconds));
    }

    public static String getDateText(long milliseconds, boolean numeric, boolean lowerCase) {
        if (numeric) {
            return getDateText(milliseconds);
        } else {
            Context context = AppRes.getContext();
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(milliseconds);
            String dateString;
            if (DateUtils.isToday(milliseconds)) {
                dateString = context.getString(R.string.today);
            } else if (DateUtil.isDateTomorrow(milliseconds)) {
                dateString = context.getString(R.string.tomorrow);
            } else if (DateUtil.isOnThisWeek(milliseconds)) {
                int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
                dateString = getWeekDay(dayOfWeek) + "na";
            } else {
                dateString = c.get(Calendar.DAY_OF_MONTH) + ". ";
                int month = c.get(Calendar.MONTH);
                if (month == Calendar.JANUARY) {
                    dateString += context.getString(R.string.january);
                } else if (month == Calendar.FEBRUARY) {
                    dateString += context.getString(R.string.february);
                } else if (month == Calendar.MARCH) {
                    dateString += context.getString(R.string.march);
                } else if (month == Calendar.APRIL) {
                    dateString += context.getString(R.string.april);
                } else if (month == Calendar.MAY) {
                    dateString += context.getString(R.string.may);
                } else if (month == Calendar.JUNE) {
                    dateString += context.getString(R.string.june);
                } else if (month == Calendar.JULY) {
                    dateString += context.getString(R.string.july);
                } else if (month == Calendar.AUGUST) {
                    dateString += context.getString(R.string.august);
                } else if (month == Calendar.SEPTEMBER) {
                    dateString += context.getString(R.string.september);
                } else if (month == Calendar.OCTOBER) {
                    dateString += context.getString(R.string.october);
                } else if (month == Calendar.NOVEMBER) {
                    dateString += context.getString(R.string.november);
                } else if (month == Calendar.DECEMBER) {
                    dateString += context.getString(R.string.december);
                }
            }
            return lowerCase ? dateString.toLowerCase() : dateString;
        }
    }

    private static String getWeekDay(int dayOfWeek) {
        Context context = AppRes.getContext();
        if (dayOfWeek == Calendar.MONDAY) {
            return context.getString(R.string.monday);
        } else if (dayOfWeek == Calendar.TUESDAY) {
            return context.getString(R.string.tuesday);
        } else if (dayOfWeek == Calendar.WEDNESDAY) {
            return context.getString(R.string.wednesday);
        } else if (dayOfWeek == Calendar.THURSDAY) {
            return context.getString(R.string.thursday);
        } else if (dayOfWeek == Calendar.FRIDAY) {
            return context.getString(R.string.friday);
        } else if (dayOfWeek == Calendar.SATURDAY) {
            return context.getString(R.string.saturday);
        } else if (dayOfWeek == Calendar.SUNDAY) {
            return context.getString(R.string.sunday);
        } else {
            return "";
        }
    }

    public static String getWeekDayText(long milliseconds) {
        Context context = AppRes.getContext();
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(milliseconds);
        int date = cal.get(Calendar.DAY_OF_WEEK);
        if (cal.get(Calendar.HOUR_OF_DAY) < 6) {
            if (date == 1) {
                date = 7;
            } else date--;
        }

        String dateText = getWeekDay(date);
        if (cal.get(Calendar.HOUR_OF_DAY) < 6) {
            dateText += " " + context.getString(R.string.night);
        }
        return dateText;
    }

    public static String getDistanceToText(BarLocation barLocation) {
        return getDistanceToText(barLocation.latitude, barLocation.longitude);
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
