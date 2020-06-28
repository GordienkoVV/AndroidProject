package com.tracking.observer.ui.map

import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.PolyUtil
import com.tracking.observer.app.AppFacade
import com.tracking.observer.domain.data.RouteWay
import com.tracking.observer.domain.data.TrackPoint
import com.tracking.observer.ui.utils.subscribeDisposable
import com.tracking.observer.ui.utils.subscribeWithDisposable
import com.tracking.observer.ui.utils.threadsIOtoUI
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.Singles
import kotlinx.android.synthetic.main.screen_map.view.*
import java.util.concurrent.TimeUnit

class MapViewModel : ViewModel() {

    private val trackPointDataSource = AppFacade.instance.getTrackPointDataSource()
    private val routeDataSource = AppFacade.instance.getRouteDataSource()

    val viewState = MapViewState()
    private var transportPoints: List<GeoPointDto> = emptyList()
    private var routes: List<RouteDto> = emptyList()
    private var routeNames: List<String> = emptyList()
    private var pointsDisposable: Disposable? = null

    init {
        loadInitData()
        observeTransportPoints()
    }

    private fun loadInitData() {
        Singles.zip(
                routeDataSource.getRouteNames(),
                routeDataSource.getRoutes()
        ) { names: List<String>, routeWays: List<RouteWay> ->
            routeNames = names
            routes = routeWays.map(::mapRoute)
            true
        }
                .doOnSuccess {
                    updateRouteNames()
                    updateRoutes()
                    observeTransportPoints()
                }
                .toObservable()
                .subscribeDisposable()
    }

    private fun mapRoute(data: RouteWay): RouteDto {
        return RouteDto(
                name = data.name,
                color = data.color,
                width = 3f,
                items = data.route.takeIf { it.isNotBlank() }
                        ?.let(::decodeRouteString)
                        ?: emptyList()
        )
    }

    private fun decodeRouteString(data: String): List<LatLng> {
        return try {
            PolyUtil.decode(data)
        } catch (e: Throwable) {
            emptyList()
        }
    }

    private fun updateRouteNames() {
        Observable
                .fromCallable {
                    routeNames
                }
                .threadsIOtoUI()
                .doOnNext { viewState.routeNames.value = it }
                .subscribeDisposable()
    }

    private fun updateRoutes() {
        Observable
                .fromCallable {
                    routes.filter(::filterRoute)
                }
                .threadsIOtoUI()
                .doOnNext { viewState.routes.value = it }
                .subscribeDisposable()
    }

    private fun filterRoute(data: RouteDto): Boolean {
        return true
    }

    private fun observeTransportPoints() {
        pointsDisposable = Observable.interval(5, TimeUnit.SECONDS)
                .flatMap { fetchTransportPoints() }
                .doOnNext {
                    transportPoints = it
                    updateViewStateTransportPoints()
                }
                .threadsIOtoUI()
                .doOnSubscribe { viewState.loading.value = true }
                .doOnTerminate { viewState.loading.value = false }
                .doOnNext { viewState.transportPoints.value = it }
                .doOnError { viewState.error.value = it }
                .subscribeWithDisposable()
    }

    private fun fetchTransportPoints(): Observable<List<GeoPointDto>> {
        return trackPointDataSource.getTracks()
                .map { it.map(::trackPointToGeoPoint) }
    }

    private fun trackPointToGeoPoint(point: TrackPoint) = GeoPointDto(
            id = "",
            title = point.name,
            type = point.type,
            latLng = LatLng(point.latitude, point.longitude)
    )

    private fun updateViewStateTransportPoints() {
        Observable
                .fromCallable { transportPoints.filter(::filterPoint) }
                .threadsIOtoUI()
                .doOnNext { viewState.transportPoints.value = it }
                .subscribeDisposable()
    }

    private fun filterPoint(point: GeoPointDto): Boolean {
//        val names = viewState.routeNamesFiltered.value
//        return names?.contains(point.title) ?: true
        return true
    }

    override fun onCleared() {
        super.onCleared()
        pointsDisposable?.dispose()
        pointsDisposable = null
    }

}
