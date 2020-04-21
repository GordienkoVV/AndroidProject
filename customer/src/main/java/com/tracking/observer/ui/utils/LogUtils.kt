package com.tracking.observer.ui.utils

import android.util.Log
import com.google.gson.Gson
import com.tracking.observer.BuildConfig

object LogUtils {
    private const val NULL = "<NULL>"
    private const val TAG = "log"
    private val gson = Gson()

    fun log(obj: Any?) {
        if (isDebug) {
            var txt = gson.toJson(obj)
            txt = txt ?: NULL
            Log.d(TAG, txt)
        }
    }

    fun log(tag: String?, obj: Any?) {
        if (isDebug) {
            var txt = gson.toJson(obj)
            txt = txt ?: NULL
            Log.d(tag, gson.toJson(obj))
        }
    }

    fun log(tag: String?, obj: Any?, throwable: Throwable?) {
        if (isDebug) {
            val txt = gson.toJson(obj) ?: "<NULL>"
            Log.d(tag, txt, throwable)
        }
    }

    fun log(txt: String?) {
        if (isDebug) {
            Log.d(TAG, txt ?: "<NULL>")
        }
    }

    fun log(tag: String?, txt: String?) {
        if (isDebug) {
            Log.d(tag, txt ?: "<NULL>")
        }
    }

    fun log(tag: String?, txt: String?, throwable: Throwable?) {
        if (isDebug) {
            Log.d(tag, txt, throwable)
        }
    }

    val isDebug: Boolean
        get() = BuildConfig.DEBUG
}
