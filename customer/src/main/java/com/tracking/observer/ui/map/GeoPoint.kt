package com.tracking.observer.ui.map

data class GeoPoint(
        val id: String = "",
        val title: String = "",
        val type: String = "",
        val latitude: Double = 0.0,
        val longitude: Double = 0.0
) {

    fun isEmpty() = this == EMPTY

    companion object {
        val EMPTY = GeoPoint()
    }
}
