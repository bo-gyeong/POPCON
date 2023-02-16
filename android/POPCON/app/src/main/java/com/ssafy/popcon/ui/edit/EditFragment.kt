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
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
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
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "EditFragment"

class EditFragment : Fragment() {
    private lateinit var binding: FragmentEditBinding
    private lateinit var mainActivity: MainActivity
    private lateinit var barNum: String
    private lateinit var gifticon: Gifticon
    private var gifticonInfo = AddInfo()
    private var gifticonEffectiveness = AddInfoNoImgBoolean()

    private val editViewModel: EditViewModel by activityViewModels { ViewModelFactory(requireContext()) }
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

        binding.cbPrice.setOnClickListener {
            clickChkState()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setLayout(view: View) {
        //수정 누르면 업데이트
        binding.btnEdit.setOnClickListener {
            if (chkEffectiveness()) {
                Log.d("TAG", "setLayout: ${binding.gifticon}")
                mainActivity.changeFragment(HomeFragment())

                PopconSnackBar.make(view, "수정이 완료되었어요").show()

                val req = setGifticon()
                viewModel.updateGifticon(req, SharedPreferencesUtil(requireContext()).getUser())
            }
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
            productChk()
            dateFormat()
            changeChkState()
            setPrice()
            setMemo()

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
        //gifticon.isVoucher = gifticonInfo.isVoucher
        if (binding.etPrice.text.toString() == "" || !binding.cbPrice.isChecked) {
            gifticon.price = -1
        } else {
            gifticon.price = binding.etPrice.text.toString().toInt()
        }
        gifticon.state = Utils.calState(gifticon)

        return UpdateRequest(
            gifticon.barcodeNum,
            gifticon.brand!!.brandName,
            gifticon.due,
            gifticon.memo,
            //gifticon.isVoucher,
            gifticon.price ?: -1,
            gifticon.productName,
            SharedPreferencesUtil(requireContext()).getUser().email!!,
            SharedPreferencesUtil(requireContext()).getUser().social,
            Utils.calState(gifticon)
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

    // 상품명 리스트에 저장
    private fun productChk() {
        var changeProduct = false

        binding.etProductName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                val pLength = p0.toString().length
                if (pLength < 1) {
                    binding.tilProductName.error = "상품명을 입력해주세요"
                    gifticonEffectiveness.productName = false
                } else {
                    binding.tilProductName.error = null
                    binding.tilProductName.isErrorEnabled = false

                    gifticonEffectiveness.productName = true
                    gifticonInfo.productName = binding.etProductName.text.toString()
                }
                changeProduct = true
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })

        if (!changeProduct) {
            var product = ""
            if (gifticonInfo.productName != "") {
                product = gifticonInfo.productName
            }

            if (product != "") {
                gifticonEffectiveness.productName = true
            } else {
                binding.tilProductName.error = "상품명을 입력해주세요"
            }
            changeProduct = false
        }
    }

    // 유효기간 검사
    val dateArr = arrayOf(31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
    private fun dateFormat() {
        var changDate = false

        binding.etDate.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                val dateLength = binding.etDate.text!!.length
                val nowText = p0.toString()

                when (dateLength) {
                    10 -> {
                        val newYear = nowText.substring(0, 4).toInt()
                        val newMonth = nowText.substring(5, 7).toInt()
                        val newDay = nowText.substring(8).toInt()

                        val nowYear = SimpleDateFormat(
                            "yyyy",
                            Locale.getDefault()
                        ).format(System.currentTimeMillis()).toInt()
                        val nowDateFormat = SimpleDateFormat(
                            "yyyy-MM-dd",
                            Locale.getDefault()
                        ).format(System.currentTimeMillis())
                        val nowDate =
                            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(nowDateFormat)
                        var newDate = Date()
                        try {
                            newDate = SimpleDateFormat(
                                "yyyy-MM-dd",
                                Locale.getDefault()
                            ).parse(p0.toString())!!
                        } catch (e: java.lang.Exception) {
                            newDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(
                                nowDateFormat
                            )!!
                        }

                        val calDate = newDate.compareTo(nowDate)
                        gifticonEffectiveness.due = false

                        if (newYear > 2100 || newYear.toString().length < 4) {
                            binding.tilDate.error = "정확한 날짜를 입력해주세요"
                        } else if (newMonth < 1 || newMonth > 12) {
                            binding.tilDate.error = "정확한 날짜를 입력해주세요"
                        } else if (newDay > dateArr[newMonth - 1] || newDay == 0) {
                            binding.tilDate.error = "정확한 날짜를 입력해주세요"
                        } else if (calDate < 0) {
                            binding.tilDate.error = "이미 지난 날짜입니다"
                        } else {
                            binding.tilDate.error = null
                            binding.tilDate.isErrorEnabled = false
                            gifticonEffectiveness.due = true
                            gifticonInfo.due = nowText
                        }
                    }
                    else -> {
                        binding.tilDate.error = "정확한 날짜를 입력해주세요"
                        gifticonEffectiveness.due = false
                    }
                }

                if (dateLength < 10) {
                    binding.tilDate.error = "정확한 날짜를 입력해주세요"
                    gifticonEffectiveness.due = false
                }
                changDate = true
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //p0: 현재 입력된 문자열, p1: 새로 추가될 문자열 위치, p2: 변경될 문자열의 수, p3: 새로 추가될 문자열 수
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //p0: 현재 입력된 문자열, p1: 새로 추가될 문자열 위치, p2: 삭제된 기존 문자열 수, p3: 새로 추가될 문자열 수
                val dateLength = binding.etDate.text!!.length
                if (dateLength == 4 && p1 != 4 || dateLength == 7 && p1 != 7) {
                    val add = binding.etDate.text.toString() + "-"
                    binding.etDate.setText(add)
                    binding.etDate.setSelection(add.length)
                }
            }
        })

        if (!changDate) {
            var date = ""
            if (gifticonInfo.due != "") {
                date = gifticonInfo.due
            }

            if (date != "") {
                gifticonEffectiveness.due = true
            } else {
                binding.tilDate.error = "정확한 날짜를 입력해주세요"
            }
            changDate = false
        }
    }

