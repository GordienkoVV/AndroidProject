package com.tracking.treking_gps.ui.settings

data class SettingsDto(
        val name: String = "",
        val transportType: String = "",
        val routeNumber: String = "",
        val isValid: Boolean = false
)
