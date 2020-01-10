package com.example.treking_gps;

import android.Manifest;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.annimon.stream.Stream;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;

public final class TrackingSettings extends FragmentButterKnife {

    private static String TAG = TrackingSettings.class.getSimpleName();
    private static String TRACKER_WORKER = "tracker_worker";

    @BindView(R.id.statusView)
    protected TextView statusView;

    private WorkManager workManager;


    @Override
    protected int getLayoutId() {
        return R.layout.screen_tracking_settings;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context context = getContext();
        workManager = WorkManager.getInstance(context);
        startLocationWorkerStateListening();
    }

    private void startLocationWorkerStateListening() {
        workManager.getWorkInfosForUniqueWorkLiveData(TRACKER_WORKER)
                .observe(this, this::onLocationWorkerStatusChanged);
    }

    private void onLocationWorkerStatusChanged(List<WorkInfo> items) {
        if (items == null || items.isEmpty()) {
            Logger.log(TAG, "Has no info about tracking worker");
            return;
        }

        String logText = Stream.of(items)
                .map(WorkInfo::toString)
                .reduce((value1, value2) -> value1+"\n"+value2)
                .orElse("");

        Logger.log(TAG, "WorkerInfo: "+ logText);
        statusView.setText(logText);
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
        workManager.enqueueUniquePeriodicWork(
                TRACKER_WORKER,
                ExistingPeriodicWorkPolicy.REPLACE,
                createLocationWorkRequest()
        );
    }

    private PeriodicWorkRequest createLocationWorkRequest() {
//        Constraints constraints = new Constraints.Builder()
//                .setRequiredNetworkType(NetworkType.CONNECTED)
//                .build();

        return new PeriodicWorkRequest.Builder(StarterWorker.class, 900_000L, TimeUnit.SECONDS)
//                        .setConstraints(constraints)
                        .build();
    }

    @OnClick(R.id.stopTrackingButton)
    protected void stopTracking() {
        StarterWorker.stopAllSubWorkers(getContext());
        workManager.cancelUniqueWork(TRACKER_WORKER);
    }

}
