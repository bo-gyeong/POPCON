package com.ssafy.popcon.repository.map

import com.ssafy.popcon.dto.StoreRequest
import com.ssafy.popcon.dto.Store
import com.ssafy.popcon.dto.StoreByBrandRequest
import com.ssafy.popcon.network.api.MapApi

class MapRemoteDataSource(private val apiClient: MapApi) : MapDataSource {
    // 현재 유저의 위치를 보내면 지도에 표시해줄 결과
    override suspend fun getStoreByLocation(storeRequest: StoreRequest): List<Store> {
        return apiClient.getStoreByLocation(storeRequest)
    }

    override suspend fun getStoreByBrand(storeByBrandRequest: StoreByBrandRequest): List<Store> {
        return apiClient.getStoreByBrand(storeByBrandRequest)
    }
}