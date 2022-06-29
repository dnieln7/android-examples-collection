package com.dnieln7.collection

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dnieln7.collection.animations.AnimationsActivity
import com.dnieln7.collection.databinding.ActivityMainBinding
import com.dnieln7.collection.maps.MapsActivity
import com.dnieln7.collection.notification.NotificationsActivity
import com.dnieln7.collection.qr.QrActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.qr.setOnClickListener {
            startActivity(Intent(this, QrActivity::class.java))
        }

        binding.notifications.setOnClickListener {
            startActivity(Intent(this, NotificationsActivity::class.java))
        }

        binding.animations.setOnClickListener {
            startActivity(Intent(this, AnimationsActivity::class.java))
        }

        binding.location.setOnClickListener {
            if (MapsActivity.hasLocationPermissions(this)) {
                startActivity(Intent(this, MapsActivity::class.java))
            } else {
                MapsActivity.requestLocationPermissions(this)
            }
        }
    }
}