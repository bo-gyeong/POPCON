package com.ssafy.popcon.repository.auth

import com.ssafy.popcon.dto.TokenResponse
import com.ssafy.popcon.dto.User
import com.ssafy.popcon.dto.UserResponse
import com.ssafy.popcon.network.api.AuthApi

class AuthRemoteDataSource(private val apiClient : AuthApi) : AuthDataSource{
//    override suspend fun signIn(user: User): TokenResponse {
//        return apiClient.signIn(user)
//    }
    override suspend fun signIn(user: User): UserResponse {
        return apiClient.signIn(user)
    }

    override suspend fun refreshToken(refreshToken: String): TokenResponse {
        return apiClient.refreshToken(refreshToken)
    }
}