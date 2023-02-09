package com.ssafy.popcon.repository.fcm

import com.ssafy.popcon.network.api.FCMApi
import com.ssafy.popcon.network.response.FCMResponse

class FCMRemoteDataSource(private val apiClient:FCMApi): FCMDataSource {
    override suspend fun sendNotification(body: FCMResponse) {
        return apiClient.sendNotification(body)
    }

    override suspend fun uploadToken(token: String): String {
        return apiClient.uploadToken(token)
    }

    override suspend fun broadCast(title: String, body: String): Int {
        return apiClient.broadCast(title, body)
    }

    override suspend fun sendMessageTo(token: String, title: String, body: String) {
        return apiClient.sendMessageTo(token, title, body)
    }
}