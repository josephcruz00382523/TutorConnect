package com.pdm0126.tutorconnectproyect.presentation.postdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pdm0126.tutorconnectproyect.data.model.Comment
import com.pdm0126.tutorconnectproyect.data.repository.AuthRepository
import com.pdm0126.tutorconnectproyect.data.repository.PostRepository
import com.pdm0126.tutorconnectproyect.domain.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostDetailViewModel @Inject constructor(
    private val postRepository: PostRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {

    data class State(
        val comments: List<Comment> = emptyList(),
        val isLoading: Boolean = false,
        val input: String = "",
        val error: String? = null,
    )

    private val _state = MutableStateFlow(State())
    val state = _state.asStateFlow()
    private var postId: String = ""
    private var started = false

    fun start(postId: String) {
        if (started) return
        started = true
        this.postId = postId
        viewModelScope.launch {
            postRepository.getComments(postId).collect { res ->
                when (res) {
                    is Resource.Loading -> _state.update { it.copy(isLoading = true) }
                    is Resource.Success -> _state.update { it.copy(isLoading = false, comments = res.data) }
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
            postRepository.addComment(
                Comment(
                    postId = postId,
                    authorId = user?.id ?: "",
                    authorName = user?.name ?: "Usuario",
                    text = text,
                )
            )
        }
    }
}
