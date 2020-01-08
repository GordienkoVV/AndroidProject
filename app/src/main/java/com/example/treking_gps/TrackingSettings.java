package com.example.treking_gps;

import android.Manifest;
import android.os.Build;

import com.tbruyelle.rxpermissions2.RxPermissions;

import butterknife.OnClick;

public final class TrackingSettings extends FragmentButterKnife {


    @Override
    protected int getLayoutId() {
        return R.layout.screen_tracking_settings;
    }

    @OnClick(R.id.startTrackingButton)
    protected void startTracking() {
        requestPermissions();
    }

    private void requestPermissions() {
        new RxPermissions(this)
                .request(getPermissionNames())
                .subscribe(new SimpleObserver<>("RequestLocationPermissions"));
    }

    private String[] getPermissionNames() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            return new String[] {
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            };
        } else{
            return new String[] {
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            };
        }
    }

    @OnClick(R.id.stopTrackingButton)
    protected void stopTracking() {

    }

}
