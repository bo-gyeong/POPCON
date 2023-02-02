package com.ssafy.popcon.network.api

import com.ssafy.popcon.dto.BrandRequest
import com.ssafy.popcon.dto.MapBrandLogo
import retrofit2.http.Body
import retrofit2.http.POST

interface MapApi {
    // 현재 위치 보내기  --> DataClass 만들어서 하나의 객체만 Body로 보내야하는듯
    @POST("local/search")
    suspend fun getStoreByLocation(@Body storeRequest : BrandRequest):  List<MapBrandLogo>
}