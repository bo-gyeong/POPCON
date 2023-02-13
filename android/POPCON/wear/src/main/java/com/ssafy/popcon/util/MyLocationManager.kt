package com.ssafy.popcon.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.location.Location
import android.location.LocationManager

object MyLocationManager {

    @SuppressLint("MissingPermission")
    fun getLocation(lm : LocationManager): Location? {
        return lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
    }
    fun getLocationManager(context: Context): LocationManager {
        return context.getSystemService(LOCATION_SERVICE) as LocationManager
    }
}