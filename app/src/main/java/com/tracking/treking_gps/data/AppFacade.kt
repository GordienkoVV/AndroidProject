package com.tracking.treking_gps.data

import android.content.Context
import com.google.firebase.database.FirebaseDatabase
import com.tracking.treking_gps.domain.TrackPointDataSource
import com.tracking.treking_gps.domain.UserSettingsDataSource

class AppFacade {

    private val trackPointDataSource: TrackPointDataSource
    private val userSettingsDataSource: UserSettingsDataSource

    private constructor(context: Context) {
        userSettingsDataSource = UserSettingsDataSourceImpl(context)
        trackPointDataSource = TrackPointDataSourceImpl(
                FirebaseDatabase.getInstance().getReference("messages")
        )
    }

    fun getTrackPointDataSource(): TrackPointDataSource = trackPointDataSource
    fun getUserSettingsDataSource(): UserSettingsDataSource = userSettingsDataSource

    companion object {
        lateinit var instance: AppFacade

        fun initFacade(context: Context) {
            instance = AppFacade(context)
        }
    }
}
