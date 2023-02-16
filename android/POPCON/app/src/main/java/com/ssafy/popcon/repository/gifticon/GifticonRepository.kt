package com.ssafy.popcon.repository.gifticon

import com.ssafy.popcon.dto.*

class GifticonRepository(private val remoteDataSource: GifticonRemoteDataSource) {
    suspend fun getGifticonByUser(user: User): List<Gifticon> {
        return remoteDataSource.getGifticonByUser(user.email!!, user.social.toString())
    }

    suspend fun getGifticonMapByUser(user: User): List<Gifticon>{
        return remoteDataSource.getGifticonMapByUser(user.email!!, user.social)
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

    suspend fun getHistory(req: UserDeleteRequest): List<Gifticon> {
        return remoteDataSource.getHistory(req)
    }

    suspend fun updateGifticon(gifticon: UpdateRequest): UpdateResponse {
        return remoteDataSource.updateGifticon(gifticon)
    }

    suspend fun getBrandsByLocation(storeRequest: StoreRequest): List<Brand> {
        return remoteDataSource.getBrandsByLocation(storeRequest)
    }

    suspend fun deleteGifticon(barcodeNum: DeleteRequest) {
        return remoteDataSource.deleteGifticon(barcodeNum)
    }
}