package com.ssafy.popcon.ui.add

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.viewModelFactory
import com.ssafy.popcon.R
import com.ssafy.popcon.ui.common.MainActivity
import com.ssafy.popcon.util.SharedPreferencesUtil
import com.ssafy.popcon.viewmodel.FCMViewModel
import com.ssafy.popcon.viewmodel.ViewModelFactory
import java.io.BufferedReader
import java.io.InputStreamReader
import java.text.MessageFormat

private const val TAG = "MMSReceiver_###"
@SuppressLint("Range")
class MMSReceiver: BroadcastReceiver() {
    lateinit var context: Context
    lateinit var contentResolver: ContentResolver
    lateinit var mainActivity: MainActivity

    override fun onReceive(_context: Context?, _intent: Intent?) {
        context = _context!!
        contentResolver = context.contentResolver
        mainActivity = MainActivity.getInstance()!!
        Toast.makeText(context, "whtttttttttttt", Toast.LENGTH_SHORT).show()
        chkMMS()
    }

    // SMS MMS 구분
    private fun chkMMS(){
        val contentResolver: ContentResolver = contentResolver
        val projection = arrayOf("*") //   "_id", "ct_t" "*" -> 모든 대화목록
        val uri = Uri.parse("content://mms-sms/conversations/")
        val query = contentResolver.query(uri, projection, null, null, null)!!

        if (query.moveToFirst()){
            while (query.moveToNext()){
                val mmsId = query.getString(query.getColumnIndex("_id"))
                val type = query.getString(query.getColumnIndex("ct_t"))  // 텍스트인지 이미지인지
                val subString = query.getString(query.getColumnIndex("sub")) ?: continue // 제목

                if ("application/vnd.wap.multipart.related" == type){  //mms
                    val title = String(subString.toByteArray(Charsets.ISO_8859_1), Charsets.UTF_8)
                    val threadId = query.getString(query.getColumnIndex("thread_id"))
                    getMMSData(mmsId, title, threadId)
                }
            }
        }
        query.close()
    }

    // MMS 타입 1차로 알아내기
    private fun getMMSData(mmsId: String, title: String, threadId: String){
        val selectionPart = "mid=$mmsId"
        val uri = Uri.parse("content://mms/part")
        val cPart =contentResolver.query(
            uri, null, selectionPart, null, null
        )!!
        //Log.d(TAG, "getMMSData: $title")

        if (cPart.moveToFirst()){
            while (cPart.moveToNext()){
                val type = cPart.getString(cPart.getColumnIndex("ct"))

                if (type == "text/plain"
                    && title == "[SSAFY] 싸피데이 이벤트 당첨 안내"){
                    Log.d(TAG, "getMMSData: $title")
                    val bitmap = getMMSImg(cPart, mmsId)
                    Log.d(TAG, "Bit: $bitmap")
                    if (bitmap != null){
                        compareBitmap(bitmap)
                    }

                    //val body = getMMSBody(cPart)
                    //Log.d(TAG, "getMMSData: $body")
                }
            }
        }
        cPart.close()
    }

    // 가장 최근에 읽어들인 MMS Bitmap 확인 후 update 및 푸시 알림
    private fun compareBitmap(bitmap: Bitmap){
        val spUtil = SharedPreferencesUtil(context)

        val beforeBitmapStr = spUtil.getLatelyMMSBitmap()
        val encodeByte = Base64.decode(beforeBitmapStr, Base64.DEFAULT)
        val beforeBitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.size)
        if (beforeBitmap != bitmap){
            MainActivity.fromMMSReceiver = bitmap
            spUtil.setMMSBitmap(bitmap)

//            mainActivity.sendMessageTo(
//                spUtil.getFCMToken(),
//                "새로운 기프티콘이 있습니다",
//                "앱을 실행해주세요"
//            )
            MainActivity.newMMSImg = true
        }
    }

    // 채팅방 고유 ID로 문자 내역 뽑아오기
    private fun getMMSDataByThreadId(threadId: String){
        val selectionPart = "thread_id=$threadId"

        /* https://m.blog.naver.com/PostView.naver?isHttpsRedirect=true&blogId=horajjan&logNo=110188907638 */
        val uri = Uri.parse("content://mms")  // inbox/
        val cPart =contentResolver.query(
            uri, null, selectionPart, null, null
        )!!

        for (i in 0 until cPart.count){
            Log.d(TAG, "getMMSDataByThreadId: ${cPart.columnNames[i]}")
        }

        if (cPart.moveToFirst()){
            while (cPart.moveToNext()){
                val partId = cPart.getString(cPart.getColumnIndex("_id"))
                val type = cPart.getString(cPart.getColumnIndex("ct"))

            }
        }
        cPart.close()
    }

    // MMS 내용 알아오기
    private fun getMMSBody(pCursor: Cursor): String{
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
        val inputStream = contentResolver.openInputStream(partUri)

        if (inputStream != null) {
            val inputStreamReader = InputStreamReader(inputStream, "UTF-8")
            val bufferedReader = BufferedReader(inputStreamReader)
            var temp = bufferedReader.readLine()
            while (temp != null) {
                stringBuilder.append(temp)
                temp = bufferedReader.readLine()
            }
            inputStream.close()
        }

        return stringBuilder.toString()
    }

    // MMS 타입 2차로 알아내기, 이미지 -> Bitmap
    private fun getMMSImg(pCursor: Cursor, mmsId: String): Bitmap?{
        val selectionPart = "mid=$mmsId"
        val uri = Uri.parse("content://mms/part")
        val cPart: Cursor = contentResolver.query(
            uri, null,
            selectionPart, null, null
        )!!

        if (!cPart.moveToFirst()) return null

        while (cPart.moveToNext()){
            val partId = cPart.getString(pCursor.getColumnIndex("_id"))
            val type = cPart.getString(pCursor.getColumnIndex("ct"))

            if (
                "image/jpeg" == type || "image/bmp" == type
                || "image/gif" == type || "image/jpg" == type
                || "image/png" == type
            ){
                val partURI = Uri.parse("content://mms/part/$partId")

                val inputStream = contentResolver.openInputStream(partURI)
                if(inputStream != null){
                    return BitmapFactory.decodeStream(inputStream)
                }
            }
        }
        cPart.close()

        return null
    }

    // MMS 보낸 전화번호 알아오기
    private fun getAddressNumber(id: Int): String{
        val selectionAdd = "msg_id=$id"
        val uriStr = MessageFormat.format("content://mms/{0}/addr", id)
        val uriAddress = Uri.parse(uriStr)

        val cAdd = contentResolver.query(
            uriAddress, null, selectionAdd, null, null
        )!!

        var name = ""
        if (cAdd.moveToFirst()){
            while (cAdd.moveToFirst()){
                val number = cAdd.getString(cAdd.getColumnIndex("address"))
                if (number != null){
                    try {
                        number.replace("-", "").toLong()
                        name = number
                    } catch (e: NumberFormatException){
                        if (name == null){
                            name = number
                        }
                    }
                }
            }
        }
        if (cAdd != null){
            cAdd.close()
        }
        return name
    }
}