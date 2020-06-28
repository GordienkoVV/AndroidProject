package com.tracking.observer.ui.map

import android.annotation.SuppressLint
import android.graphics.Color
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
import com.google.android.gms.maps.model.*
import com.google.maps.android.PolyUtil
import com.tracking.observer.R

class MapFragment : Fragment(), OnMapReadyCallback {

    private val viewModel: MapViewModel by viewModels()
    private lateinit var googleMap: GoogleMap
    private var cachedPolylines: List<Polyline>? = null
    private var cachedMarkers: List<Marker>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.screen_map, container, false)
    }

    @SuppressLint("FragmentLiveDataObserve")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        val tiraspol = LatLng(46.837597, 29.632806)
        this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(tiraspol, 13f))
        val viewState = viewModel.viewState
        viewState.error.observe(this, Observer { onError(it) })
        viewState.loading.observe(this, Observer { onLoading(it) })
        viewState.routes.observe(this, Observer { onRoutesChanged(it) })
        viewState.transportPoints.observe(this, Observer { onTransportPointsChanged(it) })
    }

    private fun onError(throwable: Throwable?) {
        // TODO use for display error message
    }

    private fun onLoading(value: Boolean?) {
        // TODO use for display loading state
    }

    private fun onRoutesChanged(value: List<RouteDto>?) {
        cachedPolylines?.forEach { it.remove() }
        cachedPolylines = value
                ?.filter { it.items.isNotEmpty() }
                ?.map { PolylineOptions()
                        .addAll(it.items)
                        .color(it.color)
                        .width(it.width)
                }
                ?.map(googleMap::addPolyline)
    }

    private fun onTransportPointsChanged(value: List<GeoPointDto>?) {
        cachedMarkers?.forEach { it.remove() }
        cachedMarkers = value
                ?.map { MarkerOptions()
                    .title(it.title)
                    .snippet(it.type)
                    .position(it.latLng)
                }?.map(googleMap::addMarker)
    }

}
