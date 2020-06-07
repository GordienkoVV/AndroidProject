package com.tracking.treking_gps.ui.settings

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import butterknife.BindView
import butterknife.OnClick
import com.tbruyelle.rxpermissions2.RxPermissions
import com.tracking.treking_gps.R
import com.tracking.treking_gps.android.StarterWorker
import com.tracking.treking_gps.domain.data.TransportType
import com.tracking.treking_gps.ui.FragmentButterKnife
import com.tracking.treking_gps.ui.SimpleAdapterViewItemSelectedListener
import com.tracking.treking_gps.utils.SimpleObserver

class TrackingSettingsFragment : FragmentButterKnife() {

    @BindView(R.id.toolbar)
    lateinit var toolbar: Toolbar
    @BindView(R.id.transportNameField)
    lateinit var nameField: EditText
    @BindView(R.id.transportTypeSpinner)
    lateinit var transportTypeSpinner: Spinner
    @BindView(R.id.transportRouteNumberSpinner)
    lateinit var transportRouteNumberSpinner: Spinner
    @BindView(R.id.startTrackingButton)
    lateinit var startTrackingButton: View
    @BindView(R.id.statusView)
    lateinit var statusView: TextView

    lateinit var transportTypeValues: List<String>
    lateinit var transportTypeTitles: List<String>
    var routeNumbers: List<String> = emptyList()
    lateinit var transportTypeSpinnerAdapter: ArrayAdapter<String>
    lateinit var routeNumberSpinnerAdapter: ArrayAdapter<String>

    private val viewModel: TrackingSettingsViewModel by viewModels()

    override fun getLayoutId(): Int = R.layout.screen_tracking_settings

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        listeningWorkerState()
    }

    private fun listeningWorkerState() {
        StarterWorker.getEnabledLiveData(context)
                .observe(this, Observer { value: Boolean? -> onWorkerEnabledChanged(value) })
    }

    private fun onWorkerEnabledChanged(value: Boolean?) {
        val statusText = if (value != null && value) getString(R.string.enabled) else getString(R.string.disabled)
        statusView.text = statusText
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        viewListeners()
        initDataListeners()
    }

    private fun initViews() {
        toolbar.setTitle(R.string.tracking_settings_title)

        transportTypeValues = listOf(TransportType.MINIBUS, TransportType.TROLLEYBUS)
        transportTypeTitles = resources.getStringArray(R.array.transport_types)
                .toList()

        transportTypeSpinnerAdapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item)
        transportTypeSpinner.adapter = transportTypeSpinnerAdapter
        transportTypeSpinnerAdapter.clear()
        transportTypeSpinnerAdapter.addAll(transportTypeTitles)

        routeNumberSpinnerAdapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item)
        transportRouteNumberSpinner.adapter = routeNumberSpinnerAdapter
    }

    private fun viewListeners() {
        nameField.doOnTextChanged { text, _, _, _ ->
            viewModel.setName(text.toString() ?: "")
        }

        transportTypeSpinner.onItemSelectedListener = SimpleAdapterViewItemSelectedListener { index ->
            transportTypeValues.getOrNull(index)
                    ?.also { viewModel.setTransportType(it) }
        }

        transportRouteNumberSpinner.onItemSelectedListener = SimpleAdapterViewItemSelectedListener { index ->
            routeNumbers.getOrNull(index)
                    ?.also { viewModel.setRouteNumber(it) }
        }
    }

    private fun initDataListeners() {
        val viewState = viewModel.viewState
        viewState.data.observe(viewLifecycleOwner, Observer { onDataChanged(it) })
    }

    private fun onDataChanged(data: SettingsDto?) {
        data ?: return

        if (data.name != nameField.text.toString()) {
            nameField.setText(data.name)
        }

        val typeIndex = transportTypeValues.indexOf(data.transportType)
        val typeSelectedPosition = transportTypeSpinner.selectedItemPosition
        val transportTypeChanged = typeIndex != typeSelectedPosition
                && typeIndex < routeNumberSpinnerAdapter.count
        if (transportTypeChanged) {
            transportTypeSpinner.setSelection(typeIndex)
        }

        val needRefillTransportRouteNumbers = typeIndex != typeSelectedPosition
                || routeNumberSpinnerAdapter.isEmpty
        if (needRefillTransportRouteNumbers) {
            refillTransportRouteNumbers(data.transportType)
        }

        val routeNumberIndex = routeNumbers.indexOf(data.routeNumber)
        val routeNumberSelectedPosition = transportRouteNumberSpinner.selectedItemPosition
        val routeNumberChanged = routeNumberIndex != routeNumberSelectedPosition
                && routeNumberIndex < transportTypeSpinnerAdapter.count
        if (routeNumberChanged) {
            transportRouteNumberSpinner.setSelection(routeNumberIndex)
        }

        startTrackingButton.isEnabled = data.isValid
    }

    private fun refillTransportRouteNumbers(transportType: String) {
        routeNumbers = when (transportType) {
            TransportType.MINIBUS -> resources.getStringArray(R.array.minibus_route_numbers).toList()
            TransportType.TROLLEYBUS -> resources.getStringArray(R.array.trolleybus_route_numbers).toList()
            else -> emptyList()
        }
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
        private val TAG = TrackingSettingsFragment::class.java.simpleName
    }
}
