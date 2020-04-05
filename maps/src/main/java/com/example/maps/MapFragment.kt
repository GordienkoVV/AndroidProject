package com.example.maps

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        val tiraspol = LatLng(46.837597, 29.632806)
        this.googleMap.addMarker(MarkerOptions().position(tiraspol).title("Tiraspol"))
        this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(tiraspol, 12f))
        this.googleMap.setOnMapClickListener { latLng ->
            this.googleMap.clear()
            this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12f))
            val marker: Marker = this.googleMap.addMarker(MarkerOptions().position(latLng))
        }
    }

}
