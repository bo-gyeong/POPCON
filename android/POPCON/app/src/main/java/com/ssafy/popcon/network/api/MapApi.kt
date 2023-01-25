package com.ssafy.popcon.network.api

import com.ssafy.popcon.dto.Gifticon
import com.ssafy.popcon.dto.MapBrandLogo
import com.ssafy.popcon.dto.MapNowPos
import com.ssafy.popcon.dto.User
import net.daum.mf.map.api.MapPoint
import org.intellij.lang.annotations.JdkConstants.TitledBorderTitlePosition
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.QueryMap

interface MapApi {
    // 현재 위치 보내기  --> DataClass 만들어서 하나의 객체만 Body로 보내야하는듯
    @GET("local")
    suspend fun sendUserPosition(@QueryMap nowPos: Map<String, String>):  ArrayList<MapBrandLogo>
    //suspend fun sendUserPosition(@Body mapNowPos:MapNowPos):  ArrayList<MapBrandLogo>
}