package com.pdm0126.tutorconnectproyect.data.repository

import com.pdm0126.tutorconnectproyect.data.model.Booking
import com.pdm0126.tutorconnectproyect.data.model.ChatMessage
import com.pdm0126.tutorconnectproyect.data.model.Post
import com.pdm0126.tutorconnectproyect.data.model.User
import com.pdm0126.tutorconnectproyect.domain.Resource
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUser: Flow<User?>
    suspend fun login(email: String, pass: String): Resource<User>
    suspend fun register(email: String, pass: String, name: String, isTutor: Boolean): Resource<User>
    suspend fun logout(): Resource<Unit>
}

interface TutorRepository {
    suspend fun getAllTutors(): Resource<List<User>>
    suspend fun getTutorById(tutorId: String): Resource<User>
}

interface PostRepository {
    fun getAllPosts(): kotlinx.coroutines.flow.Flow<Resource<List<Post>>>
    suspend fun createPost(post: Post): Resource<Unit>
    suspend fun addComment(comment: com.pdm0126.tutorconnectproyect.data.model.Comment): Resource<Unit>
    fun getComments(postId: String): kotlinx.coroutines.flow.Flow<Resource<List<com.pdm0126.tutorconnectproyect.data.model.Comment>>>
}

interface BookingRepository {
    suspend fun createBooking(booking: Booking): Resource<Unit>
    // Un solo método inteligente: Si es tutor busca donde él es tutor, si es estudiante busca sus solicitudes
    suspend fun getBookingsForUser(userId: String, isTutor: Boolean): Resource<List<Booking>>
    suspend fun updateBookingStatus(bookingId: String, newStatus: String): Resource<Unit>
}

interface ChatRepository {
    suspend fun sendMessage(message: ChatMessage): Resource<Unit>
    // NOTA TÉCNICA: Usamos Flow en lugar de 'suspend' porque un chat
    // necesita emitir datos en tiempo real cada vez que llega un mensaje nuevo.
    fun getMessages(userId1: String, userId2: String): Flow<Resource<List<ChatMessage>>>

    fun getGroupChatsForUser(userId: String): kotlinx.coroutines.flow.Flow<Resource<List<com.pdm0126.tutorconnectproyect.data.model.GroupChat>>>
    suspend fun sendGroupMessage(message: com.pdm0126.tutorconnectproyect.data.model.GroupMessage): Resource<Unit>
    fun getGroupMessages(groupChatId: String): kotlinx.coroutines.flow.Flow<Resource<List<com.pdm0126.tutorconnectproyect.data.model.GroupMessage>>>
}
