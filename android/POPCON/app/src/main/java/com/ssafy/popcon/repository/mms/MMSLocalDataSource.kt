package com.ssafy.popcon.repository.mms

import com.ssafy.popcon.database.MMSItemDao
import com.ssafy.popcon.dto.MMSItem

class MMSLocalDataSource (
    private val dao: MMSItemDao
) : MMSDataSource {
    override suspend fun addMMSItem(mmsItem: MMSItem) {
        dao.insert(mmsItem)
    }

    override suspend fun getMMSItems(): List<MMSItem> {
        return dao.load()
    }
}