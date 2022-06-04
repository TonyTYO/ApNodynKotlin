package com.example.apnodyn.data

import androidx.room.TypeConverter
import java.util.*

// No Date datatype in SQLLite
// Converters to change to and from long Int
class Converter {

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}