package com.tracking.treking_gps;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.concurrent.TimeUnit;

public class StarterWorker extends Worker {

    private static final String TRACKER_WORKER_STARTER = "tracker_worker_starter";
    private static final String TRACKER_WORKER = "tracker_worker";
    private static final long delay = 15_000;


    public StarterWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        start(getApplicationContext(), delay);
        return Result.success();
    }

    public static void start(Context context) {
        start(context, 0);
    }

    private static void start(Context context, long delay) {
        if (context == null) return;

        WorkManager workManager = WorkManager.getInstance(context);
        OneTimeWorkRequest requestStarter = new OneTimeWorkRequest.Builder(StarterWorker.class)
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .build();
        OneTimeWorkRequest requestLocation = new OneTimeWorkRequest.Builder(LocationWorker.class)
                .build();

        workManager.enqueueUniqueWork(TRACKER_WORKER, ExistingWorkPolicy.REPLACE, requestLocation);

        new Handler(Looper.getMainLooper()).postDelayed(
                () -> workManager.enqueueUniqueWork(TRACKER_WORKER_STARTER, ExistingWorkPolicy.REPLACE, requestStarter),
                100
        );
    }

    public static void stopAll(Context context) {
        if (context == null) return;

        WorkManager workManager = WorkManager.getInstance(context);
        workManager.cancelUniqueWork(TRACKER_WORKER_STARTER);
        workManager.cancelUniqueWork(TRACKER_WORKER);
    }

    public static LiveData<Boolean> getEnabledLiveData(Context context) {
        if (context == null) return new MutableLiveData<>();

        MediatorLiveData<Boolean> liveData = new MediatorLiveData<>();
        liveData.addSource(
                WorkManager.getInstance(context).getWorkInfosForUniqueWorkLiveData(TRACKER_WORKER_STARTER),
                items -> {
                    if (items.isEmpty()) {
                        liveData.setValue(false);
                        return;
                    }

                    WorkInfo.State state = items.get(0).getState();
                    Logger.log(TRACKER_WORKER_STARTER, "State: "+state);

                    boolean isStopped = state == WorkInfo.State.FAILED || state == WorkInfo.State.CANCELLED;
                    liveData.setValue(!isStopped);
                });

        return liveData;
    }
}
