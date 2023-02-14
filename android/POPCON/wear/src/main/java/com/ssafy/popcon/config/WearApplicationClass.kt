package com.ssafy.popcon.config

import android.Manifest
import android.app.Application
import android.content.Context
import android.util.Log
import com.google.gson.GsonBuilder
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.NaverIdLoginSDK
import com.ssafy.popcon.BuildConfig
import com.ssafy.popcon.util.AuthInterceptor
import com.ssafy.popcon.util.SharedPreferencesUtil
import com.ssafy.popcon.util.TokenAuthenticator
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

private const val TAG = "ApplicationClass_싸피"
class WearApplicationClass : Application() {
    companion object {
        const val SERVER_URL = BuildConfig.BASE_URL

        lateinit var sharedPreferencesUtil: SharedPreferencesUtil
        lateinit var retrofit: Retrofit
        lateinit var refreshRetrofit: Retrofit

        // 모든 퍼미션 관련 배열
        val requiredPermissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
        )

        fun makeRetrofit(url: String): Retrofit {
            val okHttpClient = OkHttpClient.Builder()
                .readTimeout(5000, TimeUnit.MILLISECONDS)
                .connectTimeout(5000, TimeUnit.MILLISECONDS)
                // 로그캣에 okhttp.OkHttpClient로 검색하면 http 통신 내용을 보여줍니다.
                .authenticator(TokenAuthenticator())
                .addInterceptor(AuthInterceptor())
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build()

            retrofit = Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
                .client(okHttpClient)
                .build()

            return retrofit
        }

        fun makeRefreshRetrofit(url: String): Retrofit {
            val okHttpClient = OkHttpClient.Builder()
                .readTimeout(5000, TimeUnit.MILLISECONDS)
                .connectTimeout(5000, TimeUnit.MILLISECONDS)
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build()

            refreshRetrofit = Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
                .client(okHttpClient)
                .build()

            return refreshRetrofit
        }
    }

    private fun kakaoLoginState() {
        KakaoSdk.init(applicationContext, BuildConfig.KAKAO_API_KEY)

        UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
            if (error != null) {
                // 동의화면에서 동의 누르기 전에 뜸
                Log.d(TAG, "init_error: ${error}")
                if (tokenInfo == null) {
                    // 디비에 값 저장
                    Log.d(TAG, "kakaoLoginState: ")
                }
            } else if (tokenInfo != null) {
                // 로그인 되어있는 상태
                Log.d(TAG, "init_tokenInfo: ${tokenInfo}")
            }
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
        sharedPreferencesUtil = SharedPreferencesUtil(applicationContext)
        super.onCreate()
        //shared preference 초기화

        makeRetrofit(SERVER_URL)
        kakaoLoginState()
        makeRefreshRetrofit(SERVER_URL)
        setNaverModule(applicationContext)
    }
}