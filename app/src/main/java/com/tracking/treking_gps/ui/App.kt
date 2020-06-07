package com.tracking.treking_gps.ui

import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDexApplication
import com.squareup.leakcanary.LeakCanary
import com.tracking.treking_gps.data.AppFacade
import com.tracking.treking_gps.utils.Logger

class App : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        initLeakCanary()
        AppFacade.initFacade(this)
    }

    private fun initLeakCanary() {
        if (!Logger.IS_ENABLED) return
        if (!LeakCanary.isInAnalyzerProcess(this)) {
            LeakCanary.install(this)
        }
    }

    companion object {
        init {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        }
    }
}
