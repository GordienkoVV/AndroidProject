package com.example.treking_gps;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.multidex.MultiDexApplication;

import com.squareup.leakcanary.LeakCanary;

public class App extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        initLeakCanary();
    }

    private void initLeakCanary() {
        if (!Logger.IS_ENABLED) return;

        if (!LeakCanary.isInAnalyzerProcess(this)) {
            LeakCanary.install(this);
        }
    }

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

}
