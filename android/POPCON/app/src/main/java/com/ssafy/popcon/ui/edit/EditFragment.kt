package com.ssafy.popcon.ui.edit

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import com.ssafy.popcon.config.ApplicationClass
import com.ssafy.popcon.databinding.FragmentEditBinding
import com.ssafy.popcon.dto.*
import com.ssafy.popcon.ui.common.MainActivity
import com.ssafy.popcon.ui.common.PopconSnackBar
import com.ssafy.popcon.ui.home.HomeFragment
import com.ssafy.popcon.ui.popup.GifticonDialogFragment.Companion.isShow
import com.ssafy.popcon.ui.popup.ImageDialogFragment
import com.ssafy.popcon.util.SharedPreferencesUtil
import com.ssafy.popcon.util.Utils
import com.ssafy.popcon.viewmodel.GifticonViewModel
import com.ssafy.popcon.viewmodel.ViewModelFactory
import kotlinx.coroutines.*

class EditFragment : Fragment() {
    private lateinit var binding: FragmentEditBinding
    private lateinit var mainActivity: MainActivity
    private lateinit var barNum: String
    private lateinit var gifticon: Gifticon
    private val editViewModel: EditViewModel by activityViewModels {
        ViewModelFactory(
            requireContext()
        )
    }
    private val viewModel: GifticonViewModel by activityViewModels { ViewModelFactory(requireContext()) }
    val user = ApplicationClass.sharedPreferencesUtil.getUser()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onStart() {
        super.onStart()
        mainActivity.hideBottomNav(true)
        isShow = true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditBinding.inflate(inflater, container, false)

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        editViewModel.barNum.observe(viewLifecycleOwner) {
            barNum = it

            setLayout(view)
        }

        dateFormat()

        binding.cbPrice.setOnClickListener {
            changeChkState()
        }

        /*binding.btnRegi.setOnClickListener {
            //유효성 검사
            if (chkCnt >= OriginalImgUris.size){
                for (i in 0 until delImgUris.size){
                    delCropImg(delImgUris[i])
                }

                viewModel.addGifticon(makeAddInfoList())
                mainActivity.changeFragment(HomeFragment())
            } else{
                Toast.makeText(requireContext(), "기프티콘 정보를 확인해주세요", Toast.LENGTH_SHORT).show()
            }
        }*/
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setLayout(view: View) {
        //수정 누르면 업데이트
        binding.btnEdit.setOnClickListener {
            Log.d("TAG", "setLayout: ${binding.gifticon}")
            mainActivity.changeFragment(HomeFragment())

            PopconSnackBar.make(view, "수정이 완료되었어요").show()

            val req = setGifticon()
            viewModel.updateGifticon(req)
            viewModel.getGifticonByUser(SharedPreferencesUtil(requireContext()).getUser())
        }

        viewModel.getGifticonByBarcodeNum(barNum)
        viewModel.gifticon.observe(viewLifecycleOwner) { g ->
            Log.d("TAG", "setLayout: $g")
            gifticon = Gifticon(
                g.barcodeNum,
                g.barcode_filepath ?: "",
                Brand("", g.brandName),
                g.due,
                g.hash,
                g.price,
                g.memo ?: "",
                g.origin_filepath ?: "",
                g.productName,
                g.product_filepath ?: "",
                g.state
            )

            binding.btnOriginalSee.setOnClickListener {
                openImgDialog(gifticon.origin_filepath)
            }

            binding.ivBarcodeImg.setOnClickListener {
                openImgDialog(gifticon.barcode_filepath)
            }

            binding.ivCouponImg.setOnClickListener {
                openImgDialog(gifticon.product_filepath)
            }

            binding.gifticon = gifticon
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setGifticon(): UpdateRequest {
        gifticon.productName = binding.etProductName.text.toString()
        gifticon.brand?.brandName = binding.etProductBrand.text.toString()
        gifticon.due = binding.etDate.text.toString()
        gifticon.memo = binding.etWriteMemo.text.toString()
        gifticon.price = binding.etPrice.text.toString().toInt()
        gifticon.state = Utils.calState(gifticon)

        return UpdateRequest(
            gifticon.barcodeNum,
            gifticon.brand!!.brandName,
            gifticon.due,
            gifticon.memo,
            gifticon.price ?: -1,
            gifticon.productName,
            SharedPreferencesUtil(requireContext()).getUser().email!!,
            SharedPreferencesUtil(requireContext()).getUser().social,
            gifticon.state
        )
    }

    // 이미지 팝업
    private fun openImgDialog(url: String) {
        val args = Bundle()
        args.putString("url", url)

        val dialogFragment = ImageDialogFragment()
        dialogFragment.arguments = args
        dialogFragment.show(childFragmentManager, "originalUrl")
    }

    private fun dateFormat() {
        binding.etDate.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                //p0: 추가된 문자열
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //p0: 현재 입력된 문자열, p1: 새로 추가될 문자열 위치, p2: 변경될 문자열의 수, p3: 새로 추가될 문자열 수
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //p0: 현재 입력된 문자열, p1: 새로 추가될 문자열 위치, p2: 삭제된 기존 문자열 수, p3: 새로 추가될 문자열 수
                val dateLength = binding.etDate.text.length
                if (dateLength == 4 && p1 != 4 || dateLength == 7 && p1 != 7) {
                    val add = binding.etDate.text.toString() + "-"
                    binding.etDate.setText(add)
                    binding.etDate.setSelection(add.length)
                }
            }
        })
    }

    // 체크박스 클릭 시 상태변화
    private fun changeChkState() {
        val chkState = binding.cbPrice.isChecked
        if (!chkState) {
            binding.cbPrice.isChecked = false
            binding.lPrice.visibility = View.GONE
        } else {
            binding.cbPrice.isChecked = true
            binding.lPrice.visibility = View.VISIBLE
        }
    }

    // 유효성 검사
    private fun chkEffectiveness(): Boolean {
        if (binding.ivBarcodeImg.drawable == null || binding.ivCouponImg.drawable == null
            || binding.etDate.text == null
        ) {
            return false
        }
        return true
    }

    override fun onDestroy() {
        super.onDestroy()

        mainActivity.hideBottomNav(false)
        isShow = false
    }
}