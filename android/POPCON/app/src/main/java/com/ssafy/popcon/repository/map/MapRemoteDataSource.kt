package com.ssafy.popcon.repository.map

import com.ssafy.popcon.dto.BrandRequest
import com.ssafy.popcon.dto.MapBrandLogo
import com.ssafy.popcon.network.api.MapApi

class MapRemoteDataSource(private val apiClient: MapApi) : MapDataSource {
    // 현재 유저의 위치를 보내면 지도에 표시해줄 결과
    override suspend fun getStoreByLocation(storeRequest: BrandRequest): List<MapBrandLogo> {
        return apiClient.getStoreByLocation(storeRequest)
    }

}