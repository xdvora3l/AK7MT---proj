package com.example.m_dvoracek__mt_proj.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.m_dvoracek__mt_proj.data.Location
import com.example.m_dvoracek__mt_proj.databinding.ItemLocationBinding

class adapter(private var locations: List<Location>) : RecyclerView.Adapter<adapter.LocationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        val binding = ItemLocationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LocationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        val location = locations[position]
        holder.bind(location)
    }

    override fun getItemCount(): Int = locations.size

    fun submitList(newLocations: List<Location>) {
        locations = newLocations
        notifyDataSetChanged()
    }

    inner class LocationViewHolder(private val binding: ItemLocationBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(location: Location) {
            binding.locationLatitude.text = "Latitude: ${location.latitude}"
            binding.locationLongitude.text = "Longitude: ${location.longitude}"
        }
    }
}