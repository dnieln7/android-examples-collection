package com.dnieln7.collection.bubble

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.dnieln7.collection.R
import com.dnieln7.collection.databinding.ActivityBubbleBinding
import com.dnieln7.collection.utils.permissions.OverlayPermission
import com.dnieln7.collection.utils.toastLong

class BubbleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBubbleBinding

    private val overlayPermission = OverlayPermission()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBubbleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        updateServiceStatus()

        overlayPermission.registerPermissionListener(
            componentActivity = this,
            onAllowed = { attemptToTurnOn() },
            onDenied = { toastLong(getString(R.string.permissions_denied)) }
        )

        binding.turnOn.setOnClickListener { attemptToTurnOn() }
        binding.turnOff.setOnClickListener {
            if (BatteryBubbleService.isActive) {
                stopService(Intent(this, BatteryBubbleService::class.java))
            }

            binding.turnOn.isVisible = true
            binding.turnOff.isVisible = false
        }
    }

    override fun onDestroy() {
        overlayPermission.onDestroy()

        super.onDestroy()
    }

    private fun attemptToTurnOn() {
        val hasPermission = OverlayPermission.hasPermission(this)

        if (hasPermission) {
            if (!BatteryBubbleService.isActive) {
                startService(Intent(this, BatteryBubbleService::class.java))
            }

            binding.turnOn.isVisible = false
            binding.turnOff.isVisible = true
        } else {
            overlayPermission.requestPermission(this)
        }
    }

    private fun updateServiceStatus() {
        binding.turnOn.isVisible = !BatteryBubbleService.isActive
        binding.turnOff.isVisible = BatteryBubbleService.isActive
    }
}