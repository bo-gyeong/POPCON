package com.ssafy.popcon.repository.gifticon

import com.ssafy.popcon.dto.*

interface GifticonDataSource {
    suspend fun getGifticonByUser(email: String, social : String): List<Gifticon>
    suspend fun getGifticonByBarNum(barcodeNum: String) : GifticonResponse
    suspend fun getGifticonByBrand(gifticonByBrandRequest: GifticonByBrandRequest): List<Gifticon>
    suspend fun getHistory(userId: String): List<Gifticon>
    suspend fun updateGifticon(gifticon: UpdateRequest) : UpdateResponse
    suspend fun getBrandsByLocation(brandRequest: BrandRequest) : List<Brand>
    suspend fun deleteGifticon(barcodeNum : DeleteRequest)
    suspend fun getHomeBrands(email: String, social: String) : List<BrandResponse>
}