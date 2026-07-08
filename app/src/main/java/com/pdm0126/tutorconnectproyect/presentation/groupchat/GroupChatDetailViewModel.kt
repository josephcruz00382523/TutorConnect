package com.pdm0126.tutorconnectproyect.presentation.groupchat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pdm0126.tutorconnectproyect.data.model.GroupMessage
import com.pdm0126.tutorconnectproyect.data.repository.AuthRepository
import com.pdm0126.tutorconnectproyect.data.repository.ChatRepository
import com.pdm0126.tutorconnectproyect.domain.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroupChatDetailViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {

    data class State(
        val messages: List<GroupMessage> = emptyList(),
        val input: String = "",
        val isLoading: Boolean = false,
        val error: String? = null,
    )

    private val _state = MutableStateFlow(State())
    val state = _state.asStateFlow()
    private var groupChatId: String = ""
    private var started = false

    fun start(groupChatId: String) {
        if (started) return
        started = true
        this.groupChatId = groupChatId
        viewModelScope.launch {
            chatRepository.getGroupMessages(groupChatId).collect { res ->
                when (res) {
                    is Resource.Loading -> _state.update { it.copy(isLoading = true) }
                    is Resource.Success -> _state.update { it.copy(isLoading = false, messages = res.data) }
                    is Resource.Error -> _state.update { it.copy(isLoading = false, error = res.message) }
                }
            }
        }
    }

    fun onInputChange(t: String) = _state.update { it.copy(input = t) }

    fun send() {
        val text = _state.value.input.trim()
        if (text.isEmpty()) return
        viewModelScope.launch {
            val user = authRepository.currentUser.firstOrNull()
            _state.update { it.copy(input = "") }
            chatRepository.sendGroupMessage(
                GroupMessage(
                    groupChatId = groupChatId,
                    senderId = user?.id ?: "",
                    senderName = user?.name ?: "Usuario",
                    text = text,
                )
            )
        }
    }
}
