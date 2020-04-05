package com.example.maps.app

import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDexApplication
import com.example.maps.LogUtils
import io.reactivex.plugins.RxJavaPlugins

class App : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        initRx()
    }

    private fun initRx() {
        RxJavaPlugins.setErrorHandler {
            LogUtils.log("App", "RxJavaPlugins error handle", it)
        }
    }

    companion object {
        init {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        }
    }

}
