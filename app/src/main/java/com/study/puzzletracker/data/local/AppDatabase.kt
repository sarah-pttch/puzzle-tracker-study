package com.study.puzzletracker.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.study.puzzletracker.data.local.dao.CollectionDao
import com.study.puzzletracker.data.local.entity.CollectionEntity

@Database(entities = [CollectionEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun collectionDao(): CollectionDao
}