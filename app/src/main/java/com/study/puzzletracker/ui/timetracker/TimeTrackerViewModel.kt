package com.study.puzzletracker.ui.timetracker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.study.puzzletracker.data.local.dao.PuzzleDetailsDao
import com.study.puzzletracker.data.local.entity.AttemptWithPuzzle
import com.study.puzzletracker.data.local.entity.PuzzleCacheEntity
import com.study.puzzletracker.data.repository.AttemptRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class TimeTrackerViewModel(
    private val attemptRepository: AttemptRepository,
    private val puzzleDetailsDao: PuzzleDetailsDao
) : ViewModel() {
    val inProgressAttempts: StateFlow<List<AttemptWithPuzzle>> = attemptRepository.getInProgressAttempts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val selectablePuzzles: StateFlow<List<PuzzleCacheEntity>> = puzzleDetailsDao
        .getAllPuzzlesWithDetails()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    suspend fun createAttempt(puzzleId: String): Long = attemptRepository.createAttempt(puzzleId)
}