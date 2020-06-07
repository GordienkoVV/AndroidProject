package com.tracking.treking_gps.data

import android.content.Context
import androidx.core.content.edit
import com.tracking.treking_gps.domain.data.UserSettings
import com.tracking.treking_gps.domain.UserSettingsDataSource
import io.reactivex.Single

class UserSettingsDataSourceImpl(
        context: Context
): UserSettingsDataSource {

    private val prefs = context.getSharedPreferences("user_settings", Context.MODE_PRIVATE)

    override fun getUserSettings(): Single<UserSettings> {
        return Single.fromCallable {
            UserSettings(
                    id = prefs.getString(FIELD_ID, null) ?: "",
                    name = prefs.getString(FIELD_NAME, null) ?: "",
                    transportType = prefs.getString(FIELD_TYPE, null) ?: "",
                    routeNumber = prefs.getString(FIELD_ROUTE_NUMBER, null)
                            ?: ""
            )
        }
    }

    override fun setUserSettings(data: UserSettings): Single<UserSettings> {
        return Single.fromCallable {
            prefs.edit {
                putString(FIELD_ID, data.id)
                putString(FIELD_NAME, data.name)
                putString(FIELD_TYPE, data.transportType)
                putString(FIELD_ROUTE_NUMBER, data.routeNumber)
            }
            data
        }
    }

    companion object {
        private const val FIELD_ID = "id"
        private const val FIELD_NAME = "name"
        private const val FIELD_TYPE = "type"
        private const val FIELD_ROUTE_NUMBER = "routeNumber"
    }
}
