package com.ssafy.popcon.repository.map

import com.ssafy.popcon.dto.Gifticon
import com.ssafy.popcon.dto.MapBrandLogo
import com.ssafy.popcon.dto.MapNowPos
import com.ssafy.popcon.dto.User
import net.daum.mf.map.api.MapPoint

interface MapDataSource {
    // 1. 서버에 현재 위치 보내면 그에 맞는 근처 매장들 다 줘!
    suspend fun sendUserPosition(nowPos: Map<String, String>) : List<MapBrandLogo>
    //suspend fun sendUserPosition(mapNowPos: MapNowPos) : ArrayList<MapBrandLogo>  --> 객체로 바꿀 경우
}