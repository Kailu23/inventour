package com.kailu.inventour.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.messaging.FirebaseMessaging
import com.kailu.inventour.model.User
import com.kailu.inventour.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AuthUiEvent {
    object AuthSuccess : AuthUiEvent()
}

data class AuthUiState(
    val user: User? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isAuthenticated: Boolean = false
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState(user = authRepository.currentUser, isAuthenticated = authRepository.currentUser != null))
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<AuthUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    init {
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = authRepository.login(email, password)
            result.onSuccess { user ->
                _uiState.update { it.copy(user = user, isLoading = false, isAuthenticated = true) }
                _uiEvent.emit(AuthUiEvent.AuthSuccess)
            }.onFailure { e ->
                _uiState.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = authRepository.register(name, email, password)
            result.onSuccess { user ->
                _uiState.update { it.copy(user = user, isLoading = false, isAuthenticated = true) }
                _uiEvent.emit(AuthUiEvent.AuthSuccess)
            }.onFailure { e ->
                _uiState.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    fun logout() {
        authRepository.logout()
        _uiState.update { it.copy(user = null, isAuthenticated = false) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
