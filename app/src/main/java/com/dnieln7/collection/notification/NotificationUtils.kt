package com.dnieln7.collection.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import com.dnieln7.collection.R

class NotificationUtils(context: Context) : ContextWrapper(context) {

    private val default = "General Messages"
    private val defaultID = "com.dnieln7.collection.GENERAL"

    init {
        createGeneralChannel()
    }

    private fun getManager(): NotificationManager {
        return getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    private fun createGeneralChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val generalChannel = NotificationChannel(
                defaultID,
                default,
                NotificationManager.IMPORTANCE_DEFAULT
            )

            generalChannel.enableLights(true)
            generalChannel.enableVibration(true)
            generalChannel.importance = NotificationManager.IMPORTANCE_DEFAULT
            generalChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC

            getManager().createNotificationChannel(generalChannel)
        }
    }

    fun actionNotification(): Notification {
        val notification: Notification
        val builder = NotificationCompat.Builder(this, defaultID)

        val imageBitmap = BitmapFactory.decodeResource(
            applicationContext.resources,
            R.drawable.ic_fastfood
        )
        val style = NotificationCompat.BigPictureStyle()
            .bigPicture(imageBitmap)
            .bigLargeIcon(null)

        val actionIntent = Intent(applicationContext, ActionReceiver::class.java)
        val actionPendingIntent: PendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            ActionReceiver.REQUEST_CODE,
            actionIntent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        notification = builder
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(getString(R.string.test_notification_action))
            .setSmallIcon(R.mipmap.ic_launcher)
            .setStyle(style)
            .setLargeIcon(imageBitmap)
            .addAction(R.drawable.ic_touch, getString(R.string.send_action), actionPendingIntent)
            .build()

        return notification
    }

    fun notify(notification: Notification) {
        getManager().notify(0, notification)
    }

    fun clear() {
        getManager().cancelAll()
    }
}