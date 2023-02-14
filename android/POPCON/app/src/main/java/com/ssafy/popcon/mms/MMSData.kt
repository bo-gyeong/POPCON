package com.ssafy.popcon.mms

import android.annotation.SuppressLint
import android.app.Application
import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import com.ssafy.popcon.config.ApplicationClass
import com.ssafy.popcon.dto.MMSItem
import com.ssafy.popcon.repository.mms.MMSLocalDataSource
import com.ssafy.popcon.repository.mms.MMSRepository
import com.ssafy.popcon.ui.common.MainActivity
import com.ssafy.popcon.util.SharedPreferencesUtil
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.text.MessageFormat

@SuppressLint("Range")
class MMSData(
    private val resolver: ContentResolver,
    private val context: Context,
    private val init: Boolean
): Application() {
    private var mainActivity = MainActivity.getInstance()!!
    private var fcmCall = false
    var dateList = mutableListOf<String>()

    override fun onCreate() {
        super.onCreate()
        mainActivity = MainActivity.getInstance()!!
    }

    // SMS MMS 구분
    fun chkMMS(): MutableList<String>{
        val projection = arrayOf("*") //   "*" -> 모든 대화목록, "_id", "ct_t"... -> 원하는 값만 추출
        val uri = Uri.parse("content://mms-sms/conversations/")
        val query = resolver.query(uri, projection, null, null, null)!!

        val mmsIdList = mutableListOf<String>()
        if (query.moveToFirst()){
            while (query.moveToNext()){
                val mmsId = query.getString(query.getColumnIndex("_id"))
                val type = query.getString(query.getColumnIndex("ct_t"))  // 텍스트인지 이미지인지
                val date = query.getLong(query.getColumnIndex("normalized_date"))
                val subString = query.getString(query.getColumnIndex("sub")) ?: continue // 제목

                if ("application/vnd.wap.multipart.related" == type){  //mms
                    val title = String(subString.toByteArray(Charsets.ISO_8859_1), Charsets.UTF_8)
                    val threadId = query.getString(query.getColumnIndex("thread_id"))
                    mmsIdList.add(mmsId)
                    dateList.add(date.toString())

                    if(!init){
                        CoroutineScope(Dispatchers.Main).launch {
                            getMMSData(mmsId, date)
                        }
                    }
                }
            }
        }
        query.close()

        return mmsIdList
    }

    // MMS 타입 1차로 알아내기 (mms 전체 조회)
    private fun getMMSData(mmsId: String, date: Long){
        val selectionPart = "mid=$mmsId"
        val uri = Uri.parse("content://mms/part")
        val cursor = resolver.query(
            uri, null, selectionPart, null, null
        )!!

        val mmsDao = ApplicationClass().provideDatabase(context).mmsDao()
        val mmsRepo = MMSRepository(MMSLocalDataSource(mmsDao))

        if (cursor.moveToFirst()){
            while (cursor.moveToNext()){
                val type = cursor.getString(cursor.getColumnIndex("ct"))

                if (type == "text/plain"){
                    val body = getMMSBody(cursor)
                    if (
                        body.contains("싸피")
                        || body.contains("SSAFY")
                        || body.contains("기프티콘")
                        || body.contains("쿠폰번호")
                        || body.contains("쿠폰 번호")
                    ){
                        chkBeforeBitmap(cursor, mmsId, mmsRepo, date.toString())
                    }
                }
            }
        }
        cursor.close()
    }

    // room에 저장된 각 bitmap 조회
    // 새로운 번호에서 온 문자라면 insert
    // 기존 번호에서 갱신된 문자라면 update
    private fun chkBeforeBitmap(
        cursor: Cursor, mmsId: String, mmsRepo: MMSRepository, date: String
    ){
        val bitmap = getMMSImg(cursor, mmsId)

        if (bitmap != null){
            CoroutineScope(Dispatchers.IO).launch {
                val phoneNumber = getAddressNumber(mmsId)
                val beforeDate = mmsRepo.selectDate(phoneNumber)

                if (beforeDate == null){
                    mmsRepo.addMMSItem(
                        MMSItem(phoneNumber, date)
                    )
                    compareSpBitmap(bitmap, date)
                } else if (beforeDate != date){
                    mmsRepo.updateDate(phoneNumber, date)
                    compareSpBitmap(bitmap, date)
                }
            }
        }
    }

    // 가장 최근에 읽어들인 date 확인 후 update 및 푸시 알림
    private fun compareSpBitmap(bitmap: Bitmap, date: String){
        val spUtil = SharedPreferencesUtil(context)

        val beforeDate = spUtil.getLatelyMMSDate()
        if (beforeDate != date){
            if (!init && !fcmCall){
                MainActivity.fromMMSReceiver = bitmap
                spUtil.setMMSDate(date)

                CoroutineScope(Dispatchers.IO).launch {
                    mainActivity.sendMessageTo(
                        spUtil.getFCMToken(),
                        "새로운 기프티콘이 있습니다",
                        "앱을 실행해주세요"
                    )
                }
                fcmCall = true
            }
        }
    }

    // MMS 내용 알아오기
    fun getMMSBody(pCursor: Cursor): String{
        val partId = pCursor.getString(pCursor.getColumnIndex("_id"))
        val data = pCursor.getString(pCursor.getColumnIndex("_data"))

        if (data != null){
            getMessageText(partId)
        }
        return pCursor.getString(pCursor.getColumnIndex("text"))
    }

    // MMS 내용의 Text 추출
    private fun getMessageText(id: String): String {
        val partUri = Uri.parse("content://mms/part/$id")
        val stringBuilder = StringBuilder()
        val inputStream = resolver.openInputStream(partUri)

        if (inputStream != null) {
            val inputStreamReader = InputStreamReader(inputStream, "UTF-8")
            val bufferedReader = BufferedReader(inputStreamReader)
            var brRead = bufferedReader.readLine()
            while (brRead != null) {
                stringBuilder.append(brRead)
                brRead = bufferedReader.readLine()
            }
            inputStream.close()
        }

        return stringBuilder.toString()
    }

    // MMS 타입 2차로 알아내기, 이미지 -> Bitmap
    fun getMMSImg(mmsCursor: Cursor, mmsId: String): Bitmap?{
        val selectionPart = "mid=$mmsId"
        val partUri = Uri.parse("content://mms/part")
        val cursor = resolver.query(
            partUri, null, selectionPart, null, null
        )!!

        if (!cursor.moveToFirst()) return null

        while (cursor.moveToNext()){
            val partId = cursor.getString(mmsCursor.getColumnIndex("_id"))
            val type = cursor.getString(mmsCursor.getColumnIndex("ct"))

            if (
                "image/jpeg" == type || "image/bmp" == type
                || "image/gif" == type || "image/jpg" == type
                || "image/png" == type
            ){
                val partURI = Uri.parse("content://mms/part/$partId")

                val inputStream = resolver.openInputStream(partURI)
                if(inputStream != null){
                    return BitmapFactory.decodeStream(inputStream)
                }
            }
        }
        cursor.close()

        return null
    }

    // MMS 보낸 전화번호 알아오기
    fun getAddressNumber(mmsId: String): String{
        val selectionAdd = "msg_id=$mmsId"
        val uriStr = MessageFormat.format("content://mms/{0}/addr", mmsId)
        val mmsUri = Uri.parse(uriStr)

        val cursor = resolver.query(
            mmsUri, null, selectionAdd, null, null
        )!!

        var number = ""
        if (cursor.moveToFirst()){
            while (cursor.moveToFirst()){
                val address = cursor.getString(cursor.getColumnIndex("address"))
                if (address != null){
                    try {
                        number = address.replace("-", "")
                        break
                    } catch (e: NumberFormatException){
                        if (number == ""){
                            number = address
                        }
                    }
                }
            }
        }
        cursor.close()

        return number
    }
}