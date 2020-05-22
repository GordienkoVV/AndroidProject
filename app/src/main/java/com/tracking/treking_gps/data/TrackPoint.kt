package com.tracking.treking_gps.data

import java.util.*

data class TrackPoint(
        val type: String,
        val name: String,
        val latitude: Double = 0.0,
        val longitude: Double = 0.0,
        val time: Date = Date()
)
