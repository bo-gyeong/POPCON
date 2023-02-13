package com.ssafy.popcon.network.api

import com.ssafy.popcon.dto.DonateRequest
import com.ssafy.popcon.dto.Gifticon
import com.ssafy.popcon.dto.TokenResponse
import com.ssafy.popcon.dto.User
import retrofit2.http.*

interface WearApi {
    //네이버 로그인
    @POST("user/login/naver")
    suspend fun signInNaver(@Body user: User): User

    //카카오 로그인
    @POST("user/login/kakao")
    suspend fun signInKakao(@Body user: User): User

    @POST("presents/give_present")
    suspend fun givePresent(@Body request: DonateRequest)

    //사용자 기프티콘 목록
    @GET("gifticons/{email}/{social}")
    suspend fun getGifticonByUser(
        @Path("email") email: String,
        @Path("social") social: String
    ): List<Gifticon>

    @POST("user/login")
    suspend fun signIn(@Body user: User): TokenResponse

    @GET("user/refresh")
    suspend fun refreshToken(
        @Header("Authorization") refreshToken: String?
    ): TokenResponse
}