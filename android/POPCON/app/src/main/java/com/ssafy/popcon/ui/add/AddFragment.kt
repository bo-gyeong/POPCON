package com.ssafy.popcon.ui.add

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore.Images
import android.provider.OpenableColumns
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.loader.content.CursorLoader
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.soundcloud.android.crop.Crop
import com.ssafy.popcon.config.ApplicationClass
import com.ssafy.popcon.databinding.FragmentAddBinding
import com.ssafy.popcon.dto.*
import com.ssafy.popcon.ui.common.EventObserver
import com.ssafy.popcon.ui.common.MainActivity
import com.ssafy.popcon.ui.common.onSingleClickListener
import com.ssafy.popcon.ui.home.HomeFragment
import com.ssafy.popcon.ui.popup.GifticonDialogFragment.Companion.isShow
import com.ssafy.popcon.viewmodel.AddViewModel
import com.ssafy.popcon.viewmodel.ViewModelFactory
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okio.BufferedSink
import okio.ByteString.Companion.toByteString
import okio.source
import org.json.JSONObject
import java.io.*
import java.nio.file.Files
import java.nio.file.Paths
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.io.path.Path

private const val TAG = "###_AddFragment"
class AddFragment : Fragment(), onItemClick {
    private lateinit var binding: FragmentAddBinding
    private lateinit var mainActivity: MainActivity
    private val viewModel: AddViewModel by activityViewModels { ViewModelFactory(requireContext()) }

    private var delImgUris = ArrayList<Uri>()
    private var multipartFiles = ArrayList<MultipartBody.Part>()
    private var ocrResults = ArrayList<OCRResult>()
    private var fileNames = ArrayList<String>()
    private var originalImgUris = ArrayList<GifticonImg>()
    private var productImgUris = ArrayList<GifticonImg>()
    private var barcodeImgUris = ArrayList<GifticonImg>()
    private lateinit var addImgAdapter: AddImgAdapter
    val user = ApplicationClass.sharedPreferencesUtil.getUser()
    var imgNum = 0
    var clickCv = ""
    var effectivenessBrand = false
    var effectivenessBarcode = false
    var effectivenessDate = false

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
        openGalleryFirst()

        binding.cvAddCoupon.setOnClickListener {
            for (i in 0 until delImgUris.size){
                delCropImg(delImgUris[i])
            }
            delImgUris.clear()
            multipartFiles.clear()
            ocrResults.clear()
            fileNames.clear()

            openGalleryFirst()
        }

        binding.cvProductImg.setOnClickListener(object : onSingleClickListener(){
            override fun onSingleClick(v: View) {
                clickCv = PRODUCT
                seeCropImgDialog(productImgUris[imgNum], PRODUCT)
            }
        })

        binding.cvBarcodeImg.setOnClickListener(object : onSingleClickListener(){
            override fun onSingleClick(v: View) {
                clickCv = BARCODE
                seeCropImgDialog(barcodeImgUris[imgNum], BARCODE)
            }
        })

        brandChk()
        brandBarcodeNum()
        dateFormat()
        changeChkState()

        binding.btnOriginalSee.setOnClickListener {
            if (originalImgUris.size != 0){
                seeOriginalImgDialog(originalImgUris[imgNum])
            }
        }

        binding.cbPrice.setOnClickListener{
            changeChkState()
        }

