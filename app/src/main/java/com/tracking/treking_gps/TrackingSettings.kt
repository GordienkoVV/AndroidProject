package com.tracking.treking_gps

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import butterknife.BindView
import butterknife.OnClick
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.functions.Predicate

class TrackingSettings : FragmentButterKnife() {

    @BindView(R.id.toolbar)
    lateinit var toolbar: Toolbar
    @BindView(R.id.transportNameField)
    lateinit var nameField: EditText
    @BindView(R.id.transportTypeSpinner)
    lateinit var transportTypeSpinner: Spinner
    @BindView(R.id.statusView)
    lateinit var statusView: TextView

    override fun getLayoutId(): Int {
        return R.layout.screen_tracking_settings
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        listeningWorkerState()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar.setTitle(R.string.tracking_settings_title)
        initTransportTypeSpinner()
    }

    private fun initTransportTypeSpinner() {
//        transportTypeSpinner.setAdapter();
    }

    private fun listeningWorkerState() {
        StarterWorker.getEnabledLiveData(context)
                .observe(this, Observer { value: Boolean? -> onWorkerEnabledChanged(value) })
    }

    private fun onWorkerEnabledChanged(value: Boolean?) {
        val statusText = if (value != null && value) getString(R.string.enabled) else getString(R.string.disabled)
        statusView.text = statusText
    }

    @OnClick(R.id.startTrackingButton)
    fun startTracking() {
        RxPermissions(this)
                .request(*permissionNames)
                .filter { it }
                .doOnNext { startWorker() }
                .subscribe(SimpleObserver("RequestLocationPermissions"))
    }

    private val permissionNames: Array<String>
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            arrayOf(
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            )
        } else {
            arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            )
        }

    private fun startWorker() {
        StarterWorker.start(context)
    }

    @OnClick(R.id.stopTrackingButton)
    fun stopTracking() {
        StarterWorker.stopAll(context)
    }

    companion object {
        private val TAG = TrackingSettings::class.java.simpleName
    }
}
