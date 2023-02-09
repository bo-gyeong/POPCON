package com.ssafy.popcon.repository.mms

import com.ssafy.popcon.dto.MMSItem

interface MMSDataSource {
    //add
    suspend fun addMMSItem(mmsItem : MMSItem)
    //get
    suspend fun getMMSItems() : List<MMSItem>
    // select date
    suspend fun selectDate(phoneNumber: String): String
    // update bitmap
    suspend fun updateDate(phoneNumber: String, beforeDate : String)
    // delete
    suspend fun deleteAll()
}