        binding.btnRegi.setOnClickListener {
            if (chkClickImgCnt() && chkEffectiveness()){
                //viewModel.addGifticonImg(makeAddImgInfoList())
                //viewModel.addGifticon(makeAddInfoList())
                //mainActivity.changeFragment(HomeFragment())
                Toast.makeText(requireContext(), "통과", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val result =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { it ->
            when (it.resultCode) {
                Activity.RESULT_OK -> {
                    val clipData = it.data!!.clipData

                    if (clipData != null) {  //첫 add
                        originalImgUris = ArrayList()
                        productImgUris = ArrayList()
                        barcodeImgUris = ArrayList()

                        for (i in 0 until clipData.itemCount){
                            val originalImgUri = clipData.getItemAt(i).uri
                            originalImgUris.add(GifticonImg(originalImgUri))

                            val realData = originalImgUri.asMultipart("file", requireContext().contentResolver)
                            multipartFiles.add(realData!!)
                        }

                        viewModel.addFileToGCP(multipartFiles.toTypedArray())
                        viewModel.gcpResult.observe(viewLifecycleOwner, EventObserver{
                            for (gcpResult in it){
                                fileNames.add(gcpResult.fileName)
                            }

                            viewModel.useOcr(fileNames.toTypedArray())
                            viewModel.ocrResult.observe(viewLifecycleOwner, EventObserver{
                                for (ocrResult in it){
                                    ocrResults.add(ocrResult)
                                }

                                for (i in 0 until clipData.itemCount){
                                    val cropImgUri = cropXY(i, PRODUCT)
                                    val cropBarcodeUri = cropXY(i, BARCODE)

                                    productImgUris.add(GifticonImg(cropImgUri))
                                    barcodeImgUris.add(GifticonImg(cropBarcodeUri))
                                    delImgUris.add(cropImgUri)
                                    delImgUris.add(cropBarcodeUri)
                                }

                                fillContent(0)
                                makeImgList()
                            })
                        })
                    } else{  //수동 크롭
                        if (clickCv == PRODUCT){
                            productImgUris[imgNum] = GifticonImg(Crop.getOutput(it.data))
                            delImgUris.add(productImgUris[imgNum].imgUri)
                        } else if (clickCv == BARCODE){
                            barcodeImgUris[imgNum] = GifticonImg(Crop.getOutput(it.data))
                            delImgUris.add(barcodeImgUris[imgNum].imgUri)
                        }
                        fillContent(imgNum)
                    }
                }
                Activity.RESULT_CANCELED -> {
                    if (originalImgUris.size == 0){ // add탭 클릭 후 이미지 선택 안하고 뒤로가기 클릭 시
                        mainActivity.changeFragment(HomeFragment())
                    }
                }
            }
        }

    // uri to multipart
    @SuppressLint("Range")
    private fun Uri.asMultipart(name: String, contentResolver: ContentResolver): MultipartBody.Part?{
        return contentResolver.query(this, null, null, null, null)?.let {
            if (it.moveToNext()){
                val displayName = it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                val requestBody = object : RequestBody(){
                    override fun contentType(): MediaType? {
                        return contentResolver.getType(this@asMultipart)?.toMediaType()
                    }

                    @SuppressLint("Recycle")
                    override fun writeTo(sink: BufferedSink) {
                        sink.writeAll(contentResolver.openInputStream(this@asMultipart)?.source()!!)
                    }
                }
                it.close()
                MultipartBody.Part.createFormData(name, displayName, requestBody)
            } else{
                it.close()
                null
            }
        }
    }

    // ocr결과 null체크
    private fun ocrResultNullChk(value: String?): String{
        if (value == null){
            return ""
        }
        return value
    }

    // View 값 채우기
    private fun fillContent(idx: Int){
        imgNum = idx

        binding.addInfo = AddInfo(
            originalImgUris[idx].imgUri,
            productImgUris[idx].imgUri,
            barcodeImgUris[idx].imgUri,
            ocrResultNullChk(ocrResults[idx].barcodeNum),
            ocrResultNullChk(ocrResults[idx].brandName),
            ocrResultNullChk(ocrResults[idx].productName),
            jsonParsingDate(ocrResults[idx].due),
            user.email!!,
            user.social
        )

        binding.cbPrice.isChecked
        binding.lPrice.visibility = View.GONE
        if (ocrResults[idx].isVoucher == 1){
            binding.cbPrice.isChecked = true
            binding.lPrice.visibility = View.VISIBLE
        }

        binding.ivCouponImgPlus.visibility = View.GONE
        binding.ivBarcodeImgPlus.visibility = View.GONE
    }

    override fun onClick(idx: Int) {
        fillContent(idx)
    }

    // ocrResult 날짜 조합
    private fun jsonParsingDate(value: Map<String, String>?): String {
        if (value == null){
            return ""
        }

        val jsonObject = JsonParser.parseString(value.toString()).asJsonObject
        val result = Gson().fromJson(jsonObject, OCRResultDate::class.java)
        return "${result.Y}-${result.M}-${result.D}"
    }

    // ocrResult 이미지 좌표 split
    private fun jsonParsingCoordinate(value: Map<String, String>?): OCRResultCoordinate{
        if(value == null){
            return OCRResultCoordinate("0", "0", "0", "0", "0", "0", "0", "0")
        }

        val jsonObject = JsonParser.parseString(value.toString()).asJsonObject
        return Gson().fromJson(jsonObject, OCRResultCoordinate::class.java)
    }

    // 좌표로 이미지 크롭
    private fun cropXY(idx: Int, type:String): Uri{
        val fileName: String
        val coordinate: OCRResultCoordinate
        if (type == PRODUCT){
            fileName = "popconImg${PRODUCT}"
            coordinate = jsonParsingCoordinate(ocrResults[idx].productImg)
        } else{
            fileName = "popconImg${BARCODE}"
            coordinate = jsonParsingCoordinate(ocrResults[idx].barcodeImg)
        }

        val x1 = coordinate.x1.toInt()
        val y1 = coordinate.y1.toInt()
        val x4 = coordinate.x4.toInt()
        val y4 = coordinate.y4.toInt()

        val bitmap = uriToBitmap(originalImgUris[idx].imgUri)
        var newBitmap = Bitmap.createBitmap(bitmap, 0, 0, 100, 100)
        if (x1 == 0 && x4 == 0){
            return saveFile(fileName + System.currentTimeMillis(), newBitmap)!!
        }
        newBitmap = Bitmap.createBitmap(bitmap, x1, y1, (x4-x1), (y4-y1))
        return saveFile(fileName + System.currentTimeMillis(), newBitmap)!!
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
        val bitmap = uriToBitmap(originalImgUris[idx].imgUri)
        var destination:Uri? = "".toUri()
        if (fromCv == PRODUCT){
            destination = saveFile("popconImgProduct", bitmap)
        } else if (fromCv == BARCODE){
            destination = saveFile("popconImgBarcode", bitmap)
        }
        val crop = Crop.of(originalImgUris[idx].imgUri, destination)

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
    fun delCropImg(delImgUri: Uri){
        val file = File(getPath(delImgUri))
        file.delete()
    }

    // 상단 리사이클러뷰 만들기
    private fun makeImgList(){
        addImgAdapter = AddImgAdapter(originalImgUris, productImgUris, barcodeImgUris, this)

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

    private fun makeAddImgInfoList(): Array<AddImgInfo>{
        val imgInfo = mutableListOf<AddImgInfo>()
        for (i in 0 until originalImgUris.size){
            val productData = productImgUris[i].imgUri.asMultipart("file", requireContext().contentResolver)!!
            val barcodeData = barcodeImgUris[i].imgUri.asMultipart("file", requireContext().contentResolver)!!

            imgInfo.add(
                AddImgInfo(
                    arrayOf(productData, barcodeData),
                    binding.etBarcode.text.toString(),
                    fileNames[i]
                )
            )
        }
        return imgInfo.toTypedArray()
    }

    private fun makeAddInfoList(): MutableList<AddInfoNoImg>{
        val addInfo = mutableListOf<AddInfoNoImg>()
        for (i in 0 until originalImgUris.size){
            addInfo.add(
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
        return addInfo
    }

    // 브랜드 존재여부 검사
    private fun brandChk(){
        binding.etProductBrand.addTextChangedListener (object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
                viewModel.chkBrand(p0.toString())
                viewModel.brandChk.observe(viewLifecycleOwner, EventObserver{
                    if (it.result == 0){
                        binding.tilProductBrand.error = "올바른 브랜드를 입력해주세요"
                        effectivenessBrand = false
                    } else{
                        binding.tilProductBrand.error = null
                        binding.tilProductBrand.isErrorEnabled = false
                        effectivenessBrand = true
                    }
                })
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })
    }

    // 바코드 번호 중복 검사
    private fun brandBarcodeNum(){
        binding.tilBarcode.error = "바코드 번호를 입력해주세요"
        effectivenessBarcode = false

        binding.etBarcode.addTextChangedListener (object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
                viewModel.chkBarcode(p0.toString())
                viewModel.barcodeChk.observe(viewLifecycleOwner, EventObserver{
                    if (it.result == 0){
                        binding.tilBarcode.error = "이미 등록된 바코드 번호입니다"
                        effectivenessBarcode = false
                    } else{
                        binding.tilBarcode.error = null
                        binding.tilBarcode.isErrorEnabled = false
                        effectivenessBarcode = true
                    }
                })
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })
    }

    // 유효기간 검사
    val dateArr = arrayOf(31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
    private fun dateFormat(){
        binding.etDate.addTextChangedListener (object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
                val dateLength = binding.etDate.text!!.length
                val nowText = p0.toString()

                when (dateLength){
                    10 -> {
                        val newYear = nowText.substring(0, 4).toInt()
                        val newMonth = nowText.substring(5, 7).toInt()
                        val newDay = nowText.substring(8).toInt()

                        val nowYear = SimpleDateFormat("yyyy", Locale.getDefault()).format(System.currentTimeMillis()).toInt()
                        val nowDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(System.currentTimeMillis())
                        val nowDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(nowDateFormat)
                        val newDate =  SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(p0.toString())
                        val calDate = newDate!!.compareTo(nowDate)
                        effectivenessDate = false

                        if (newYear < nowYear || newYear > 2100 || newYear.toString().length < 4){
                            binding.tilDate.error = "정확한 날짜를 입력해주세요"
                        } else if(newMonth < 1 || newMonth > 12){
                            binding.tilDate.error = "정확한 날짜를 입력해주세요"
                        } else if(newDay > dateArr[newMonth-1] || newDay == 0){
                            binding.tilDate.error = "정확한 날짜를 입력해주세요"
                        } else if (calDate < 0){
                            binding.tilDate.error = "이미 지난 날짜입니다"
                        } else{
                            binding.tilDate.error = null
                            binding.tilDate.isErrorEnabled = false
                            effectivenessDate = true
                        }
                    }
                    else -> {
                        binding.tilDate.error = "정확한 날짜를 입력해주세요"
                        effectivenessDate = false
                    }
                }

                if (dateLength < 10){
                    binding.tilDate.error = "정확한 날짜를 입력해주세요"
                    effectivenessDate = false
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //p0: 현재 입력된 문자열, p1: 새로 추가될 문자열 위치, p2: 변경될 문자열의 수, p3: 새로 추가될 문자열 수
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //p0: 현재 입력된 문자열, p1: 새로 추가될 문자열 위치, p2: 삭제된 기존 문자열 수, p3: 새로 추가될 문자열 수
                val dateLength = binding.etDate.text!!.length
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

    // 리사이클러뷰의 기프티콘 이미지 모두 클릭했는지 확인
    private fun chkClickImgCnt(): Boolean{
        if (chkCnt >= originalImgUris.size){
            return true
        }
        Toast.makeText(requireContext(), "등록한 기프티콘을 확인해주세요", Toast.LENGTH_SHORT).show()
        return false
    }

    // 유효성 검사
    private fun chkEffectiveness(): Boolean {
        if (binding.ivBarcodeImg.drawable == null || binding.ivCouponImg.drawable == null
            || binding.etProductName.text.toString() == ""
            || !effectivenessBrand || binding.etProductBrand.text.toString() ==""
            || !effectivenessBarcode || binding.etBarcode.text.toString() ==""
            || !effectivenessDate || binding.etDate.text.toString() ==""
            || binding.cbPrice.isChecked && binding.etPrice.text.toString() == ""
            || binding.cbPrice.isChecked && binding.etPrice.text.toString() == "0"
        ) {
            Toast.makeText(requireContext(), "입력 정보를 확인해주세요", Toast.LENGTH_SHORT).show()
            return false
        }

        for (i in 0 until delImgUris.size){
            delCropImg(delImgUris[i])
        }
        return true
    }

    override fun onDestroy() {
        super.onDestroy()

        mainActivity.hideBottomNav(false)
        isShow = false
    }
}