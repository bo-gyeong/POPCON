package com.ssafy.popcon.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.oauth.OAuthLoginCallback
import com.navercorp.nid.profile.NidProfileCallback
import com.navercorp.nid.profile.data.NidProfileResponse
import com.ssafy.popcon.BuildConfig
import androidx.navigation.fragment.findNavController
import com.ssafy.popcon.R
import com.ssafy.popcon.config.ApplicationClass
import com.ssafy.popcon.ui.common.MainActivity
import com.ssafy.popcon.databinding.FragmentLoginBinding

class NaverLoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
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
        val mainActivity = activity as MainActivity
        mainActivity.hideBottomNav(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //로그인 된 상태인지 확인
        var user = ApplicationClass.sharedPreferencesUtil.getUser()

        //로그인 상태 확인. id가 있다면 로그인 된 상태
        if (user.email != "") {//있음
            findNavController().navigate(R.id.action_naverLoginFragment_to_homeFragment)
        }

        Log.d("TAG", "onViewCreated: ")
        binding.run {
            binding.btnNaverLogin.setOnClickListener {
                val oAuthLoginCallback = object : OAuthLoginCallback {
                    override fun onSuccess() {
                        // 네이버 로그인 API 호출 성공 시 유저 정보를 가져온다
                        NidOAuthLogin().callProfileApi(object :
                            NidProfileCallback<NidProfileResponse> {
                            override fun onSuccess(result: NidProfileResponse) {
                                email = result.profile?.email.toString()

                                findNavController().navigate(R.id.action_naverLoginFragment_to_homeFragment)
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
    }

    override fun onDestroy() {
        super.onDestroy()

        val mainActivity = activity as MainActivity
        mainActivity.hideBottomNav(false)
    }
}