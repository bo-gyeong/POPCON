package com.ssafy.popcon.ui.settings

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.oauth.OAuthLoginCallback
import com.ssafy.popcon.R
import com.ssafy.popcon.databinding.FragmentSettingsBinding
import com.ssafy.popcon.dto.User
import com.ssafy.popcon.ui.common.MainActivity
import com.ssafy.popcon.ui.login.LoginFragment
import com.ssafy.popcon.ui.popup.GifticonDialogFragment
import com.ssafy.popcon.util.SharedPreferencesUtil

private const val TAG = "SettingsFragment_싸피"
class SettingsFragment: Fragment() {
    private lateinit var binding: FragmentSettingsBinding

    private lateinit var mainActivity: MainActivity
    private lateinit var user:User

    override fun onAttach(context: Context) {
        super.onAttach(context)
        user = SharedPreferencesUtil(requireContext()).getUser()
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
        binding.user = user

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        notiActive()
        settingVisibility()

        binding.run {
            clickJoin()

            if (user!!.type == 1){
                signOutKakao()
                withdrawKakao()
            } else if (user!!.type == 2){
                signOutNaver()
                withdrawNaver()
            }
        }
    }

    // 비회원이 로그인하기 클릭 시
    private fun clickJoin(){
        if (user.type == 0){
            binding.lAccount.setOnClickListener {
                settingsToLogin()
            }
        }
    }

    // 설정에서 로그인화면으로 이동 및 로그인정보 삭제
    private fun settingsToLogin(){
        SharedPreferencesUtil(requireContext()).deleteUser()
        mainActivity.onBackPressed()
        mainActivity.changeFragment(LoginFragment())
    }

    // 비회원일 경우 로그인한계정, 로그아웃, 회원탈퇴 안보이도록
    private fun settingVisibility(){
        if (user.type == 0){
            binding.tvJoin.visibility = View.VISIBLE
            binding.tvTitleAccount.visibility = View.GONE
            binding.tvAccount.visibility = View.GONE
            binding.tvLogout.visibility = View.GONE
            binding.tvWithdraw.visibility = View.GONE
        } else{
            binding.tvJoin.visibility = View.GONE
            binding.tvTitleAccount.visibility = View.VISIBLE
            binding.tvAccount.visibility = View.VISIBLE
            binding.tvLogout.visibility = View.VISIBLE
            binding.tvWithdraw.visibility = View.VISIBLE
        }
    }

    // 알림 활성화 여부
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
                    settingsToLogin()
                    Log.e(TAG, "kakaoLogout: 로그아웃 실패, SDK에서 토큰 삭제됨", error)
                } else {
                    settingsToLogin()
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
                    settingsToLogin()
                    Log.e(TAG, "연결 끊기 실패", error)
                } else {
                    settingsToLogin()
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
                settingsToLogin()
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
                        settingsToLogin()
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