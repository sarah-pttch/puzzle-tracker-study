package com.study.puzzletracker.data.repository

import com.study.puzzletracker.data.local.dao.AttemptDao
import com.study.puzzletracker.data.local.dao.PuzzleDetailsDao
import com.study.puzzletracker.data.local.entity.AttemptEntity
import com.study.puzzletracker.data.local.entity.AttemptMode
import com.study.puzzletracker.data.local.entity.AttemptStatus

class AttemptRepository(
    private val attemptDao: AttemptDao,
    private val puzzleDetailsDao: PuzzleDetailsDao
) {
    suspend fun createAttempt(puzzleId: String): Long {
        val detailsId = requireNotNull(puzzleDetailsDao.getDetailsIdForPuzzle(puzzleId)) {
            "No PuzzleDetails found for puzzle $puzzleId - puzzle must be added to a collection first"
        }
        return attemptDao.insert(
            AttemptEntity(puzzleDetailsId = detailsId, startedAt = System.currentTimeMillis())
        )
    }

    suspend fun addAttemptManually(
        puzzleId: String, startedAt: Long, completionTimeSeconds: Long,
        mode: AttemptMode?, numberOfPuzzlers: Int?
    ) {
        val detailsId = requireNotNull(puzzleDetailsDao.getDetailsIdForPuzzle(puzzleId)) {
            "No PuzzleDetails found for puzzle $puzzleId - puzzle must be added to a collection first"
        }
        attemptDao.insert(
            AttemptEntity(
                puzzleDetailsId = detailsId,
                status = AttemptStatus.FINISHED,
                startedAt = startedAt,
                completionTimeSeconds = completionTimeSeconds,
                mode = mode,
                numberOfPuzzlers = numberOfPuzzlers
            )
        )
    }

    fun getInProgressAttempts() = attemptDao.getInProgressAttemptsWithPuzzle()

    fun getAttempt(attemptId: Long) = attemptDao.getByIdWithPuzzle(attemptId)

    fun getCompletionTimesForPuzzle(puzzleId: String) = attemptDao.getCompletionTimesForPuzzle(puzzleId)

    suspend fun pause(attemptId: Long) = attemptDao.pause(attemptId)

    suspend fun resume(attemptId: Long) = attemptDao.resume(attemptId)

    suspend fun finish(attemptId: Long) = attemptDao.finish(attemptId)

    suspend fun reopen(attemptId: Long) = attemptDao.reopen(attemptId)

    suspend fun deleteIfInProgress(attemptId: Long) = attemptDao.deleteIfInProgress(attemptId)

    suspend fun editDetails(attemptId: Long, mode: AttemptMode?, numberOfPuzzlers: Int?) =
        attemptDao.editDetails(attemptId, mode, numberOfPuzzlers)
}