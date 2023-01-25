package com.ssafy.popcon.repository.gifticon

import com.ssafy.popcon.dto.Gifticon

class GifticonRepository(private val remoteDataSource: GifticonRemoteDataSource) {
    suspend fun getGifticonByUserId(userId: String): List<Gifticon> {
        return remoteDataSource.getGifticonByUserId(userId)
    }

    suspend fun getGifticonByBrand(userId: String, brandName: String): List<Gifticon> {
        return remoteDataSource.getGifticonByBrand(userId, brandName)
    }

    suspend fun getHistory(userId: String): List<Gifticon> {
        return remoteDataSource.getHistory(userId)
    }

    suspend fun updateGifticon(gifticon: Gifticon): Gifticon {
        return remoteDataSource.updateGifticon(gifticon)
    }
}