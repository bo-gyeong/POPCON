package com.ssafy.popcon.repository.gifticon

import com.ssafy.popcon.dto.*

class GifticonRepository(private val remoteDataSource: GifticonRemoteDataSource) {
    suspend fun getGifticonByUser(user: User): List<Gifticon> {
        return remoteDataSource.getGifticonByUser(user.email!!, user.social.toString())
    }

    suspend fun getGifticonByBarNum(barcodeNum: String): GifticonResponse {
        return remoteDataSource.getGifticonByBarNum(barcodeNum)
    }

    suspend fun getHomeBrands(user: User): List<BrandResponse> {
        return remoteDataSource.getHomeBrands(user.email!!, user.social)
    }

    suspend fun getGifticonByBrand(gifticonByBrandRequest: GifticonByBrandRequest): List<Gifticon> {
        return remoteDataSource.getGifticonByBrand(gifticonByBrandRequest)
    }

    suspend fun getHistory(userId: String): List<Gifticon> {
        return remoteDataSource.getHistory(userId)
    }

    suspend fun updateGifticon(gifticon: UpdateRequest): UpdateResponse {
        return remoteDataSource.updateGifticon(gifticon)
    }

    suspend fun getBrandsByLocation(brandRequest: BrandRequest): List<Brand> {
        return remoteDataSource.getBrandsByLocation(brandRequest)
    }

    suspend fun deleteGifticon(barcodeNum: DeleteRequest) {
        return remoteDataSource.deleteGifticon(barcodeNum)
    }
}