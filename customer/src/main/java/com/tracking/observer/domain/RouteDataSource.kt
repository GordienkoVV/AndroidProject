package com.tracking.observer.domain

import com.tracking.observer.domain.data.RouteWay
import io.reactivex.Single

interface RouteDataSource {
    fun getRouteNames(): Single<List<String>>
    fun getRoutes(names: List<String> = emptyList()): Single<List<RouteWay>>
}
