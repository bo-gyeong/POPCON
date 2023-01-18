package com.ssafy.popcon.repository.user

import com.ssafy.popcon.dto.User
import com.ssafy.popcon.network.api.UserApi

class UserRemoteDataSource(private val apiClient: UserApi) : UserDataSource {
    override suspend fun signIn(user: User): Boolean {
        return apiClient.signIn(user)
    }
}