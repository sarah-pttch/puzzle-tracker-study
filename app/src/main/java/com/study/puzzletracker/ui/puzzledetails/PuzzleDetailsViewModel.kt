package com.study.puzzletracker.ui.puzzledetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.study.puzzletracker.data.local.dao.PuzzleDetailsDao
import com.study.puzzletracker.data.local.entity.Difficulty
import com.study.puzzletracker.data.local.entity.PuzzleCacheEntity
import com.study.puzzletracker.data.local.entity.PuzzleDetailsEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PuzzleDetailsViewModel(
    private val puzzleId: String,
    private val dao: PuzzleDetailsDao
) : ViewModel() {

    val puzzleCache: StateFlow<PuzzleCacheEntity?> = dao.getPuzzleCache(puzzleId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val details: StateFlow<PuzzleDetailsEntity?> = dao.getDetails(puzzleId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun saveDetails(difficulty: Difficulty?, tags: String?, notes: String?) {
        viewModelScope.launch {
            dao.updateDetails(puzzleId, difficulty, tags, notes)
        }
    }
}