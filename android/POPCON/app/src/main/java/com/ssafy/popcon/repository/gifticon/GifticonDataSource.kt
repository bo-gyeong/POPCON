package com.ssafy.popcon.repository.gifticon

import com.ssafy.popcon.dto.Gifticon

interface GifticonDataSource {
    suspend fun getGifticonByUserId(userId: String): List<Gifticon>
    suspend fun getGifticonByBrand(userId: String, brandName: String): List<Gifticon>
    suspend fun getHistory(userId: String): List<Gifticon>
}