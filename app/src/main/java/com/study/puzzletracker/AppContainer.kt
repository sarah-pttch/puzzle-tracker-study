package com.study.puzzletracker

import android.content.Context
import androidx.room.Room
import com.study.puzzletracker.data.local.AppDatabase
import com.study.puzzletracker.data.repository.AuthRepository
import com.study.puzzletracker.data.repository.CollectionRepository

class AppContainer(context: Context) {
    private val database = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java,
        "puzzle-tracker-db"
    ).build()

    val authRepository = AuthRepository()
    val collectionRepository = CollectionRepository(database.collectionDao())
}