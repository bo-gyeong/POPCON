package com.ssafy.popcon.repository.map

import com.ssafy.popcon.dto.Gifticon
import com.ssafy.popcon.dto.MapBrandLogo
import com.ssafy.popcon.dto.User
import com.ssafy.popcon.network.api.MapApi
import com.ssafy.popcon.network.api.UserApi
import net.daum.mf.map.api.MapPoint
import retrofit2.http.Path

class MapRemoteDataSource(private val apiClient: MapApi) : MapDataSource {
    // 현재 유저의 위치를 보내면 지도에 표시해줄 결과
    override suspend fun sendUserPosition(email : String, social : Int, x : String, y : String, radius : String): ArrayList<MapBrandLogo> {
        return apiClient.sendUserPosition(email, social, x, y, radius)
    }
}