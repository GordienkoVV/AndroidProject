package com.tracking.observer.ui.map

import com.google.android.gms.maps.model.LatLng

data class GeoPointDto(
        val id: String = "",
        val title: String = "",
        val type: String = "",
        val latLng: LatLng = LatLng(0.0, 0.0)
) {

    fun isEmpty() = this == EMPTY

    companion object {
        val EMPTY = GeoPointDto()
    }
}
