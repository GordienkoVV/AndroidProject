package com.tracking.treking_gps.domain

import com.tracking.treking_gps.domain.data.TrackPoint
import io.reactivex.Single

interface TrackPointDataSource {
    fun setTrack(trackerId: String, point: TrackPoint): Single<Boolean>
}
