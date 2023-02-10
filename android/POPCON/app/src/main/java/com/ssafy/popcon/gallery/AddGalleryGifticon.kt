package com.ssafy.popcon.gallery

import android.content.ContentResolver
import android.content.Context
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.ssafy.popcon.ui.common.MainActivity
import kotlin.math.log

private const val TAG = "AddGalleryGifticon_@@@"
class AddGalleryGifticon(
    private val contentResolver: ContentResolver
) {
    fun getImgList(){
        val fileList = mutableListOf<String>()
        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.MediaColumns.DATA,
            MediaStore.MediaColumns.DISPLAY_NAME
        )

        val cursor = contentResolver.query(
            uri, projection, null, null, MediaStore.MediaColumns.DATE_ADDED + " desc"
        )!!
        val columnIdx = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
        //val dateAdded = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_ADDED)
//        val dateEXPIRES = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_EXPIRES)
        //val dateMODIFIED = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_MODIFIED)
       // val dateTAKEN = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_TAKEN)
        //val columnDisplayName = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME)
        //var lastIdx = 0
        while (cursor.moveToNext()){
            val absolutePath = cursor.getString(columnIdx)
            //val fileName = cursor.getString(columnDisplayName)
//            lastIdx = absolutePath.lastIndexOf(fileName)
//            if (lastIdx < 0){
//                lastIdx = fileName.length - 1
//            }

            if (!TextUtils.isEmpty(absolutePath)){
               // Log.d(TAG, "getImgList: ${dateTAKEN}")
                //Log.d(TAG, "getImgList: ${dateAdded}\n ${dateEXPIRES}\n ${dateMODIFIED} \n${dateTAKEN}")
                //Log.d(TAG, "getImgList: ${absolutePath}")
                fileList.add(absolutePath)
            }
        }
    }
}