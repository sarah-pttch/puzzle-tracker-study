package com.study.puzzletracker.ui.search

import com.study.puzzletracker.data.remote.dto.PuzzleDto

sealed interface SearchUiState {
    data object Idle : SearchUiState
    data object Loading : SearchUiState
    data object Offline : SearchUiState
    data object Failed : SearchUiState
    data class Results(val puzzles: List<PuzzleDto>) : SearchUiState
}