package com.ssafy.popcon.mms

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import com.ssafy.popcon.dto.MMSItem
import com.ssafy.popcon.ui.common.MainActivity
import com.ssafy.popcon.viewmodel.MMSViewModel

@SuppressLint("Range")
class RoomInitLogin(
    private val mContext: Context,
    private val mmsViewModel: MMSViewModel
    ) {
    private lateinit var resolver: ContentResolver
    private lateinit var mmsData: MMSData

    fun initRoom(){
        resolver = mContext.contentResolver
        mmsData = MMSData(
            resolver,
            mContext.applicationContext,
            true
        )

        val mmsIdList = mmsData.chkMMS()
        for (i in 0 until mmsIdList.size){
            getMMSData(mmsIdList[i], i)
        }
    }

    // MMS 타입 1차로 알아낸 후 room에 모두 저장 (mms 전체 조회)
    private fun getMMSData(mmsId: String, idx: Int){
        val selectionPart = "mid=$mmsId"
        val uri = Uri.parse("content://mms/part")
        val cursor = resolver.query(
            uri, null, selectionPart, null, null
        )!!

        if (cursor.moveToFirst()){
            while (cursor.moveToNext()){
                val type = cursor.getString(cursor.getColumnIndex("ct"))

                if (type == "text/plain"){
                    val body = mmsData.getMMSBody(cursor)
                    if (
                        body.contains("싸피")
                        || body.contains("SSAFY")
                        || body.contains("기프티콘")
                        || body.contains("쿠폰번호")
                        || body.contains("쿠폰 번호")
                    ){
                        val phoneNumber = mmsData.getAddressNumber(mmsId)
                        val date = mmsData.dateList[idx]

                        mmsViewModel.addMMSItem(
                            MMSItem(phoneNumber, date)
                        )
                    }
                }
            }
        }
        cursor.close()
    }
}