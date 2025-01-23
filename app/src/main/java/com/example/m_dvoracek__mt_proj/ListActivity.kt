package com.example.m_dvoracek__mt_proj

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.m_dvoracek__mt_proj.viewmodel.LocationViewModel
import com.example.m_dvoracek__mt_proj.ui.adapter

class ListActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var backButton: Button
    private val locationViewModel: LocationViewModel by viewModels() // Předpokládám, že používáte ViewModel pro práci s databází
    private lateinit var locationAdapter: adapter // Adapter pro RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        recyclerView = findViewById(R.id.recyclerView)
        backButton = findViewById(R.id.buttonBack)

        // Nastavení RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        locationAdapter = adapter(mutableListOf()) // Vytvořte si vlastní adapter pro zobrazení dat
        recyclerView.adapter = locationAdapter

        // Získání dat z ViewModelu a aktualizace RecyclerView
        locationViewModel.allLocations.observe(this, { locations ->
            locationAdapter.submitList(locations) // Předání dat do adapteru
        })

        // Nastavení tlačítka pro návrat zpět na MainActivity
        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}

