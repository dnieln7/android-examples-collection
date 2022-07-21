package com.dnieln7.collection.utils.permissions

import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment

class LocationSettings {

    private var launcher: ActivityResultLauncher<Intent>? = null

    fun registerEnableListener(
        componentActivity: ComponentActivity,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        launcher = componentActivity.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (isEnabled(componentActivity)) {
                onSuccess()
            } else {
                onFailure()
            }
        }
    }

    fun registerEnableListener(
        fragment: Fragment,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        launcher = fragment.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (isEnabled(fragment.requireContext())) {
                onSuccess()
            } else {
                onFailure()
            }
        }
    }

    fun requestEnable() {
        launcher?.launch(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
    }

    fun onDestroy() {
        launcher?.unregister()
        launcher = null
    }

    companion object {

        fun isEnabled(context: Context): Boolean {
            return try {
                val manager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

                manager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            } catch (e: Exception) {
                Log.e("LocationManager", "Error checking location state", e)

                false
            }
        }
    }
}