package com.ssafy.popcon.repository.gifticon

import com.ssafy.popcon.dto.Brand
import com.ssafy.popcon.dto.BrandRequest
import com.ssafy.popcon.dto.Gifticon
import com.ssafy.popcon.dto.User

interface GifticonDataSource {
    suspend fun getGifticonByUser(email: String, social : String): List<Gifticon>
    suspend fun getGifticonByBrand(user: User, brandName: String): List<Gifticon>
    suspend fun getHistory(userId: String): List<Gifticon>
    suspend fun updateGifticon(gifticon: Gifticon) : Gifticon
    suspend fun getBrandsByLocation(brandRequest: BrandRequest) : List<Brand>
    suspend fun deleteGifticon(barcodeNum : String)
}