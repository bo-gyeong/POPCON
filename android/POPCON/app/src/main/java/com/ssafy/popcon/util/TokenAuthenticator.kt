package com.ssafy.popcon.util

import android.content.Context
import android.util.Log
import com.ssafy.popcon.config.ApplicationClass
import com.ssafy.popcon.network.api.AuthApi
import com.ssafy.popcon.repository.auth.AuthRemoteDataSource
import com.ssafy.popcon.repository.auth.AuthRepository
import com.ssafy.popcon.repository.user.UserRemoteDataSource
import com.ssafy.popcon.repository.user.UserRepository
import kotlinx.coroutines.runBlocking
import okhttp3.*

class TokenAuthenticator: Authenticator {

    companion object {
        private val TAG = TokenAuthenticator::class.java.simpleName
    }

    override fun authenticate(route: Route?, response: Response): Request? {
        Log.d(TAG, "authenticatedddd: ${response.code}")
        /*if (response.code == 401) {
            if (!ApplicationClass.sharedPreferencesUtil.refreshToken.isNullOrEmpty()) {
                val authRepo = AuthRepository(AuthRemoteDataSource(RetrofitUtil.authService))

                Log.d(TAG, "authenticate: ${ApplicationClass.sharedPreferencesUtil.refreshToken}")
                runBlocking {
                    val res = authRepo.refreshToken(ApplicationClass.sharedPreferencesUtil.refreshToken!!)
                    ApplicationClass.sharedPreferencesUtil.accessToken = res.acessToken
                    ApplicationClass.sharedPreferencesUtil.refreshToken = res.refreshToekn
                }
            }
        }*/
        val authRepo = AuthRepository(AuthRemoteDataSource(RetrofitUtil.authService))
        runBlocking {
            Log.d(TAG, "authenticatedddd: ")
            val res = authRepo.refreshToken(ApplicationClass.sharedPreferencesUtil.refreshToken!!)
            ApplicationClass.sharedPreferencesUtil.accessToken = res.acessToken
            ApplicationClass.sharedPreferencesUtil.refreshToken = res.refreshToekn
        }

        return response.request.newBuilder()
            .header("Authorization", "Bearer ${ApplicationClass.sharedPreferencesUtil.accessToken?:""}")
            .build()
    }
}
