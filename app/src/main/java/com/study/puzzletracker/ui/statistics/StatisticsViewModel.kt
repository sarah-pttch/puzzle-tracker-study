package com.study.puzzletracker.ui.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.study.puzzletracker.data.local.dao.PuzzleDetailsDao
import com.study.puzzletracker.data.repository.AttemptRepository
import com.study.puzzletracker.data.repository.CollectionRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class StatisticsViewModel(
    collectionRepository: CollectionRepository,
    puzzleDetailsDao: PuzzleDetailsDao,
    attemptRepository: AttemptRepository
) : ViewModel() {
    val uiState: StateFlow<StatisticsUIState> = combine(
        collectionRepository.getAllCollections().map { it.size },
        puzzleDetailsDao.getPuzzleCount(),
        attemptRepository.getAllFinishedAttemptsWithPuzzle()
    ) { _, puzzleCount, attempts ->
        val firstAttemptPerPuzzle = attempts
            .groupBy { it.puzzleId }
            .mapNotNull { (_, rows) -> rows.minByOrNull { it.startedAt } }

        StatisticsUIState(
            collectionCount = 0,
            puzzleCount = puzzleCount,
            attemptCount = attempts.size,
            fastest500 = attempts
                .filter { it.pieceCount == 500 }
                .minByOrNull { it.completionTimeSeconds }
                ?.let { FastestAttempt(it.puzzleName, it.completionTimeSeconds) },
            fastest1000 = attempts
                .filter { it.pieceCount == 1000 }
                .minByOrNull { it.completionTimeSeconds }
                ?.let { FastestAttempt(it.puzzleName, it.completionTimeSeconds) },
            fastest500FirstAttempt = firstAttemptPerPuzzle
                .filter { it.pieceCount == 500 }
                .minByOrNull { it.completionTimeSeconds }
                ?.let { FastestAttempt(it.puzzleName, it.completionTimeSeconds) }
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), StatisticsUIState())
}