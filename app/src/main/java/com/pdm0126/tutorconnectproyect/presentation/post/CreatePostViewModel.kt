package com.pdm0126.tutorconnectproyect.presentation.post

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pdm0126.tutorconnectproyect.data.model.Post
import com.pdm0126.tutorconnectproyect.data.repository.AuthRepository
import com.pdm0126.tutorconnectproyect.data.repository.FileUploader
import com.pdm0126.tutorconnectproyect.data.repository.PostRepository
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
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class CreatePostViewModel @Inject constructor(
    private val repository: PostRepository,
    private val authRepository: AuthRepository,
    private val fileUploader: FileUploader
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreatePostUiState())
    val uiState: StateFlow<CreatePostUiState> = _uiState.asStateFlow()

    private val _events = Channel<CreatePostUiEvent>()
    val events = _events.receiveAsFlow()

    fun onAction(action: CreatePostUiAction) {
        when (action) {
            is CreatePostUiAction.TitleChanged -> _uiState.update { it.copy(title = action.value) }
            is CreatePostUiAction.DescriptionChanged -> _uiState.update { it.copy(description = action.value) }
            is CreatePostUiAction.AttachFile -> _uiState.update { it.copy(attachmentUri = action.uri, attachmentName = action.fileName) }
            CreatePostUiAction.RemoveAttachment -> _uiState.update { it.copy(attachmentUri = null, attachmentName = null) }
            CreatePostUiAction.Submit -> submit()
        }
    }

    private fun submit() {
        val current = _uiState.value
        if (!current.isValid) {
            _uiState.update { it.copy(showErrors = true) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true) }
            val user = authRepository.currentUser.firstOrNull()

            var fileUrl = ""
            var fileType = ""
            var uploadErrorMessage: String? = null

            if (current.attachmentUri != null) {
                val uploadResult = fileUploader.upload(current.attachmentUri, current.attachmentName ?: "archivo")
                if (uploadResult is Resource.Success) {
                    fileUrl = uploadResult.data
                    fileType = if ((current.attachmentName ?: "").endsWith(".pdf", ignoreCase = true)) "pdf" else "image"
                } else if (uploadResult is Resource.Error) {
                    uploadErrorMessage = uploadResult.message
                }
            }

            val post = Post(
                authorId = user?.id ?: "",
                authorName = user?.name ?: "Usuario",
                title = current.title.trim(),
                content = current.description.trim(), // Tu UI le llama description, Firebase le llama content
                fileUrl = fileUrl,
                fileType = fileType,
                timestamp = Date()
            )

            when (val result = repository.createPost(post)) {
                is Resource.Success -> {
                    _uiState.update { CreatePostUiState() }
                    if (uploadErrorMessage != null) {
                        _events.send(CreatePostUiEvent.ShowMessage(uploadErrorMessage))
                    }
                    _events.send(CreatePostUiEvent.Published)
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isSubmitting = false) }
                    _events.send(CreatePostUiEvent.ShowMessage(result.message))
                }
                is Resource.Loading -> {}
            }
        }
    }
}
