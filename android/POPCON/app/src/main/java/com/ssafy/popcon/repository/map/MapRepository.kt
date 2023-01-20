package com.ssafy.popcon.repository.map

import com.ssafy.popcon.dto.Gifticon
import com.ssafy.popcon.dto.MapBrandLogo
import com.ssafy.popcon.dto.User
import net.daum.mf.map.api.MapCurrentLocationMarker
import net.daum.mf.map.api.MapPoint

class MapRepository(private val remoteDataSource: MapRemoteDataSource) {
    suspend fun sendUserPosition(email : String, social : Int, x : String, y : String, radius : String): ArrayList<MapBrandLogo> {
        return remoteDataSource.sendUserPosition(email, social, x, y, radius)
    }

}