package com.study.puzzletracker.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

enum class AttemptStatus { IN_PROGRESS, FINISHED }
enum class AttemptMode { SPEED, RELAX, BLIND }

@Entity(
    tableName = "attempts",
    foreignKeys = [
        ForeignKey(
            entity = PuzzleDetailsEntity::class,
            parentColumns = ["id"],
            childColumns = ["puzzleDetailsId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("puzzleDetailsId")]
)
data class AttemptEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val puzzleDetailsId: Long,
    val status: AttemptStatus = AttemptStatus.IN_PROGRESS,
    val startedAt: Long,
    val completionTimeSeconds: Long = 0,
    val runningSince: Long? = null,
    val mode: AttemptMode? = null,
    val numberOfPuzzlers: Int? = null
)
