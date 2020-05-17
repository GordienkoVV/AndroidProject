package com.tracking.treking_gps;

import android.util.Log;

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
