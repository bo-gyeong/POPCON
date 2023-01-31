package com.ssafy.popcon.repository.user

import com.ssafy.popcon.dto.User
import com.ssafy.popcon.dto.UserDeleteRequest

class UserRepository(private val remoteDataSource: UserRemoteDataSource) {

    suspend fun signInNaver(user: User): User {
        return remoteDataSource.signInNaver(user)
    }

    suspend fun signInKakao(user: User): User {
        return remoteDataSource.signInKakao(user)
    }

    suspend fun withdraw(user: UserDeleteRequest) {
        return remoteDataSource.withdraw(user)
    }

    suspend fun updateUser(user: User, hash:Int): User{
        return remoteDataSource.updateUser(user, hash)
    }
}