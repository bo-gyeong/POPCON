package com.ssafy.popcon.dto

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "mms_item"
)
data class MMSItem(
    @PrimaryKey @ColumnInfo(name = "phoneNumber")
    val phoneNumber : String,
    @ColumnInfo(name = "beforeDate")
    val beforeDate : String
)
