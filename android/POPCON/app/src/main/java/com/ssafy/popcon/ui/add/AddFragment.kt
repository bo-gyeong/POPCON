package com.ssafy.popcon.ui.add

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore.Images
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.ssafy.popcon.R
import com.ssafy.popcon.databinding.FragmentAddBinding
import com.ssafy.popcon.ui.common.MainActivity
import com.ssafy.popcon.ui.login.LoginFragment
import com.ssafy.popcon.util.CheckPermission

class AddFragment : Fragment() {
    private lateinit var binding: FragmentAddBinding

    lateinit var mainActivity: MainActivity
    val REQ_CODE_SELECT_IMAGE = 1000
    lateinit var imgUris:ArrayList<Uri>
    var imgNum = 1

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onStart() {
        super.onStart()
        mainActivity.hideBottomNav(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        openGalleryFirst()

        binding.btnRegi.setOnClickListener {
//            if (chkEffectiveness()){ // 유효성검사
//                mainActivity.changeFragment(0)
//            }
            mainActivity.changeFragment(0)
        }

        binding.cvProductImg.setOnClickListener {
            openGallery()
        }

        binding.cvBarcodeImg.setOnClickListener {
            openGallery()
        }

        binding.btnOriginalSee.setOnClickListener {
            // 몇 번째 이미지인지 확인하기
            if (imgUris.size != 0){
                seeOriginalImg(imgUris[imgNum-1])
            }
        }
    }
//https://rlg1133.tistory.com/74 이미지 크롭
    // https://velog.io/@jinny_0422/Android%EB%B6%88%EB%9F%AC%EC%98%A8ImageCrop%ED%95%98%EA%B8%B0
    // https://philosopher-chan.tistory.com/1258
    private val result =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            when (it.resultCode){
                Activity.RESULT_OK -> {
                    val clipData = it.data!!.clipData

                    if (clipData != null){
                        imgUris = ArrayList()

                        for (i in 0 until clipData.itemCount){
                            imgUris.add(clipData.getItemAt(i).uri)

                            Glide.with(this).load(imgUris[i]).centerCrop().into(binding.ivCouponImg)
                            Glide.with(this).load(imgUris[i]).centerCrop().into(binding.ivBarcodeImg)
                            binding.etProductName.setText("상품이름")
                            binding.etProductBrand.setText("브랜드")
                            binding.etDate.setText("2023..01.01")
                            binding.ivCouponImgPlus.visibility = View.GONE
                            binding.ivBarcodeImgPlus.visibility = View.GONE
                            binding.tvRegiCoupon.text = String.format(resources.getString(R.string.regi_coupon), imgNum , clipData.itemCount)
                        }
                    }
                }
            }
        }

    // add탭 클릭하자마자 나오는 갤러리
    private fun openGalleryFirst(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.setDataAndType(Images.Media.EXTERNAL_CONTENT_URI, "image/*")
        result.launch(intent)
    }

    // cardView를 클릭했을 때 나오는 갤러리
    private fun openGallery(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.putExtra("crop", true)
        intent.setDataAndType(Images.Media.EXTERNAL_CONTENT_URI, "image/*")
        result.launch(intent)
    }

    private fun cropImg(imgUri:Uri){
    }

    private fun seeOriginalImg(imgUri:Uri){
        OriginalImgDialogFragment(imgUri).show(
            childFragmentManager, "OriginalDialog"
        )
    }

    // 유효성 검사
    private fun chkEffectiveness():Boolean{
        if(binding.ivBarcodeImg.drawable==null || binding.ivCouponImg.drawable==null
            || binding.etDate.text==null || binding.etBarcode.text==null){
            return false
        }
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mainActivity.hideBottomNav(false)
    }
}