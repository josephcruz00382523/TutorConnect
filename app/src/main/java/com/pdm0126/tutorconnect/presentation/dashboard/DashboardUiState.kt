package com.pdm0126.tutorconnectproyect.presentation.dashboard

import com.pdm0126.tutorconnectproyect.data.model.FeaturedPost
import com.pdm0126.tutorconnectproyect.data.model.UiChatMessage

data class Subject(
    val id: String = "",
    val name: String = "",
    val completed: Boolean = false
)

data class DashboardUiState(
    val studentName: String = "",          // vendrá del backend al hacer login
    val tutorSubjects: List<Subject> = emptyList(),
    val additionalLoad: List<Subject> = emptyList(),
    val featuredPosts: List<FeaturedPost> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val messages: List<UiChatMessage> = emptyList(),
    val isTutor: Boolean = false
)

sealed interface DashboardUiAction {
    data object Retry : DashboardUiAction
    data class ReplyToPost(val postId: String) : DashboardUiAction
    data object OpenTutors : DashboardUiAction
    data object SwitchToTutorView : DashboardUiAction
    data object Logout : DashboardUiAction
}

sealed interface DashboardUiEvent {
    data object NavigateToTutors : DashboardUiEvent
    data class ShowMessage(val message: String) : DashboardUiEvent
    data object NavigateToTutorView : DashboardUiEvent
    data object NavigateToLogin : DashboardUiEvent
}
