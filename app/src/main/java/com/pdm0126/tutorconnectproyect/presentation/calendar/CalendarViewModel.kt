package com.pdm0126.tutorconnectproyect.presentation.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pdm0126.tutorconnectproyect.data.model.TutoringSession
import com.pdm0126.tutorconnectproyect.data.repository.AuthRepository
import com.pdm0126.tutorconnectproyect.data.repository.BookingRepository
import com.pdm0126.tutorconnectproyect.domain.Resource
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
class CalendarViewModel @Inject constructor(
    private val bookingRepository: BookingRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

    private val _events = Channel<CalendarUiEvent>()
    val events = _events.receiveAsFlow()

    init { load() }

    fun onAction(action: CalendarUiAction) {
        when (action) {
            CalendarUiAction.Retry -> load()
            is CalendarUiAction.SessionClicked -> {
                val s = _uiState.value.sessions.firstOrNull { it.id == action.sessionId }
                _uiState.update { it.copy(selectedSession = s) }
            }
            CalendarUiAction.DismissDialog -> _uiState.update { it.copy(selectedSession = null) }
            is CalendarUiAction.UpdateStatus -> viewModelScope.launch {
                bookingRepository.updateBookingStatus(action.bookingId, action.newStatus)
                _uiState.update { it.copy(selectedSession = null) }
                load()
            }
        }
    }

    private fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            // 1. Saber quién está viendo el calendario
            val currentUser = authRepository.currentUser.firstOrNull()
            if (currentUser == null) {
                _uiState.update { it.copy(isLoading = false, error = "Usuario no autenticado") }
                return@launch
            }

            val isTutor = currentUser.isTutor || currentUser.role == "TUTOR"

            // 2. Traer las reservas filtradas desde Firebase
            when (val result = bookingRepository.getBookingsForUser(currentUser.id, isTutor)) {
                is Resource.Success -> {
                    // 3. Traducir 'Booking' (Firebase) a 'TutoringSession' (Tu UI)
                    val sessions = result.data.map { booking ->
                        TutoringSession(
                            id = booking.id,
                            title = booking.subject,
                            subject = booking.subject,
                            tutorName = booking.tutorName.ifEmpty { "Tutor Asignado" },
                            date = booking.date,
                            time = booking.time,
                            status = booking.status,
                            confirmed = (booking.status == "ACCEPTED")
                        )
                    }

                    _uiState.update { it.copy(sessions = sessions, isLoading = false, isTutor = isTutor) }
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = result.message) }
                }
                is Resource.Loading -> {}
            }
        }
    }
}
