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
import com.ssafy.popcon.dto.AddInfo
import com.ssafy.popcon.dto.AddInfoNoImg
import com.ssafy.popcon.dto.GifticonImg
import com.ssafy.popcon.ui.common.MainActivity
import com.ssafy.popcon.ui.common.onSingleClickListener
import com.ssafy.popcon.ui.home.HomeFragment
import com.ssafy.popcon.ui.popup.GifticonDialogFragment.Companion.isShow
import com.ssafy.popcon.viewmodel.AddViewModel
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

class AddFragment : Fragment(), onItemClick {
    private lateinit var binding: FragmentAddBinding
    private val viewModel:AddViewModel by activityViewModels { ViewModelFactory(requireContext()) }

    private lateinit var mainActivity: MainActivity
    private lateinit var addImgAdapter: AddImgAdapter
    private lateinit var OriginalImgUris:ArrayList<GifticonImg>
    private lateinit var cropXyImgUris:ArrayList<GifticonImg>
    private lateinit var barcodeImgUris:ArrayList<GifticonImg>
    private val delImgUri = ArrayList<Uri>()
    val user = ApplicationClass.sharedPreferencesUtil.getUser()
    var imgNum = 0
    val PRODUCT = "Product"
    val BARCODE = "Barcode"

    companion object{
        var chkCnt = 1
    }

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
        binding.addInfo = AddInfo()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        chkCnt = 1
        OriginalImgUris = ArrayList()
        cropXyImgUris = ArrayList()
        barcodeImgUris = ArrayList()
        openGalleryFirst()

        binding.cvProductImg.setOnClickListener(object : onSingleClickListener(){
            override fun onSingleClick(v: View) {
                seeCropImgDialog(cropXyImgUris[imgNum], PRODUCT)
            }
        })

        binding.cvBarcodeImg.setOnClickListener(object : onSingleClickListener(){
            override fun onSingleClick(v: View) {
                seeCropImgDialog(barcodeImgUris[imgNum], BARCODE)
            }
        })

        dateFormat()

        binding.cbPrice.setOnClickListener{
            changeChkState()
        }

        binding.btnRegi.setOnClickListener {
            //유효성 검사
            if (chkCnt >= OriginalImgUris.size){
                for (i in 0 until delImgUri.size){
                    delCropImg(delImgUri[i])
                }

                viewModel.addGifticon(makeAddInfoList())
                mainActivity.changeFragment(HomeFragment())
            } else{
                Toast.makeText(requireContext(), "기프티콘 정보를 확인해주세요", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnOriginalSee.setOnClickListener {
            if (OriginalImgUris.size != 0){
                seeOriginalImgDialog(OriginalImgUris[imgNum])
            }
        }
    }

    private val result =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { it ->
            when (it.resultCode) {
                Activity.RESULT_OK -> {
                    val clipData = it.data!!.clipData

                    if (clipData != null) {  //첫 add
                        OriginalImgUris = ArrayList()
                        cropXyImgUris = ArrayList()
                        barcodeImgUris = ArrayList()

                        for (i in 0 until clipData.itemCount){
                            OriginalImgUris.add(GifticonImg(clipData.getItemAt(i).uri))
                            //viewModel.useOcr(getPath(clipData.getItemAt(i).uri))
                            //clipData.getItemAt(i).uri.path.toString()  --> \external\images\media\30
                            // getPath(clipData.getItemAt(i).uri) --> \storage\emulated\0\Download\media_0(3).jpg
                            val cropXYImgUri = cropXY(i)
                            val cropXYBarcodeUri = cropXYBar(i)
                            cropXyImgUris.add(GifticonImg(cropXYImgUri))
                            barcodeImgUris.add(GifticonImg(cropXYBarcodeUri))
                            delImgUri.add(cropXYImgUri)
                            delImgUri.add(cropXYBarcodeUri)
                        }
                        //viewModel.useOcr("https://cloud.google.com/vision/docs/images/bicycle_example.png")
                        //viewModel.useOcr("C:\\1.PNG")
                        //viewModel.useOcr("file:\\storage\\emulated\\0\\Download\\media_0(3).jpg")
                        fillContent(0)
                        makeImgList()
                    } else{  //이미지 크롭
                        val imgName = File(getPath(cropXyImgUris[imgNum].imgUri)).name
                        Log.d("###", "${imgName}")  //바코드 넘버여서 안됨,,,,,,,,,,,
                        if (imgName.contains(PRODUCT)){
                            cropXyImgUris[imgNum] = GifticonImg(Crop.getOutput(it.data))
                            delImgUri.add(cropXyImgUris[imgNum].imgUri)
                        } else if (imgName.contains(BARCODE)){
                            barcodeImgUris[imgNum] = GifticonImg(Crop.getOutput(it.data))
                            delImgUri.add(barcodeImgUris[imgNum].imgUri)
                        }
                        fillContent(imgNum)
                    }
                }
                Activity.RESULT_CANCELED -> {
                    if (OriginalImgUris.size == 0){ // add탭 클릭 후 이미지 선택 안하고 뒤로가기 클릭 시
                        mainActivity.changeFragment(HomeFragment())
                    }
                }
            }
        }

    // View 값 채우기
    private fun fillContent(idx: Int){
        imgNum = idx

        binding.addInfo = AddInfo(
            OriginalImgUris[idx].imgUri,
            cropXyImgUris[idx].imgUri,
            barcodeImgUris[idx].imgUri,
            "${idx}-1231-2345~~~",
            "브랜드${idx}",
            "상품이름${idx}",
            binding.etDate.text.toString(),
            user.email!!,
            user.social
        )
        binding.ivCouponImgPlus.visibility = View.GONE
        binding.ivBarcodeImgPlus.visibility = View.GONE
        binding.tvRegiImgCount.text = String.format(resources.getString(R.string.regi_img_count), idx+1 , OriginalImgUris.size)
    }

    override fun onClick(idx: Int) {
        fillContent(idx)
    }

    private fun cropXY(idx: Int): Uri{
        val bitmap = uriToBitmap(OriginalImgUris[idx].imgUri)
        val newBitmap = Bitmap.createBitmap(bitmap, 35, 35, 150, 150)
        return saveFile("popconImg${PRODUCT}"+System.currentTimeMillis(), newBitmap)!!
    }

    private fun cropXYBar(idx: Int): Uri{
        val bitmap = uriToBitmap(OriginalImgUris[idx].imgUri)
        val newBitmap = Bitmap.createBitmap(bitmap, 10, 270, 400, 100)
        return saveFile("popconImg${BARCODE}"+System.currentTimeMillis(), newBitmap)!!
    }

    // add탭 클릭하자마자 나오는 갤러리
    private fun openGalleryFirst() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true) //Intent.EXTRA_ALLOW_MULTIPLE
        intent.setDataAndType(Images.Media.EXTERNAL_CONTENT_URI, "image/*")
        result.launch(intent)
    }

