package com.study.puzzletracker.ui.collections

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.study.puzzletracker.PuzzleTrackerApp

class CollectionsViewModelFactory(
    private val app: PuzzleTrackerApp
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CollectionsViewModel(
            app.container.collectionRepository,
            app.container.authRepository
        ) as T
    }
}