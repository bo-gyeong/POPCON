package com.ssafy.popcon.util

import android.util.Log
import com.ssafy.popcon.config.ApplicationClass
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var req =
            chain.request().newBuilder().addHeader("accessToken", ApplicationClass.sharedPreferencesUtil.accessToken ?: "").build()
        Log.d("TAG", "intercept: ${ApplicationClass.sharedPreferencesUtil.accessToken}")
        return chain.proceed(req)
    }
}