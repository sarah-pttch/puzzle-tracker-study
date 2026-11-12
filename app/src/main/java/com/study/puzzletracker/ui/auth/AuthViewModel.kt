package com.study.puzzletracker.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.study.puzzletracker.data.NetworkMonitor
import com.study.puzzletracker.data.repository.AuthRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
class AuthViewModel(
    private val authRepository: AuthRepository,
    private val networkMonitor: NetworkMonitor
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    fun onEmailChange(value: String) {
        _uiState.value = _uiState.value.copy(email = value, errorMessage = null)
    }

    fun onPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(password = value, errorMessage = null)
    }

    fun signIn() = runAuthAction("Signing in requires an internet connection.", "Sign in failed") {
        authRepository.signIn(_uiState.value.email, _uiState.value.password)
    }

    fun signUp() = runAuthAction("Creating an account requires an internet connection", "Sign up failed") {
        authRepository.signUp(_uiState.value.email, _uiState.value.password)
    }

    private fun runAuthAction(
        offlineMessage: String,
        failureMessage: String,
        action: suspend () -> Unit
    ) {
        if (!networkMonitor.isOnline()) {
            _uiState.value = _uiState.value.copy(errorMessage = offlineMessage)
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                action()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(errorMessage = e.message ?: failureMessage)
            } finally {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }
}