package com.tracking.observer.domain

import com.tracking.observer.domain.data.TrackPoint
import io.reactivex.Observable

interface TrackPointDataSource {

    fun getTracks(): Observable<List<TrackPoint>>

}
