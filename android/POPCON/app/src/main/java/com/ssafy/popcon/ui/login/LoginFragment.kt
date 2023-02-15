package com.ssafy.popcon.ui.login

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.target.DrawableImageViewTarget
import com.bumptech.glide.request.transition.Transition
import com.google.firebase.messaging.FirebaseMessaging
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.oauth.OAuthLoginCallback
import com.navercorp.nid.profile.NidProfileCallback
import com.navercorp.nid.profile.data.NidProfileResponse
import com.ssafy.popcon.R
import com.ssafy.popcon.config.ApplicationClass
import com.ssafy.popcon.databinding.FragmentLoginBinding
import com.ssafy.popcon.dto.Gallery
import com.ssafy.popcon.dto.User
import com.ssafy.popcon.dto.UserResponse
import com.ssafy.popcon.mms.RoomInitLogin
import com.ssafy.popcon.repository.auth.AuthRemoteDataSource
import com.ssafy.popcon.repository.auth.AuthRepository
import com.ssafy.popcon.ui.common.MainActivity
import com.ssafy.popcon.ui.home.HomeFragment
import com.ssafy.popcon.util.RetrofitUtil
import com.ssafy.popcon.util.SharedPreferencesUtil
import com.ssafy.popcon.viewmodel.FCMViewModel
import com.ssafy.popcon.viewmodel.MMSViewModel
import com.ssafy.popcon.viewmodel.UserViewModel
import com.ssafy.popcon.viewmodel.ViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*


private const val TAG = "LoginFragment_싸피"

