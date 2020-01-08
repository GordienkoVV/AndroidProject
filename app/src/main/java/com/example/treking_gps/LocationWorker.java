package com.example.treking_gps;

import android.content.Context;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.work.RxWorker;
import androidx.work.WorkerParameters;

import com.google.android.gms.location.LocationRequest;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import pl.charmas.android.reactivelocation2.ReactiveLocationProvider;

public class LocationWorker extends RxWorker {


    /**
     * @param appContext   The application {@link Context}
     * @param workerParams Parameters to setup the internal state of this worker
     */
    public LocationWorker(@NonNull Context appContext, @NonNull WorkerParameters workerParams) {
        super(appContext, workerParams);
    }

    @NonNull
    @Override
    public Single<Result> createWork() {
        return requestLocation()
                .flatMap(this::handleLocation)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .toList()
                .map(value -> Result.success());
    }

    private Observable<Location> requestLocation() {
        return new  ReactiveLocationProvider(getApplicationContext())
                .getUpdatedLocation(getLocationRequest());
    }

    private LocationRequest getLocationRequest() {
        return LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setNumUpdates(1)
                .setInterval(100);
    }

    private Observable<Boolean> handleLocation(Location location) {
        Logger.log("Location", location.toString());
        return Observable.just(true);
    }
}
