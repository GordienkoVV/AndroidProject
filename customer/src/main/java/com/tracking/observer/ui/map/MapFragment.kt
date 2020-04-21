package com.tracking.observer.ui.map

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.tracking.observer.R

class MapFragment : Fragment(), OnMapReadyCallback {

    private val viewModel: MapViewModel by viewModels()
    private lateinit var googleMap: GoogleMap

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.screen_map, container, false)
    }

    @SuppressLint("FragmentLiveDataObserve")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        viewModel.viewState.error.observe(this, Observer { onError(it) })
        viewModel.viewState.loading.observe(this, Observer { onLoading(it) })
        viewModel.viewState.data.observe(this, Observer { onDataChanged(it) })
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        val tiraspol = LatLng(46.837597, 29.632806)
        this.googleMap.addMarker(MarkerOptions().position(tiraspol).title("Tiraspol"))
        this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(tiraspol, 13f))
    }

    private fun onError(throwable: Throwable?) {
        // TODO use for display error message
    }

    private fun onLoading(value: Boolean?) {
        // TODO use for display loading state
    }

    private fun onDataChanged(value: List<GeoPoint>?) {
        googleMap.clear()
        value?.map {
            MarkerOptions()
                    .title(it.title)
                    .snippet(it.type)
                    .position(LatLng(it.latitude, it.longitude))
        }?.forEach { googleMap.addMarker(it) }
    }

}
