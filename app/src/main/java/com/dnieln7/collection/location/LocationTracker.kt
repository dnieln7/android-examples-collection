package com.dnieln7.collection.location

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.location.Location
import android.os.Looper
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@SuppressLint("MissingPermission")
class LocationTracker : LocationCallback() {

    private lateinit var settingsClient: SettingsClient
    private lateinit var locationClient: FusedLocationProviderClient

    private var updatesEnabled: Boolean = false

    private val _lastKnownLocation = MutableStateFlow(LocationState())
    val lastKnownLocation = _lastKnownLocation.asStateFlow()

    private val _currentLocation = MutableStateFlow(LocationState())
    val currentLocation = _currentLocation.asStateFlow()

    private val _locationUpdates = MutableStateFlow(LocationUpdatesState())
    val locationUpdates = _locationUpdates.asStateFlow()

    private val currentLocationRequest
        get() = CurrentLocationRequest.Builder()
            .setDurationMillis(5000)
            .setMaxUpdateAgeMillis(60000)
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
            .build()

    private val locationUpdatesRequest = LocationRequest.create().apply {
        interval = 10000
        fastestInterval = 5000
        priority = Priority.PRIORITY_HIGH_ACCURACY
    }

    fun onCreate(context: Context) {
        locationClient = LocationServices.getFusedLocationProviderClient(context)
        settingsClient = LocationServices.getSettingsClient(context)
    }

    fun onPause() {
        stopLocationUpdates()
    }

    fun onResume() {
        if (updatesEnabled) {
            startLocationUpdates()
        }
    }

    fun getLastKnownLocation() {
        _lastKnownLocation.tryEmit(LocationState(tracking = true))

        locationClient.lastLocation.addOnCompleteListener {
            if (it.isSuccessful) {
                _lastKnownLocation.tryEmit(LocationState(location = it.result))
            } else {
                _lastKnownLocation.tryEmit(LocationState(error = it.exception?.message))
            }
        }
    }

    fun calculateCurrentLocation() {
        _currentLocation.tryEmit(LocationState(tracking = true))

        locationClient.getCurrentLocation(currentLocationRequest, null).addOnCompleteListener {
            if (it.isSuccessful) {
                _currentLocation.tryEmit(LocationState(location = it.result))
            } else {
                _currentLocation.tryEmit(LocationState(error = it.exception?.message))
            }
        }
    }

    fun changeUpdatesSettings(enable: Boolean) {
        updatesEnabled = enable

        if (updatesEnabled) {
            startLocationUpdates()
        } else {
            stopLocationUpdates()
        }
    }

    private fun startLocationUpdates() {
        locationClient.requestLocationUpdates(
            locationUpdatesRequest,
            this,
            Looper.getMainLooper()
        )
    }

    private fun stopLocationUpdates() {
        locationClient.removeLocationUpdates(this)
    }

    override fun onLocationResult(result: LocationResult) {
        if (result.lastLocation != null) {
            _locationUpdates.tryEmit(LocationUpdatesState(location = result.lastLocation))
        } else {
            _locationUpdates.tryEmit(LocationUpdatesState(error = "No location"))
        }
    }

    private fun currentSettingsSatisfy(context: Activity) {
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationUpdatesRequest)
        val task: Task<LocationSettingsResponse> =
            settingsClient.checkLocationSettings(builder.build())

        task.addOnSuccessListener {
            // Location settings are satisfied
        }

        task.addOnFailureListener {
            if (it is ResolvableApiException) {
                // Location settings are not satisfied
            }
        }
    }

    data class LocationState(
        val tracking: Boolean = false,
        val location: Location? = null,
        val error: String? = null
    )

    data class LocationUpdatesState(
        val location: Location? = null,
        val error: String? = null
    )
}