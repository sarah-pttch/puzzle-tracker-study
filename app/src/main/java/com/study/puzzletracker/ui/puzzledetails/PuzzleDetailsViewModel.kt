package com.study.puzzletracker.ui.puzzledetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.study.puzzletracker.data.local.dao.PuzzleDetailsDao
import com.study.puzzletracker.data.local.entity.AttemptMode
import com.study.puzzletracker.data.local.entity.Difficulty
import com.study.puzzletracker.data.local.entity.PuzzleCacheEntity
import com.study.puzzletracker.data.local.entity.PuzzleDetailsEntity
import com.study.puzzletracker.data.repository.AttemptRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PuzzleDetailsViewModel(
    private val puzzleId: String,
    private val dao: PuzzleDetailsDao,
    private val attemptRepository: AttemptRepository
) : ViewModel() {

    val puzzleCache: StateFlow<PuzzleCacheEntity?> = dao.getPuzzleCache(puzzleId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val details: StateFlow<PuzzleDetailsEntity?> = dao.getDetails(puzzleId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val completionTimes: StateFlow<List<Long>> = attemptRepository.getCompletionTimesForPuzzle(puzzleId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )
    fun saveDetails(difficulty: Difficulty?, tags: String?, notes: String?) {
        viewModelScope.launch {
            dao.updateDetails(puzzleId, difficulty, tags, notes)
        }
    }

    fun addAttemptManually(startedAt: Long, completionTimeSeconds: Long, mode: AttemptMode?, numberOfPuzzlers: Int?) {
        viewModelScope.launch {
            attemptRepository.addAttemptManually(puzzleId, startedAt, completionTimeSeconds, mode, numberOfPuzzlers)
        }
    }

    suspend fun createAttempt(puzzleId: String): Long = attemptRepository.createAttempt(puzzleId)
}