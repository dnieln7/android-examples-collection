package com.dnieln7.collection.animations

import android.view.View

/**
 * Transform the view permanently
 * By = adds more value, e.g. more rotation (from 180 to 360) as more interactions
 * Not by = does not adds more value if more interactions value will stay as e.g. rotation 180
 */
object CommonAnimations {

    fun rotate(view: View, degrees: Float, clockWise: Boolean = true) {
        view.animate()
            .rotationBy(if (clockWise) degrees else (degrees * -1))
            .setDuration(1000)
            .start()
    }
}