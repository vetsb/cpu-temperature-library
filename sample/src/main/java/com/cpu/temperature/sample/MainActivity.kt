package com.cpu.temperature.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cpu.temperature.library.CpuTemperatureProvider
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private val provider: CpuTemperatureProvider by lazy {
        CpuTemperatureProvider(applicationContext)
    }

    private lateinit var timer: Timer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        timer = Timer()

        timer.schedule(object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    tvTemperature.run {
                        text = provider.getTemperature().toString()
                    }
                }
            }
        }, 1000L, 1000L)
    }

    override fun onDestroy() {
        super.onDestroy()

        timer.cancel()
    }
}
