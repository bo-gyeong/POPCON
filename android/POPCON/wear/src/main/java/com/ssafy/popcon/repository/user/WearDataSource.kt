package com.ssafy.popcon.repository.user

import com.ssafy.popcon.dto.*

interface WearDataSource {
    suspend fun getGifticonByUser(email: String, social : String): List<Gifticon>
    suspend fun signInNaver(user: User): User
    suspend fun signInKakao(user: User): User
    suspend fun givePresent(donateRequest: DonateRequest)

    suspend fun signIn(user: User) : TokenResponse
    suspend fun refreshToken(refreshToken : String) : TokenResponse

}