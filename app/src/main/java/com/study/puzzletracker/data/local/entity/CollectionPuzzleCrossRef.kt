package com.study.puzzletracker.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "collection_puzzles",
    primaryKeys = ["collectionId", "puzzleId"],
    foreignKeys = [
        ForeignKey(
            entity = CollectionEntity::class,
            parentColumns = ["id"],
            childColumns = ["collectionId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = PuzzleCacheEntity::class,
            parentColumns = ["id"],
            childColumns = ["puzzleId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("puzzleId")]
)
data class CollectionPuzzleCrossRef(
    val collectionId: Long,
    val puzzleId: String
)