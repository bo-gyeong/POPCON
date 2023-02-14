package com.ssafy.popcon.util

import android.util.Log
import com.ssafy.popcon.config.WearApplicationClass
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var req =
            chain.request().newBuilder().addHeader(
                "Authorization",
                "Bearer ${WearApplicationClass.sharedPreferencesUtil.accessToken ?: ""}"
            ).build()
        Log.d("TAG", "auth intercept: ${WearApplicationClass.sharedPreferencesUtil.accessToken}")

        return chain.proceed(req)
    }
}