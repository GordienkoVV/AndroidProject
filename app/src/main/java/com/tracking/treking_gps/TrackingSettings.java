package com.tracking.treking_gps;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.tbruyelle.rxpermissions2.RxPermissions;

import butterknife.BindView;
import butterknife.OnClick;

public final class TrackingSettings extends FragmentButterKnife {

    private static String TAG = TrackingSettings.class.getSimpleName();

    @BindView(R.id.toolbar)
    protected Toolbar toolbar;
    @BindView(R.id.statusView)
    protected TextView statusView;

    @Override
    protected int getLayoutId() {
        return R.layout.screen_tracking_settings;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listeningWorkerState();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar.setTitle(R.string.tracking_settings_title);
    }

    private void listeningWorkerState() {
        StarterWorker.getEnabledLiveData(getContext())
                .observe(this, this::onWorkerEnabledChanged);
    }

    private void onWorkerEnabledChanged(Boolean value) {
        String statusText = value != null && value
                ? getString(R.string.enabled)
                : getString(R.string.disabled);

        statusView.setText(statusText);
    }

    @OnClick(R.id.startTrackingButton)
    protected void startTracking() {
        new RxPermissions(this)
                .request(getPermissionNames())
                .filter(Boolean::booleanValue)
                .doOnNext(value -> startWorker())
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

    private void startWorker() {
        StarterWorker.start(getContext());
    }

    @OnClick(R.id.stopTrackingButton)
    protected void stopTracking() {
        StarterWorker.stopAll(getContext());
    }

}
