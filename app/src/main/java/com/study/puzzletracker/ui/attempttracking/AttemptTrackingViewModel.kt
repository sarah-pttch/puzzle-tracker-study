package com.study.puzzletracker.ui.attempttracking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.study.puzzletracker.data.local.entity.AttemptMode
import com.study.puzzletracker.data.local.entity.AttemptWithPuzzle
import com.study.puzzletracker.data.repository.AttemptRepository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AttemptTrackingViewModel(
    private val attemptId: Long,
    private val repository: AttemptRepository
) : ViewModel() {
    val attempt: StateFlow<AttemptWithPuzzle?> = repository.getAttempt(attemptId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _elapsedSeconds = MutableStateFlow(0L)
    val elapsedSeconds: StateFlow<Long> = _elapsedSeconds.asStateFlow()

    init {
        viewModelScope.launch {
            attempt.collectLatest { current ->
                val a = current?.attempt ?: return@collectLatest
                if (a.runningSince != null) {
                    while (true) {
                        _elapsedSeconds.value = a.completionTimeSeconds + (System.currentTimeMillis() - a.runningSince) / 1000
                        delay(1000)
                    }
                } else {
                    _elapsedSeconds.value = a.completionTimeSeconds
                }
            }
        }
    }

    fun pause() { viewModelScope.launch { repository.pause(attemptId) } }

    fun resume() { viewModelScope.launch { repository.resume(attemptId) } }

    fun finish() { viewModelScope.launch { repository.finish(attemptId) } }

    fun reopen() { viewModelScope.launch { repository.reopen(attemptId) } }

    suspend fun deleteAttempt() = repository.deleteIfInProgress(attemptId)

    fun editDetails(mode: AttemptMode?, numberOfPuzzlers: Int?) {
        viewModelScope.launch {
            repository.editDetails(attemptId, mode, numberOfPuzzlers)
        }
    }
}