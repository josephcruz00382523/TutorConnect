package com.pdm0126.tutorconnectproyect.presentation.tutor_home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pdm0126.tutorconnectproyect.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TutorDashboardUiState(
    val userName: String = "",
    val isTutor: Boolean = false
)

sealed interface TutorDashboardUiEvent {
    data object SwitchToStudentView : TutorDashboardUiEvent
    data object NavigateToLogin : TutorDashboardUiEvent
}

@HiltViewModel
class TutorDashboardViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    val uiState: StateFlow<TutorDashboardUiState> = authRepository.currentUser
        .map { user ->
            TutorDashboardUiState(
                userName = user?.name ?: "",
                isTutor = user?.isTutor ?: false
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = TutorDashboardUiState()
        )

    private val _events = Channel<TutorDashboardUiEvent>()
    val events = _events.receiveAsFlow()

    fun onSwitchToStudent() {
        viewModelScope.launch {
            _events.send(TutorDashboardUiEvent.SwitchToStudentView)
        }
    }

    fun onLogout() {
        viewModelScope.launch {
            authRepository.logout()
            _events.send(TutorDashboardUiEvent.NavigateToLogin)
        }
    }
}
