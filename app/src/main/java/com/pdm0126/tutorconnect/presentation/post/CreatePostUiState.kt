package com.pdm0126.tutorconnectproyect.presentation.post

data class CreatePostUiState(
    val title: String = "",
    val description: String = "",
    val attachmentName: String? = null,
    val attachmentUri: android.net.Uri? = null,
    val isSubmitting: Boolean = false,
    val showErrors: Boolean = false,
) {
    val titleError: Boolean get() = showErrors && title.isBlank()
    val descriptionError: Boolean get() = showErrors && description.isBlank()
    val isValid: Boolean get() = title.isNotBlank() && description.isNotBlank()
}

sealed interface CreatePostUiAction {
    data class TitleChanged(val value: String) : CreatePostUiAction
    data class DescriptionChanged(val value: String) : CreatePostUiAction
    data class AttachFile(val uri: android.net.Uri, val fileName: String) : CreatePostUiAction
    data object RemoveAttachment : CreatePostUiAction
    data object Submit : CreatePostUiAction
}

sealed interface CreatePostUiEvent {
    data object Published : CreatePostUiEvent
    data class ShowMessage(val message: String) : CreatePostUiEvent
}
