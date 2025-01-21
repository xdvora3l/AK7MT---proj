package com.example.m_dvoracek__mt_proj

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Rect
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import org.osmdroid.config.Configuration
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import android.util.Log
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.api.IMapController
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent

class MainActivity : AppCompatActivity(), MapListener {

    private lateinit var mapView: MapView
    private lateinit var controller: IMapController
    private lateinit var myLocationOverlay: MyLocationNewOverlay

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Nastavení knihovny OSMDroid
        Configuration.getInstance().userAgentValue = packageName

        // Nastavení layoutu pro mapu
        setContentView(R.layout.activity_main)

        mapView = findViewById(R.id.mapview)

        // Zkontrolujte oprávnění pro lokalizaci
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Požádejte uživatele o oprávnění
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        } else {
            setupMap()
        }
    }

    private fun setupMap() {
        mapView = findViewById(R.id.mapview)
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.mapCenter
        mapView.setMultiTouchControls(true)
        mapView.getLocalVisibleRect(Rect())
        mapView.controller.setZoom(12)
        // Inicializace mapy a zobrazení uživatelovy polohy
        controller = mapView.controller
        myLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(this), mapView)
        myLocationOverlay.enableMyLocation() // Povolení sledování polohy
        myLocationOverlay.enableFollowLocation() // Follow the user's location
        myLocationOverlay.isDrawAccuracyEnabled = true

        // Nastavení počátečního centra mapy
        myLocationOverlay.runOnFirstFix {
            runOnUiThread {
                val userLocation = myLocationOverlay.myLocation
                if (userLocation != null) {
                    controller.setCenter(userLocation)  // Nastavíme mapu na aktuální polohu
                    controller.animateTo(userLocation)
                    addMarker(userLocation)
                }
            }
        }

        // Přidání overlay do mapy
        mapView.overlays.add(myLocationOverlay)
        mapView.addMapListener(this)
    }

    private fun addMarker(location: GeoPoint) {
        val marker = Marker(mapView)
        marker.position = location
        marker.title = "Me"
        mapView.overlays.add(marker)
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