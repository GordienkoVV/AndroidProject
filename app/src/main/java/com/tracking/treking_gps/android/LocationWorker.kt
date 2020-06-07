package com.tracking.treking_gps.android

import android.content.Context
import android.location.Location
import androidx.work.RxWorker
import androidx.work.WorkerParameters
import com.google.android.gms.location.LocationRequest
import com.tracking.treking_gps.data.AppFacade
import com.tracking.treking_gps.utils.Logger
import com.tracking.treking_gps.domain.TrackPointDataSource
import com.tracking.treking_gps.domain.UserSettingsDataSource
import com.tracking.treking_gps.domain.data.TrackPoint
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import pl.charmas.android.reactivelocation2.ReactiveLocationProvider
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class LocationWorker
/**
 * @param appContext   The application [Context]
 * @param workerParams Parameters to setup the internal state of this worker
 */
(appContext: Context, workerParams: WorkerParameters) : RxWorker(appContext, workerParams) {

    private val userSettingsDataSource: UserSettingsDataSource = AppFacade.instance.getUserSettingsDataSource()
    private val trackPointDataSource: TrackPointDataSource = AppFacade.instance.getTrackPointDataSource()
    private val locationProvider = ReactiveLocationProvider(appContext)
    private val timeFormatter: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ", Locale.getDefault())

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

    private fun handleLocation(location: Location) = Observable.defer {
        userSettingsDataSource.getUserSettings()
                .filter { it.id.isNotBlank()
                            && it.routeNumber.isNotBlank()
                            && it.transportType.isNotBlank()
                }
                .flatMapSingle {
                    val point = TrackPoint(
                            type = it.transportType,
                            name = it.routeNumber,
                            latitude = location.latitude,
                            longitude = location.longitude,
                            time = Date()
                    )
                    trackPointDataSource.setTrack(it.id, point)
                }.toObservable()
    }
    companion object {
        private val TAG = LocationWorker::class.java.simpleName
    }
}
