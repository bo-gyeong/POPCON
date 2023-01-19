package com.ssafy.popcon.ui.settings

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.oauth.OAuthLoginCallback
import com.ssafy.popcon.R
import com.ssafy.popcon.databinding.FragmentSettingsBinding
import com.ssafy.popcon.ui.common.MainActivity
import com.ssafy.popcon.ui.login.LoginFragment
import com.ssafy.popcon.ui.popup.GifticonDialogFragment
import com.ssafy.popcon.util.SharedPreferencesUtil

private const val TAG = "SettingsFragment_싸피"
class SettingsFragment: Fragment() {
    private lateinit var binding: FragmentSettingsBinding

    private lateinit var mainActivity: MainActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onStart() {
        super.onStart()
        mainActivity.hideBottomNav(true)
        GifticonDialogFragment.isShow = true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        notiActive()

        // sharedPreference type에 따라 구분하기
        binding.run {
            signOutNaver()
            withdrawNaver()
        }
    }

    private fun notiActive(){
        binding.switchNoti.setOnCheckedChangeListener { compoundButton, b ->
            if (b){
                binding.tvNotiTitleFirst.setTextColor(ContextCompat.getColor(requireContext(), R.color.popcon_black_03))
                binding.tvNotiTitleInterval.setTextColor(ContextCompat.getColor(requireContext(), R.color.popcon_black_03))
                binding.tvNotiTitleTime.setTextColor(ContextCompat.getColor(requireContext(), R.color.popcon_black_03))
                binding.tvNotiSettingFirst.setTextColor(ContextCompat.getColor(requireContext(), R.color.popcon_grey_04))
                binding.tvNotiSettingInterval.setTextColor(ContextCompat.getColor(requireContext(), R.color.popcon_grey_04))
                binding.tvNotiSettingTime.setTextColor(ContextCompat.getColor(requireContext(), R.color.popcon_grey_04))
            } else{
                binding.tvNotiTitleFirst.setTextColor(ContextCompat.getColor(requireContext(), R.color.popcon_grey_07))
                binding.tvNotiTitleInterval.setTextColor(ContextCompat.getColor(requireContext(), R.color.popcon_grey_07))
                binding.tvNotiTitleTime.setTextColor(ContextCompat.getColor(requireContext(), R.color.popcon_grey_07))
                binding.tvNotiSettingFirst.setTextColor(ContextCompat.getColor(requireContext(), R.color.popcon_transparent_grey_07))
                binding.tvNotiSettingInterval.setTextColor(ContextCompat.getColor(requireContext(), R.color.popcon_transparent_grey_07))
                binding.tvNotiSettingTime.setTextColor(ContextCompat.getColor(requireContext(), R.color.popcon_transparent_grey_07))
            }
        }
    }

    // 카카오 로그아웃
    private fun signOutKakao() {
        binding.tvLogout.setOnClickListener {
            UserApiClient.instance.logout { error ->
                if (error != null) {
                    Log.e(TAG, "kakaoLogout: 로그아웃 실패, SDK에서 토큰 삭제됨", error)
                } else {
                    mainActivity.changeFragment(LoginFragment())
                    Log.e(TAG, "kakaoLogout: 로그아웃 성공, SDK에서 토큰 삭제됨")
                }
            }
        }
    }

    // 카카오 회원탈퇴
    private fun withdrawKakao() {
        binding.tvWithdraw.setOnClickListener{
            UserApiClient.instance.unlink { error ->
                if (error != null) {
                    Log.e(TAG, "연결 끊기 실패", error)
                } else {
                    mainActivity.changeFragment(LoginFragment())
                    Log.i(TAG, "연결 끊기 성공. SDK에서 토큰 삭제 됨")
                }
            }
        }
    }

    //네이버 로그아웃
    private fun signOutNaver() {
        binding.tvLogout.setOnClickListener {
            if (SharedPreferencesUtil(requireContext()).getUser().type == 2) {
                SharedPreferencesUtil(requireContext()).deleteUser()

                mainActivity.changeFragment(LoginFragment())
            }
        }
    }

    //네이버 회원탈퇴
    private fun withdrawNaver() {
        binding.tvWithdraw.setOnClickListener {
            if (SharedPreferencesUtil(requireContext()).getUser().type == 2) {
                SharedPreferencesUtil(requireContext()).deleteUser()

                NidOAuthLogin().callDeleteTokenApi(requireContext(), object : OAuthLoginCallback {
                    override fun onSuccess() {
                        //서버에서 토큰 삭제에 성공한 상태입니다.
                        SharedPreferencesUtil(requireContext()).deleteUser()
                        mainActivity.changeFragment(LoginFragment())
                    }

                    override fun onFailure(httpStatus: Int, message: String) {
                        // 서버에서 토큰 삭제에 실패했어도 클라이언트에 있는 토큰은 삭제되어 로그아웃된 상태입니다.
                        // 클라이언트에 토큰 정보가 없기 때문에 추가로 처리할 수 있는 작업은 없습니다.
                        Log.d(
                            "NAVER DELETE",
                            "errorCode: ${NaverIdLoginSDK.getLastErrorCode().code}"
                        )
                        Log.d(
                            "NAVER DELETE",
                            "errorDesc: ${NaverIdLoginSDK.getLastErrorDescription()}"
                        )
                    }

                    override fun onError(errorCode: Int, message: String) {
                        // 서버에서 토큰 삭제에 실패했어도 클라이언트에 있는 토큰은 삭제되어 로그아웃된 상태입니다.
                        // 클라이언트에 토큰 정보가 없기 때문에 추가로 처리할 수 있는 작업은 없습니다.
                        onFailure(errorCode, message)
                    }
                })
            }
        }
    }
}