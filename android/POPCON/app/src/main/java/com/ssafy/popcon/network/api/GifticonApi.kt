package com.ssafy.popcon.network.api

import com.ssafy.popcon.dto.Brand
import com.ssafy.popcon.dto.BrandRequest
import com.ssafy.popcon.dto.Gifticon
import com.ssafy.popcon.dto.User
import retrofit2.http.*

interface GifticonApi {
    //사용자 기프티콘 목록
    @GET("gifticons/{email}/{social}")
    suspend fun getGifticonByUser(
        @Path("email") email: String,
        @Path("social") social: String
    ): List<Gifticon>

    //현재위치에서 가능한 브랜드 목록
    @POST("local/shake")
    suspend fun getBrandsByLocation(@Body request: BrandRequest): List<Brand>

    //브랜드 별 사용자 기프티콘 목록
    @GET("gifticon_brand/{userId}/{brandName}")
    suspend fun getGifticonByBrand(
        @Path("userId") user: User,
        brandName: String
    ): List<Gifticon>

    //히스토리
    @GET("gifticons/{userId}/{}")
    suspend fun getHistory(@Path("userId") userId: String): List<Gifticon>

    //업데이트
    @HTTP(method = "PUT", path = "gifticons", hasBody = true)
    suspend fun updateGifticon(@Body gifticon: Gifticon): Gifticon

    //삭제
    @HTTP(method = "DELETE", path = "gifticons", hasBody = true)
    suspend fun deleteGifticon(@Body barcodeNum: String)

}