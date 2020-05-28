package com.tracking.treking_gps

import android.content.Context
import android.location.Location
import androidx.work.RxWorker
import androidx.work.WorkerParameters
import com.google.android.gms.location.LocationRequest
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.tracking.treking_gps.data.TrackPoint
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import pl.charmas.android.reactivelocation2.ReactiveLocationProvider
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*

class LocationWorker
/**
 * @param appContext   The application [Context]
 * @param workerParams Parameters to setup the internal state of this worker
 */
(appContext: Context, workerParams: WorkerParameters) : RxWorker(appContext, workerParams) {

    private val locationProvider = ReactiveLocationProvider(appContext)
    private val database = FirebaseDatabase.getInstance()
    private val myRef1 = database.getReference("messages")
    private val timeFormatter: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ", Locale.getDefault())
    private val gson = GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ssZ")
            .create()
    private val locationRequest = LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setNumUpdates(2)
            .setInterval(1000)

    override fun createWork(): Single<Result> {
        val timeStamp = timeFormatter.format(Date())
        Logger.log(TAG, "CreateWork at $timeStamp")
        return requestLocation()
                .flatMap(::handleLocation)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .all { it }
                .onErrorReturn {
                    Logger.log(TAG, "Request location falure $timeStamp", it)
                    false
                }
                .map {
                    if (it ) Result.success()
                    else Result.failure()
                }
    }

    private fun requestLocation(): Observable<Location> {
        return locationProvider.getUpdatedLocation(locationRequest)
    }

    private fun handleLocation(location: Location) = Observable.fromCallable {
        val timeStamp = timeFormatter.format(Date())
        Logger.log(TAG, "at $timeStamp  $location")

        val trackerId = "bus2"
        val point = TrackPoint(
                type = "bus",
                name = "19",
                latitude = location.latitude,
                longitude = location.longitude
        )

        val pointJson = gson.toJson(point)
        myRef1.child(trackerId).setValue(pointJson)
//        myRef1.push().setValue(pointJson)
        true
    }
    companion object {
        private val TAG = LocationWorker::class.java.simpleName
    }
}
