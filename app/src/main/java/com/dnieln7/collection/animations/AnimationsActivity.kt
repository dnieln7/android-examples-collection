package com.dnieln7.collection.animations

import android.os.Bundle
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import com.dnieln7.collection.R
import com.dnieln7.collection.databinding.ActivityAnimationsBinding

class AnimationsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAnimationsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnimationsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rotate.setOnClickListener {
            CommonAnimators.rotate(binding.waterDrop, 360F)
        }

        binding.translate.setOnClickListener {
            CommonAnimators.translateHorizontal(binding.waterDrop, 300F)
        }

        binding.scale.setOnClickListener {
            CommonAnimators.scale(binding.waterDrop, 3F)
        }

        binding.fade.setOnClickListener {
            CommonAnimators.fade(binding.waterDrop, 0F)
        }

        binding.changeColor.setOnClickListener {
            CommonAnimators.changeColor(
                binding.waterDrop,
                getColor(R.color.secondary),
                getColor(R.color.primary)
            )
        }

        binding.shower.setOnClickListener {
            CommonAnimators.shower(binding.waterContainer, newWaterDrop())
        }
    }

    private fun newWaterDrop(): ImageView {
        val water = ImageView(this)

        water.setImageResource(R.drawable.ic_water_drop)
        water.layoutParams = RelativeLayout.LayoutParams(
            50,
            50,
        )

        val color1 = getColor(R.color.secondary)
        val color2 = getColor(R.color.secondaryDark)
        val color3 = getColor(R.color.secondaryLight)

        val random = Math.random().toFloat()

        val waterColor = when {
            random <= 0.5F -> color1
            random > 0.5F -> color2
            else -> color3
        }

        water.setColorFilter(waterColor)

        water.translationX = random * (binding.waterContainer.width - 50F)

        return water
    }
}