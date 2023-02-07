package com.ssafy.popcon.ui.add

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.widget.Toast
import java.io.InputStream
import java.text.MessageFormat

private const val TAG = "MMSReceiver_###"
@SuppressLint("Range")
class MMSReceiver: BroadcastReceiver() {
    lateinit var contentResolver: ContentResolver

    override fun onReceive(context: Context?, intent: Intent?) {
        contentResolver = context!!.contentResolver
        Toast.makeText(context, "whtttttttttttt", Toast.LENGTH_SHORT).show()
        chkMMS()
    }

    private fun chkMMS(){
        val contentResolver: ContentResolver = contentResolver
        val projection = arrayOf("*") //   "_id", "ct_t" "*" -> 모든 대화목록 ct_t -> 텍스트인지 이미지인지
        val uri = Uri.parse("content://mms-sms/conversations/")
        val query = contentResolver.query(uri, projection, null, null, null)!!

        if (query.moveToFirst()){
            while (query.moveToNext()){
                val mmsId = query.getString(query.getColumnIndex("_id"))
                val type = query.getString(query.getColumnIndex("ct_t"))
                val subString = query.getString(query.getColumnIndex("sub")) ?: continue

                if ("application/vnd.wap.multipart.related" == type){  //mms
                    val title = String(subString.toByteArray(Charsets.ISO_8859_1), Charsets.UTF_8)
                    val threadId = query.getString(query.getColumnIndex("thread_id"))
                    getMMSData(mmsId, title, threadId)
                    Log.d(TAG, "chkMMS: WORKKKKKKKKK")
                }
            }
        }
        query.close()
    }

    private fun initMMSData(mmsId: String){
        val uri = Uri.parse("content://mms/")
        val selection = "_id = $mmsId"
        val cursor = contentResolver.query(uri, null, selection, null, null)

    }

    private fun getMMSData(mmsId: String, title: String, threadId: String){
        val selectionPart = "mid=$mmsId"
        val uri = Uri.parse("content://mms/part")
        val cPart =contentResolver.query(
            uri, null, selectionPart, null, null
        )!!
        //Log.d(TAG, "getMMSData: $title")

        if (cPart.moveToFirst()){
            while (cPart.moveToNext()){
                val partId = cPart.getString(cPart.getColumnIndex("_id"))
                val type = cPart.getString(cPart.getColumnIndex("ct"))

                //"image/jpeg" == type || "image/bmp" == type
                //                    || "image/gif" == type || "image/jpg" == type
                //                    || "image/png" == type
                if (type == "text/plain"
                    && title == "안녕하세요! SSAFY 사무국입니다 :)"){
                    //val bitmap = getMMSImage(partId)
                    val partURI = Uri.parse("content://mms/part/$partId")
                    Log.d(TAG, "getMMSData: ${partURI}")

                }
            }
        }
        cPart.close()
    }

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

    private fun getMMSImage(_id: String): Bitmap{
        val partURI = Uri.parse("content://mms/part/$_id")
        val inputStream: InputStream
        val bitmap: Bitmap

        //try catch
        inputStream = contentResolver.openInputStream(partURI)!!
        bitmap = BitmapFactory.decodeStream(inputStream)

        return bitmap
    }

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