package com.tracking.treking_gps;

import android.content.Context;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.work.RxWorker;
import androidx.work.WorkerParameters;

import com.google.android.gms.location.LocationRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import pl.charmas.android.reactivelocation2.ReactiveLocationProvider;

public class LocationWorker extends RxWorker {


    /**
     * private ArrayList<String> messages1 = new ArrayList<>();
     */

    private final static String TAG = LocationWorker.class.getSimpleName();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef1 = database.getReference("messages");
    private DateFormat timeFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

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
        String timeStamp = timeFormatter.format(new Date());
        Logger.log(TAG, "CreateWork at "+timeStamp);

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
        String timeStamp = timeFormatter.format(new Date());
        Logger.log(TAG, "at "+ timeStamp + "  "+ location.toString());


        myRef1.push().setValue(location.getLatitude() + ", " + location.getLongitude()+ ", 19");




        //TODO make handle location, such as send to the server

        return Observable.just(true);
    }
}
