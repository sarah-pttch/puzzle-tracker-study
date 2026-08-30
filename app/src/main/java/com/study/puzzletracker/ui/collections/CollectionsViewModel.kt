package com.study.puzzletracker.ui.collections

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.study.puzzletracker.data.local.entity.CollectionEntity
import com.study.puzzletracker.data.repository.AuthRepository
import com.study.puzzletracker.data.repository.CollectionRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CollectionsViewModel(
    private val collectionRepository: CollectionRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    val collections: StateFlow<List<CollectionEntity>> = collectionRepository.getAllCollections()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun createCollection(name: String) {
        val puzzlerId = authRepository.currentUserId ?: return
        viewModelScope.launch {
            collectionRepository.createCollection(name, puzzlerId)
        }
    }
}