    // cardView를 클릭했을 때 나오는 갤러리
    fun openGallery(idx: Int, fromCv: String) {
        val bitmap = uriToBitmap(OriginalImgUris[idx].imgUri)
        var destination:Uri? = "".toUri()
        Log.d("###open", "openGallery: $fromCv")
        if (fromCv == PRODUCT){
            destination = saveFile("popconImgProduct", bitmap)
        } else if (fromCv == BARCODE){
            destination = saveFile("popconImgBarcode", bitmap)
        }
        val crop = Crop.of(OriginalImgUris[idx].imgUri, destination)

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
    private fun delCropImg(delImgUri: Uri){
        val file = File(getPath(delImgUri))
        file.delete()
    }

    // 상단 리사이클러뷰 만들기
    private fun makeImgList(){
        addImgAdapter = AddImgAdapter(OriginalImgUris, cropXyImgUris, barcodeImgUris, this)

        binding.rvCouponList.apply {
            adapter = addImgAdapter
            layoutManager = LinearLayoutManager(this.context, RecyclerView.HORIZONTAL, false)
            adapter!!.stateRestorationPolicy =
                RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        }
    }

    // 크롭된 이미지 다이얼로그
    private fun seeCropImgDialog(gifticonImg: GifticonImg, clickFromCv:String){
        val dialog = CropImgDialogFragment(gifticonImg, clickFromCv)
        dialog.show(childFragmentManager, "CropDialog")
        dialog.setOnClickListener(object: CropImgDialogFragment.BtnClickListener{
            override fun onClicked(fromCv: String) {
                if (fromCv == PRODUCT){
                    openGallery(imgNum, PRODUCT)
                } else if (fromCv == BARCODE){
                    openGallery(imgNum, BARCODE)
                }
            }
        })
    }

    // 이미지 원본보기
    private fun seeOriginalImgDialog(gifticonImg: GifticonImg){
        OriginalImgDialogFragment(gifticonImg).show(
            childFragmentManager, "OriginalDialog"
        )
    }

    private fun makeAddInfoList(): MutableList<AddInfoNoImg>{
        val addInfos = mutableListOf<AddInfoNoImg>()
        for (i in 0 until OriginalImgUris.size){
            addInfos.add(
                AddInfoNoImg(
                    binding.etBarcode.text.toString(),
                    binding.etProductBrand.text.toString(),
                    binding.etProductName.text.toString(),
                    binding.etDate.text.toString(),
                    user.email!!,
                    user.social
                )
            )
        }
        return addInfos
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