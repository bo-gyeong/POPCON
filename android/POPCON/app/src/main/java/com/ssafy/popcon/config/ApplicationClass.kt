package com.ssafy.popcon.config

import android.Manifest
import android.app.Application
import android.content.Context
import com.google.gson.GsonBuilder
import com.navercorp.nid.NaverIdLoginSDK
import com.ssafy.popcon.BuildConfig
import com.ssafy.popcon.util.SharedPreferencesUtil
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ApplicationClass : Application() {
    companion object {
        const val SERVER_URL = BuildConfig.BASE_URL

        lateinit var sharedPreferencesUtil: SharedPreferencesUtil
        lateinit var retrofit: Retrofit

        // 모든 퍼미션 관련 배열
        val requiredPermissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
        )

        fun makeRetrofit(url: String): Retrofit {
            val okHttpClient = OkHttpClient.Builder()
                .readTimeout(5000, TimeUnit.MILLISECONDS)
                .connectTimeout(5000, TimeUnit.MILLISECONDS)
                // 로그캣에 okhttp.OkHttpClient로 검색하면 http 통신 내용을 보여줍니다.
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .connectTimeout(30, TimeUnit.SECONDS).build()

            retrofit = Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
                .client(okHttpClient)
                .build()

            return retrofit
        }
    }

    fun setNaverModule(context: Context) {
        NaverIdLoginSDK.initialize(
            context,
            BuildConfig.naverClientID,
            BuildConfig.naverClientSecret,
            "POPCON"
        )
    }

    override fun onCreate() {
        super.onCreate()

        //shared preference 초기화
        sharedPreferencesUtil = SharedPreferencesUtil(applicationContext)

        makeRetrofit(SERVER_URL)
        setNaverModule(applicationContext)
    }
}