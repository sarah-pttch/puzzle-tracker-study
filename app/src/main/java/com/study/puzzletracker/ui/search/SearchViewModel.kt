package com.study.puzzletracker.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.study.puzzletracker.data.NetworkMonitor
import com.study.puzzletracker.data.local.dao.PuzzleDetailsDao
import com.study.puzzletracker.data.remote.dto.PuzzleDto
import com.study.puzzletracker.data.repository.AuthRepository
import com.study.puzzletracker.data.repository.SearchRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchViewModel(
    private val searchRepository: SearchRepository,
    private val puzzleDetailsDao: PuzzleDetailsDao,
    private val authRepository: AuthRepository,
    private val networkMonitor: NetworkMonitor
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query = _query.asStateFlow()

    private val _uiState = MutableStateFlow<SearchUiState>(SearchUiState.Idle)
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    fun onQueryChange(value: String) {
        _query.value = value
        searchJob?.cancel()

        if (value.length < 3) {
            _uiState.value = SearchUiState.Idle
            return
        }
        if (!networkMonitor.isOnline()) {
            _uiState.value = SearchUiState.Offline
            return
        }

        _uiState.value = SearchUiState.Loading
        searchJob = viewModelScope.launch {
            delay(300)
            _uiState.value = try {
                SearchUiState.Results(
                    searchRepository.searchPuzzles(value, authRepository.currentUserId)
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                SearchUiState.Failed
            }
        }
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