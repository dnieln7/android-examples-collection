package com.dnieln7.collection.location

import android.location.Location
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.dnieln7.collection.R
import com.dnieln7.collection.databinding.ActivityFusedLocationBinding
import kotlinx.coroutines.flow.collectLatest

class FusedLocationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFusedLocationBinding

    private val fusedLocationViewModel by viewModels<FusedLocationViewModel> {
        FusedLocationViewModel.Factory(LocationTracker().apply { onCreate(this@FusedLocationActivity) })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFusedLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fusedLocationViewModel.tracker.onCreate(this)

        lifecycleScope.launchWhenResumed {
            fusedLocationViewModel.tracker.lastKnownLocation.collect {
                if (it.location != null) {
                    val labels = getLocationLabels(it.location)

                    binding.lkLatitude.text = labels.first
                    binding.lkLongitude.text = labels.second
                    binding.lkAltitude.text = labels.third

                    binding.lastKnownLocation.isEnabled = true
                }
            }
        }

        lifecycleScope.launchWhenResumed {
            fusedLocationViewModel.tracker.currentLocation.collect {
                if (it.location != null) {
                    val labels = getLocationLabels(it.location)

                    binding.clLatitude.text = labels.first
                    binding.clLongitude.text = labels.second
                    binding.clAltitude.text = labels.third

                    binding.currentLocation.isEnabled = true
                }
            }
        }

        lifecycleScope.launchWhenResumed {
            fusedLocationViewModel.tracker.locationUpdates.collectLatest {
                if (it.location != null) {
                    val labels = getLocationLabels(it.location)

                    binding.luLatitude.text = labels.first
                    binding.luLongitude.text = labels.second
                    binding.luAltitude.text = labels.third
                }
            }
        }

        binding.lastKnownLocation.setOnClickListener {
            binding.lastKnownLocation.isEnabled = false
            fusedLocationViewModel.tracker.getLastKnownLocation()
        }
        binding.currentLocation.setOnClickListener {
            binding.currentLocation.isEnabled = false
            fusedLocationViewModel.tracker.calculateCurrentLocation()
        }
        binding.updatesSwitch.setOnCheckedChangeListener { _, isChecked ->
            fusedLocationViewModel.tracker.changeUpdatesSettings(isChecked)
        }
    }

    override fun onPause() {
        super.onPause()
        fusedLocationViewModel.tracker.onPause()
    }

    override fun onResume() {
        super.onResume()

        fusedLocationViewModel.tracker.onResume()
    }

    private fun getLocationLabels(location: Location): Triple<String, String, String> {
        val latitude = getString(R.string.latitude_placeholder, location.latitude.toString())
        val longitude = getString(R.string.longitude_placeholder, location.longitude.toString())
        val altitude = getString(R.string.altitude_placeholder, location.altitude.toString())

        return Triple(latitude, longitude, altitude)
    }
}