class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private val viewModel: UserViewModel by viewModels { ViewModelFactory(requireContext()) }
    private val fcmViewModel: FCMViewModel by viewModels { ViewModelFactory(requireContext()) }
    private val mmsViewModel: MMSViewModel by viewModels { ViewModelFactory(requireContext()) }
    lateinit var sp: SharedPreferencesUtil
    lateinit var userResponse: UserResponse

    private var userUUID: String = ""
    var fcmToken = ""
    var user = User("", "", "")

    lateinit var kakaoCallback: (OAuthToken?, Throwable?) -> Unit
    lateinit var mainActivity: MainActivity

    companion object {
        var fromSettingsFragment = false
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = activity as MainActivity
        sp = SharedPreferencesUtil(requireContext())
    }

    @SuppressLint("ResourceAsColor")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)

        Glide.with(requireContext()).load(R.raw.pop_1200)
            .into(object : DrawableImageViewTarget(binding.popconGif) {
                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable>?
                ) {
                    if (resource is GifDrawable) {
                        (resource as GifDrawable).setLoopCount(1)
                    }
                    super.onResourceReady(resource, transition)
                }
            })
        mainActivity.updateStatusBarColor("#F7B733")
        return binding.root
    }

    override fun onResume() {
        super.onResume()

        mainActivity.hideBottomNav(true)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getFCMToken()
        init()
        chkRoute()

        binding.run {
            kakaoLogin()
            naverLogin()
        }
    }

    private fun init() {
        mainActivity = activity as MainActivity
    }

    // 앱을 처음 실행한 것인지, 로그아웃 또는 회원탈퇴를 한 직후인지 확인
    private fun chkRoute() {
        if (!fromSettingsFragment) {
            //자동로그인
            if (sp.getUser().email != "") {
                mainActivity.changeFragment(HomeFragment())
            }
        } else {
            sp.deleteUser()
            fromSettingsFragment = false
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun successLogin(userResponse: UserResponse) {
        fcmToken = fcmViewModel.token

        val newUser = User(
            userResponse.email,
            userResponse.social,
            userResponse.nday,
            userResponse.alarm,
            userResponse.manner_temp,
            userResponse.term,
            userResponse.timezone,
            fcmToken
        )

        RoomInitLogin(requireContext(), mmsViewModel).initRoom()
        sp.updateUser(newUser)
        sp.setFCMToken(fcmToken)
        sp.setGalleryInfo(
            Gallery(System.currentTimeMillis(), 0)
        )

        viewModel.updateUser(newUser)
        mainActivity.makeGalleryDialogFragment(requireContext(), mainActivity.contentResolver)
        mainActivity.changeFragment(HomeFragment())
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun kakaoLogin() {
        binding.btnKakaoLogin.setOnClickListener {
            kakaoCallback = { tokenInfo, error ->
                if (error != null) {
                    Log.e(TAG, "kakaoLogin_error: ${error}")
                } else if (tokenInfo != null) {
                    // 로그인 되어있는 상태
                    Log.d(TAG, "kakaoLogin_tokenInfo: ${tokenInfo}")
                }
            }

            if (UserApiClient.instance.isKakaoTalkLoginAvailable(mainActivity)) {
                // 앱 이용 동의 화면 출력
                UserApiClient.instance.loginWithKakaoTalk(mainActivity) { token, error ->
                    if (error != null) {
                        // 사용자가 취소했을 경우
                        if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                            return@loginWithKakaoTalk
                        } else {
                            loginWithKakaoAccount()
                        }
                    } else if (token != null) {
                        // 로그인 성공
                        UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
                            successKakaoLogin()
                        }
                    }
                }
            } else {
                loginWithKakaoAccount()
            }
        }
    }

    // 카카오 계정 웹 로그인
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun loginWithKakaoAccount() {
        UserApiClient.instance.loginWithKakaoAccount(mainActivity) { token, error ->
            if (error != null) {
                Log.e(TAG, "kakaoLogin_error: ${error}")
            } else if (token != null) {
                // 로그인
                successKakaoLogin()
            }
        }
    }

    // 카카오 로그인 성공
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun successKakaoLogin() {
        UserApiClient.instance.me { meUser, error ->
            val email = meUser?.kakaoAccount?.email.toString()

            user = User(email, "카카오", fcmToken)
            sp.addUser(user)
            sp.setGalleryInfo(
                Gallery(System.currentTimeMillis(), 0)
            )

            val authRepo =
                AuthRepository(AuthRemoteDataSource(RetrofitUtil.authService))

            val job = CoroutineScope(Dispatchers.IO).launch {
                userResponse = authRepo.signIn(user)
            }
            runBlocking {
                job.join()
                ApplicationClass.sharedPreferencesUtil.accessToken =
                    userResponse.acessToken
                ApplicationClass.sharedPreferencesUtil.refreshToken =
                    userResponse.refreshToekn
                Log.d(
                    TAG,
                    "onSuccess: ${ApplicationClass.sharedPreferencesUtil.accessToken}"
                )
                successLogin(userResponse)
            }


//           viewModel.signInKakao(user)
//           viewModel.user.observe(viewLifecycleOwner) {
//                 successLogin(it)
//            }
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
                        @RequiresApi(Build.VERSION_CODES.Q)
                        override fun onSuccess(result: NidProfileResponse) {
                            val email = result.profile?.email.toString()
                            user = User(email, "네이버", fcmToken)
                            sp.addUser(user)
                            sp.setGalleryInfo(
                                Gallery(System.currentTimeMillis(), 0)
                            )

                            Log.e("TAG", "네이버 로그인한 유저 정보 - 이메일 : $email")
                            val authRepo =
                                AuthRepository(AuthRemoteDataSource(RetrofitUtil.authService))

                            val job = CoroutineScope(Dispatchers.IO).launch {
                                userResponse = authRepo.signIn(user)
                            }
                            runBlocking {
                                job.join()
                                ApplicationClass.sharedPreferencesUtil.accessToken =
                                    userResponse.acessToken
                                ApplicationClass.sharedPreferencesUtil.refreshToken =
                                    userResponse.refreshToekn
                                Log.d(
                                    TAG,
                                    "onSuccess: ${ApplicationClass.sharedPreferencesUtil.accessToken}"
                                )
                                successLogin(userResponse)
                            }
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

            NaverIdLoginSDK.authenticate(requireContext(), oAuthLoginCallback)
        }
    }

    // 토큰 보내기
    fun uploadToken(token: String) {
        fcmViewModel.uploadToken(token)
    }

    // 토큰 생성
    private fun getFCMToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@addOnCompleteListener
            }
            Log.d(TAG, "token 정보: ${task.result ?: "task.result is null"}")
            if (task.result != null) {
                uploadToken(task.result)
                fcmViewModel.setToken(task.result)
                sp.setFCMToken(task.result)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        val mainActivity = activity as MainActivity
        mainActivity.hideBottomNav(false)
    }
}