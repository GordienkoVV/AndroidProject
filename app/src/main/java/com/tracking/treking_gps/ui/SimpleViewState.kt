package com.tracking.treking_gps.ui

import androidx.lifecycle.MutableLiveData
import com.tracking.treking_gps.ui.utils.SingleLiveEvent

data class SimpleViewState<T>(
        val loading: MutableLiveData<Boolean?> = MutableLiveData(),
        val error: SingleLiveEvent<Throwable?> = SingleLiveEvent(),
        val data: MutableLiveData<T?> = MutableLiveData()
)
