package com.ssafy.popcon.ui.settings

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.oauth.OAuthLoginCallback
import com.ssafy.popcon.R
import com.ssafy.popcon.databinding.FragmentSettingsBinding
import com.ssafy.popcon.dto.User
import com.ssafy.popcon.dto.UserDeleteRequest
import com.ssafy.popcon.ui.common.EventObserver
import com.ssafy.popcon.ui.common.MainActivity
import com.ssafy.popcon.ui.login.LoginFragment
import com.ssafy.popcon.ui.popup.GifticonDialogFragment
import com.ssafy.popcon.util.SharedPreferencesUtil
import com.ssafy.popcon.viewmodel.UserViewModel
import com.ssafy.popcon.viewmodel.ViewModelFactory

private const val TAG = "SettingsFragment_싸피"
class SettingsFragment : Fragment() {
    private lateinit var binding: FragmentSettingsBinding
    private val viewModel: UserViewModel by activityViewModels { ViewModelFactory(requireContext()) }

    private lateinit var mainActivity: MainActivity
    private lateinit var user: User
    private lateinit var fcmToken: String
    private lateinit var shardPreference:SharedPreferences

    override fun onAttach(context: Context) {
        super.onAttach(context)
        user = SharedPreferencesUtil(requireContext()).getUser()
        fcmToken = SharedPreferencesUtil(requireContext()).getFCMToken()
        shardPreference = SharedPreferencesUtil(requireContext()).preferences
        mainActivity = context as MainActivity
    }

    override fun onStart() {
        super.onStart()
        GifticonDialogFragment.isShow = true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val timeZone = shardPreference.getInt("noti_time", 1)
        changeTime(timeZone)

        user = User(
            user.email,
            user.social,
            shardPreference.getInt("noti_first", 1),
            shardPreference.getInt("alarm", 1),
            shardPreference.getInt("manner_temp", 0),
            shardPreference.getInt("noti_interval", 1),
            timeZone,
            fcmToken
        )
        binding.user = user

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getLevel()
        switchClick()
        showSettingTime()
        settingVisibility()

        binding.lNotiFirst.setOnClickListener {
            makeDialog(0)
        }
        binding.lNotiInterval.setOnClickListener {
            makeDialog(1)
        }
        binding.lNotiTime.setOnClickListener {
            makeDialog(2)
        }
        switchState()

        binding.run {
            clickJoin()

            if (user!!.social == "카카오") {
                ivTitleAccount.setImageResource(R.drawable.kakaotalk_icon)
                signOutKakao()
                withdrawKakao()
            } else if (user!!.social == "네이버") {
                ivTitleAccount.setImageResource(com.nhn.android.oauth.R.drawable.naver_icon)
                signOutNaver()
                withdrawNaver()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        GifticonDialogFragment.isShow = false
    }

    // 비회원이 로그인하기 클릭 시
    private fun clickJoin() {
        if (user.social == "비회원") {
            binding.lAccount.setOnClickListener {
                settingsToLogin()
            }
        }
    }

    // user의 level 받아오기
    private fun getLevel(){
        viewModel.getUserLv()
        viewModel.userLv.observe(viewLifecycleOwner, EventObserver{
            when(it){
                0 -> Glide.with(requireContext()).load(R.drawable.lv0).circleCrop().into(binding.ivLevel)
                1 -> Glide.with(requireContext()).load(R.drawable.lv1).circleCrop().into(binding.ivLevel)
                2 -> Glide.with(requireContext()).load(R.drawable.lv2).circleCrop().into(binding.ivLevel)
                3 -> Glide.with(requireContext()).load(R.drawable.lv3).circleCrop().into(binding.ivLevel)
                4 -> Glide.with(requireContext()).load(R.drawable.lv4).circleCrop().into(binding.ivLevel)
                5 -> Glide.with(requireContext()).load(R.drawable.lv5).circleCrop().into(binding.ivLevel)
            }
            setSpMannerTmp(it)
        })
    }

    // sp의 user update
    private fun setSpMannerTmp(userLv : Int){
        var userTmp = 0
        when(userLv){
            1 -> userTmp = 1
            2 -> userTmp = 3
            3 -> userTmp = 6
            4 -> userTmp = 9
            5 -> userTmp = 12
        }

        // sp user update
        val newUser = User(
            user.email,
            user.social,
            shardPreference.getInt("noti_first", 1),
            shardPreference.getInt("alarm", 1),
            userTmp,
            shardPreference.getInt("noti_interval", 1),
            shardPreference.getInt("noti_time", 1),
            user.token
        )
        SharedPreferencesUtil(requireContext()).updateUser(newUser)
    }

    // 알림 다이얼로그 생성
    private fun makeDialog(notiListPosition: Int){
        val dialog = NotiDialogFragment(notiListPosition)
        dialog.show(childFragmentManager, "NotiDialog")
        dialog.setOnClickListener(object: NotiDialogFragment.BtnClickListener{
            override fun onClicked(selectPos: Int, selectValue:Int) {
                var userInfo: User? = null
                Log.d(TAG, "onClicked: ${selectPos}   ${selectValue}")
                when (selectPos){
                    0 -> {
                        var term = user.term
                        if (term > selectValue){
                            term = selectValue
                        }
                        userInfo = User(user.email, user.social, selectValue, user.alarm, "the world",user.manner_temp, term, user.timezone, user.token)
                    }
                    1 -> userInfo = User(user.email, user.social, user.nday, user.alarm, "the world", user.manner_temp, selectValue, user.timezone, user.token)
                    2 -> {
                        userInfo = User(user.email, user.social, user.nday, user.alarm, "the world", user.manner_temp, user.term, selectValue, user.token)
                        changeTime(selectValue)
                    }
                }
                SharedPreferencesUtil(requireContext()).updateUser(userInfo!!)
                user = userInfo

                viewModel.updateUser(user)
                viewModel.user.observe(viewLifecycleOwner){
                    binding.user = it
                }
            }
        })
    }

    // user의 timeZone을 시간 String으로 변경
    private fun changeTime(timeZone: Int){
        val timeList = arrayOf(
            this.resources.getText(R.string.time_9) as String,
            this.resources.getText(R.string.time_13) as String,
            this.resources.getText(R.string.time_18) as String
        )

        binding.notiTime = timeList[timeZone]
    }

    // 설정에서 로그인화면으로 이동 및 로그인정보 삭제
    private fun settingsToLogin() {
        LoginFragment.fromSettingsFragment = true
        mainActivity.changeFragment(LoginFragment())

        mainActivity.bottomNav.menu
            .findItem(R.id.homeFragment).isChecked = true
    }

    // 시간 설정 text변경
    private fun showSettingTime(){
        when(user.timezone) {
            0 -> binding.tvNotiSettingTime.text = resources.getText(R.string.time_9)
            1 -> binding.tvNotiSettingTime.text = resources.getText(R.string.time_13)
            2 -> binding.tvNotiSettingTime.text = resources.getText(R.string.time_18)
        }
    }

    // 비회원일 경우 로그인한계정, 로그아웃, 회원탈퇴 안보이도록
    private fun settingVisibility() {
        if (user.social == "비회원") {
            binding.tvJoin.visibility = View.VISIBLE
            binding.tvTitleAccount.visibility = View.GONE
            binding.tvAccount.visibility = View.GONE
            binding.tvLogout.visibility = View.GONE
            binding.tvWithdraw.visibility = View.GONE
        } else {
            binding.tvJoin.visibility = View.GONE
            binding.tvTitleAccount.visibility = View.VISIBLE
            binding.tvAccount.visibility = View.VISIBLE
            binding.tvLogout.visibility = View.VISIBLE
            binding.tvWithdraw.visibility = View.VISIBLE
        }
    }

    private fun switchState(){
        if (binding.switchNoti.isChecked){
            notiActive()
        } else{
            notiInactive()
        }
    }

    // 알림 활성화 여부
    private fun switchClick() {
        var userInfo: User
        binding.switchNoti.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                userInfo = User(user.email, user.social, user.nday, 1, "the world", user.manner_temp, user.term, user.timezone, user.token)
                notiActive()
            } else {
                userInfo = User(user.email, user.social, user.nday, 0, "the world", user.manner_temp, user.term, user.timezone, user.token)
                notiInactive()
            }
            SharedPreferencesUtil(requireContext()).updateUser(userInfo)
            user = userInfo

            viewModel.updateUser(user)
        }
    }

