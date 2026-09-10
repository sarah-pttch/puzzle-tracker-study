package com.study.puzzletracker.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.study.puzzletracker.data.local.dao.PuzzleDetailsDao
import com.study.puzzletracker.data.remote.dto.PuzzleDto
import com.study.puzzletracker.data.repository.AuthRepository
import com.study.puzzletracker.data.repository.SearchRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SearchViewModel(
    private val searchRepository: SearchRepository,
    private val puzzleDetailsDao: PuzzleDetailsDao,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query = _query.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    val results: StateFlow<List<PuzzleDto>> = _query
        .debounce(300)
        .distinctUntilChanged()
        .onEach { _isLoading.value = it.length >= 3 }
        .flatMapLatest { q ->
            if (q.length < 3) {
                flowOf(emptyList())
            } else {
                flow { emit(searchRepository.searchPuzzles(q)) }
            }
        }
        .onEach { _isLoading.value = false }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onQueryChange(value: String) {
        _query.value = value
    }

    fun addPuzzleToCollection(collectionId: Long, puzzle: PuzzleDto) {
        viewModelScope.launch {
            puzzleDetailsDao.addPuzzleToCollection(
                collectionId = collectionId,
                puzzleId = puzzle.id,
                name = puzzle.name,
                pieceCount = puzzle.pieceCount,
                manufacturer = puzzle.manufacturer
            )
        }
    }

    suspend fun createPuzzle(
        name: String, barcode: String?, pieceCount: Int, manufacturer: String,
        publicationYear: Int?, toGlobalCollection: Boolean
    ): PuzzleDto {
        return searchRepository.createPuzzle(
            name = name,
            barcode = barcode,
            pieceCount = pieceCount,
            manufacturer = manufacturer,
            publicationYear = publicationYear,
            toGlobalCollection = toGlobalCollection,
            submittedBy = authRepository.currentUserId
        )
    }
}