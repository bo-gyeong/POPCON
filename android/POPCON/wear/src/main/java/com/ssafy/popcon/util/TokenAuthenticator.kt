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
        //Log.d(TAG, "authenticatedddd: ${response.code}")
        if (response.code == 401) {
            if (!WearApplicationClass.sharedPreferencesUtil.refreshToken.isNullOrEmpty()) {
                val authRepo = WearRepository(WearRemoteDataSource(WearRetrofitUtil.userService))

                runBlocking {
                    val res = authRepo.refreshToken("Bearer" + WearApplicationClass.sharedPreferencesUtil.refreshToken!!)
                    Log.d(TAG, "authenticatedddd: $res")
                    WearApplicationClass.sharedPreferencesUtil.accessToken = res.acessToken
                    WearApplicationClass.sharedPreferencesUtil.refreshToken = res.refreshToekn
                }
            }
        }
        /*val authRepo = WearRepository(WearRemoteDataSource(WearRetrofitUtil.userService))
        runBlocking {
            val res = authRepo.refreshToken(WearApplicationClass.sharedPreferencesUtil.refreshToken!!)
            Log.d(TAG, "authenticatedddd: $res")
        }
            WearApplicationClass.sharedPreferencesUtil.accessToken = res.acessToken
            WearApplicationClass.sharedPreferencesUtil.refreshToken = res.refreshToekn
        */

        return response.request.newBuilder()
            .header("Authorization", "Bearer ${WearApplicationClass.sharedPreferencesUtil.accessToken?:""}")
            .build()
    }
}
