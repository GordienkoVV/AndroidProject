package com.tracking.treking_gps.domain.data


data class UserSettings(
        val id: String = "",
        val name: String = "",
        val transportType: String = "",
        val routeNumber: String = ""
)

object TransportType {
    const val MINIBUS = "minibus"
    const val TROLLEYBUS = "trolleybus"
}
