package com.study.puzzletracker.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.study.puzzletracker.PuzzleTrackerApp

class AuthViewModelFactory(
    private val app: PuzzleTrackerApp
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AuthViewModel(
            app.container.authRepository
        ) as T
    }
}