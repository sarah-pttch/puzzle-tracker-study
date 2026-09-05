package com.study.puzzletracker.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.study.puzzletracker.data.local.entity.AttemptEntity
import com.study.puzzletracker.data.local.entity.AttemptMode
import com.study.puzzletracker.data.local.entity.AttemptWithPuzzle
import kotlinx.coroutines.flow.Flow

@Dao
interface AttemptDao {
    @Insert
    suspend fun insert(attempt: AttemptEntity): Long

    @Query("SELECT * FROM attempts WHERE status = 'IN_PROGRESS' ORDER BY startedAt DESC")
    fun getInProgressAttempts(): Flow<List<AttemptEntity>>

    @Query("SELECT * FROM attempts WHERE id = :attemptId")
    fun getById(attemptId: Long): Flow<AttemptEntity?>

    @Query("""
        UPDATE attempts
        SET completionTimeSeconds = completionTimeSeconds + ((:now - runningSince) / 1000),
            runningSince = NULL
        WHERE id = :attemptId AND runningSince IS NOT NULL
    """)
    suspend fun pause(attemptId: Long, now: Long = System.currentTimeMillis())

    @Query("UPDATE attempts SET runningSince = :now WHERE id = :attemptId AND runningSince IS NULL")
    suspend fun resume(attemptId: Long, now: Long = System.currentTimeMillis())

    @Query("UPDATE attempts SET status = 'FINISHED' WHERE id = :attemptId")
    suspend fun finish (attemptId: Long)

    @Query("DELETE FROM attempts WHERE id = :attemptId AND status = 'IN_PROGRESS'")
    suspend fun deleteIfInProgress(attemptId: Long)

    @Query("""
        UPDATE attempts SET mode = :mode, numberOfPuzzlers = :numberOfPuzzlers
        WHERE id = :attemptId
    """)
    suspend fun editDetails(attemptId: Long, mode: AttemptMode?, numberOfPuzzlers: Int?)

    @Query("UPDATE attempts SET status = 'IN_PROGRESS' WHERE id = :attemptId")
    suspend fun reopen(attemptId: Long)

    @Query("""
        SELECT
            attempts.*,
            pc.name AS puzzleName,
            pc.manufacturer AS puzzleManufacturer,
            pc.pieceCount AS puzzlePieceCount
        FROM attempts
        INNER JOIN puzzle_details pd ON pd.id = attempts.puzzleDetailsId
        INNER JOIN puzzle_cache pc ON pc.id = pd.puzzleId
        WHERE attempts.status = 'IN_PROGRESS'
        ORDER BY attempts.startedAt DESC
    """)
    fun getInProgressAttemptsWithPuzzle(): Flow<List<AttemptWithPuzzle>>

    @Query("""
        SELECT
            attempts.*,
            pc.name AS puzzleName,
            pc.manufacturer AS puzzleManufacturer,
            pc.pieceCount AS puzzlePieceCount
        FROM attempts
        INNER JOIN puzzle_details pd ON pd.id = attempts.puzzleDetailsId
        INNER JOIN puzzle_cache pc ON pc.id = pd.puzzleId
        WHERE attempts.id = :attemptId
    """)
    fun getByIdWithPuzzle(attemptId: Long): Flow<AttemptWithPuzzle?>

    @Query("""
        SELECT completionTimeSeconds FROM attempts
        INNER JOIN puzzle_details pd ON pd.id = attempts.puzzleDetailsId
        WHERE pd.puzzleId = :puzzleId AND attempts.status = 'FINISHED'
        ORDER BY attempts.startedAt ASC
    """)
    fun getCompletionTimesForPuzzle(puzzleId: String): Flow<List<Long>>
}