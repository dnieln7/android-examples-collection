package com.dnieln7.collection

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dnieln7.collection.animations.AnimationsActivity
import com.dnieln7.collection.bubble.BubbleActivity
import com.dnieln7.collection.databinding.ActivityMainBinding
import com.dnieln7.collection.location.FusedLocationActivity
import com.dnieln7.collection.maps.MapsActivity
import com.dnieln7.collection.notification.NotificationsActivity
import com.dnieln7.collection.qr.QrActivity
import com.dnieln7.collection.utils.permissions.PermissionsManager
import com.dnieln7.collection.utils.permissions.LocationSettings
import com.dnieln7.collection.utils.toastLong

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val locationPermissions = PermissionsManager()
    private val locationSettings = LocationSettings()

    private var waitingIntent: Intent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        locationPermissions.registerPermissionsListener(
            componentActivity = this,
            onAllowed = { checkLocation() },
            onDenied = { toastLong(getString(R.string.permissions_denied)) }
        )

        locationSettings.registerEnableListener(
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

        binding.bubble.setOnClickListener {
            waitingIntent = Intent(this, BubbleActivity::class.java)
            checkLocation()
        }
    }

    override fun onDestroy() {
        locationPermissions.onDestroy()
        locationSettings.onDestroy()

        super.onDestroy()
    }

    private fun checkLocation() {
        val location = PermissionsManager.hasPermissions(
            context = this,
            permissions = PermissionsManager.locationPermissions
        ).allGranted

        val gps = LocationSettings.isEnabled(this)

        if (location) {
            if (gps) {
                launchWaitingIntent()
            } else {
                locationSettings.requestEnable()
            }
        } else {
            locationPermissions.requestPermissions(PermissionsManager.locationPermissions)
        }
    }

    private fun launchWaitingIntent() {
        startActivity(waitingIntent)
    }
}