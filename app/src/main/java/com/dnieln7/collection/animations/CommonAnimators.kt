package com.dnieln7.collection.animations

import android.animation.AnimatorSet
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import androidx.core.animation.addListener
import com.google.android.material.card.MaterialCardView

/**
 * Transform the view temporally or permanently
 */
object CommonAnimators {

    fun rotate(view: View, degrees: Float, clockWise: Boolean = true) {
        ObjectAnimator.ofFloat(
            view,
            View.ROTATION,
            0f,
            if (clockWise) degrees else (degrees * -1)
        ).apply {
            duration = 1000
        }.start()
    }

    fun translateHorizontal(view: View, distance: Float, toRight: Boolean = false) {
        ObjectAnimator.ofFloat(
            view,
            View.TRANSLATION_X,
            0F,
            if (toRight) distance else (distance * -1)
        ).apply {
            duration = 1000
            repeatCount = 1
            repeatMode = ObjectAnimator.REVERSE
        }.start()
    }

    fun scale(view: View, times: Float) {
        val scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1F, times)
        val scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1F, times)

        ObjectAnimator.ofPropertyValuesHolder(view, scaleX, scaleY).apply {
            duration = 1000
            repeatCount = 1
            repeatMode = ObjectAnimator.REVERSE
        }.start()
    }

    fun fade(view: View, amount: Float) {
        ObjectAnimator.ofFloat(
            view,
            View.ALPHA,
            1F,
            amount
        ).apply {
            duration = 1000
            repeatCount = 1
            repeatMode = ObjectAnimator.REVERSE
        }.start()
    }

    fun changeColor(view: ImageView, fromColor: Int, toColor: Int) {
        ObjectAnimator.ofArgb(view, "colorFilter", fromColor, toColor).apply {
            duration = 1000
            repeatCount = 1
            repeatMode = ObjectAnimator.REVERSE
        }.start()
    }

    fun shower(container: ViewGroup, view: View) {
        val scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1F, 2F)
        val scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1F, 2F)

        val scale = ObjectAnimator.ofPropertyValuesHolder(view, scaleX, scaleY).apply {
            duration = 1000
            interpolator = LinearInterpolator()
        }

        val fall = ObjectAnimator.ofFloat(
            view,
            View.TRANSLATION_Y,
            (view.height * -1F),
            (container.height + view.height).toFloat()
        ).apply {
            duration = 1000
            interpolator = AccelerateInterpolator()
        }

        AnimatorSet().apply {
            playTogether(scale, fall)
            duration = (Math.random() * 1500 + 500).toLong()
            addListener(
                onStart = { container.addView(view) },
                onEnd = { container.removeView(view) }
            )
        }.start()
    }
}