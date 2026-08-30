package com.study.puzzletracker.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.study.puzzletracker.data.local.entity.CollectionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CollectionDao {
    @Query("SELECT * FROM collections ORDER BY name")
    fun getAll(): Flow<List<CollectionEntity>>

    @Query("SELECT * FROM collections WHERE id = :collectionId")
    fun getById(collectionId: Long): Flow<CollectionEntity?>

    @Insert
    suspend fun insert(collection: CollectionEntity): Long

    @Delete
    suspend fun delete(collection: CollectionEntity)
}