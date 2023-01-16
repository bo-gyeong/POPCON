package com.ssafy.popcon.ui.add

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.MediaStore.Images
import android.provider.MediaStore.getMediaUri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import androidx.activity.result.component1
import androidx.activity.result.component2
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.loader.content.CursorLoader
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.bumptech.glide.Glide
import com.ssafy.popcon.R
import com.ssafy.popcon.databinding.FragmentAddBinding
import com.ssafy.popcon.dto.GifticonImg
import com.ssafy.popcon.ui.common.MainActivity
import com.ssafy.popcon.ui.popup.GifticonDialogFragment.Companion.isShow
import java.io.ByteArrayOutputStream
import java.io.File

class AddFragment : Fragment(), onItemClick {
    private lateinit var binding: FragmentAddBinding

    private lateinit var mainActivity: MainActivity
    private lateinit var addImgAdapter: AddImgAdapter
    val REQ_CODE_SELECT_IMAGE = 1000
    private lateinit var imgUris: ArrayList<GifticonImg>
    private val delImgIdx = ArrayList<Int>()
    var imgNum = 0

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
        binding = FragmentAddBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imgUris = ArrayList()
        openGalleryFirst()

        binding.cvProductImg.setOnClickListener {
            openGallery(imgNum)
        }

        binding.cvBarcodeImg.setOnClickListener {
            openGallery(imgNum)
        }

        binding.btnRegi.setOnClickListener {
            for (i in 0 until delImgIdx.size) {
                delCropImg(delImgIdx[i])
            }
            //유효성 검사
            findNavController().navigate(R.id.action_addFragment_to_homeFragment)
        }

        binding.btnOriginalSee.setOnClickListener {
            if (imgUris.size != 0) {
                seeOriginalImg(imgUris[imgNum].imgUri)
            }
        }
    }

    private val result =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            when (it.resultCode) {
                Activity.RESULT_OK -> {
                    val clipData = it.data!!.clipData

                    if (clipData != null) {  //첫 add
                        imgUris = ArrayList()

                        for (i in 0 until clipData.itemCount) {
                            imgUris.add(GifticonImg(clipData.getItemAt(i).uri))
                        }
                        fillContent(0)
                    } else {  //이미지 크롭
                        val extras = it.data!!.extras
                        val cropImgUri = getImgUri(requireContext(), extras!!.get("data") as Bitmap)
                        imgUris.set(imgNum, GifticonImg(cropImgUri))
                        delImgIdx.add(imgNum)
                        fillContent(imgNum)
                    }
                    makeImgList()
                }
                Activity.RESULT_CANCELED -> {
                    if (imgUris.size == 0) { // add탭 클릭 후 이미지 선택 안하고 뒤로가기 클릭 시
                        binding.root.findNavController()
                            .navigate(R.id.action_addFragment_to_homeFragment)
                    }
                }
            }
        }

    // View 값 채우기
    fun fillContent(idx: Int) {
        imgNum = idx

        Glide.with(this).load(imgUris[idx].imgUri).centerCrop().into(binding.ivCouponImg)
        Glide.with(this).load(imgUris[idx].imgUri).centerCrop().into(binding.ivBarcodeImg)
        binding.etProductName.setText("상품이름${idx}")
        binding.etProductBrand.setText("브랜드")
        binding.etDate.setText("2023..01.01")
        binding.ivCouponImgPlus.visibility = View.GONE
        binding.ivBarcodeImgPlus.visibility = View.GONE
        binding.tvRegiImgCount.text =
            String.format(resources.getString(R.string.regi_img_count), idx + 1, imgUris.size)
    }

    override fun onClick(idx: Int) {
        fillContent(idx)
    }

    // add탭 클릭하자마자 나오는 갤러리
    private fun openGalleryFirst() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.setDataAndType(Images.Media.EXTERNAL_CONTENT_URI, "image/*")
        result.launch(intent)
    }

    // cardView를 클릭했을 때 나오는 갤러리
    private fun openGallery(idx: Int) {
        val intent = Intent("com.android.camera.action.CROP")
        intent.putExtra("crop", true)
        intent.putExtra("outputX", 125)
        intent.putExtra("outputY", 170)
        intent.putExtra("scale", true)
        intent.putExtra("return-data", true)
        intent.setDataAndType(imgUris[idx].imgUri, "image/*")

        result.launch(intent)
    }

    // 이미지 절대경로 가져오기
    private fun getPath(uri: Uri): String {
        val data: Array<String> = arrayOf(Images.Media.DATA)
        val cursorLoader = CursorLoader(requireContext(), uri, data, null, null, null)
        val cursor = cursorLoader.loadInBackground()
        val idx = cursor!!.getColumnIndexOrThrow(Images.Media.DATA)
        cursor.moveToFirst()

        return cursor.getString(idx)
    }

    // bitmap to uri
    private fun getImgUri(context: Context, bitMapImg: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        bitMapImg.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = Images.Media.insertImage(context.contentResolver, bitMapImg, "PopConImg", null)
        return Uri.parse(path)
    }

    // 크롭되면서 새로 생성된 이미지 삭제
    private fun delCropImg(idx: Int) {
        val file = File(getPath(imgUris[idx].imgUri))
        file.delete()
    }

    // 상단 리사이클러뷰 만들기
    private fun makeImgList() {
        addImgAdapter = AddImgAdapter(imgUris, this)

        binding.rvCouponList.apply {
            adapter = addImgAdapter
            layoutManager = LinearLayoutManager(this.context, RecyclerView.HORIZONTAL, false)
            adapter!!.stateRestorationPolicy =
                RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        }
    }

    // 이미지 원본보기
    private fun seeOriginalImg(imgUri: Uri) {
        OriginalImgDialogFragment(imgUri).show(
            childFragmentManager, "OriginalDialog"
        )
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

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("Tkvl", "onDestroy: ")
        //parentFragmentManager.beginTransaction().remove(this).commit()
    }

    override fun onDestroy() {
        super.onDestroy()

        mainActivity.hideBottomNav(false)
        isShow = false
    }
}