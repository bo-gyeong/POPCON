package com.ssafy.popcon.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ssafy.popcon.dto.MMSItem

@Database(entities = [MMSItem::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun mmsDao(): MMSItemDao
}