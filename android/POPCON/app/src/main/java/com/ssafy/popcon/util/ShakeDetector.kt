package com.ssafy.popcon.util

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.sqrt

class ShakeDetector : SensorEventListener {
    private lateinit var mListener: OnShakeListener
    private var mShakeTimestamp: Long = 0
    private var mShakeCount: Int = 0
    private val SHAKE_COUNT_RESET_TIME_MS = 3000
    private val SHAKE_THRESHOLD_GRAVITY = 2.7f
    private val SHAKE_SLOP_TIME_MS = 500

    fun setOnShakeListener(listener: OnShakeListener) {
        mListener = listener
    }

    interface OnShakeListener {
        fun onShake(count: Int)
    }

    override fun onSensorChanged(event: SensorEvent) {
        val x: Float = event.values[0]
        val y: Float = event.values[1]
        val z: Float = event.values[2]
        val gX = x / SensorManager.GRAVITY_EARTH
        val gY = y / SensorManager.GRAVITY_EARTH
        val gZ = z / SensorManager.GRAVITY_EARTH
        val gForce = sqrt((gX * gX + gY * gY + gZ * gZ).toDouble()).toFloat()

        if (gForce > SHAKE_THRESHOLD_GRAVITY) {
            val now = System.currentTimeMillis()
            if (mShakeTimestamp + SHAKE_SLOP_TIME_MS > now) {
                return
            }

            if (mShakeTimestamp + SHAKE_COUNT_RESET_TIME_MS < now) {
                mShakeCount = 0
            }

            mShakeTimestamp = now;
            mShakeCount++;
            mListener.onShake(mShakeCount)
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

    }
}