package com.dnieln7.collection.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.dnieln7.collection.R
import com.dnieln7.collection.utils.toastLong

/**
 * This Receiver must be declared on Manifest
 */
class ActionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        if (context != null) {
            context.toastLong(context.getString(R.string.notification_action_triggered))

            NotificationUtils(context).clear()
        }
    }

    companion object {
        const val REQUEST_CODE = 999
    }
}