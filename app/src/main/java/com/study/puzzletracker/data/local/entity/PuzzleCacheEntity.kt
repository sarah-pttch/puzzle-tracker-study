package com.study.puzzletracker.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "puzzle_cache")
data class PuzzleCacheEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val pieceCount: Int,
    val manufacturer: String
)
