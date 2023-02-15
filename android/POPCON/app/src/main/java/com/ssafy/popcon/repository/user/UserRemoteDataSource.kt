package com.ssafy.popcon.repository.user

import com.ssafy.popcon.dto.User
import com.ssafy.popcon.dto.UserDeleteRequest
import com.ssafy.popcon.network.api.UserApi

class UserRemoteDataSource(private val apiClient: UserApi) : UserDataSource {
    override suspend fun signInNaver(user: User): User {
        return apiClient.signInNaver(user)
    }

    override suspend fun signInKakao(user: User): User {
        return apiClient.signInKakao(user)
    }

    override suspend fun withdraw(user: UserDeleteRequest) {
        return apiClient.withdraw(user)
    }

    override suspend fun updateUser(user: User): User {
        return apiClient.updateUser(user)
    }

    override suspend fun getUserLv(): Int {
        return apiClient.getUserLv()
    }
}