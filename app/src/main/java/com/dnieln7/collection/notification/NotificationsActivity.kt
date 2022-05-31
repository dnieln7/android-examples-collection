package com.dnieln7.collection.notification

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dnieln7.collection.databinding.ActivityNotificationsBinding

class NotificationsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotificationsBinding
    private lateinit var notificationUtils: NotificationUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        notificationUtils = NotificationUtils(this)

        binding.notificationAction.setOnClickListener {
            notificationUtils.clear()
            notificationUtils.notify(notificationUtils.actionNotification())
        }
    }
}