//package com.ssafy.popcon.ui.setting
//
//import android.os.Bundle
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.fragment.app.Fragment
//import com.navercorp.nid.NaverIdLoginSDK
//import com.navercorp.nid.oauth.NidOAuthLogin
//import com.navercorp.nid.oauth.OAuthLoginCallback
//import com.ssafy.popcon.databinding.FragmentSettingsBinding
//import com.ssafy.popcon.ui.common.MainActivity
//import com.ssafy.popcon.ui.login.LoginFragment
//import com.ssafy.popcon.util.SharedPreferencesUtil
//
//class SettingFragment : Fragment() {
//    lateinit var binding: FragmentSettingsBinding
//    lateinit var mainActivity: MainActivity
//
//    override fun onStart() {
//        super.onStart()
//        mainActivity = activity as MainActivity
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        binding = FragmentSettingsBinding.inflate(inflater, container, false)
//
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        binding.run {
//            signOut()
//            deleteNaverUser()
//        }
//    }
//
//    //네이버 로그아웃
//    private fun signOut() {
//        binding.btnSignout.setOnClickListener {
//            if (SharedPreferencesUtil(requireContext()).getUser().type == 2) {
//                SharedPreferencesUtil(requireContext()).deleteUser()
//
//                mainActivity.changeFragment(LoginFragment())
//            }
//        }
//    }
//
//    //네이버 회원탈퇴
//    private fun deleteNaverUser() {
//        binding.btnDelete.setOnClickListener {
//            if (SharedPreferencesUtil(requireContext()).getUser().type == 2) {
//                SharedPreferencesUtil(requireContext()).deleteUser()
//
//                NidOAuthLogin().callDeleteTokenApi(requireContext(), object : OAuthLoginCallback {
//                    override fun onSuccess() {
//                        //서버에서 토큰 삭제에 성공한 상태입니다.
//                        SharedPreferencesUtil(requireContext()).deleteUser()
//                        mainActivity.changeFragment(LoginFragment())
//                    }
//
//                    override fun onFailure(httpStatus: Int, message: String) {
//                        // 서버에서 토큰 삭제에 실패했어도 클라이언트에 있는 토큰은 삭제되어 로그아웃된 상태입니다.
//                        // 클라이언트에 토큰 정보가 없기 때문에 추가로 처리할 수 있는 작업은 없습니다.
//                        Log.d(
//                            "NAVER DELETE",
//                            "errorCode: ${NaverIdLoginSDK.getLastErrorCode().code}"
//                        )
//                        Log.d(
//                            "NAVER DELETE",
//                            "errorDesc: ${NaverIdLoginSDK.getLastErrorDescription()}"
//                        )
//                    }
//
//                    override fun onError(errorCode: Int, message: String) {
//                        // 서버에서 토큰 삭제에 실패했어도 클라이언트에 있는 토큰은 삭제되어 로그아웃된 상태입니다.
//                        // 클라이언트에 토큰 정보가 없기 때문에 추가로 처리할 수 있는 작업은 없습니다.
//                        onFailure(errorCode, message)
//                    }
//                })
//            }
//        }
//    }
//}