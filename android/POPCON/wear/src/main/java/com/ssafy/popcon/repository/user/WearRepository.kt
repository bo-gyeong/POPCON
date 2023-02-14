package com.ssafy.popcon.repository.user

import com.ssafy.popcon.dto.*

class WearRepository(private val remoteDataSource: WearRemoteDataSource) {
    suspend fun signInKakao(user: User): User {
        return remoteDataSource.signInKakao(user)
    }

    suspend fun getGifticonByUser(user: User): List<Gifticon> {
        return remoteDataSource.getGifticonByUser(user.email!!, user.social.toString())
    }

    suspend fun donate(donateRequest: DonateRequest) {
        return remoteDataSource.givePresent(donateRequest)
    }

    suspend fun signIn(user: User): TokenResponse {
        return remoteDataSource.signIn(user)
    }

    suspend fun refreshToken(refreshToken: String): TokenResponse {
        return remoteDataSource.refreshToken(refreshToken)
    }
}