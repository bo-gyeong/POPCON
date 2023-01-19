package com.ssafy.popcon.network.api

import com.ssafy.popcon.dto.User
import retrofit2.http.Body
import retrofit2.http.POST

interface UserApi {
    //네이버 로그인
    @POST("api/user/naver-login/")
    suspend fun signIn(@Body user: User): User
}