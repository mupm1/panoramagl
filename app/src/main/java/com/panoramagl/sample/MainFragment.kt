package com.panoramagl.sample

import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.panoramagl.PLConstants
import com.panoramagl.PLImage
import com.panoramagl.PLManager
import com.panoramagl.PLSphericalPanorama
import com.panoramagl.hotspots.ActionPLHotspot
import com.panoramagl.hotspots.HotSpotListener
import com.panoramagl.sample.databinding.ActivityMainBinding
import com.panoramagl.utils.PLUtils

class MainFragment : Fragment(), HotSpotListener {

    private lateinit var plManager: PLManager
    private var currentIndex = -1
    private val resourceIds = intArrayOf(R.raw.sighisoara_sphere, R.raw.sighisoara_sphere_2)
    private lateinit var panorama: PLSphericalPanorama

    companion object {
        private const val MY_INT = "my_int"

        fun newInstance(anInt: Int) = MainFragment().apply {
            arguments = Bundle(1).apply {
                putInt(MY_INT, anInt)
            }
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.frag, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        panorama = PLSphericalPanorama()
        plManager = PLManager(requireActivity()).apply {
            setContentView(view.findViewById(R.id.content_view))
            onCreate()
            isAccelerometerEnabled = false
            isInertiaEnabled = false
            isZoomEnabled = false
            isAcceleratedTouchScrollingEnabled = true
        }

        plManager.panorama = panorama

        arguments?.getInt(MY_INT,0)?.let { changePanorama(it) }

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

    var pl: PLImage? = null
    private fun changePanorama(index: Int) {
        if (currentIndex == index)
            return
        val image3D = PLUtils.getBitmap(requireActivity(), resourceIds[index])
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
    override fun onClick(identifier: Long) {
        requireActivity().runOnUiThread {
            pos = if (pos == 1) {
                0
            } else {
                1
            }
            changePanorama(pos)
        }
    }
}
