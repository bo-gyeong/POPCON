package com.ssafy.popcon.repository.user

import com.ssafy.popcon.dto.*
import com.ssafy.popcon.network.api.WearApi

class WearRemoteDataSource(private val apiClient: WearApi) : WearDataSource {
    override suspend fun getGifticonByUser(email: String, social: String): List<Gifticon> {
        return apiClient.getGifticonByUser(email, social)
    }

    override suspend fun signInNaver(user: User): User {
        return apiClient.signInNaver(user)
    }

    override suspend fun signInKakao(user: User): User {
        return apiClient.signInKakao(user)
    }

    override suspend fun givePresent(donateRequest: DonateRequest) {
        return apiClient.givePresent(donateRequest)
    }

    override suspend fun signIn(user: User): TokenResponse {
        return apiClient.signIn(user)
    }

    override suspend fun refreshToken(refreshToken: String): TokenResponse {
        return apiClient.refreshToken(refreshToken)
    }
}