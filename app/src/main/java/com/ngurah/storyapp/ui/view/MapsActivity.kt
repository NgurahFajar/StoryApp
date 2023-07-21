package com.ngurah.storyapp.ui.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.ngurah.storyapp.R
import com.ngurah.storyapp.data.remote.response.StoryAppItem
import com.ngurah.storyapp.databinding.ActivityMapsBinding
import com.ngurah.storyapp.ui.viewmodel.MapsViewModel
import com.ngurah.storyapp.ui.viewmodelfactory.ViewModelFactory
import com.ngurah.storyapp.utils.*
import java.text.SimpleDateFormat
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private var bearer: String = ""
    private val mapsViewModel: MapsViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar()
        val preferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        bearer = "Bearer ${preferences.getString(TOKEN, "")}"
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun supportActionBar(){
        supportActionBar?.apply {
            this.title = getString(R.string.map_story)
            setDisplayHomeAsUpEnabled(true)
        }
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.apply {
            isZoomControlsEnabled = true
            isIndoorLevelPickerEnabled = true
            isCompassEnabled = true
        }

        mapsViewModel.getStoryMap(bearer).observe(this) { storyResponse ->
            val storyMap = storyResponse.listStory as List<StoryAppItem>
            addManyMarker(storyMap)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun addManyMarker(storyMap: List<StoryAppItem>) {
        val boundsBuilder = LatLngBounds.Builder()

        storyMap.forEach { story ->
            val latLng = LatLng(story.lat, story.lon)
            val name = story.name
            val createdAt = story.createdAt

            addMarkerToMap(latLng, name, createdAt)
            boundsBuilder.include(latLng)
        }

        val bounds = boundsBuilder.build()
        animateCameraToBounds(bounds)
    }

    private fun vectorToBitmap(@DrawableRes id: Int, @ColorInt color: Int): BitmapDescriptor {
        val vectorDrawable = ResourcesCompat.getDrawable(resources, id, null)
        if (vectorDrawable == null) {
            Log.e("BitmapHelper", "Resource not found")
            return BitmapDescriptorFactory.defaultMarker()
        }
        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
        DrawableCompat.setTint(vectorDrawable, color)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    private fun addMarkerToMap(latLng: LatLng, title: String, createdAt: String) {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val date = inputFormat.parse(createdAt)
        val formattedDate = date?.let { outputFormat.format(it) }
        mMap.addMarker(
            MarkerOptions()
                .position(latLng)
                .title(title)
                .snippet(formattedDate)
                .icon(vectorToBitmap(R.drawable.marker_icon, Color.parseColor("#BD9CF8")))
        )
    }

    private fun animateCameraToBounds(bounds: LatLngBounds) {
        mMap.animateCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds,
                400
            )
        )
    }
}