package com.panoramagl.sample

import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MotionEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.panoramagl.PLConstants
import com.panoramagl.PLImage
import com.panoramagl.PLManager
import com.panoramagl.PLSphericalPanorama
import com.panoramagl.hotspots.ActionPLHotspot
import com.panoramagl.hotspots.HotSpotListener
import com.panoramagl.sample.databinding.ActivityMainBinding
import com.panoramagl.utils.PLUtils
import android.app.ActivityManager


class MainActivity : AppCompatActivity(), HotSpotListener {

    private lateinit var binding: ActivityMainBinding

    private lateinit var plManager: PLManager
    private var currentIndex = -1
    private val resourceIds = intArrayOf(R.raw.sighisoara_sphere, R.raw.sighisoara_sphere_2)

    private val useAcceleratedTouchScrolling = false
    val handler = Handler(Looper.getMainLooper())
    var count = 0
    private lateinit var panorama: PLSphericalPanorama

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        panorama = PLSphericalPanorama()
        plManager = PLManager(this).apply {
            setContentView(binding.contentView)
            onCreate()
        }
        plManager.accelerometerSensitivity = 5f
        plManager.isInertiaEnabled = false
        plManager.isScrollingEnabled = false
        plManager.isResetEnabled = false
        plManager.isScrollingEnabled = false
        plManager.isZoomEnabled = true
        panorama.camera.fov = 55.0f
        panorama.camera.setFovRange(40.0f, 110.0f)
        panorama.camera.isReverseRotation = true
        plManager.panorama = panorama

        changePanorama(0)

        play()
    }

    private fun play() {
        handler.postDelayed({
            onChange(100)
            val mi = ActivityManager.MemoryInfo()
            val activityManager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
            activityManager.getMemoryInfo(mi)
            val availableMegs: Long = mi.availMem / 0x100000L
            binding.txt.text = "Count $count/100 ♦ Available Memory $availableMegs MB"
            count++
            if (count <= 100) {
                play()
            }
        }, 1000)
    }

    override fun onResume() {
        super.onResume()
        plManager.onResume()
    }

    override fun onPause() {
        plManager.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        plManager.onDestroy()
        super.onDestroy()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return plManager.onTouchEvent(event)
    }

    var pl: PLImage? = null
    private fun changePanorama(index: Int) {
        if (currentIndex == index)
            return
        val image3D = PLUtils.getBitmap(this, resourceIds[index])
        pl = PLImage(image3D, false)
        panorama.setImage(pl)

        if (currentIndex != -1) {
            plManager.panorama.camera?.apply {
                pitch = this.pitch
                yaw = this.yaw
                zoomFactor = this.zoomFactor
            }
        }
        panorama.removeAllHotspots()
        val hotSpotId: Long = 100
        val normalizedX = 500f / image3D.width
        val normalizedY = 700f / image3D.height
        val plHotspot1 = ActionPLHotspot(
            this,
            hotSpotId,
            PLImage(BitmapFactory.decodeResource(resources, R.raw.hotspot)),
            0f,
            0f,
            PLConstants.kDefaultHotspotSize,
            PLConstants.kDefaultHotspotSize
        )
        //plHotspot1.setPosition(normalizedX, normalizedY)
        val plHotspot2 = ActionPLHotspot(
            this,
            hotSpotId + 1,
            PLImage(BitmapFactory.decodeResource(resources, R.raw.hotspot)),
            180f,
            0f,
            PLConstants.kDefaultHotspotSize,
            PLConstants.kDefaultHotspotSize
        )
        panorama.addHotspot(plHotspot1)
        panorama.addHotspot(plHotspot2)


        currentIndex = index
    }

    var pos = 0
    fun onChange(identifier: Long) {
        runOnUiThread {
            pos = if (pos == 1) {
                0
            } else {
                1
            }
            changePanorama(pos)
        }
    }

    override fun onClick(identifier: Long) {
        runOnUiThread {
            Toast.makeText(this, "Clicked", Toast.LENGTH_SHORT).show()
        }
    }
}
