package com.ssafy.popcon.ui.add

import android.annotation.SuppressLint
import android.app.Service
import android.content.BroadcastReceiver
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.IBinder
import java.io.InputStream
import java.text.MessageFormat

@SuppressLint("Range")
class MMSReceiver: Service() { //, BroadcastReceiver()
    override fun onBind(p0: Intent?): IBinder? {
        val contentResolver: ContentResolver = applicationContext.contentResolver
        val projection = arrayOf("*")
        val uri = Uri.parse("content://mms-sms/conversations/")
        val query = contentResolver.query(uri, projection, null, null, null)!!

        if (query.moveToFirst()){
            while (query.moveToNext()){
                val string = query.getString(query.getColumnIndex("ct_t"))
                if ("application/vnd.wap.multipart.related" == string){  //mms

                }
            }
        }

    }

    private fun initMMSData(){
        val uri = Uri.parse("content://mms/")
        val selection = "_id = " //+ mmsid
        val cursor = applicationContext.contentResolver.query(uri, null, selection, null, null)

        val selectionPart = "mid=" //+mmsId
    }

    private fun getMMSData(){
        val selectionPart = "mid=" //+mmsId
        val uri = Uri.parse("content://mms/part")
        val cPart = applicationContext.contentResolver.query(
            uri, null, selectionPart, null, null
        )!!

        if (cPart.moveToFirst()){
            while (cPart.moveToNext()){
                val partId = cPart.getString(cPart.getColumnIndex("_id"))
                val type = cPart.getString(cPart.getColumnIndex("ct"))
                if ("image/jpeg" == type || "image/bmp" == type
                    || "image/gif" == type || "image/jpg" == type
                    || "image/png" == type){
                    val bitmap = getMMSData(partId)
                }
            }
        }
    }

    private fun getMMSImage(_id: String): Bitmap{
        val partURI = Uri.parse("content://mms/part/" + _id)
        var inputStream: InputStream
        var bitmap: Bitmap

        //try catch
        inputStream = applicationContext.contentResolver.openInputStream(partURI)!!
        bitmap = BitmapFactory.decodeStream(inputStream)

        return bitmap
    }

    private fun getAddressNumber(id: Int): String{
        val selectionAdd = "msg_id=$id"
        val uriStr = MessageFormat.format("content://mms/{0}/addr", id)
        val uriAddress = Uri.parse(uriStr)

        val cAdd = applicationContext.contentResolver.query(
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