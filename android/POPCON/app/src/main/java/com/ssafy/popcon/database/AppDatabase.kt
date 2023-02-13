package com.ssafy.popcon.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ssafy.popcon.dto.MMSItem

<<<<<<< HEAD
@Database(entities = [MMSItem::class], version = 2)
=======
@Database(entities = [MMSItem::class], version = 1)
>>>>>>> feat/워치
abstract class AppDatabase : RoomDatabase() {
    abstract fun mmsDao(): MMSItemDao
}