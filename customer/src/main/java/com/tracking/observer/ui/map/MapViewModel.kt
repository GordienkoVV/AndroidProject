package com.tracking.observer.ui.map

import androidx.lifecycle.ViewModel
import com.tracking.observer.app.AppFacade
import com.tracking.observer.domain.data.TrackPoint
import com.tracking.observer.ui.SimpleViewState
import com.tracking.observer.ui.utils.subscribeWithDisposable
import com.tracking.observer.ui.utils.threadsIOtoUI
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit

class MapViewModel : ViewModel() {

    val viewState = SimpleViewState<List<GeoPoint>>()
    private var pointsDisposable: Disposable? = null

    init {
        pointsDisposable = Observable.interval(5, TimeUnit.SECONDS)
                .flatMap { getGeoPoints() }
                .threadsIOtoUI()
                .doOnSubscribe { viewState.loading.value = true }
                .doOnTerminate { viewState.loading.value = false }
                .doOnNext { viewState.data.value = it }
                .doOnError { viewState.error.value = it }
                .subscribeWithDisposable()
    }

    private fun getGeoPoints(): Observable<List<GeoPoint>> {
        return AppFacade.instance.getTrackPointDataSource()
                .getTracks()
                .map { it.filter(::filterPoint)
                            .map(::trackPointToGeoPoint)
                }
    }

    private fun trackPointToGeoPoint(point: TrackPoint) = GeoPoint(
            id = "",
            title = point.name,
            type = point.type,
            latitude = point.latitude,
            longitude = point.longitude
    )

    private fun filterPoint(point: TrackPoint): Boolean {
        // insert code for filter point
        return true
    }

    override fun onCleared() {
        super.onCleared()
        pointsDisposable?.dispose()
        pointsDisposable = null
    }

}
