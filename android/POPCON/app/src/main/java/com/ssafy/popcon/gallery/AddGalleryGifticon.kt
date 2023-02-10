package com.ssafy.popcon.gallery

import android.app.Application
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.content.ContextWrapper
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.ssafy.popcon.ui.common.MainActivity
import com.ssafy.popcon.util.SharedPreferencesUtil
import java.text.DateFormat
import java.util.Calendar
import kotlin.math.log

private const val TAG = "AddGalleryGifticon_@@@"
class AddGalleryGifticon(
    private val mainActivity: MainActivity,
    private val mContext: Context,
    private val _contentResolver: ContentResolver
): Application() {
    private val sp = SharedPreferencesUtil(mContext)
    private val newImgUri = mutableListOf<Uri>()

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
        //val columnDisplayName = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME)

        val galleryDate = sp.getLatelyGalleryDate()
        while (cursor.moveToNext()){
            val absolutePath = cursor.getString(columnIdx)
            val date = cursor.getLong(dateTAKEN)
            val imgUri = ContentUris.withAppendedId(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cursor.getLong(columnId)
            )

            val calendar = Calendar.getInstance()
            calendar.timeInMillis = date
            val dateStr = android.text.format.DateFormat.format("yyyy/MM/dd kk:mm:ss", calendar).toString()
            //val fileName = cursor.getString(columnDisplayName)

            if (!TextUtils.isEmpty(absolutePath)){
                // taken이 sp에 저장된 날짜보다 작거나 같다면 break
                // sp에 저장된 날짜의 시작은 로그인 날짜
                calendar.timeInMillis = galleryDate
                val galleryDateStr = android.text.format.DateFormat.format("yyyy/MM/dd kk:mm:ss", calendar).toString()
                //Log.d(TAG, "getImgList: ${dateStr}==${date}==${fileName}   ${galleryDateStr}")
//                if (date <= galleryDate){
//                    break
//                }

                if (cursor.isFirst){
                    sp.setGalleryDate(date)
                }
                newImgUri.add(imgUri)
                Log.d(TAG, "getImgList: ${dateStr}  $imgUri")
            }
        }
        cursor.close()
    }
}