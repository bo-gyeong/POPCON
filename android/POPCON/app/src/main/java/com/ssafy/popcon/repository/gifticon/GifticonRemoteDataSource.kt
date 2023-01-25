package com.ssafy.popcon.repository.gifticon

import com.kakao.sdk.user.UserApiClient
import com.ssafy.popcon.dto.Gifticon
import com.ssafy.popcon.network.api.GifticonApi

class GifticonRemoteDataSource(private val apiClient: GifticonApi) : GifticonDataSource {
    override suspend fun getGifticonByUserId(userId: String): List<Gifticon> {
        return apiClient.getGifticonByUserId(userId)
    }

    override suspend fun getGifticonByBrand(userId: String, brandName: String): List<Gifticon> {
        return apiClient.getGifticonByBrand(userId, brandName)
    }

    override suspend fun getHistory(userId: String): List<Gifticon> {
        return apiClient.getHistory(userId)
    }

    override suspend fun updateGifticon(gifticon : Gifticon): Gifticon {
        return apiClient.updateGifticon(gifticon)
    }
}