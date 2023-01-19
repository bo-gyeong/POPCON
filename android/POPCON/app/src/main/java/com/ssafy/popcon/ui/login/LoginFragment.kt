package com.ssafy.popcon.ui.login

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
import androidx.navigation.fragment.findNavController
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.oauth.OAuthLoginCallback
import com.navercorp.nid.profile.NidProfileCallback
import com.navercorp.nid.profile.data.NidProfileResponse
import com.ssafy.popcon.BuildConfig
import com.ssafy.popcon.R
import com.ssafy.popcon.databinding.FragmentLoginBinding
import com.ssafy.popcon.dto.User
import com.ssafy.popcon.ui.common.MainActivity
import com.ssafy.popcon.ui.home.HomeFragment
import com.ssafy.popcon.util.SharedPreferencesUtil
import java.util.*

private const val TAG = "NaverLoginFragment_싸피"

class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private var userUUID: String = ""

    lateinit var kakaoCallback: (OAuthToken?, Throwable?) -> Unit
    lateinit var mainActivity: MainActivity
    private var email: String = ""

    override fun onStart() {
        super.onStart()
        mainActivity = activity as MainActivity
    }

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

        mainActivity = activity as MainActivity

        //자동로그인
        if (SharedPreferencesUtil(requireContext()).getUser().email != "") {
            mainActivity.changeFragment(HomeFragment())
        }

        init()

        binding.run {
            kakaoLogin()
            naverLogin()
        }
        binding.btnNonmemberLogin.setOnClickListener {
            nonMemberLogin()
        }
    }

    private fun init() {
        mainActivity = activity as MainActivity

        kakaoLoginState()
    }

    private fun kakaoLoginState() {
        KakaoSdk.init(mainActivity, BuildConfig.KAKAO_API_KEY)

        UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
            if (error != null) {
                // 동의화면에서 동의 누르기 전에 뜸
                Log.d(TAG, "init_error: ${error}")
                if (tokenInfo == null) {
                    // 디비에 값 저장
                }
            } else if (tokenInfo != null) {
                Log.d(TAG, "init_tokenInfo: ${tokenInfo}")
                // 로그인 되어있는 상태(로그인 화면 보여줄 필요 없음)
            }
        }
    }

    private fun kakaoLogin() {
        binding.btnKakaoLogin.setOnClickListener {
            kakaoCallback = { tokenInfo, error ->
                if (error != null) {
                    Log.e(TAG, "kakaoLogin_error: ${error}")
                } else if (tokenInfo != null) {
                    Log.d(TAG, "kakaoLogin_tokenInfo: ${tokenInfo}")
                    // 로그인 되어있는 상태
                }
            }

            if (UserApiClient.instance.isKakaoTalkLoginAvailable(mainActivity)) {
                // 앱 이용 동의 화면 출력
                UserApiClient.instance.loginWithKakaoTalk(mainActivity) { token, error ->
                    if (error != null) {
                        Log.e(TAG, "kakaoLogin: ${error}")
                        // 사용자가 취소했을 경우
                        if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                            return@loginWithKakaoTalk
                        } else {
                            // 카카오 계정으로 로그인
                            UserApiClient.instance.loginWithKakaoAccount(
                                mainActivity,
                                callback = kakaoCallback
                            )
                        }
                    } else if (token != null) {
                        // 로그인 성공
                        UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
                            UserApiClient.instance.me { user, error ->
                                Log.d(TAG, "kakaoLogin: ${user?.kakaoAccount?.email}")
                                mainActivity.changeFragment(HomeFragment())
                            }
                        }
                    }
                }
            } else {
                // 카카오 계정으로 로그인
                UserApiClient.instance.loginWithKakaoAccount(mainActivity, callback = kakaoCallback)
            }
        }
    }

    //네이버로그인
    private fun naverLogin() {
        binding.btnNaverLogin.setOnClickListener {
            val oAuthLoginCallback = object : OAuthLoginCallback {
                override fun onSuccess() {
                    // 네이버 로그인 API 호출 성공 시 유저 정보를 가져온다
                    NidOAuthLogin().callProfileApi(object :
                        NidProfileCallback<NidProfileResponse> {
                        override fun onSuccess(result: NidProfileResponse) {
                            email = result.profile?.email.toString()

                            SharedPreferencesUtil(requireContext()).addUser(User(email, 2))
                            //Log.e("TAG", "네이버 로그인한 유저 정보 - 이메일 : $email")
                            mainActivity.changeFragment(HomeFragment())
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

    // 비회원 로그인 : UUID 생성 후 리텅
    private fun nonMemberLogin() {
        if (userUUID == "")
            userUUID = UUID.randomUUID().toString()
        // 서버에게 생성한 UUID 전송할 레트로핏 코드
        Log.d(TAG, "nonMemberLogin: $userUUID")
        //
    }

    override fun onDestroy() {
        super.onDestroy()

        val mainActivity = activity as MainActivity
        mainActivity.hideBottomNav(false)
    }
}