package com.tracking.treking_gps.data

import com.google.firebase.database.DatabaseReference
import com.google.gson.GsonBuilder
import com.tracking.treking_gps.domain.TrackPointDataSource
import com.tracking.treking_gps.domain.data.TrackPoint
import io.reactivex.Single

class TrackPointDataSourceImpl(
        private val pointsReference: DatabaseReference
) : TrackPointDataSource {
    private val gson = GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ssZ")
            .create()

    override fun setTrack(trackerId: String, point: TrackPoint): Single<Boolean> {
        return Single.create { emitter ->
            val pointJson = gson.toJson(point)
            pointsReference.child(trackerId).setValue(pointJson)
                    .addOnCompleteListener { emitter.onSuccess(true) }
                    .addOnCanceledListener { emitter.onSuccess(false) }
                    .addOnFailureListener { emitter.onError(it) }
        }
    }

}
