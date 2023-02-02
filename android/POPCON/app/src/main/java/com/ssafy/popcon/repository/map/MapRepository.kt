package com.ssafy.popcon.repository.map

import com.ssafy.popcon.dto.Gifticon
import com.ssafy.popcon.dto.MapBrandLogo
import com.ssafy.popcon.dto.MapNowPos
import com.ssafy.popcon.dto.User
import net.daum.mf.map.api.MapCurrentLocationMarker
import net.daum.mf.map.api.MapPoint

class MapRepository(private val remoteDataSource: MapRemoteDataSource) {
    suspend fun sendUserPosition(nowPos: Map<String, String>): List<MapBrandLogo> {
        return remoteDataSource.sendUserPosition(nowPos)
    }
//    suspend fun sendUserPosition(mapNowPos: MapNowPos): ArrayList<MapBrandLogo> {
//        return remoteDataSource.sendUserPosition(mapNowPos)
//    }
}