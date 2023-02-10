package com.ssafy.popcon.mms

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.ImageDecoder
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.loader.content.CursorLoader
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.ssafy.popcon.R
import com.ssafy.popcon.config.ApplicationClass
import com.ssafy.popcon.databinding.DialogMmsBinding
import com.ssafy.popcon.dto.*
import com.ssafy.popcon.ui.common.Event
import com.ssafy.popcon.ui.common.EventObserver
import com.ssafy.popcon.ui.common.MainActivity
import com.ssafy.popcon.viewmodel.AddViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okio.BufferedSink
import okio.source
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

private const val TAG = "MMSDialog_###"
class MMSDialog(private val viewModel: AddViewModel): DialogFragment() {
    private lateinit var binding: DialogMmsBinding
    private lateinit var mainActivity: MainActivity
    private lateinit var mContext: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogMmsBinding.inflate(layoutInflater)
        mainActivity = MainActivity.getInstance()!!
        mContext = requireContext()

        val builder = AlertDialog.Builder(context, R.style.WrapContentDialog)
        builder.setView(binding.root)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        isCancelable = false

        binding.lMms.visibility = View.VISIBLE
        binding.btnCancel.setOnClickListener {
            MainActivity.fromMMSReceiver = null
            dismiss()
        }

        binding.btnGoToAdd.setOnClickListener {
            initData()
            /** 갤러리에서 불러올 경우 수정
             * viewModel event 직전 데이터 갱신까지 2번 호출되는 문제 **/
            for (i in 0 until 1){
                val uri = bitmapToUri(MainActivity.fromMMSReceiver!!)
                //Log.d(TAG, "onCreateDialog**: ${uri}")
                originalImgUris.add(GifticonImg(uri))
                gifticonEffectiveness.add(AddInfoNoImgBoolean())
            }
            firstAdd()

            binding.lMms.visibility = View.GONE
            makeProgressDialog()
            changeProgressDialogState(true)
            MainActivity.fromMMSReceiver = null
        }

        return builder.create()
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
    private lateinit var loadingDialog: AlertDialog.Builder
    private lateinit var dialogCreate: AlertDialog
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

            val realData = originalImgUri.asMultipart("file", requireContext().contentResolver)
            multipartFiles.add(realData!!)
        }

        viewModel.addFileToGCP(multipartFiles.toTypedArray())
        viewModel.gcpResult.observeForever(EventObserver{
            ocrSendList.clear()
            for (i in 0 until it.size){
                val gcpResult = it[i]
                val originalImgBitmap = uriToBitmap(originalImgUris[i].imgUri)

                ocrSendList.add(
                    OCRSend(
                        gcpResult.fileName, originalImgBitmap.width, originalImgBitmap.height
                    )
                )
            }

            viewModel.useOcr(ocrSendList.toTypedArray())
            viewModel.ocrResult.observeForever(EventObserver{
                for (ocrResult in it){
                    ocrResults.add(ocrResult)

                    if (ocrResult.barcodeNum != ""){
                        for (i in 0 until it.size){
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
            })
        })
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

    // 로딩화면 띄우기
    private fun makeProgressDialog(){
        loadingDialog = AlertDialog.Builder(requireContext())
        loadingDialog.setView(R.layout.dialog_progress).setCancelable(false)
        dialogCreate = loadingDialog.create()
    }

    // 상태에 따라 다이얼로그 만들기/없애기
    private fun changeProgressDialogState(state: Boolean){
        if (state){
            dialogCreate.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialogCreate.show()
        } else{
            dialogCreate.dismiss()
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

        val uri = requireContext().contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        if (uri != null) {
            val descriptor = requireContext().contentResolver.openFileDescriptor(uri, "w")

            if (descriptor != null) {
                val fos = FileOutputStream(descriptor.fileDescriptor)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                fos.close()
                descriptor.close()

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    values.clear()
                    values.put(MediaStore.Images.Media.IS_PENDING, 0)
                    requireContext().contentResolver.update(uri, values, null, null)
                }
            }
        }
        return uri
    }

    // 이미지 절대경로 가져오기
    private fun getPath(uri: Uri):String{
        val data:Array<String> = arrayOf(MediaStore.Images.Media.DATA)
        val cursorLoader = CursorLoader(requireContext(), uri, data, null, null, null)
        val cursor = cursorLoader.loadInBackground()!!
        val idx = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()

        return cursor.getString(idx)
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

    /** 코드 다시 찾기 **/
    // bitmap -> uri
    private fun bitmapToUri(bitmap: Bitmap): Uri {
        bitmap.compress(
            Bitmap.CompressFormat.JPEG, 100, ByteArrayOutputStream()
        )

        val path = MediaStore.Images.Media.insertImage(
            requireContext().contentResolver, bitmap, "mmsBitmapToUri", null
        )

        return Uri.parse(path)
    }

    // 크롭되면서 새로 생성된 이미지 삭제
    fun delCropImg(delImgUri: Uri){
        val file = File(getPath(delImgUri))
        file.delete()
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
            viewModel.chkBrand(brand)
            viewModel.brandChk.observeForever(EventObserver{
                if (it.result != 0){
                    gifticonEffectiveness[imgNum].brandName = true
                    brandBarcodeNum()
                } else{
                    add()
                }
            })
        }
    }

    // 바코드 번호 중복 검사
    private fun brandBarcodeNum(){
        var barcode = ""
        if (gifticonInfoList[imgNum].barcodeNum != ""){
            barcode = gifticonInfoList[imgNum].barcodeNum
        }

        if (barcode != ""){
            viewModel.chkBarcode(barcode)
            viewModel.barcodeChk.observeForever(EventObserver{
                if (it.result == 1){
                    gifticonEffectiveness[imgNum].barcodeNum = true
                }
                add()
            })
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
            val productData = productImgUris[i].imgUri.asMultipart("file", requireContext().contentResolver)!!
            val barcodeData = barcodeImgUris[i].imgUri.asMultipart("file", requireContext().contentResolver)!!

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
        Log.d(TAG, "notifyFail: ")
        for (i in 0 until delImgUris.size){
            delCropImg(delImgUris[i])
        }

        Toast.makeText(context, "인식에 실패하였습니다. 직접 등록해주세요.", Toast.LENGTH_SHORT).show()
        changeProgressDialogState(false)
        dismiss()
    }

    // 기프티콘 정보담긴 리스트 내용 검사
    private fun chkAllList(): Boolean{
        var idx = 0
        for (gifticon in gifticonEffectiveness){
            if (!gifticon.productName || !gifticon.brandName
                || !gifticon.barcodeNum || !gifticon.due){
                Log.d(TAG, "chkAllList: ${idx}")
                Log.d(TAG, "chkAllList111: ${gifticon.productName}\n ${gifticon.brandName}\n" +
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
            viewModel.addGifticon(makeAddInfoList())
            viewModel.addGifticonResult.observeForever(EventObserver{
                viewModel.addOtherFileToGCP(makeAddImgMultipartList())
            })

            viewModel.gcpOtherResult.observeForever(EventObserver{
                viewModel.addImgInfo(makeAddImgInfoList(it))
                for (i in 0 until delImgUris.size){
                    delCropImg(delImgUris[i])
                }

                viewModel.addImgInfoResult.observeForever(EventObserver{
                    /*** fragment home 갱신  ***/
                    changeProgressDialogState(false)
                    dismiss()
                })
            })
        } else{
            /** 갤러리에서 클릭할때 추후 생각 **/
            notifyFail()
        }
    }
}