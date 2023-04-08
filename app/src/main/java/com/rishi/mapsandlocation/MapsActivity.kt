package com.rishi.mapsandlocation

import android.annotation.SuppressLint
import android.app.LocaleManager
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.rishi.mapsandlocation.databinding.ActivityMapsBinding

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }


    override fun onStart() {
        requestAccessFineLocation()
        super.onStart()
        when{
            isFineLocationGranted() ->{
                setupLocationListener()
            }
            else-> requestAccessFineLocation()
        }
    }



    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        while (requestCode == 999){
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                setupLocationListener()
        }else{
            Toast.makeText(this,"Permisson not granted",Toast.LENGTH_LONG).show();

        }

        }
    }

    @SuppressLint("MissingPermission")
    private fun setupLocationListener() {
        val lm = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val providers = lm.getProviders(true)

        var l:Location? = null
        for (i in providers.indices.reversed()){
            l = lm.getLastKnownLocation(providers[i])
            if(l!= null) break
        }
        l?.let{
            if (::mMap.isInitialized){
                val current = LatLng(it.latitude, it.longitude)
                mMap.addMarker(MarkerOptions().position(current).title("Marker in current"))
                mMap.moveCamera(CameraUpdateFactory.newLatLng(current))
            }
        }

    }

    fun isFineLocationGranted():Boolean{
        return checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestAccessFineLocation() {

        this.requestPermissions(
            arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION),
            999
        )

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.apply {
            isZoomControlsEnabled = true
            isZoomGesturesEnabled = true
            isMyLocationButtonEnabled = true
            isCompassEnabled = true
        }
        mMap.setMaxZoomPreference(13f)

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
        mMap.addPolyline(
            PolylineOptions()
                .add(sydney,LatLng(20.59,78.39))
                .color(ContextCompat.getColor(baseContext,R.color.purple_700))
        )
            .width = 2f
    }
}