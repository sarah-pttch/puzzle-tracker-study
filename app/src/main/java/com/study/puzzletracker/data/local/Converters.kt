package com.study.puzzletracker.data.local

import androidx.room.TypeConverter
import com.study.puzzletracker.data.local.entity.AttemptMode
import com.study.puzzletracker.data.local.entity.AttemptStatus
import com.study.puzzletracker.data.local.entity.Difficulty

class Converters {
    @TypeConverter
    fun fromDifficulty(value: Difficulty?): String? = value?.name
    @TypeConverter
    fun toDifficulty(value: String?): Difficulty? = value?.let { Difficulty.valueOf(it) }

    @TypeConverter
    fun fromAttemptStatus(value: AttemptStatus): String = value.name
    @TypeConverter
    fun toAttemptStatus(value: String): AttemptStatus = AttemptStatus.valueOf(value)

    @TypeConverter
    fun fromAttemptMode(value: AttemptMode?): String? = value?.name
    @TypeConverter
    fun toAttemptMode(value: String?): AttemptMode? = value?.let { AttemptMode.valueOf(it) }
}