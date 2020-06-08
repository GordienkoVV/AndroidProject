package com.tracking.treking_gps.ui.settings

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import butterknife.BindView
import butterknife.OnClick
import com.tbruyelle.rxpermissions2.RxPermissions
import com.tracking.treking_gps.R
import com.tracking.treking_gps.android.StarterWorker
import com.tracking.treking_gps.domain.data.TransportType
import com.tracking.treking_gps.ui.FragmentButterKnife
import com.tracking.treking_gps.ui.SimpleItemSelectListener
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

    private lateinit var transportTypeValues: List<String>
    private lateinit var transportTypeTitles: List<String>
    private lateinit var minibusRouteNumbers: List<String>
    private lateinit var trolleybusRouteNumbers: List<String>
    private var routeNumbers: List<String> = emptyList()
    private lateinit var transportTypeSpinnerAdapter: ArrayAdapter<String>
    private lateinit var routeNumberSpinnerAdapter: ArrayAdapter<String>

    private val viewModel: TrackingSettingsViewModel by viewModels()

    override fun getLayoutId(): Int = R.layout.screen_tracking_settings

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        listeningWorkerState()
    }

    private fun listeningWorkerState() {
        StarterWorker.getEnabledLiveData(context)
                .observe(this, Observer { onWorkerEnabledChanged(it) })
    }

    private fun onWorkerEnabledChanged(value: Boolean?) {
        val statusText = if (value != null && value) getString(R.string.enabled) else getString(R.string.disabled)
        statusView.text = statusText
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initNameFieldListeners()
        initTransportTypeSpinnerListeners()
        initTransportRouteNumberSpinnerListeners()
        initDataListeners()
    }

    private fun initViews() {
        toolbar.setTitle(R.string.tracking_settings_title)

        transportTypeValues = listOf(TransportType.MINIBUS, TransportType.TROLLEYBUS)
        transportTypeTitles = resources.getStringArray(R.array.transport_types).toList()
        minibusRouteNumbers = resources.getStringArray(R.array.minibus_route_numbers).toList()
        trolleybusRouteNumbers = resources.getStringArray(R.array.trolleybus_route_numbers).toList()

        transportTypeSpinnerAdapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_dropdown_item)
        transportTypeSpinner.adapter = transportTypeSpinnerAdapter
        transportTypeSpinnerAdapter.clear()
        transportTypeSpinnerAdapter.addAll(transportTypeTitles)

        routeNumberSpinnerAdapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_dropdown_item)
        transportRouteNumberSpinner.adapter = routeNumberSpinnerAdapter
    }

    private fun initNameFieldListeners() {
        nameField.setOnFocusChangeListener { _, focused ->
            if (!focused) {
                viewModel.setName(nameField.text.toString().trim())
            }
        }
        nameField.setOnKeyListener { _, _, keyEvent ->
            when {
                keyEvent.keyCode == KeyEvent.KEYCODE_NAVIGATE_NEXT && keyEvent.action == KeyEvent.ACTION_UP -> {
                    viewModel.setName(nameField.text.toString().trim())
                    true
                }
                else -> false
            }
        }
    }

    private fun initTransportTypeSpinnerListeners() {
        transportTypeSpinner.onItemSelectedListener = SimpleItemSelectListener { index ->
            transportTypeValues.getOrNull(index)
                    ?.also { viewModel.setTransportType(it) }
        }
    }

    private fun initTransportRouteNumberSpinnerListeners() {
        transportRouteNumberSpinner.onItemSelectedListener = SimpleItemSelectListener { index ->
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

        updateTransportType(data.transportType)
        updateTransportRouteNumbers(data.transportType)
        updateRouteNumber(data.routeNumber)

        startTrackingButton.isEnabled = data.isValid
    }

    private fun updateTransportType(transportType: String) {
        val typeIndex = transportTypeValues.indexOf(transportType)
        val typeSelectedPosition = transportTypeSpinner.selectedItemPosition
        val transportTypeChanged = typeIndex != typeSelectedPosition
                && typeIndex < transportTypeSpinner.count
        if (transportTypeChanged) {
            transportTypeSpinner.setSelection(typeIndex)
        }
    }

    private fun updateTransportRouteNumbers(transportType: String) {
        val numbers = when (transportType) {
            TransportType.MINIBUS -> minibusRouteNumbers
            TransportType.TROLLEYBUS -> trolleybusRouteNumbers
            else -> emptyList()
        }
        if (routeNumbers != numbers) {
            routeNumbers = numbers
            routeNumberSpinnerAdapter.clear()
            routeNumberSpinnerAdapter.addAll(routeNumbers)
            routeNumberSpinnerAdapter.notifyDataSetChanged()
            transportRouteNumberSpinner.setSelection(-1)
        }
    }

    private fun updateRouteNumber(routeNumber: String) {
        val routeNumberIndex = routeNumbers.indexOf(routeNumber)
        val routeNumberSelectedPosition = transportRouteNumberSpinner.selectedItemPosition
        val routeNumberChanged = routeNumberIndex != routeNumberSelectedPosition
                && routeNumberIndex < transportTypeSpinnerAdapter.count
        if (routeNumberChanged) {
            transportRouteNumberSpinner.setSelection(routeNumberIndex)
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
