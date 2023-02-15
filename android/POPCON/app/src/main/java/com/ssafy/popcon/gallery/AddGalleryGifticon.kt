package com.ssafy.popcon.gallery

import android.annotation.SuppressLint
import android.content.*
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.loader.content.CursorLoader
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.ssafy.popcon.config.ApplicationClass
import com.ssafy.popcon.databinding.DialogProgressBinding
import com.ssafy.popcon.dto.*
import com.ssafy.popcon.repository.add.AddRemoteDataSource
import com.ssafy.popcon.repository.add.AddRepository
import com.ssafy.popcon.ui.add.ProgressDialog
import com.ssafy.popcon.ui.common.MainActivity
import com.ssafy.popcon.ui.home.HomeFragment
import com.ssafy.popcon.util.RetrofitUtil
import com.ssafy.popcon.util.SharedPreferencesUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okio.BufferedSink
import okio.source
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

private const val TAG = "AddGalleryGifticon"
class AddGalleryGifticon(
    private val mainActivity: MainActivity,
    private val mContext: Context,
    private val _contentResolver: ContentResolver
): Fragment() {
    private val sp = SharedPreferencesUtil(mContext)
    private val newImgUri = mutableListOf<Uri>()
    private lateinit var binding: DialogProgressBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogProgressBinding.inflate(layoutInflater, container, false)

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getImgList()
    }

    // 갤러리에 저장된 이미지 목록 받아오기
    @RequiresApi(Build.VERSION_CODES.Q)
    fun getImgList(){
        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Images.ImageColumns._ID,
            MediaStore.MediaColumns.DATA,
            MediaStore.MediaColumns.DATE_TAKEN
        )

        val cursor = _contentResolver.query(
            uri, projection, null, null, MediaStore.MediaColumns._ID + " desc"
        )!!
        val columnId = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns._ID)
        val columnIdx = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
        val dateTAKEN = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_TAKEN)

        val galleryInfo = sp.getLatelyGalleryInfo()
        var spImgCnt = galleryInfo.imgCnt
        while (cursor.moveToNext()){
            val absolutePath = cursor.getString(columnIdx)
            val date = cursor.getLong(dateTAKEN)
            val imgUri = ContentUris.withAppendedId(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cursor.getLong(columnId)
            )

            val calendar = Calendar.getInstance()
            calendar.timeInMillis = date

            if (!TextUtils.isEmpty(absolutePath)){
                calendar.timeInMillis = galleryInfo.date
                Log.d(TAG, "getImgList: ${spImgCnt}   ${cursor.count}")

                if (cursor.isFirst){
                    newImgUri.add(imgUri)
                    addImg()
                    break
                }
            }
        }
        cursor.close()
    }

    private fun addImg(){
        initData()
        for (i in 0 until newImgUri.size){
            if (!getFileSize(newImgUri[i])){
                continue
            }
            originalImgUris.add(GifticonImg(newImgUri[i]))
            gifticonEffectiveness.add(AddInfoNoImgBoolean())
        }

        if(originalImgUris.size < 1){
            Toast.makeText(requireContext(), "잘못된 이미지 입니다", Toast.LENGTH_SHORT).show()
            return
        }
        firstAdd()

        changeProgressDialogState(true)
    }

    private var delImgUris = ArrayList<Uri>()
    private var multipartFiles = ArrayList<MultipartBody.Part>()
    private var ocrResults = ArrayList<OCRResult>()
    private var ocrSendList = ArrayList<OCRSend>()
    private var originalImgUris = ArrayList<GifticonImg>()
    private var productImgUris = ArrayList<GifticonImg>()
    private var barcodeImgUris = ArrayList<GifticonImg>()
    private var gifticonInfoList = ArrayList<AddInfo>()
    private var gifticonEffectiveness = ArrayList<AddInfoNoImgBoolean>()
    private val loadingDialog = ProgressDialog(false)
    private val repo = AddRepository(AddRemoteDataSource(RetrofitUtil.addService))
    val user = ApplicationClass.sharedPreferencesUtil.getUser()
    var imgNum = 0

    val PRODUCT = "Product"
    val BARCODE = "Barcode"

    private fun initData(){
        originalImgUris.clear()
        productImgUris.clear()
        barcodeImgUris.clear()
        ocrSendList.clear()
        ocrResults.clear()
        delImgUris.clear()
        multipartFiles.clear()
        gifticonInfoList.clear()
        gifticonEffectiveness.clear()
    }

    private fun firstAdd(){
        for (i in 0 until originalImgUris.size){
            val originalImgUri = originalImgUris[i].imgUri
            delImgUris.add(originalImgUri)

            val realData = originalImgUri.asMultipart("file", mContext.contentResolver)
            multipartFiles.add(realData!!)
        }

        CoroutineScope(Dispatchers.IO).launch {
            val gcpResponse = repo.addFileToGCP(multipartFiles.toTypedArray())
            launch {
                for (i in 0 until gcpResponse.size){
                    val gcpResult = gcpResponse[i]
                    val originalImgBitmap = uriToBitmap(originalImgUris[i].imgUri)

                    ocrSendList.add(
                        OCRSend(
                            gcpResult.fileName, originalImgBitmap.width, originalImgBitmap.height
                        )
                    )
                }

                val ocrResponse = repo.useOcr(ocrSendList.toTypedArray())
                if (ocrResponse.isEmpty()){
                    notifyFail()
                    return@launch
                }
                launch {
                    for (ocrResult in ocrResponse){
                        ocrResults.add(ocrResult)

                        if (ocrResult.barcodeNum != ""){
                            for (i in 0 until ocrResponse.size){
                                val cropImgUri = cropXY(i, PRODUCT)
                                val cropBarcodeUri = cropXY(i, BARCODE)

                                productImgUris.add(GifticonImg(cropImgUri))
                                barcodeImgUris.add(GifticonImg(cropBarcodeUri))
                                delImgUris.add(cropImgUri)
                                delImgUris.add(cropBarcodeUri)

                                addGifticonInfo(i)

                                imgNum = i
                                productChk()
                                brandChk()
                                dateFormat()
                                changeChkState(i)
                                setPrice()
                            }
                        }
                        else{
                            notifyFail()
                        }
                    }
                }.join()
            }.join()
        }
    }

    // get img size
    private fun getFileSize(imgUri: Uri): Boolean{
        val path = getPath(imgUri)
        if (path == ""){
            return false
        }

        val file = File(path)
        val fileSize = Integer.parseInt((file.length()).toString())
        if(fileSize > 1040000){
            return false
        }
        return true
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

    // 상태에 따라 로딩화면 만들기/없애기
    private fun changeProgressDialogState(state: Boolean){
        if (state){
            loadingDialog.show(mainActivity.supportFragmentManager, null)
        } else{
            loadingDialog.dismiss()
        }
    }

    private fun addGifticonInfo(idx: Int){
        var price = ocrResults[idx].price
        if (price == -1){
            price = 0
        }

        val addInfo = AddInfo(
            originalImgUris[idx].imgUri,
            productImgUris[idx].imgUri,
            barcodeImgUris[idx].imgUri,
            ocrResultNullChk(ocrResults[idx].barcodeNum),
            ocrResultNullChk(ocrResults[idx].brandName),
            ocrResultNullChk(ocrResults[idx].productName),
            jsonParsingDate(ocrResults[idx].due),
            ocrResults[idx].isVoucher,
            price,
            "",
            user.email!!,
            user.social
        )
        gifticonInfoList.add(addInfo)
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
    private fun jsonParsingCoordinate(value: Map<String, String>?): OCRResultCoordinate {
        if(value == null){
            return OCRResultCoordinate("0", "0", "0", "0", "0", "0", "0", "0")
        }

        val jsonObject = JsonParser.parseString(value.toString()).asJsonObject
        return Gson().fromJson(jsonObject, OCRResultCoordinate::class.java)
    }

    // 좌표로 이미지 크롭
    private fun cropXY(idx: Int, type:String): Uri {
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

    // 크롭한 이미지 저장
    private fun saveFile(fileName:String, bitmap: Bitmap): Uri?{
        val values = ContentValues()
        values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.Images.Media.IS_PENDING, 1)
        }

        val uri = mContext.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        if (uri != null) {
            val descriptor = mContext.contentResolver.openFileDescriptor(uri, "w")

            if (descriptor != null) {
                val fos = FileOutputStream(descriptor.fileDescriptor)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                fos.close()
                descriptor.close()

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    values.clear()
                    values.put(MediaStore.Images.Media.IS_PENDING, 0)
                    mContext.contentResolver.update(uri, values, null, null)
                }
            }
        }
        return uri
    }

    // 이미지 절대경로 가져오기
    private fun getPath(uri: Uri):String{
        try {
            val data:Array<String> = arrayOf(MediaStore.Images.Media.DATA)
            val cursorLoader = CursorLoader(mContext, uri, data, null, null, null)
            val cursor = cursorLoader.loadInBackground()!!
            val idx = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()

            return cursor.getString(idx)
        } catch (e:java.lang.Exception){
            Log.e(TAG, "getPath: cursor Null")
        }
        return ""
    }

    // uri -> bitmap
    private fun uriToBitmap(uri: Uri): Bitmap {
        lateinit var bitmap: Bitmap
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
            bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(mContext.contentResolver, uri))
        } else{
            bitmap = MediaStore.Images.Media.getBitmap(mContext.contentResolver, uri)
        }

        return bitmap
    }

    // 크롭되면서 새로 생성된 이미지 삭제
    fun delCropImg(delImgUri: Uri){
        Handler(Looper.getMainLooper()).post {
            kotlin.run {
                val path = getPath(delImgUri)
                if (path == ""){
                    changeProgressDialogState(false)
                    return@run
                }

                val file = File(path)
                file.delete()
            }
        }
    }

    // 상품명 리스트에 저장
    private fun productChk(){
        var product = ""
        if (gifticonInfoList[imgNum].productName != ""){
            product = gifticonInfoList[imgNum].productName
        }

        if (product != ""){
            gifticonEffectiveness[imgNum].productName = true
        }
    }

    // 브랜드 존재여부 검사
    private fun brandChk(){
        var brand = ""
        if (gifticonInfoList[imgNum].brandName != ""){
            brand = gifticonInfoList[imgNum].brandName
        }

        if (brand != ""){
            CoroutineScope(Dispatchers.IO).launch {
                val brandResponse = repo.chkBrand(brand)
                launch {
                    if (brandResponse.result != 0){
                        gifticonEffectiveness[imgNum].brandName = true
                        brandBarcodeNum()
                    } else{
                        add()
                    }
                }.join()
            }
        }
    }

    // 바코드 번호 중복 검사
    private fun brandBarcodeNum(){
        var barcode = ""
        if (gifticonInfoList[imgNum].barcodeNum != ""){
            barcode = gifticonInfoList[imgNum].barcodeNum
        }

        if (barcode != ""){
            CoroutineScope(Dispatchers.IO).launch {
                val barcodeResponse = repo.chkBarcode(barcode)
                launch {
                    if (barcodeResponse.result == 1){
                        gifticonEffectiveness[imgNum].barcodeNum = true
                    }
                    add()
                }.join()
            }
        }
    }

    // 유효기간 검사
    val dateArr = arrayOf(31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
    private fun dateFormat(){
        var date = ""
        if (gifticonInfoList[imgNum].due != ""){
            date = gifticonInfoList[imgNum].due
        }

        if (date != ""){
            val newYear = date.substring(0, 4).toInt()
            val newMonth = date.substring(5, 7).toInt()
            val newDay = date.substring(8).toInt()

            val nowDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(System.currentTimeMillis())
            val nowDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(nowDateFormat)
            var newDate = Date()
            try {
                newDate =  SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(date)!!
            } catch (e: java.lang.Exception){
                newDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(nowDateFormat)!!
            }

            val calDate = newDate.compareTo(nowDate)
            gifticonEffectiveness[imgNum].due = false

            if (newYear > 2100 || newYear.toString().length < 4){
            } else if(newMonth < 1 || newMonth > 12){
            } else if(newDay > dateArr[newMonth-1] || newDay == 0){
            } else if (calDate < 0){
            } else{
                gifticonEffectiveness[imgNum].due = true
            }
        }
    }

    // 체크박스 상태에 따른 변화
    private fun changeChkState(idx: Int){
        val voucherChk = gifticonInfoList[idx].isVoucher
        if (voucherChk == 1){
            gifticonEffectiveness[imgNum].isVoucher = true
        }
    }

    // price를 리스트에 저장
    private fun setPrice(){
        var price = ""
        if (gifticonInfoList[imgNum].price != -1){
            price = gifticonInfoList[imgNum].price.toString()
        }

        if (price != "" && price.length > 2){
            gifticonEffectiveness[imgNum].price = true
        }
    }

    // 등록하기 클릭 시 디비에 저장할 이미지 리스트 생성
    private fun makeAddImgMultipartList(): Array<MultipartBody.Part>{
        val multipartImg = mutableListOf<MultipartBody.Part>()
        for (i in 0 until originalImgUris.size){
            val productData = productImgUris[i].imgUri.asMultipart("file", mContext.contentResolver)!!
            val barcodeData = barcodeImgUris[i].imgUri.asMultipart("file", mContext.contentResolver)!!

            multipartImg.add(productData)
            multipartImg.add(barcodeData)
        }

        return multipartImg.toTypedArray()
    }

    // 등록하기 클릭 시 디비에 저장할 이미지 정보 리스트 생성
    private fun makeAddImgInfoList(gcpResult: List<GCPResult>): Array<AddImgInfo>{
        var idx = 0
        val imgInfo = mutableListOf<AddImgInfo>()
        for (i in 0 until gcpResult.size step(2)){
            val productImgName = gcpResult[i].fileName
            val barcodeImgName = gcpResult[i+1].fileName

            imgInfo.add(
                AddImgInfo(
                    gifticonInfoList[idx].barcodeNum,
                    ocrSendList[idx].fileName,
                    productImgName,
                    barcodeImgName
                )
            )
            idx++
        }
        return imgInfo.toTypedArray()
    }

    // 등록하기 클릭 시 디비에 저장할 기프티콘 정보 리스트 생성
    private fun makeAddInfoList(): MutableList<AddInfoNoImg>{
        val addInfo = mutableListOf<AddInfoNoImg>()
        for (i in 0 until gifticonInfoList.size){
            addInfo.add(
                AddInfoNoImg(
                    gifticonInfoList[i].barcodeNum,
                    gifticonInfoList[i].brandName,
                    gifticonInfoList[i].productName,
                    gifticonInfoList[i].due,
                    gifticonInfoList[i].isVoucher,
                    gifticonInfoList[i].price,
                    gifticonInfoList[i].memo,
                    user.email!!,
                    user.social
                )
            )
        }
        return addInfo
    }

    // 인식에 실패할 경우
    private fun notifyFail(){
        for (i in 0 until delImgUris.size){
            delCropImg(delImgUris[i])
        }

        Handler(Looper.getMainLooper()).post {
            kotlin.run {
                Toast.makeText(requireContext(), "인식에 실패하였습니다.\n직접 등록해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
        changeProgressDialogState(false)
    }

    // 기프티콘 정보담긴 리스트 내용 검사
    private fun chkAllList(): Boolean{
        var idx = 0
        for (gifticon in gifticonEffectiveness){
            if (!gifticon.productName || !gifticon.brandName
                || !gifticon.barcodeNum || !gifticon.due){
                Log.d(TAG, "chkAllList: ${idx}")
                Log.d(
                    TAG, "chkAllList111: ${gifticon.productName}\n ${gifticon.brandName}\n" +
                        "${gifticon.barcodeNum}\n${gifticon.due}\n")
                return false
            }
            if (gifticon.isVoucher && !gifticon.price){
                Log.d(TAG, "chkAllList222: ${gifticon.isVoucher}\n ${gifticon.price}\n")
                return false
            }
            idx++
        }
        return true
    }

    // 최종 등록
    private fun add(){
        if (chkAllList()){
            CoroutineScope(Dispatchers.IO).launch {
                repo.addGifticon(makeAddInfoList())
                var gcpResult = listOf<GCPResult>()
                launch {
                    gcpResult = repo.addFileToGCP(makeAddImgMultipartList())
                }.join()

                launch {
                    repo.addImgInfo(makeAddImgInfoList(gcpResult))
                    for (i in 0 until delImgUris.size){
                        delCropImg(delImgUris[i])
                    }

                    launch {
                        mainActivity.changeFragment(HomeFragment())
                        changeProgressDialogState(false)
                    }.join()
                }.join()
            }
        } else{
            notifyFail()
        }
    }
}