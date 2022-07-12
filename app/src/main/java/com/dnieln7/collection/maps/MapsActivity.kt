package com.dnieln7.collection.maps

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.dnieln7.collection.R
import com.dnieln7.collection.databinding.ActivityMapsBinding
import com.dnieln7.collection.databinding.UiMapSettingsBinding
import com.dnieln7.collection.utils.addStyle
import com.dnieln7.collection.utils.removeStyle
import com.dnieln7.collection.utils.setMyLocationButtonPosition
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.GroundOverlayOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.maps.android.ktx.awaitMap

@SuppressLint("MissingPermission")
class MapsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMapsBinding
    private lateinit var map: GoogleMap

    private var hasBeenStyled: Boolean = false

    private val marker: BitmapDescriptor by lazy {
        BitmapHelper.vectorToBitmap(this, R.drawable.ic_map_pin, null)
    }

    private val favorite: BitmapDescriptor by lazy {
        BitmapHelper.vectorToBitmap(this, R.drawable.ic_favorite, null)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.setMyLocationButtonPosition()

        binding.search.setOnClickListener {
            if (!binding.latitude.text.isNullOrBlank() && !binding.longitude.text.isNullOrBlank()) {
                val latitude = binding.latitude.text.toString().toDouble()
                val longitude = binding.longitude.text.toString().toDouble()
                val latLng = LatLng(latitude, longitude)

                goToCoordinates(latLng)
            }
        }

        binding.mark.setOnClickListener {
            if (!binding.latitude.text.isNullOrBlank() && !binding.longitude.text.isNullOrBlank()) {
                val latitude = binding.latitude.text.toString().toDouble()
                val longitude = binding.longitude.text.toString().toDouble()
                val latLng = LatLng(latitude, longitude)

                goToCoordinates(latLng)
                addMarker(latLng)
            }
        }

        binding.settings.setOnClickListener { showSettings() }

        lifecycleScope.launchWhenCreated {
            map = mapFragment.awaitMap()

            map.setOnMapLongClickListener {
                binding.latitude.setText(it.latitude.toString())
                binding.longitude.setText(it.longitude.toString())
            }

            map.isMyLocationEnabled = true
        }
    }

    private fun goToCoordinates(latLng: LatLng) {
        val zoomLevel = 15F // 1: World 5: Continent 10: City 15: Streets 20: Buildings

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel))
    }

    private fun addMarker(latLng: LatLng) {
        val snippet = "This is a user created marker"
        val marker = MarkerOptions()
            .icon(marker)
            .position(latLng)
            .title("My marker")
            .snippet(snippet)

        map.addMarker(marker)
    }

    private fun addFavorite(latLng: LatLng) {
        val overlay = GroundOverlayOptions()
            .image(favorite)
            .position(latLng, 200F)

        map.addGroundOverlay(overlay)
    }

    private fun showSettings() {
        val sheetBinding = UiMapSettingsBinding.inflate(layoutInflater)
        val sheet = BottomSheetDialog(this)
        sheet.setContentView(sheetBinding.root)

        val types = resources.getStringArray(R.array.map_types)
        val index = when (map.mapType) {
            GoogleMap.MAP_TYPE_NORMAL -> 0
            GoogleMap.MAP_TYPE_SATELLITE -> 1
            GoogleMap.MAP_TYPE_HYBRID -> 2
            GoogleMap.MAP_TYPE_TERRAIN -> 3
            else -> 0
        }

        sheetBinding.type.setSimpleItems(R.array.map_types)
        sheetBinding.type.setText(types[index], false)
        sheetBinding.minimalisticMap.isChecked = hasBeenStyled
        sheetBinding.locationTracking.isChecked = map.isMyLocationEnabled

        sheetBinding.type.onItemClickListener = AdapterView.OnItemClickListener { _, _, index, _ ->
            when (index) {
                0 -> map.mapType = GoogleMap.MAP_TYPE_NORMAL
                1 -> map.mapType = GoogleMap.MAP_TYPE_SATELLITE
                2 -> map.mapType = GoogleMap.MAP_TYPE_HYBRID
                3 -> map.mapType = GoogleMap.MAP_TYPE_TERRAIN
            }

            sheet.dismiss()
        }

        sheetBinding.minimalisticMap.setOnCheckedChangeListener { _, isChecked ->
            hasBeenStyled = if (isChecked) {
                map.addStyle(this, R.raw.map_style_min)
                true
            } else {
                map.removeStyle()
                false
            }

            sheet.dismiss()
        }

        sheetBinding.locationTracking.setOnCheckedChangeListener { _, isChecked ->
            map.isMyLocationEnabled = isChecked
            sheet.dismiss()
        }

        sheetBinding.clear.setOnClickListener {
            map.clear()
            sheet.dismiss()
        }

        sheet.show()
    }

    //<editor-fold desc="Permissions">

    companion object {
        private const val fineLocation = Manifest.permission.ACCESS_FINE_LOCATION
        private const val coarseLocation = Manifest.permission.ACCESS_COARSE_LOCATION
        private const val granted = PackageManager.PERMISSION_GRANTED

        fun hasLocationPermissions(context: Context): Boolean {
            return ContextCompat.checkSelfPermission(context, fineLocation) == granted &&
                    ContextCompat.checkSelfPermission(context, coarseLocation) == granted
        }

        fun requestLocationPermissions(activity: Activity) {
            ActivityCompat.requestPermissions(activity, arrayOf(fineLocation, coarseLocation), 0)
        }
    }

    //</editor-fold>
}