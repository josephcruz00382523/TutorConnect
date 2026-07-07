package com.pdm0126.tutorconnectproyect.presentation.tutor_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pdm0126.tutorconnectproyect.data.repository.TutorRepository
import com.pdm0126.tutorconnectproyect.domain.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TutorDetailViewModel @Inject constructor(
    private val tutorRepository: TutorRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TutorDetailUiState())
    val uiState: StateFlow<TutorDetailUiState> = _uiState.asStateFlow()

    private val _events = Channel<TutorDetailUiEvent>()
    val events = _events.receiveAsFlow()

    fun onAction(action: TutorDetailUiAction) {
        when (action) {
            is TutorDetailUiAction.Load -> loadTutor(action.tutorId)
            TutorDetailUiAction.Book -> {
                val tutor = _uiState.value.tutor
                if (tutor != null) {
                    viewModelScope.launch { _events.send(TutorDetailUiEvent.Book(tutor.id, tutor.name)) }
                }
            }
            TutorDetailUiAction.OpenChat -> {
                val tutor = _uiState.value.tutor
                if (tutor != null) {
                    viewModelScope.launch { _events.send(TutorDetailUiEvent.OpenChat(tutor.id, tutor.name)) }
                }
            }
            TutorDetailUiAction.Back -> viewModelScope.launch { _events.send(TutorDetailUiEvent.Back) }
            TutorDetailUiAction.SendNudge -> viewModelScope.launch { _events.send(TutorDetailUiEvent.ShowMessage("¡Nudge enviado!")) }
        }
    }

    private fun loadTutor(tutorId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            when (val result = tutorRepository.getTutorById(tutorId)) {
                is Resource.Success -> {
                    val user = result.data
                    val mappedTutor = com.pdm0126.tutorconnectproyect.data.model.Tutor(
                        id = user.id,
                        name = user.name,
                        subjects = user.subjects,
                        rating = user.rating,
                        photoUrl = user.profileImageUrl,
                        bio = user.bio.ifBlank { "Estudiante de excelencia académica dispuesto a ayudarte." }
                    )
                    _uiState.update { it.copy(isLoading = false, tutor = mappedTutor) }
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = result.message) }
                }
                is Resource.Loading -> {}
            }
        }
    }
}
