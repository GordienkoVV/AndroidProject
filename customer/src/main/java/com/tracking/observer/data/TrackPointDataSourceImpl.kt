package com.tracking.observer.data

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.gson.GsonBuilder
import com.tracking.observer.domain.TrackPointDataSource
import com.tracking.observer.domain.data.TrackPoint
import com.tracking.observer.ui.utils.LogUtils
import io.reactivex.Observable
import io.reactivex.Single

class TrackPointDataSourceImpl(
        private val pointsReference: DatabaseReference
) : TrackPointDataSource {

    private val gson = GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ssZ")
            .create()

    override fun getTracks(): Observable<List<TrackPoint>> {
        return Single.create<List<TrackPoint>> {  emitter ->
            pointsReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    pointsReference.removeEventListener(this)
                    val points = snapshot.children.map { snapshotItem ->
                        val valueJson = snapshotItem.value as? String
                        gson.fromJson(valueJson, TrackPoint::class.java)
                    }
                    emitter.onSuccess(points)
                }

                override fun onCancelled(error: DatabaseError) {
                    pointsReference.removeEventListener(this)
                    val exception = error.toException()
                    LogUtils.log("Database", "onCancelled", exception)
                    emitter.onError(exception)
                }
            })
        }.toObservable()
    }
}
