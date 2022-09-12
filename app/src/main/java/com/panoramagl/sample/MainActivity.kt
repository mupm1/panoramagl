package com.panoramagl.sample

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.panoramagl.*
import com.panoramagl.hotspots.ActionPLHotspot
import com.panoramagl.hotspots.HotSpotListener
import com.panoramagl.hotspots.PLIHotspot
import com.panoramagl.ios.UITouch
import com.panoramagl.ios.structs.CGPoint
import com.panoramagl.sample.databinding.ActivityMainBinding
import com.panoramagl.structs.PLPosition
import com.panoramagl.transitions.PLITransition
import com.panoramagl.utils.PLUtils

class MainActivity : AppCompatActivity(), HotSpotListener {

    private lateinit var binding: ActivityMainBinding

    private lateinit var plManager: PLManager
    private var currentIndex = -1
    private var sensor = false
    private val resourceIds = intArrayOf(R.raw.sighisoara_sphere, R.raw.sighisoara_sphere_2)
    val panorama = PLSphericalPanorama()
    private val useAcceleratedTouchScrolling = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        plManager = PLManager(this).apply {
            setContentView(binding.contentView)
            onCreate()
            isAccelerometerEnabled = false
            isInertiaEnabled = true
            isScrollingEnabled = true
            minDistanceToEnableScrolling = 30
            minDistanceToEnableDrawing = 1
            inertiaInterval = 2f
            isZoomEnabled = true
            isAcceleratedTouchScrollingEnabled = useAcceleratedTouchScrolling
        }



        var pitch = 5f
        var yaw = 0f
        if (currentIndex != -1) {
            plManager.panorama.camera?.apply {
                pitch = this.pitch
                yaw = this.yaw
            }
        }
        panorama.camera.setFov(100.0f, false)
        panorama.camera.setFovRange(15.0f, 100.0f)
        panorama.camera.lookAt(pitch, yaw, false)
        if (!useAcceleratedTouchScrolling) {
            // If not using the accelerated scrolling, increasing the camera's rotation sensitivity will allow the
            // image to pan faster with finger movement. 180f gives about a ~1:1 move sensitivity.
            // Higher will move the map faster
            // Range 1-270
            panorama.camera.rotationSensitivity = 300f
            panorama.camera.rotationSensitivityFactorInZoomScaleEnable = true
            panorama.camera.rotationSensitivityFactorInZoomScale = 2f
        }
        plManager.panorama = panorama
        plManager.setListener(object : PLViewListener() {
            override fun onDidClickHotspot(view: PLIView?, hotspot: PLIHotspot?, screenPoint: CGPoint?, scene3DPoint: PLPosition?) {
                super.onDidClickHotspot(view, hotspot, screenPoint, scene3DPoint)
                Log.i("plManager", "onDidClickHotspot")
            }
            override fun onDidEndZooming(view: PLIView?) {
                super.onDidEndZooming(view)

            }

            override fun onDidBeginTransition(view: PLIView?, transition: PLITransition?) {
                super.onDidBeginTransition(view, transition)
                Log.i("plManager", "onDidBeginTransition")
            }

            override fun onDidStopTransition(view: PLIView?, transition: PLITransition?, progressPercentage: Int) {
                super.onDidStopTransition(view, transition, progressPercentage)
                Log.i("plManager", "onDidStopTransition")
            }

            override fun onDidEndTransition(view: PLIView?, transition: PLITransition?) {
                super.onDidEndTransition(view, transition)
                Log.i("plManager", "onDidEndTransition")
            }

            override fun onDidBeginTouching(view: PLIView?, touches: MutableList<UITouch>?, event: MotionEvent?) {
                super.onDidBeginTouching(view, touches, event)
                Log.i("plManager", "onDidBeginTouching")
            }

            override fun onDidBeginScrolling(view: PLIView?, startPoint: CGPoint?, endPoint: CGPoint?) {
                super.onDidBeginScrolling(view, startPoint, endPoint)
                Log.i("plManager", "onDidBeginScrolling")
            }

            override fun onDidEndScrolling(view: PLIView?, startPoint: CGPoint?, endPoint: CGPoint?) {
                super.onDidEndScrolling(view, startPoint, endPoint)
                Log.i("plManager", "onDidEndScrolling")
            }

            override fun onDidEndTouching(view: PLIView?, touches: MutableList<UITouch>?, event: MotionEvent?) {
                super.onDidEndTouching(view, touches, event)
                Log.i("plManager", "onDidEndTouching")
            }

            override fun onDidBeginInertia(view: PLIView?, startPoint: CGPoint?, endPoint: CGPoint?) {
                super.onDidBeginInertia(view, startPoint, endPoint)
                Log.i("plManager", "onDidBeginInertia")
            }

            override fun onDidEndInertia(view: PLIView?, startPoint: CGPoint?, endPoint: CGPoint?) {
                super.onDidEndInertia(view, startPoint, endPoint)
                Log.i("plManager", "onDidEndInertia")
            }

            override fun onDidRunInertia(view: PLIView?, startPoint: CGPoint?, endPoint: CGPoint?) {
                super.onDidRunInertia(view, startPoint, endPoint)
                Log.i("plManager", "onDidRunInertia")
            }
        })
        changePanorama(0)
        binding.button3.setOnClickListener {
            if (sensor) {
                plManager.stopSensorialRotation()
            } else {
                plManager.startSensorialRotation()
            }
            plManager.isSensorialRotationLeftRightEnabled=false
            plManager.isSensorialRotationUpDownEnabled=true
            sensor = !sensor
        }
        binding.button1.setOnClickListener { changePanorama(0) }
        binding.button2.setOnClickListener { changePanorama(1) }
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

    private fun changePanorama(index: Int) {
        if (currentIndex == index)
            return
        val image3D = PLUtils.getBitmap(this, resourceIds[index])
        panorama.setImage(PLImage(image3D, false))
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
        plHotspot1.setPosition(normalizedX, normalizedY)
        val plHotspot2 = ActionPLHotspot(
            this,
            hotSpotId + 1,
            PLImage(BitmapFactory.decodeResource(resources, R.raw.hotspot)),
            20f,
            50f,
            PLConstants.kDefaultHotspotSize,
            PLConstants.kDefaultHotspotSize
        )
        panorama.addHotspot(plHotspot1)
        panorama.addHotspot(plHotspot2)

        currentIndex = index
    }

    override fun onClick(identifier: Long) {
        Log.i("PLTouchStatusBegan","onClick")
    }
}
