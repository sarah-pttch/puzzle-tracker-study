package com.study.puzzletracker.ui.collectiondetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.study.puzzletracker.data.local.dao.PuzzleDetailsDao
import com.study.puzzletracker.data.local.entity.CollectionEntity
import com.study.puzzletracker.data.local.entity.PuzzleCacheEntity
import com.study.puzzletracker.data.repository.CollectionRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class CollectionDetailsViewModel(
    collectionId: Long,
    collectionRepository: CollectionRepository,
    puzzleDetailsDao: PuzzleDetailsDao
) : ViewModel() {
    val collection: StateFlow<CollectionEntity?> = collectionRepository
        .getCollectionById(collectionId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val puzzles: StateFlow<List<PuzzleCacheEntity>> = puzzleDetailsDao
        .getPuzzleIdsInCollection(collectionId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}