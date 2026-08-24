package com.study.puzzletracker.data.repository

import com.study.puzzletracker.data.local.dao.CollectionDao
import com.study.puzzletracker.data.local.entity.CollectionEntity
import kotlinx.coroutines.flow.Flow

class CollectionRepository(private val dao: CollectionDao) {
    fun getAllCollections(): Flow<List<CollectionEntity>> = dao.getAll()

    suspend fun createCollection(name: String, puzzlerId: String) {
        dao.insert(CollectionEntity(name = name, puzzlerId = puzzlerId))
    }
}