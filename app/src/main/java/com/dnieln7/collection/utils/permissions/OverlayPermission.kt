package com.dnieln7.collection.utils.permissions

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment

class OverlayPermission {

    private var launcher: ActivityResultLauncher<Intent>? = null

    fun registerPermissionListener(
        componentActivity: ComponentActivity,
        onAllowed: () -> Unit,
        onDenied: () -> Unit
    ) {
        launcher = componentActivity.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (hasPermission(componentActivity)) {
                onAllowed()
            } else {
                onDenied()
            }
        }
    }

    fun registerPermissionListener(
        fragment: Fragment,
        onAllowed: () -> Unit,
        onDenied: () -> Unit
    ) {
        launcher = fragment.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (hasPermission(fragment.requireContext())) {
                onAllowed()
            } else {
                onDenied()
            }
        }
    }

    fun requestPermission(context: Context) {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:${context.packageName}")
        )

        launcher?.launch(intent)
    }

    fun onDestroy() {
        launcher?.unregister()
        launcher = null
    }

    companion object {

        fun hasPermission(context: Context): Boolean {
            return Settings.canDrawOverlays(context)
        }
    }
}