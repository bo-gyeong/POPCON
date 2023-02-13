package com.ssafy.popcon.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ssafy.popcon.dto.MMSItem

@Dao
interface MMSItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(mmsItem: MMSItem)

    @Query("SELECT * FROM mms_item")
    suspend fun load(): List<MMSItem>
}