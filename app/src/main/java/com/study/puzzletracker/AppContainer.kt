package com.study.puzzletracker

import android.content.Context
import androidx.room.Room
import com.study.puzzletracker.data.local.AppDatabase
import com.study.puzzletracker.data.repository.AttemptRepository
import com.study.puzzletracker.data.repository.AuthRepository
import com.study.puzzletracker.data.repository.CollectionRepository
import com.study.puzzletracker.data.repository.SearchRepository

class AppContainer(context: Context) {
    private val database = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java,
        "puzzle-tracker-db"
    )
        .fallbackToDestructiveMigration(true)
        .build()

    val authRepository = AuthRepository()
    val collectionRepository = CollectionRepository(database.collectionDao())
    val searchRepository = SearchRepository()
    val puzzleDetailsDao = database.puzzleDetailsDao()

    val attemptRepository = AttemptRepository(database.attemptDao(), database.puzzleDetailsDao())
}