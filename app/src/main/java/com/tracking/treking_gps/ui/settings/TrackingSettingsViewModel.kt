package com.tracking.treking_gps.ui.settings

import androidx.lifecycle.ViewModel
import com.tracking.treking_gps.ui.utils.subscribeDisposable
import com.tracking.treking_gps.ui.utils.threadsIOtoUI
import com.tracking.treking_gps.data.AppFacade
import com.tracking.treking_gps.domain.data.UserSettings
import com.tracking.treking_gps.ui.SimpleViewState

class TrackingSettingsViewModel : ViewModel() {

    private val settingsDataSource = AppFacade.instance.getUserSettingsDataSource()
    private var data = SettingsDto()
    val viewState = SimpleViewState<SettingsDto>()

    init {
        settingsDataSource.getUserSettings()
                .toObservable()
                .threadsIOtoUI()
                .doOnNext {
                    data = data.copy(
                            name = it.id,
                            transportType = it.transportType,
                            routeNumber = it.routeNumber
                    )
                    onDataChanged()
                }
                .subscribeDisposable()
    }

    fun setName(value: String) {
        data = data.copy(name = value)
        onDataChanged()
    }

    fun setTransportType(value: String) {
        data = data.copy(transportType = value)
        onDataChanged()
    }

    fun setRouteNumber(value: String) {
        data = data.copy(routeNumber = value)
        onDataChanged()
    }

    private fun onDataChanged() {
        data = data.copy(isValid = isValid())
        viewState.data.value = data
        saveData()
    }

    private fun isValid(): Boolean {
        return data.name.isNotBlank()
                && data.transportType.isNotBlank()
                && data.routeNumber.isNotBlank()
    }

    private fun saveData() {
        val settings = UserSettings(
                id = data.name,
                transportType = data.transportType,
                routeNumber = data.routeNumber
        )

        settingsDataSource.setUserSettings(settings)
                .toObservable()
                .threadsIOtoUI()
                .subscribeDisposable()
    }

}