    // 체크박스 클릭 시 상태변화
    private fun clickChkState() {
        val chkState = binding.cbPrice.isChecked
        if (!chkState) {
            gifticonInfo.isVoucher = 0
            gifticonInfo.price = -1
            binding.cbPrice.isChecked = false
            binding.lPrice.visibility = View.GONE
            gifticonEffectiveness.isVoucher = false
        } else {
            gifticonInfo.isVoucher = 1
            binding.cbPrice.isChecked = true
            binding.lPrice.visibility = View.VISIBLE
            gifticonEffectiveness.isVoucher = true
        }
    }

    // 체크박스 상태에 따른 변화
    private fun changeChkState() {
        val voucherChk = gifticonInfo.isVoucher
        if (voucherChk != 1) {
            binding.cbPrice.isChecked = false
            binding.lPrice.visibility = View.GONE
            gifticonEffectiveness.isVoucher = false
        } else {
            binding.cbPrice.isChecked = true
            binding.lPrice.visibility = View.VISIBLE
            gifticonEffectiveness.isVoucher = true
        }
    }

    // price를 리스트에 저장
    private fun setPrice() {
        var changePrice = false

        binding.etPrice.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                val pLength = p0.toString().length
                if (pLength > 2) {  //100원대부터
                    binding.tilPrice.error = null
                    binding.tilPrice.isErrorEnabled = false

                    gifticonEffectiveness.price = true
                    gifticonInfo.price = binding.etPrice.text.toString().toInt()
                } else {
                    binding.tilPrice.error = "금액을 입력해주세요"

                    gifticonEffectiveness.price = false
                    gifticonInfo.price = -1
                }
                changePrice = true
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })

        if (!changePrice) {
            var price = ""
            if (gifticonInfo.price != -1) {
                price = gifticonInfo.price.toString()
            }

            if (price != "" && price.length > 2) {
                gifticonEffectiveness.price = true
            } else {
                binding.tilPrice.error = "금액을 입력해주세요"
            }
            changePrice = false
        }
    }

    // memo를 리스트에 저장
    private fun setMemo() {
        binding.etWriteMemo.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                gifticonInfo.memo = binding.etWriteMemo.text.toString()
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })
    }

    // 기프티콘 정보담긴 리스트 내용 검사
    private fun chkAllList(): Boolean {
        val gifticon = gifticonEffectiveness

        if (!gifticon.productName || !gifticon.due) {
            Log.d(
                TAG, "chkAllList111: ${gifticon.productName}\n " +
                        "${gifticon.due}\n"
            )
            return false
        }
        if (gifticon.isVoucher && !gifticon.price) {
            Log.d(TAG, "chkAllList222: ${gifticon.isVoucher}\n ${gifticon.price}\n")
            return false
        }

        return true
    }

    // 유효성 검사
    private fun chkEffectiveness(): Boolean {
        if (binding.ivBarcodeImg.drawable == null
            || binding.ivCouponImg.drawable == null
            || !chkAllList()
        ) {
            Toast.makeText(requireContext(), "입력 정보를 확인해주세요", Toast.LENGTH_SHORT).show()
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