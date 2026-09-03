package com.study.puzzletracker.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.study.puzzletracker.data.local.dao.AttemptDao
import com.study.puzzletracker.data.local.dao.CollectionDao
import com.study.puzzletracker.data.local.dao.PuzzleDetailsDao
import com.study.puzzletracker.data.local.entity.AttemptEntity
import com.study.puzzletracker.data.local.entity.CollectionEntity
import com.study.puzzletracker.data.local.entity.CollectionPuzzleCrossRef
import com.study.puzzletracker.data.local.entity.PuzzleCacheEntity
import com.study.puzzletracker.data.local.entity.PuzzleDetailsEntity

@Database(
    entities = [
        AttemptEntity::class,
        CollectionEntity::class,
        PuzzleCacheEntity::class,
        PuzzleDetailsEntity::class,
        CollectionPuzzleCrossRef::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun attemptDao(): AttemptDao
    abstract fun collectionDao(): CollectionDao
    abstract fun puzzleDetailsDao(): PuzzleDetailsDao
}