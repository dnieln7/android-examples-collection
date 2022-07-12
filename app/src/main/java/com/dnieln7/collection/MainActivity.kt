package com.dnieln7.collection

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dnieln7.collection.animations.AnimationsActivity
import com.dnieln7.collection.databinding.ActivityMainBinding
import com.dnieln7.collection.location.FusedLocationActivity
import com.dnieln7.collection.maps.MapsActivity
import com.dnieln7.collection.notification.NotificationsActivity
import com.dnieln7.collection.qr.QrActivity
import com.dnieln7.collection.utils.permissions.LocationManager
import com.dnieln7.collection.utils.permissions.PermissionsManager
import com.dnieln7.collection.utils.toastLong

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val permissionsManager = PermissionsManager()
    private val locationManager = LocationManager()

    private var waitingIntent: Intent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        permissionsManager.registerLocationPermissionsListener(
            componentActivity = this,
            onAllowed = { checkLocation() },
            onDenied = { toastLong(getString(R.string.permissions_denied)) }
        )

        locationManager.registerEnableListener(
            componentActivity = this,
            onSuccess = { launchWaitingIntent() },
            onFailure = { toastLong(getString(R.string.services_denied)) }
        )

        binding.qr.setOnClickListener {
            startActivity(Intent(this, QrActivity::class.java))
        }

        binding.notifications.setOnClickListener {
            startActivity(Intent(this, NotificationsActivity::class.java))
        }

        binding.animations.setOnClickListener {
            startActivity(Intent(this, AnimationsActivity::class.java))
        }

        binding.maps.setOnClickListener {
            waitingIntent = Intent(this, MapsActivity::class.java)
            checkLocation()
        }

        binding.location.setOnClickListener {
            waitingIntent = Intent(this, FusedLocationActivity::class.java)
            checkLocation()
        }
    }

    override fun onDestroy() {
        permissionsManager.onDestroy()
        locationManager.onDestroy()

        super.onDestroy()
    }

    private fun checkLocation() {
        val location = PermissionsManager.hasLocationPermissions(this)
        val gps = LocationManager.isEnabled(this)

        if (location) {
            if (gps) {
                launchWaitingIntent()
            } else {
                locationManager.requestEnable()
            }
        } else {
            permissionsManager.requestLocationPermissions()
        }
    }

    private fun launchWaitingIntent() {
        startActivity(waitingIntent)
    }
}