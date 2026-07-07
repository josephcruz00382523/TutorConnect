package com.pdm0126.tutorconnectproyect.presentation.booking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pdm0126.tutorconnectproyect.data.model.Booking
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
class BookingViewModel @Inject constructor(
    private val bookingRepository: BookingRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BookingUiState())
    val uiState: StateFlow<BookingUiState> = _uiState.asStateFlow()

    private val _events = Channel<BookingUiEvent>()
    val events = _events.receiveAsFlow()

    private var tutorId: String = ""
    private var initialized = false

    fun onAction(action: BookingUiAction) {
        when (action) {
            is BookingUiAction.Init -> init(action.tutorId, action.tutorName)
            is BookingUiAction.SubjectChanged -> _uiState.update { it.copy(subject = action.value) }
            is BookingUiAction.DateChanged -> _uiState.update { it.copy(date = action.value) }
            is BookingUiAction.TimeChanged -> _uiState.update { it.copy(time = action.value) }
            is BookingUiAction.CommentsChanged -> _uiState.update { it.copy(comments = action.value) }
            BookingUiAction.Submit -> submit()
            BookingUiAction.Back -> emit(BookingUiEvent.Back)
        }
    }

    private fun init(id: String, name: String) {
        if (initialized) return
        initialized = true
        tutorId = id
        _uiState.update { it.copy(tutorName = name) }
    }

    private fun submit() {
        val current = _uiState.value
        if (!current.isValid) {
            _uiState.update { it.copy(showErrors = true) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true) }

            // Obtenemos al usuario que está logueado haciendo la reserva
            val currentUser = authRepository.currentUser.firstOrNull()

            if (currentUser == null) {
                _uiState.update { it.copy(isSubmitting = false) }
                _events.send(BookingUiEvent.ShowMessage("Debes iniciar sesión para reservar"))
                return@launch
            }

            // Creamos el modelo de Firebase
            val newBooking = Booking(
                studentId = currentUser.id,
                tutorId = tutorId,
                tutorName = current.tutorName,
                subject = current.subject.trim(),
                date = current.date.trim(),
                time = current.time.trim(),
                notes = current.comments.trim(),
                status = "PENDING"
            )

            // Enviamos a Firestore
            when (val result = bookingRepository.createBooking(newBooking)) {
                is Resource.Success -> {
                    _uiState.update { it.copy(isSubmitting = false) }
                    _events.send(BookingUiEvent.ShowMessage("¡Tutoría reservada con éxito!"))
                    _events.send(BookingUiEvent.Booked)
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isSubmitting = false) }
                    _events.send(BookingUiEvent.ShowMessage(result.message))
                }
                is Resource.Loading -> {}
            }
        }
    }

    private fun emit(event: BookingUiEvent) = viewModelScope.launch { _events.send(event) }
}
