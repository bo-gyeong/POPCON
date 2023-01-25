package com.ssafy.popcon.network.api

import com.ssafy.popcon.dto.Brand
import com.ssafy.popcon.dto.BrandRequest
import com.ssafy.popcon.dto.Gifticon
import com.ssafy.popcon.dto.User
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface GifticonApi {
    //사용자 기프티콘 목록
    @GET("gifticon/{email}/{social}")
    suspend fun getGifticonByUser(@Path("email") email: String, @Path("social") social : String): List<Gifticon>

    //현재위치에서 가능한 브랜드 목록
    @GET("shake/{}")
    suspend fun getBrandsByLocation(@Path("brandRequest") brandRequest: BrandRequest): List<Brand>

    //브랜드 별 사용자 기프티콘 목록
    @GET("gifticon_brand/{userId}/{brandName}")
    suspend fun getGifticonByBrand(
        @Path("userId") userId: String,
        brandName: String
    ): List<Gifticon>

    //히스토리
    @GET("api/gifticon/{userId}/{}")
    suspend fun getHistory(@Path("userId") userId: String): List<Gifticon>

    @POST("api/gifticon/{gifticon}")
    suspend fun updateGifticon(@Path("gifticon") gifticon: Gifticon): Gifticon
}