    // 알림 스위치 활성화상태
    private fun notiActive(){
        binding.lNotiFirst.isClickable = true
        binding.lNotiInterval.isClickable = true
        binding.lNotiTime.isClickable = true

        binding.tvNotiTitleFirst.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.popcon_black_03
            )
        )
        binding.tvNotiTitleInterval.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.popcon_black_03
            )
        )
        binding.tvNotiTitleTime.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.popcon_black_03
            )
        )
        binding.tvNotiSettingFirst.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.popcon_grey_04
            )
        )
        binding.tvNotiSettingFirstText.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.popcon_grey_04
            )
        )
        binding.tvNotiSettingInterval.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.popcon_grey_04
            )
        )
        binding.tvNotiSettingIntervalText.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.popcon_grey_04
            )
        )
        binding.tvNotiSettingTime.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.popcon_grey_04
            )
        )
    }

    // 알림 스위치 비활성화상태
    private fun notiInactive(){
        binding.lNotiFirst.isClickable = false
        binding.lNotiInterval.isClickable = false
        binding.lNotiTime.isClickable = false

        binding.tvNotiTitleFirst.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.popcon_grey_07
            )
        )
        binding.tvNotiTitleInterval.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.popcon_grey_07
            )
        )
        binding.tvNotiTitleTime.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.popcon_grey_07
            )
        )
        binding.tvNotiSettingFirst.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.popcon_transparent_grey_07
            )
        )
        binding.tvNotiSettingFirstText.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.popcon_transparent_grey_07
            )
        )
        binding.tvNotiSettingInterval.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.popcon_transparent_grey_07
            )
        )
        binding.tvNotiSettingIntervalText.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.popcon_transparent_grey_07
            )
        )
        binding.tvNotiSettingTime.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.popcon_transparent_grey_07
            )
        )
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
        binding.tvWithdraw.setOnClickListener {
            UserApiClient.instance.unlink { error ->
                if (error != null) {
                    Log.e(TAG, "연결 끊기 실패", error)
                } else {
                    viewModel.withdraw(UserDeleteRequest(user.email!!, user.social))
                    Log.i(TAG, "연결 끊기 성공. SDK에서 토큰 삭제 됨")
                }
                settingsToLogin()
            }
        }
    }

    //네이버 로그아웃
    private fun signOutNaver() {
        binding.tvLogout.setOnClickListener {
            //Log.d(TAG, "signOutNaver: ${SharedPreferencesUtil(requireContext()).getUser().social}")
            if (SharedPreferencesUtil(requireContext()).getUser().social == "네이버") {
                settingsToLogin()
            }
        }
    }

    //네이버 회원탈퇴
    private fun withdrawNaver() {
        binding.tvWithdraw.setOnClickListener {
            if (user.social == "네이버") {
                NidOAuthLogin().callDeleteTokenApi(requireContext(), object : OAuthLoginCallback {
                    override fun onSuccess() {
                        //서버에서 토큰 삭제에 성공한 상태입니다.

                        viewModel.withdraw(UserDeleteRequest(user.email!!, user.social.toString()))
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