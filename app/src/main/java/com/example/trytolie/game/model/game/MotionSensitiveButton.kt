package com.example.trytolie.game.model.game

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import kotlin.math.abs

@Composable
fun MotionSensitiveButton(
    onClick: () -> Unit,
    motionThreshold: Float = 15f,
) {
    var isMotionDetected by remember { mutableStateOf(false) }
    var hasTriggered by remember { mutableStateOf(false) } // Make sensor triggering only once

    val context = LocalContext.current
    val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    val accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    val sensorListener = remember {
        object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (!hasTriggered) {
                    event?.let {
                        val x = event.values[0]
                        val y = event.values[1]
                        val z = event.values[2]

                        val acceleration = abs(x) + abs(y) + abs(z)

                        if (acceleration > motionThreshold) {
                            isMotionDetected = true
                        }
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
    }

    DisposableEffect(sensorManager) {
        sensorManager.registerListener(sensorListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)

        onDispose {
            sensorManager.unregisterListener(sensorListener)
        }
    }

    if (isMotionDetected) {
        onClick()
        hasTriggered = true
        isMotionDetected = false
        Log.d("MotionSensitiveButton", "Motion Detected")
    }
}
