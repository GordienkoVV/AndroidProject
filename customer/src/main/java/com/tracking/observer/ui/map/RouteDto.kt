package com.tracking.observer.ui.map

import com.google.android.gms.maps.model.LatLng

data class RouteDto(
        val name: String = "",
        val items: List<LatLng> = emptyList(),
        val color: Int = 0,
        val width: Float = 1f
)
