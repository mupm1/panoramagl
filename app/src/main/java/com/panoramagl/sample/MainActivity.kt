package com.panoramagl.sample

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.panoramagl.sample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    val handler = Handler(Looper.getMainLooper())
    var count = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        play()
    }

    var pos = 0
    private fun play() {
        handler.postDelayed({
            Log.i("onClick", "$count")
            count++
            if (count < 1000) {
                pos = if (pos == 1) {
                    0
                } else {
                    1
                }
                showFragment(MainFragment.newInstance(pos))
                play()
            }
        }, 500)
    }

    fun showFragment(fragment: MainFragment) {
        val fram = supportFragmentManager.beginTransaction()
        fram.replace(R.id.masterView, fragment)
        fram.commit()
    }
}
