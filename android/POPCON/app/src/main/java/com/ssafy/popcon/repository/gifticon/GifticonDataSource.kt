package com.ssafy.popcon.repository.gifticon

import com.ssafy.popcon.dto.*

interface GifticonDataSource {
    suspend fun getGifticonByUser(email: String, social : String): List<Gifticon>
    suspend fun getGifticonByBarNum(barcodeNum: String) : Gifticon
    suspend fun getGifticonByBrand(gifticonByBrandRequest: GifticonByBrandRequest): List<Gifticon>
    suspend fun getHistory(userId: String): List<Gifticon>
    suspend fun updateGifticon(gifticon: Gifticon) : Gifticon
    suspend fun getBrandsByLocation(brandRequest: BrandRequest) : List<Brand>
    suspend fun deleteGifticon(barcodeNum : String)
}