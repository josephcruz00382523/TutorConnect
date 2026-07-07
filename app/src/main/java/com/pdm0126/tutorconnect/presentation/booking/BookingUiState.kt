package com.pdm0126.tutorconnectproyect.presentation.booking

data class BookingUiState(
    val tutorName: String = "",
    val subject: String = "",
    val date: String = "",
    val time: String = "",
    val comments: String = "",
    val isSubmitting: Boolean = false,
    val showErrors: Boolean = false,
) {
    val subjectError: Boolean get() = showErrors && subject.isBlank()
    val dateError: Boolean get() = showErrors && date.isBlank()
    val timeError: Boolean get() = showErrors && time.isBlank()
    val isValid: Boolean get() = subject.isNotBlank() && date.isNotBlank() && time.isNotBlank()
}

sealed interface BookingUiAction {
    data class Init(val tutorId: String, val tutorName: String) : BookingUiAction
    data class SubjectChanged(val value: String) : BookingUiAction
    data class DateChanged(val value: String) : BookingUiAction
    data class TimeChanged(val value: String) : BookingUiAction
    data class CommentsChanged(val value: String) : BookingUiAction
    data object Submit : BookingUiAction
    data object Back : BookingUiAction
}

sealed interface BookingUiEvent {
    data object Booked : BookingUiEvent
    data object Back : BookingUiEvent
    data class ShowMessage(val message: String) : BookingUiEvent
}
