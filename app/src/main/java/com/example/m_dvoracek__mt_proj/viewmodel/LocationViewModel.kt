package com.example.m_dvoracek__mt_proj.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.m_dvoracek__mt_proj.data.Location
import com.example.m_dvoracek__mt_proj.data.database
import com.example.m_dvoracek__mt_proj.data.LocationDataAccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LocationViewModel(application: Application) : AndroidViewModel(application) {

    private val locationDataAccess: LocationDataAccess = database.getDatabase(application).locationDataAccess()
    val allLocations: LiveData<List<Location>> = locationDataAccess.getAllLocations()

    fun addLocation(location: Location) {
        viewModelScope.launch(Dispatchers.IO) {
            locationDataAccess.insert(location)
        }
    }
}