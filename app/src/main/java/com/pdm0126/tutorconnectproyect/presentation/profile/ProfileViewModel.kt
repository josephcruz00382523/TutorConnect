package com.pdm0126.tutorconnectproyect.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pdm0126.tutorconnectproyect.data.model.UserProfile
import com.pdm0126.tutorconnectproyect.data.model.UserRole
import com.pdm0126.tutorconnectproyect.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val _events = Channel<ProfileUiEvent>()
    val events = _events.receiveAsFlow()

    init { load() }

    fun onAction(action: ProfileUiAction) {
        when (action) {
            ProfileUiAction.Retry -> load()
            ProfileUiAction.EditProfile -> viewModelScope.launch { _events.send(ProfileUiEvent.ShowMessage("Próximamente")) }
            ProfileUiAction.Logout -> logout()
        }
    }

    private fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val user = authRepository.currentUser.firstOrNull()

            if (user != null) {
                // Adaptamos el 'User' de Firebase al 'UserProfile' que pide tu UI
                val profile = UserProfile(
                    id = user.id,
                    fullName = user.name,
                    institutionalEmail = user.email,
                    career = "Ingeniería", // Dato por defecto
                    role = if (user.role == "TUTOR") UserRole.TUTOR else UserRole.TUTORADO,
                    photoUrl = user.profileImageUrl
                )
                _uiState.update { it.copy(user = profile, isLoading = false) }
            } else {
                _uiState.update { it.copy(isLoading = false, error = "Usuario no encontrado") }
            }
        }
    }

    private fun logout() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoggingOut = true) }
            authRepository.logout()
            _uiState.update { it.copy(isLoggingOut = false) }
            _events.send(ProfileUiEvent.LoggedOut)
        }
    }
}
