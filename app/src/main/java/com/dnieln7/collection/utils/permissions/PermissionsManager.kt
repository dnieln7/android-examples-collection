package com.dnieln7.collection.utils.permissions

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

/**
 * Helper class to manage app permissions.
 */
class PermissionsManager {

    private var locationPermissionsLauncher: ActivityResultLauncher<Array<String>>? = null


    /**
     * Registers a listener for location permissions requests.
     *
     * This must only be called once.
     *
     * Recommended calling this method in your activity's onCreate method.
     *
     * @param componentActivity [ComponentActivity] instance.
     * @param onAllowed Callback when grants all permissions.
     * @param onDenied Callback when denies all or at least one permission.
     */
    fun registerLocationPermissionsListener(
        componentActivity: ComponentActivity,
        onAllowed: (permissions: Map<String, Boolean>) -> Unit,
        onDenied: (permissions: Map<String, Boolean>) -> Unit
    ) {
        locationPermissionsLauncher =
            componentActivity.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                val result = allPermissionsWereAllowed(permissions)

                if (result) {
                    onAllowed(permissions)
                } else {
                    onDenied(permissions)
                }
            }
    }

    /**
     * Registers a listener for location permissions requests.
     *
     * This must only be called once.
     *
     * Recommended calling this method in your fragment's onCreateView method.
     *
     * @param fragment [Fragment] instance.
     * @param onAllowed Callback when grants all permissions.
     * @param onDenied Callback when denies all or at least one permission.
     */
    fun registerLocationPermissionsListener(
        fragment: Fragment,
        onAllowed: (permissions: Map<String, Boolean>) -> Unit,
        onDenied: (permissions: Map<String, Boolean>) -> Unit
    ) {
        locationPermissionsLauncher =
            fragment.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                val result = allPermissionsWereAllowed(permissions)

                if (result) {
                    onAllowed(permissions)
                } else {
                    onDenied(permissions)
                }
            }
    }

    /**
     * Launches intent to ask user for location permissions, see [locationPermissions].
     *
     * This will open a permissions' dialog for each permission requested.
     */
    fun requestLocationPermissions() {
        locationPermissionsLauncher?.launch(locationPermissions)
    }


    /**
     * Unregisters all listeners and destroys all related instances.
     *
     * Invoke this in your activity / fragment onDestroy method.
     */
    fun onDestroy() {
        locationPermissionsLauncher?.unregister()
        locationPermissionsLauncher = null
    }

    private fun allPermissionsWereAllowed(permissions: Map<String, Boolean>?): Boolean {
        return if (!permissions.isNullOrEmpty()) {
            val result = permissions.values.sumOf { if (it) 1 as Int else -1 }

            result == permissions.size
        } else {
            false
        }
    }

    companion object {

        private const val fineLocation = Manifest.permission.ACCESS_FINE_LOCATION
        private const val coarseLocation = Manifest.permission.ACCESS_COARSE_LOCATION

        private const val granted = PackageManager.PERMISSION_GRANTED

        /**
         * Array of the permissions grouped as locationPermissions, permissions included:
         *
         * * [Manifest.permission.ACCESS_FINE_LOCATION]
         * * [Manifest.permission.ACCESS_COARSE_LOCATION]
         */
        val locationPermissions get() = arrayOf(fineLocation, coarseLocation)

        /**
         * Checks location permissions, see [locationPermissions].
         *
         * @param context Application, activity or fragment context.
         *
         * @return True if all location permissions are granted, false otherwise.
         */
        fun hasLocationPermissions(context: Context): Boolean {
            return ContextCompat.checkSelfPermission(context, fineLocation) == granted &&
                    ContextCompat.checkSelfPermission(context, coarseLocation) == granted
        }
    }
}