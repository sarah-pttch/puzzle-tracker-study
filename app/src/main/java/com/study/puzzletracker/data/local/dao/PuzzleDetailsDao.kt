package com.study.puzzletracker.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.study.puzzletracker.data.local.entity.CollectionPuzzleCrossRef
import com.study.puzzletracker.data.local.entity.Difficulty
import com.study.puzzletracker.data.local.entity.PuzzleCacheEntity
import com.study.puzzletracker.data.local.entity.PuzzleDetailsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PuzzleDetailsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPuzzleCache(cache: PuzzleCacheEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertDetailsIfAbsent(details: PuzzleDetailsEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCollectionCrossRef(crossRef: CollectionPuzzleCrossRef)

    @Transaction
    suspend fun addPuzzleToCollection(
        collectionId: Long,
        puzzleId: String,
        name: String,
        pieceCount: Int,
        manufacturer: String
    ) {
        upsertPuzzleCache(PuzzleCacheEntity(puzzleId, name, pieceCount, manufacturer))
        insertDetailsIfAbsent(PuzzleDetailsEntity(puzzleId = puzzleId))
        insertCollectionCrossRef(CollectionPuzzleCrossRef(collectionId, puzzleId))
    }

    @Query("""
        SELECT pc.* FROM puzzle_cache pc
        INNER JOIN collection_puzzles cp ON cp.puzzleId = pc.id
        WHERE cp.collectionId = :collectionId
        """)
    fun getPuzzleIdsInCollection(collectionId: Long): Flow<List<PuzzleCacheEntity>>

    @Query("SELECT * FROM puzzle_cache WHERE id = :puzzleId")
    fun getPuzzleCache(puzzleId: String): Flow<PuzzleCacheEntity?>

    @Query("SELECT * FROM puzzle_details WHERE puzzleId = :puzzleId")
    fun getDetails(puzzleId: String): Flow<PuzzleDetailsEntity?>

    @Query("""
        UPDATE puzzle_details
        SET difficulty = :difficulty, tags = :tags, notes = :notes
        WHERE puzzleId = :puzzleId
    """)
    suspend fun updateDetails(puzzleId: String, difficulty: Difficulty?, tags: String?, notes: String?)
}