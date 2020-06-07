package com.tracking.treking_gps.domain

import com.tracking.treking_gps.domain.data.UserSettings
import io.reactivex.Single

interface UserSettingsDataSource {
    fun getUserSettings(): Single<UserSettings>
    fun setUserSettings(data: UserSettings): Single<UserSettings>
}



