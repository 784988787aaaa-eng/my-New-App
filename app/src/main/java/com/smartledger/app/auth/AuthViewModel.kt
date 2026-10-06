package com.smartledger.app.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.smartledger.app.data.AppDatabaseProvider
import com.smartledger.core.domain.UserSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed interface AuthState {
    data object Loading : AuthState
    data class SignedIn(val session: UserSession) : AuthState
    data class SignIn(val firstRun: Boolean, val error: String? = null) : AuthState
}

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val manager = SessionManager(application)
    private val db = AppDatabaseProvider.get(application)
    private val _state = MutableStateFlow<AuthState>(AuthState.Loading)
    val state: StateFlow<AuthState> = _state

    init {
        val existing = manager.restore()
        if (existing != null) {
            _state.value = AuthState.SignedIn(existing)
        } else {
            viewModelScope.launch {
                _state.value = AuthState.SignIn(db.userDao().count() == 0)
            }
        }
    }

    fun login(username: String, password: String) {
        viewModelScope.launch {
            runCatching { manager.login(username, password) }
                .onSuccess { _state.value = AuthState.SignedIn(it) }
                .onFailure { _state.value = AuthState.SignIn(false, it.message) }
        }
    }

    fun bootstrapOwner(username: String, displayName: String, password: String) {
        viewModelScope.launch {
            runCatching { manager.bootstrapOwner(username, displayName, password) }
                .onSuccess { _state.value = AuthState.SignedIn(it) }
                .onFailure { _state.value = AuthState.SignIn(true, it.message) }
        }
    }

    fun logout() {
        manager.logout()
        _state.value = AuthState.SignIn(false)
    }
}
