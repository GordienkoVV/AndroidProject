package com.tracking.treking_gps.utils;

import android.util.Log;

import com.tracking.treking_gps.BuildConfig;

public final class Logger {

    public static final boolean IS_ENABLED = BuildConfig.DEBUG;


    public static void log(String message) {
        if (!IS_ENABLED) return;
        Log.d("", message);
    }

    public static void log(String tag, String message) {
        if (!IS_ENABLED) return;
        Log.d(tag, message);
    }

    public static void log(String tag, String message, Throwable throwable) {
        if (!IS_ENABLED) return;
        Log.d(tag, message, throwable);
    }

}
