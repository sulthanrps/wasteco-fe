package com.example.wasteco.ui.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.wasteco.WasteCoApplication
import com.example.wasteco.data.repository.AuthRepository
import com.example.wasteco.domain.model.SessionState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SessionViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val isValidated = MutableStateFlow(false)

    /**
     * [SessionState.Checking] until the stored token has been validated once on app start.
     * Afterwards it follows the token stream, so a cleared token (401 or logout)
     * immediately becomes [SessionState.Unauthenticated].
     */
    val sessionState: StateFlow<SessionState> = combine(
        isValidated,
        authRepository.userTokenStream
    ) { validated, token ->
        when {
            !validated -> SessionState.Checking
            token.isNullOrBlank() -> SessionState.Unauthenticated
            else -> SessionState.Authenticated
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = SessionState.Checking
    )

    init {
        viewModelScope.launch {
            authRepository.validateSession()
            isValidated.value = true
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = (this[APPLICATION_KEY] as WasteCoApplication)
                SessionViewModel(authRepository = app.container.authRepository)
            }
        }
    }
}
