package com.ssafy.popcon.repository.gifticon

import com.kakao.sdk.user.UserApiClient
import com.ssafy.popcon.dto.*
import com.ssafy.popcon.network.api.GifticonApi

class GifticonRemoteDataSource(private val apiClient: GifticonApi) : GifticonDataSource {
    override suspend fun getGifticonByUser(email: String, social : String): List<Gifticon> {
        return apiClient.getGifticonByUser(email, social)
    }

    override suspend fun getGifticonByBarNum(barcodeNum: String): GifticonResponse {
        return apiClient.getGifticonByBarNum(barcodeNum)
    }

    override suspend fun getGifticonByBrand(gifticonByBrandRequest: GifticonByBrandRequest): List<Gifticon> {
        return apiClient.getGifticonByBrand(gifticonByBrandRequest)
    }

    override suspend fun getHistory(userId: String): List<Gifticon> {
        return apiClient.getHistory(userId)
    }

    override suspend fun updateGifticon(gifticon : UpdateRequest): UpdateResponse {
        return apiClient.updateGifticon(gifticon)
    }

    override suspend fun getBrandsByLocation(brandRequest: BrandRequest): List<Brand> {
        return apiClient.getBrandsByLocation(brandRequest)
    }

    override suspend fun deleteGifticon(barcodeNum: DeleteRequest) {
        return apiClient.deleteGifticon(barcodeNum)
    }

    override suspend fun getHomeBrands(email: String, social: String): List<BrandResponse> {
        return apiClient.getBrandHome(email, social)
    }
}