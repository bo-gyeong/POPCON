package com.ssafy.popcon.repository.map

import com.ssafy.popcon.dto.*
import com.ssafy.popcon.network.api.MapApi

class MapRemoteDataSource(private val apiClient: MapApi) : MapDataSource {
    // 현재 유저의 위치를 보내면 지도에 표시해줄 결과
    override suspend fun getStoreByLocation(storeRequest: StoreRequest): List<Store> {
        return apiClient.getStoreByLocation(storeRequest)
    }

    override suspend fun getStoreByBrand(storeByBrandRequest: StoreByBrandRequest): List<Store> {
        return apiClient.getStoreByBrand(storeByBrandRequest)
    }

    override suspend fun getPresent(presentRequest: FindPresentRequest): FindDonateResponse {
        return apiClient.getPresents(presentRequest)
    }

    override suspend fun getPresent(getPresentRequest: GetPresentRequest) {
        return apiClient.getPresent(getPresentRequest)
    }

    override suspend fun givePresent(donateRequest: DonateRequest) {
        return apiClient.givePresent(donateRequest)
    }
}