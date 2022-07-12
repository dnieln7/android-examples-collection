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

/**
 * Helper class to manage location settings.
 */
class LocationManager {

    private var enableLocationLauncher: ActivityResultLauncher<Intent>? = null

    /**
     * Registers a listener for location requests.
     *
     * This must only be called once.
     *
     * Recommended calling this method in your activity's onCreate method.
     *
     * @param componentActivity [ComponentActivity] instance.
     * @param onSuccess Callback when user turns on their location.
     * @param onFailure Callback when user does not turn on their location.
     */
    fun registerEnableListener(
        componentActivity: ComponentActivity,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        enableLocationLauncher = componentActivity.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (isEnabled(componentActivity)) {
                onSuccess()
            } else {
                onFailure()
            }
        }
    }

    /**
     * Registers a listener for location requests.
     *
     * This must only be called once.
     *
     * Recommended calling this method in your fragment's onCreateView method.
     *
     * @param fragment [Fragment] instance.
     * @param onSuccess Callback when user turns on their location.
     * @param onFailure Callback when user does not turn on their location.
     */
    fun registerEnableListener(
        fragment: Fragment,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        enableLocationLauncher = fragment.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (isEnabled(fragment.requireContext())) {
                onSuccess()
            } else {
                onFailure()
            }
        }
    }

    /**
     * Launches intent to ask user for turning on their location.
     *
     * This will open the settings view of the application.
     */
    fun requestEnable() {
        enableLocationLauncher?.launch(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
    }

    /**
     * Unregisters all listeners and destroys all related instances.
     *
     * Invoke this in your activity / fragment onDestroy method.
     */
    fun onDestroy() {
        enableLocationLauncher?.unregister()
        enableLocationLauncher = null
    }

    companion object {

        /**
         * Checks current location configuration.
         *
         * @param context Application, activity or fragment context.
         *
         * @return True if location is on, false otherwise.
         */
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