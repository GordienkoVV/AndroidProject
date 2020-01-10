package com.example.treking_gps;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.RxWorker;
import androidx.work.WorkManager;
import androidx.work.WorkerParameters;

import com.annimon.stream.Stream;

import java.util.concurrent.TimeUnit;

import io.reactivex.Single;

public class StarterWorker extends RxWorker {

    private static final String TRACKER_WORKER = "tracker_worker";
    private static final long shift = 15_000;
    private static final long count = 900_000L / shift;

    private WorkManager workManager;


    public StarterWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        workManager = WorkManager.getInstance(context);
    }

    @NonNull
    @Override
    public Single<Result> createWork() {
        return Single.fromCallable(() -> {
            getRange()
                    .forEach(value -> {
                        String name = getWorkerName(value);
                        OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(LocationWorker.class)
                                .setInitialDelay(value, TimeUnit.MILLISECONDS)
                                .build();

                        workManager.enqueueUniqueWork(name, ExistingWorkPolicy.REPLACE, request);
                        Logger.log("StartWorker", request.toString());
                    });

            return Result.success();
        });
    }

    public static void stopAllSubWorkers(Context context) {
        if (context == null) return;

        getRange()
                .map(StarterWorker::getWorkerName)
                .forEach(value -> {
                    WorkManager.getInstance(context)
                            .cancelUniqueWork(value);
                });
    }

    private static String getWorkerName(long value) {
        return TRACKER_WORKER +"_"+ value;
    }

    private static Stream<Long> getRange() {
        return Stream.range(0, count)
                .map(value -> value*shift);
    }
}
