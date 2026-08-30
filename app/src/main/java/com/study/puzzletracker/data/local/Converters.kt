package com.study.puzzletracker.data.local

import androidx.room.TypeConverter
import com.study.puzzletracker.data.local.entity.Difficulty

class Converters {
    @TypeConverter
    fun fromDifficulty(value: Difficulty?): String? = value?.name

    @TypeConverter
    fun toDifficulty(value: String?): Difficulty? = value?.let { Difficulty.valueOf(it) }
}