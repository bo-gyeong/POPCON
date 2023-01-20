package com.ssafy.popcon.network.api

import com.ssafy.popcon.dto.Gifticon
import com.ssafy.popcon.dto.MapBrandLogo
import com.ssafy.popcon.dto.User
import net.daum.mf.map.api.MapPoint
import org.intellij.lang.annotations.JdkConstants.TitledBorderTitlePosition
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface MapApi {
    // 현재 위치 보내기
    @POST("api/local")
    suspend fun sendUserPosition(@Path("email") email : String, @Path("social") social : Int, @Path("x") x : String, @Path("y") y : String, @Path("radius") radius : String):  ArrayList<MapBrandLogo>
}