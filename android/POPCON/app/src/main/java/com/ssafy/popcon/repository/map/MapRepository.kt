package com.ssafy.popcon.repository.map

import com.ssafy.popcon.dto.BrandRequest
import com.ssafy.popcon.dto.MapBrandLogo

class MapRepository(private val remoteDataSource: MapRemoteDataSource) {
    suspend fun getStoreByLocation(storeRequest: BrandRequest): List<MapBrandLogo> {
        return remoteDataSource.getStoreByLocation(storeRequest)
    }
}