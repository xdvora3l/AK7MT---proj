package com.example.m_dvoracek__mt_proj.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface LocationDataAccess {
    @Insert
    suspend fun insert(location: Location)

    @Query("SELECT * FROM locations")
    fun getAllLocations(): LiveData<List<Location>>
}