package com.ssafy.popcon.repository.mms

import com.ssafy.popcon.dto.MMSItem

interface MMSDataSource {
    //add
    suspend fun addMMSItem(mmsItem : MMSItem)
    //get
    suspend fun getMMSItems() : List<MMSItem>
}