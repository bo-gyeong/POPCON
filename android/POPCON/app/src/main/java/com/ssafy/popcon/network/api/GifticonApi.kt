package com.ssafy.popcon.network.api

import com.ssafy.popcon.dto.Gifticon
import retrofit2.http.GET
import retrofit2.http.Path

interface GifticonApi {
    //사용자 기프티콘 목록
    @GET("api/gifticon/{userId}/{}")
    suspend fun getGifticonByUserId(@Path("userId") userId: String): List<Gifticon>

    //브랜드 별 사용자 기프티콘 목록
    @GET("api/gifticon/{userId}/{brandName}")
    suspend fun getGifticonByBrand(
        @Path("userId") userId: String,
        brandName: String
    ): List<Gifticon>

    //히스토리
    @GET("api/gifticon/{userId}/{}")
    suspend fun getHistory(@Path("userId") userId: String): List<Gifticon>
}