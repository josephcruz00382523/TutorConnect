package com.pdm0126.tutorconnectproyect.presentation.calendar

import com.pdm0126.tutorconnectproyect.data.model.TutoringSession

data class CalendarUiState(
    val sessions: List<TutoringSession> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val selectedSession: TutoringSession? = null,
    val isTutor: Boolean = false
)

sealed interface CalendarUiAction {
    data object Retry : CalendarUiAction
    data class SessionClicked(val sessionId: String) : CalendarUiAction
    data object DismissDialog : CalendarUiAction
    data class UpdateStatus(val bookingId: String, val newStatus: String) : CalendarUiAction
}

sealed interface CalendarUiEvent {
    data class ShowMessage(val message: String) : CalendarUiEvent
}
