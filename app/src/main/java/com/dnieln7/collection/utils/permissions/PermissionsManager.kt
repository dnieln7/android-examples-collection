package com.dnieln7.collection.utils.permissions

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

class PermissionsManager {
    private var launcher: ActivityResultLauncher<Array<String>>? = null

    fun registerPermissionsListener(
        componentActivity: ComponentActivity,
        onAllowed: (permissions: Map<String, Boolean>) -> Unit,
        onDenied: (permissions: Map<String, Boolean>) -> Unit
    ) {
        launcher =
            componentActivity.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                val result = allPermissionsWereAllowed(permissions)

                if (result) {
                    onAllowed(permissions)
                } else {
                    onDenied(permissions)
                }
            }
    }

    fun registerPermissionsListener(
        fragment: Fragment,
        onAllowed: (permissions: Map<String, Boolean>) -> Unit,
        onDenied: (permissions: Map<String, Boolean>) -> Unit
    ) {
        launcher =
            fragment.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                val result = allPermissionsWereAllowed(permissions)

                if (result) {
                    onAllowed(permissions)
                } else {
                    onDenied(permissions)
                }
            }
    }

    fun requestPermissions(permissions: Array<String>) {
        launcher?.launch(permissions)
    }

    fun onDestroy() {
        launcher?.unregister()
        launcher = null
    }

    private fun allPermissionsWereAllowed(permissions: Map<String, Boolean>?): Boolean {
        return if (!permissions.isNullOrEmpty()) {
            val result = permissions.values.sumOf { if (it) 1 as Int else -1 }

            result == permissions.size
        } else {
            false
        }
    }

    data class PermissionsResult(val allGranted: Boolean, val results: Map<String, Boolean>)

    companion object {

        private const val fineLocation = Manifest.permission.ACCESS_FINE_LOCATION
        private const val coarseLocation = Manifest.permission.ACCESS_COARSE_LOCATION

        private const val granted = PackageManager.PERMISSION_GRANTED

        val locationPermissions get() = arrayOf(fineLocation, coarseLocation)

        fun hasPermissions(context: Context, permissions: Array<String>): PermissionsResult {
            val results = mutableMapOf<String, Boolean>()

            permissions.forEach {
                results[it] = ContextCompat.checkSelfPermission(context, it) == granted
            }

            return PermissionsResult(results.values.all { it }, results)
        }
    }
}