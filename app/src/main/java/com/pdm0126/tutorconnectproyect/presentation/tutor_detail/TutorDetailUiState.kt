package com.pdm0126.tutorconnectproyect.presentation.tutor_detail

import com.pdm0126.tutorconnectproyect.data.model.Tutor

data class TutorDetailUiState(
    val tutor: Tutor? = null,
    val isLoading: Boolean = true,
    val error: String? = null,
)

sealed interface TutorDetailUiAction {
    data class Load(val tutorId: String) : TutorDetailUiAction
    data object SendNudge : TutorDetailUiAction
    data object OpenChat : TutorDetailUiAction
    data object Book : TutorDetailUiAction
    data object Back : TutorDetailUiAction
}

sealed interface TutorDetailUiEvent {
    data class OpenChat(val tutorId: String, val tutorName: String) : TutorDetailUiEvent
    data class Book(val tutorId: String, val tutorName: String) : TutorDetailUiEvent
    data object Back : TutorDetailUiEvent
    data class ShowMessage(val message: String) : TutorDetailUiEvent
}
