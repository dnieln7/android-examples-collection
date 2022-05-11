package com.dnieln7.collection

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dnieln7.collection.databinding.ActivityMainBinding
import com.dnieln7.collection.qr.QrActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.qr.setOnClickListener {
            startActivity(Intent(this, QrActivity::class.java))
        }
    }
}