package com.ssafy.popcon.repository.gifticon

import com.ssafy.popcon.dto.Brand
import com.ssafy.popcon.dto.BrandRequest
import com.ssafy.popcon.dto.Gifticon
import com.ssafy.popcon.dto.User

class GifticonRepository(private val remoteDataSource: GifticonRemoteDataSource) {
    suspend fun getGifticonByUser(user: User): List<Gifticon> {
        return remoteDataSource.getGifticonByUser(user.email!!, user.social.toString())
    }

    suspend fun getGifticonByBrand(user: User, brandName: String): List<Gifticon> {
        return remoteDataSource.getGifticonByBrand(user, brandName)
    }

    suspend fun getHistory(userId: String): List<Gifticon> {
        return remoteDataSource.getHistory(userId)
    }

    suspend fun updateGifticon(gifticon: Gifticon): Gifticon {
        return remoteDataSource.updateGifticon(gifticon)
    }

    suspend fun getBrandsByLocation(brandRequest: BrandRequest): List<Brand> {
        return remoteDataSource.getBrandsByLocation(brandRequest)
    }

    suspend fun deleteGifticon(barcodeNum: String) {
        return remoteDataSource.deleteGifticon(barcodeNum)
    }
}