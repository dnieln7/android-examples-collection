package com.dnieln7.collection.bubble

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.util.TypedValue
import android.view.*
import androidx.core.view.updateLayoutParams
import androidx.core.widget.TextViewCompat
import com.dnieln7.collection.R
import com.dnieln7.collection.databinding.BubbleBatteryBinding
import com.dnieln7.collection.utils.NotificationUtils
import com.dnieln7.collection.utils.toastShort
import kotlinx.coroutines.*
import java.util.*
import kotlin.math.roundToInt


class BatteryBubbleService : Service() {

    private lateinit var binding: BubbleBatteryBinding
    private lateinit var layoutParams: WindowManager.LayoutParams
    private lateinit var windowManager: WindowManager

    private var type: Int? = null

    private var lastTap: Long = 0L

    private var job: Job? = null

    override fun onCreate() {
        super.onCreate()

        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        val inflater = baseContext.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater

        binding = BubbleBatteryBinding.inflate(inflater)

        type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_TOAST
        }

        layoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            type!!,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        layoutParams.gravity = Gravity.CENTER
        layoutParams.x = 0
        layoutParams.y = 0

        windowManager.addView(binding.root, layoutParams)

        enableDraggable()

        instance = this
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val random = Random()

        job = MainScope().launch {
            while (isActive) {
                val level = random.nextInt(100) + 1

                displayBatteryLevel(level)

                delay(3000)
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationUtils = NotificationUtils(this)

            startForeground(SERVICE_ID, notificationUtils.batteryBubbleNotification())
        }

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        job?.cancel()
        windowManager.removeView(binding.root)
        instance = null
    }

    private fun displayBatteryLevel(level: Int) {
        val color = when {
            level <= 25 -> R.color.error
            level <= 50 -> R.color.warning
            level <= 75 -> R.color.info
            else -> R.color.success
        }

        val height = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            level.toFloat(),
            resources.displayMetrics
        ).roundToInt()

        binding.power.text = level.toString()
        TextViewCompat.setCompoundDrawableTintList(
            binding.power,
            ColorStateList.valueOf(getColor(R.color.white))
        )
        binding.power.setCompoundDrawablesWithIntrinsicBounds(
            0,
            0,
            R.drawable.ic_power,
            0
        )

        binding.base.setBackgroundResource(color)
        binding.base.updateLayoutParams { this.height = height }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun enableDraggable() {
        binding.root.setOnTouchListener(object : View.OnTouchListener {
            val updatedLayoutParams = layoutParams
            var x = 0.0
            var y = 0.0
            var px = 0.0
            var py = 0.0

            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                event?.also {
                    when (it.action) {
                        MotionEvent.ACTION_DOWN -> {
                            x = updatedLayoutParams.x.toDouble()
                            y = updatedLayoutParams.y.toDouble()

                            px = event.rawX.toDouble()
                            py = event.rawY.toDouble()
                        }
                        MotionEvent.ACTION_MOVE -> {
                            updatedLayoutParams.x = (x + event.rawX - px).toInt()
                            updatedLayoutParams.y = (y + event.rawY - py).toInt()

                            windowManager.updateViewLayout(binding.root, updatedLayoutParams)
                        }
                        MotionEvent.ACTION_UP -> {
                            val currentTap = System.currentTimeMillis()

                            if ((currentTap - lastTap) in 100..200) {
                                toastShort("Double tap click")
                            }

                            lastTap = currentTap
                        }
                    }
                }

                return false
            }
        })
    }

    companion object {
        private var instance: BatteryBubbleService? = null

        const val SERVICE_ID = 1

        val isActive get() = instance != null
    }
}