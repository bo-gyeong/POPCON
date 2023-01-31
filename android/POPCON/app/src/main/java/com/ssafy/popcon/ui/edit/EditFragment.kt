package com.ssafy.popcon.ui.edit

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore.Images
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.loader.content.CursorLoader
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.soundcloud.android.crop.Crop
import com.ssafy.popcon.R
import com.ssafy.popcon.config.ApplicationClass
import com.ssafy.popcon.databinding.FragmentAddBinding
import com.ssafy.popcon.databinding.FragmentEditBinding
import com.ssafy.popcon.dto.*
import com.ssafy.popcon.ui.add.AddImgAdapter
import com.ssafy.popcon.ui.add.CropImgDialogFragment
import com.ssafy.popcon.ui.add.OriginalImgDialogFragment
import com.ssafy.popcon.ui.add.onItemClick
import com.ssafy.popcon.ui.common.MainActivity
import com.ssafy.popcon.ui.common.onSingleClickListener
import com.ssafy.popcon.ui.home.HomeFragment
import com.ssafy.popcon.ui.popup.GifticonDialogFragment.Companion.isShow
import com.ssafy.popcon.ui.popup.ImageDialogFragment
import com.ssafy.popcon.viewmodel.AddViewModel
import com.ssafy.popcon.viewmodel.GifticonViewModel
import com.ssafy.popcon.viewmodel.ViewModelFactory
import okio.ByteString
import okio.ByteString.Companion.toByteString
import java.io.*
import java.nio.file.Files
import java.nio.file.Paths
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.io.path.Path

class EditFragment : Fragment() {
    private lateinit var binding: FragmentEditBinding
    private lateinit var mainActivity: MainActivity
    private lateinit var barNum: String
    private val viewModel: GifticonViewModel by viewModels { ViewModelFactory(requireContext()) }

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

        val mArgs = arguments
        barNum = mArgs!!.getString("barNum")!!

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setLayout()
        dateFormat()

        binding.cbPrice.setOnClickListener{
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

    private fun setLayout() {
        viewModel.getGifticonByBarcodeNum(barNum)
        viewModel.gifticon.observe(viewLifecycleOwner){ g ->
            val gifticon = Gifticon(
                g.barcodeNum,
                g.barcode_filepath?:"",
                Brand("", g.brandName),
                g.due,
                g.hash,
                g.price,
                g.memo?:"",
                g.origin_filepath?:"",
                g.productName,
                g.product_filepath?:"",
                g.state
            )

            binding.gifticon = gifticon

            binding.btnOriginalSee.setOnClickListener {
                openImgDialog(gifticon.origin_filepath)
            }

            binding.ivBarcodeImg.setOnClickListener {
                openImgDialog(gifticon.barcode_filepath)
            }

            binding.ivCouponImg.setOnClickListener {
                openImgDialog(gifticon.product_filepath)
            }
        }

        //수정 누르면 업데이트트
        binding.btnEdit.setOnClickListener {
            Log.d("TAG", "setLayout: ${binding.gifticon}")

            //viewModel.updateGifticon(binding.gifticon)
        }
    }

    // 이미지 팝업
    private fun openImgDialog(url: String){
        val args = Bundle()
        args.putString("url", url)

        val dialogFragment = ImageDialogFragment()
        dialogFragment.arguments = args
        dialogFragment.show(childFragmentManager, "originalUrl")
    }

    private fun dateFormat(){
        binding.etDate.addTextChangedListener (object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
                //p0: 추가된 문자열
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //p0: 현재 입력된 문자열, p1: 새로 추가될 문자열 위치, p2: 변경될 문자열의 수, p3: 새로 추가될 문자열 수
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //p0: 현재 입력된 문자열, p1: 새로 추가될 문자열 위치, p2: 삭제된 기존 문자열 수, p3: 새로 추가될 문자열 수
                val dateLength = binding.etDate.text.length
                if(dateLength==4 && p1!=4 || dateLength==7 && p1!=7){
                    val add = binding.etDate.text.toString() + "-"
                    binding.etDate.setText(add)
                    binding.etDate.setSelection(add.length)
                }
            }
        })
    }

    // 체크박스 클릭 시 상태변화
    private fun changeChkState(){
        val chkState = binding.cbPrice.isChecked
        if (!chkState){
            binding.cbPrice.isChecked = false
            binding.lPrice.visibility = View.GONE
        } else{
            binding.cbPrice.isChecked = true
            binding.lPrice.visibility = View.VISIBLE
        }
    }

    // 유효성 검사
    private fun chkEffectiveness(): Boolean {
        if (binding.ivBarcodeImg.drawable == null || binding.ivCouponImg.drawable == null
            || binding.etDate.text == null || binding.etBarcode.text == null
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