package com.example.m_dvoracek__mt_proj


import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.appcompat.app.AppCompatActivity
import com.example.m_dvoracek__mt_proj.databinding.ActivityMainBinding
import org.osmdroid.config.Configuration
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.util.GeoPoint
import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private lateinit var mapView: MapView

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
        // Inicializace mapy
        val startPoint = GeoPoint(50.0755, 14.4378) // Praha
        mapView.controller.setCenter(startPoint)
        mapView.controller.setZoom(12)

        // Příklad přidání markeru na mapu
        val marker = Marker(mapView)
        marker.position = startPoint
        marker.title = "Praha"
        mapView.overlays.add(marker)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            setupMap()
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        // Obnovení mapy při návratu z pozadí
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        // Uvolnění zdrojů mapy při pauze
        mapView.onPause()
    }
}