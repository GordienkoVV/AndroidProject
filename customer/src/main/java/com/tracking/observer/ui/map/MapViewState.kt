package com.tracking.observer.ui.map

import androidx.lifecycle.MutableLiveData
import com.tracking.observer.ui.utils.SingleLiveEvent

data class MapViewState(
        val loading: MutableLiveData<Boolean?> = MutableLiveData(),
        val error: SingleLiveEvent<Throwable?> = SingleLiveEvent(),
        val transportPoints: MutableLiveData<List<GeoPointDto>> = MutableLiveData(),
        val routes: MutableLiveData<List<RouteDto>> = MutableLiveData(),
        val routeNames: MutableLiveData<List<String>> = MutableLiveData(),
        val routeNamesFiltered: MutableLiveData<List<String>> = MutableLiveData()
)
