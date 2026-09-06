package com.study.puzzletracker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.study.puzzletracker.ui.attempttracking.AttemptTrackingViewModel
import com.study.puzzletracker.ui.auth.AuthViewModel
import com.study.puzzletracker.ui.collectiondetails.CollectionDetailsViewModel
import com.study.puzzletracker.ui.collections.CollectionsViewModel
import com.study.puzzletracker.ui.home.HomeViewModel
import com.study.puzzletracker.ui.puzzledetails.PuzzleDetailsViewModel
import com.study.puzzletracker.ui.search.SearchViewModel
import com.study.puzzletracker.ui.statistics.StatisticsViewModel
import com.study.puzzletracker.ui.timetracker.TimeTrackerViewModel

class AppViewModelFactory(
    private val app: PuzzleTrackerApp,
    private val collectionId: Long? = null,
    private val puzzleId: String? = null,
    private val attemptId: Long? = null
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val container = app.container
        return when (modelClass) {
            AuthViewModel::class.java ->
                AuthViewModel(container.authRepository)

            CollectionsViewModel::class.java ->
                CollectionsViewModel(container.collectionRepository, container.authRepository)

            SearchViewModel::class.java ->
                SearchViewModel(container.searchRepository, container.puzzleDetailsDao)

            CollectionDetailsViewModel::class.java ->
                CollectionDetailsViewModel(
                    requireNotNull(collectionId) { "collectionId required for CollectionDetailsViewModel" },
                    container.collectionRepository,
                    container.puzzleDetailsDao
                )

            PuzzleDetailsViewModel::class.java ->
                PuzzleDetailsViewModel(
                    requireNotNull(puzzleId) {"puzzleId required for PuzzleDetailsViewModel"},
                    container.puzzleDetailsDao,
                    container.attemptRepository
                )

            TimeTrackerViewModel::class.java ->
                TimeTrackerViewModel(
                    container.attemptRepository,
                    container.puzzleDetailsDao
                )

            AttemptTrackingViewModel::class.java ->
                AttemptTrackingViewModel(
                    requireNotNull(attemptId) { "attemptId required for AttemptTrackingViewModel" },
                    container.attemptRepository
                )

            StatisticsViewModel::class.java ->
                StatisticsViewModel(
                    container.collectionRepository,
                    container.puzzleDetailsDao,
                    container.attemptRepository
                )

            HomeViewModel::class.java ->
                HomeViewModel(container.puzzleDetailsDao)

            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        } as T
    }
}