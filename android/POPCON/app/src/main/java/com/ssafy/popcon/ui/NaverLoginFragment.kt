package com.ssafy.popcon.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.oauth.OAuthLoginCallback
import com.navercorp.nid.profile.NidProfileCallback
import com.navercorp.nid.profile.data.NidProfileResponse
import com.ssafy.popcon.BuildConfig
import com.ssafy.popcon.ui.common.MainActivity
import com.ssafy.popcon.databinding.FragmentLoginBinding

private const val TAG = "NaverLoginFragment_싸피"
class NaverLoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding

    lateinit var kakaoCallback: (OAuthToken?, Throwable?) -> Unit
    lateinit var mainActivity:MainActivity
    private var email: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        mainActivity.hideBottomNav(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d(TAG, "onViewCreated: ")
        init()

        binding.run {
            kakaoLogin()
            naverLogin()
        }

        binding.btnKakaoWithdraw.setOnClickListener {
            kakaoWithdraw()
        }
    }

    private fun init(){
        mainActivity = activity as MainActivity

        kakaoLoginState()
    }

    private fun kakaoLoginState(){
        KakaoSdk.init(mainActivity, BuildConfig.KAKAO_API_KEY)

        //AccessTokenInfo(id=2617671289(고정 uid), expiresIn=43129, appId=849689, expiresInMillis=43129279)
        UserApiClient.instance.accessTokenInfo{ tokenInfo, error ->
            if (error != null){
                // 동의화면에서 동의 누르기 전에 뜸
                Log.d(TAG, "init_error: ${error}")
                if(tokenInfo == null){
                    // 디비에 값 저장
                }
            } else if (tokenInfo != null){
                Log.d(TAG, "init_tokenInfo: ${tokenInfo}")
                // 로그인 되어있는 상태(로그인 화면 보여줄 필요 없음)
            }
        }
    }

    private fun kakaoLogin(){
        binding.btnKakaoLogin.setOnClickListener {
            kakaoCallback = { tokenInfo, error ->
                if (error != null){
                    Log.e(TAG, "kakaoLogin_error: ${error}", )
                } else if (tokenInfo != null){
                    Log.d(TAG, "kakaoLogin_tokenInfo: ${tokenInfo}")
                    // 로그인 되어있는 상태
                }
            }

            if (UserApiClient.instance.isKakaoTalkLoginAvailable(mainActivity)){
                // 앱 이용 동의 화면 출력
                UserApiClient.instance.loginWithKakaoTalk(mainActivity){ token, error ->
                    if (error != null){
                        Log.e(TAG, "kakaoLogin: ${error}")
                        // 사용자가 취소했을 경우
                        if (error is ClientError && error.reason == ClientErrorCause.Cancelled){
                            return@loginWithKakaoTalk
                        } else{
                            // 카카오 계정으로 로그인
                            UserApiClient.instance.loginWithKakaoAccount(mainActivity, callback = kakaoCallback)
                        }
                    } else if (token != null){
                        // 로그인 성공
                        UserApiClient.instance.accessTokenInfo{ tokenInfo, error ->
                            UserApiClient.instance.me { user, error ->
                                Log.d(TAG, "kakaoLogin: ${user?.kakaoAccount?.email}")
                            }
                        }
                    }
                }
            } else{
                // 카카오 계정으로 로그인
                UserApiClient.instance.loginWithKakaoAccount(mainActivity, callback = kakaoCallback)
            }
        }
    }

    fun kakaoWithdraw(){
        UserApiClient.instance.unlink { error ->
            if (error != null) {
                Log.e(TAG, "연결 끊기 실패", error)
            }
            else {
                Log.i(TAG, "연결 끊기 성공. SDK에서 토큰 삭제 됨")
            }
        }
    }

    private fun naverLogin() {
        binding.btnNaverLogin.setOnClickListener {
            val oAuthLoginCallback = object : OAuthLoginCallback {
                override fun onSuccess() {
                    // 네이버 로그인 API 호출 성공 시 유저 정보를 가져온다
                    NidOAuthLogin().callProfileApi(object :
                        NidProfileCallback<NidProfileResponse> {
                        override fun onSuccess(result: NidProfileResponse) {
                            email = result.profile?.email.toString()
                            Log.e("TAG", "네이버 로그인한 유저 정보 - 이메일 : $email")
                        }

                        override fun onError(errorCode: Int, message: String) {
                            //
                        }

                        override fun onFailure(httpStatus: Int, message: String) {
                            //
                        }
                    })
                }

                override fun onError(errorCode: Int, message: String) {
                    val naverAccessToken = NaverIdLoginSDK.getAccessToken()
                    Log.e("TAG", "naverAccessToken : $naverAccessToken")
                }

                override fun onFailure(httpStatus: Int, message: String) {
                    //
                }
            }

            NaverIdLoginSDK.initialize(
                requireContext(),
                BuildConfig.naverClientID,
                BuildConfig.naverClientSecret,
                "POPCON"
            )

            NaverIdLoginSDK.authenticate(requireContext(), oAuthLoginCallback)
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        val mainActivity = activity as MainActivity
        mainActivity.hideBottomNav(false)
    }
}