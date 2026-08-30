package com.study.puzzletracker.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

enum class Difficulty {
    EASY, MEDIUM, HARD
}

@Entity(
    tableName = "puzzle_details",
    foreignKeys = [
        ForeignKey(
            entity = PuzzleCacheEntity::class,
            parentColumns = ["id"],
            childColumns = ["puzzleId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["puzzleId"], unique = true)]
)
data class PuzzleDetailsEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val puzzleId: String,
    val difficulty: Difficulty? = null,
    val tags: String? = null,
    val notes: String? = null
)