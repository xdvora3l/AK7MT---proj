package com.example.m_dvoracek__mt_proj

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Rect
import android.os.Bundle
import android.widget.Toast
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import org.osmdroid.config.Configuration
import org.osmdroid.views.MapView
import androidx.activity.viewModels
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import android.util.Log
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.api.IMapController
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import com.example.m_dvoracek__mt_proj.data.Location
import com.example.m_dvoracek__mt_proj.viewmodel.LocationViewModel
import android.location.LocationManager
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen


class MainActivity : AppCompatActivity(), MapListener {

    private lateinit var mapView: MapView
    private lateinit var controller: IMapController
    private lateinit var myLocationOverlay: MyLocationNewOverlay
    private val locationViewModel: LocationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        Configuration.getInstance().userAgentValue = packageName

        setContentView(R.layout.activity_main)

        val addButton: Button = findViewById(R.id.buttonAdd)
        val centerButton: Button = findViewById(R.id.buttonCenter)
        val showListButton: Button = findViewById(R.id.buttonShowList)

        mapView = findViewById(R.id.mapview)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Požádejte uživatele o oprávnění
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        } else {
            setupMap()

            addButton.setOnClickListener {
                val currentLocation = myLocationOverlay.myLocation
                if (currentLocation != null) {
                    val location = Location(
                        latitude = myLocationOverlay.myLocation.latitude,
                        longitude = myLocationOverlay.myLocation.longitude
                    )
                    locationViewModel.addLocation(location)

                    Toast.makeText(this, "Lokace uložena!", Toast.LENGTH_SHORT).show()
                }
                else{
                    Toast.makeText(this, "Lokace není dostupná!", Toast.LENGTH_SHORT).show()
                }

            }

            centerButton.setOnClickListener {
                controller.setCenter(myLocationOverlay.myLocation)  // Nastavíme mapu na aktuální polohu
                controller.animateTo(myLocationOverlay.myLocation)
            }

            showListButton.setOnClickListener {
                val intent = Intent(this, ListActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun getCurrentLocation(): android.location.Location? {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
            val provider = LocationManager.GPS_PROVIDER
            return locationManager.getLastKnownLocation(provider)
        }
        else return null
    }

    private fun setupMap() {
        mapView = findViewById(R.id.mapview)
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.mapCenter
        mapView.setMultiTouchControls(true)
        mapView.getLocalVisibleRect(Rect())
        mapView.controller.setZoom(12)
        controller = mapView.controller
        myLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(this), mapView)
        myLocationOverlay.enableMyLocation()
        myLocationOverlay.enableFollowLocation()
        myLocationOverlay.isDrawAccuracyEnabled = true

        myLocationOverlay.runOnFirstFix {
            runOnUiThread {
                val userLocation = myLocationOverlay.myLocation
                if (userLocation != null) {
                    controller.setCenter(userLocation)
                    controller.animateTo(userLocation)
                }
            }
        }

        // Přidání overlay do mapy
        mapView.overlays.add(myLocationOverlay)
        mapView.addMapListener(this)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            setupMap()
        } else {
            Toast.makeText(this, "Permission denied. The app will exit.", Toast.LENGTH_SHORT).show()
            finish() // Close the app if permissions are denied
        }
    }

    override fun onZoom(event: ZoomEvent?): Boolean {
        // Handle zoom if needed
        return true
    }

    override fun onScroll(event: ScrollEvent?): Boolean {
        // Handle scroll if needed
        Log.e("TAG", "onScroll: lat=${event?.source?.mapCenter?.latitude} lon=${event?.source?.mapCenter?.longitude}")
        return true
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume() // Ensure map works properly when app is resumed
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause() // Release map resources
    }
}