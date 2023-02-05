package com.ssafy.popcon.repository.map

import com.ssafy.popcon.dto.StoreRequest
import com.ssafy.popcon.dto.Store
import com.ssafy.popcon.dto.StoreByBrandRequest

class MapRepository(private val remoteDataSource: MapRemoteDataSource) {
    suspend fun getStoreByLocation(storeRequest: StoreRequest): List<Store> {
        return remoteDataSource.getStoreByLocation(storeRequest)
    }

    suspend fun getStoreByBrand(storeByBrandRequest: StoreByBrandRequest): List<Store> {
        return remoteDataSource.getStoreByBrand(storeByBrandRequest)
    }
}