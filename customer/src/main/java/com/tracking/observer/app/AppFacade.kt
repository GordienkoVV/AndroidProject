package com.tracking.observer.app

import com.google.firebase.database.FirebaseDatabase
import com.tracking.observer.data.TrackPointDataSourceImpl
import com.tracking.observer.domain.TrackPointDataSource

class AppFacade {

    private val database = FirebaseDatabase.getInstance()
    private val trackPointSource: TrackPointDataSource = TrackPointDataSourceImpl(
            database.getReference("messages")
    )

    fun getTrackPointDataSource(): TrackPointDataSource {
        return trackPointSource
    }

    companion object {
        val instance = AppFacade()
    }

}
