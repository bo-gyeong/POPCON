package com.ssafy.popcon.util

import android.util.Log
import com.ssafy.popcon.config.WearApplicationClass
import com.ssafy.popcon.repository.user.WearRemoteDataSource
import com.ssafy.popcon.repository.user.WearRepository
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
        val authRepo = WearRepository(WearRemoteDataSource(WearRetrofitUtil.userService))
        runBlocking {
            Log.d(TAG, "authenticatedddd: ")
            val res = authRepo.refreshToken(WearApplicationClass.sharedPreferencesUtil.refreshToken!!)
            WearApplicationClass.sharedPreferencesUtil.accessToken = res.acessToken
            WearApplicationClass.sharedPreferencesUtil.refreshToken = res.refreshToekn
        }

        return response.request.newBuilder()
            .header("Authorization", "Bearer ${WearApplicationClass.sharedPreferencesUtil.accessToken?:""}")
            .build()
    }
}
