package com.ssafy.popcon.ui.add

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
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.loader.content.CursorLoader
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.soundcloud.android.crop.Crop
import com.ssafy.popcon.R
import com.ssafy.popcon.databinding.FragmentAddBinding
import com.ssafy.popcon.dto.GifticonImg
import com.ssafy.popcon.ui.common.MainActivity
import com.ssafy.popcon.ui.popup.GifticonDialogFragment.Companion.isShow
import java.io.*

class AddFragment : Fragment(), onItemClick {
    private lateinit var binding: FragmentAddBinding

    private lateinit var mainActivity: MainActivity
    private lateinit var addImgAdapter: AddImgAdapter
    private lateinit var imgUris:ArrayList<GifticonImg>
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
            for (i in 0 until delImgIdx.size){
                delCropImg(delImgIdx[i])
            }
            //유효성 검사
            findNavController().navigate(R.id.action_addFragment_to_homeFragment)
        }

        binding.btnOriginalSee.setOnClickListener {
            if (imgUris.size != 0){
                seeOriginalImg(imgUris[imgNum].imgUri)
            }
        }
    }

    private val result =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { it ->
            when (it.resultCode) {
                Activity.RESULT_OK -> {
                    val clipData = it.data!!.clipData

                    if (clipData != null) {  //첫 add
                        imgUris = ArrayList()

                        for (i in 0 until clipData.itemCount){
                            imgUris.add(GifticonImg(clipData.getItemAt(i).uri))
                        }
                        fillContent(0)
                    } else{  //이미지 크롭
                        imgUris[imgNum] = GifticonImg(Crop.getOutput(it.data))

                        delImgIdx.add(imgNum)
                        fillContent(imgNum)
                    }
                    makeImgList()
                }
                Activity.RESULT_CANCELED -> {
                    if (imgUris.size == 0){ // add탭 클릭 후 이미지 선택 안하고 뒤로가기 클릭 시
                        binding.root.findNavController().navigate(R.id.action_addFragment_to_homeFragment)
                    }
                }
            }
        }

    // View 값 채우기
    private fun fillContent(idx: Int){
        imgNum = idx

        Glide.with(this).load(imgUris[idx].imgUri).centerCrop().into(binding.ivCouponImg)
        Glide.with(this).load(imgUris[idx].imgUri).centerCrop().into(binding.ivBarcodeImg)
        binding.etProductName.setText("상품이름${idx}")
        binding.etProductBrand.setText("브랜드")
        binding.etDate.setText("2023..01.01")
        binding.ivCouponImgPlus.visibility = View.GONE
        binding.ivBarcodeImgPlus.visibility = View.GONE
        binding.tvRegiImgCount.text = String.format(resources.getString(R.string.regi_img_count), idx+1 , imgUris.size)
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
        val bitmap = uriToBitmap(imgUris[idx].imgUri)
        val destination = saveFile("popconImg", bitmap)
        val crop = Crop.of(imgUris[idx].imgUri, destination)

        result.launch(crop.getIntent(mainActivity))
    }

    // 크롭한 이미지 저장
    private fun saveFile(fileName:String, bitmap: Bitmap):Uri?{
        val values = ContentValues()
        values.put(Images.Media.DISPLAY_NAME, fileName)
        values.put(Images.Media.MIME_TYPE, "image/jpeg")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(Images.Media.IS_PENDING, 1)
        }

        val uri = requireContext().contentResolver.insert(Images.Media.EXTERNAL_CONTENT_URI, values)
        if (uri != null) {
            val descriptor = requireContext().contentResolver.openFileDescriptor(uri, "w")

            if (descriptor != null) {
                val fos = FileOutputStream(descriptor.fileDescriptor)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                fos.close()
                descriptor.close()

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    values.clear()
                    values.put(Images.Media.IS_PENDING, 0)
                    requireContext().contentResolver.update(uri, values, null, null)
                }
            }
        }
        return uri
    }

    // 이미지 절대경로 가져오기
    private fun getPath(uri: Uri):String{
        val data:Array<String> = arrayOf(Images.Media.DATA)
        val cursorLoader = CursorLoader(requireContext(), uri, data, null, null, null)
        val cursor = cursorLoader.loadInBackground()!!
        val idx = cursor.getColumnIndexOrThrow(Images.Media.DATA)
        cursor.moveToFirst()

        return cursor.getString(idx)
    }

    // uri -> bitmap
    private fun uriToBitmap(uri:Uri): Bitmap{
        lateinit var bitmap:Bitmap
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
            bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(requireContext().contentResolver, uri))
        } else{
            bitmap = Images.Media.getBitmap(requireContext().contentResolver, uri)
        }

        return bitmap
    }

    // 크롭되면서 새로 생성된 이미지 삭제
    private fun delCropImg(idx: Int){
        val file = File(getPath(imgUris[idx].imgUri))
        file.delete()
    }

    // 상단 리사이클러뷰 만들기
    private fun makeImgList(){
        addImgAdapter = AddImgAdapter(imgUris, this)

        binding.rvCouponList.apply {
            adapter = addImgAdapter
            layoutManager = LinearLayoutManager(this.context, RecyclerView.HORIZONTAL, false)
            adapter!!.stateRestorationPolicy =
                RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        }
    }

    // 이미지 원본보기
    private fun seeOriginalImg(imgUri:Uri){
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