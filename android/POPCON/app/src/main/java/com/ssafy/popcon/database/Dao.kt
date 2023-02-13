package com.ssafy.popcon.database

<<<<<<< HEAD
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.room.*
import com.ssafy.popcon.dto.MMSItem
import org.jetbrains.annotations.NotNull
=======
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ssafy.popcon.dto.MMSItem
>>>>>>> feat/워치

@Dao
interface MMSItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(mmsItem: MMSItem)

    @Query("SELECT * FROM mms_item")
    suspend fun load(): List<MMSItem>
<<<<<<< HEAD

    @Query("SELECT beforeDate FROM mms_item WHERE phoneNumber=:phoneNumber")
    suspend fun selectDate(phoneNumber: String): String

    @Query("UPDATE mms_item SET beforeDate=:beforeDate WHERE phoneNumber=:phoneNumber")
    suspend fun updateDate(phoneNumber: String, beforeDate : String)

    @Query("DELETE FROM mms_item")
    suspend fun deleteAll()
=======
>>>>>>> feat/워치
}