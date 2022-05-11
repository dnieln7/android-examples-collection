package com.dnieln7.collection.qr

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.text.isDigitsOnly
import com.dnieln7.collection.R
import com.dnieln7.collection.databinding.ActivityQrBinding

class QrActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQrBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQrBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val action: String? = intent?.action
        val data: Uri? = intent?.data

        if (action != Intent.ACTION_VIEW) {
            binding.fromQr.visibility = View.GONE
            binding.colorDescription.visibility = View.GONE
            binding.color.visibility = View.GONE

            binding.notFromQr.visibility = View.VISIBLE
            binding.example.visibility = View.VISIBLE
        } else {
            val color = if (data != null) {
                try {
                    data.toString().split("#")[1]
                } catch (ignored: Exception) {
                    "none"
                }
            } else "none"


            if (color != "none" && color.isNotBlank() && color.isDigitsOnly() && color.length in 6..8) {
                val colorInt = Color.parseColor("#$color")

                binding.colorDescription.text = getString(R.string.your_color_is, "#$color")
                binding.color.setBackgroundColor(colorInt)
            } else {
                binding.colorDescription.text = "Could not find a color in the link :("
            }

            binding.notFromQr.visibility = View.GONE
            binding.example.visibility = View.GONE

            binding.fromQr.visibility = View.VISIBLE
            binding.colorDescription.visibility = View.VISIBLE
            binding.color.visibility = View.VISIBLE
        }
    }
}