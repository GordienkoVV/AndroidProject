package com.tracking.observer.app

import com.google.firebase.database.FirebaseDatabase
import com.tracking.observer.data.RouteDataSourceImpl
import com.tracking.observer.data.TrackPointDataSourceImpl
import com.tracking.observer.domain.RouteDataSource
import com.tracking.observer.domain.TrackPointDataSource

class AppFacade {

    private val database = FirebaseDatabase.getInstance()
    private val trackPointSource: TrackPointDataSource
            = TrackPointDataSourceImpl(database.getReference("messages"))
    private val routeDataSource: RouteDataSource
            = RouteDataSourceImpl(database.app.applicationContext)

    fun getTrackPointDataSource(): TrackPointDataSource = trackPointSource

    fun getRouteDataSource(): RouteDataSource = routeDataSource

    companion object {
        val instance = AppFacade()
    }

}
