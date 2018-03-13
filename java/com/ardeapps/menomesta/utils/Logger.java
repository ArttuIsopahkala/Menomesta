package com.ardeapps.menomesta.utils;

import android.util.Log;
import android.widget.Toast;

import com.ardeapps.menomesta.AppRes;
import com.ardeapps.menomesta.BuildConfig;

/**
 * Created by Arttu on 21.8.2017.
 */

public class Logger {
    public static void log(Object message) {
        if (BuildConfig.DEBUG) {
            String className = new Exception().getStackTrace()[1].getFileName();
            Log.e(className, message + "");
        }
    }
    public static void toast(Object message) {
        Toast.makeText(AppRes.getContext(), message+"", Toast.LENGTH_LONG).show();
    }
    public static void toast(int resourceId) {
        Toast.makeText(AppRes.getContext(), resourceId, Toast.LENGTH_SHORT).show();
    }
}
