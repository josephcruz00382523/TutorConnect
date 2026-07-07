package com.pdm0126.tutorconnectproyect.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.pdm0126.tutorconnectproyect.data.model.ChatMessage
import com.pdm0126.tutorconnectproyect.data.model.GroupChat
import com.pdm0126.tutorconnectproyect.data.model.GroupMessage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import com.pdm0126.tutorconnectproyect.domain.Resource
import javax.inject.Inject

class FirebaseChatRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) : ChatRepository {

    override suspend fun sendMessage(message: ChatMessage): Resource<Unit> {
        return try {
            val docRef = firestore.collection("messages").document()
            val newMessage = message.copy(id = docRef.id)
            docRef.set(newMessage).await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error al enviar mensaje")
        }
    }

    override fun getMessages(userId1: String, userId2: String): Flow<Resource<List<ChatMessage>>> = callbackFlow {
        // Consultamos todos los mensajes. En una app real de millones de usuarios se filtraría mejor,
        // pero para nuestro alcance universitario, filtramos localmente los que pertenecen a este chat.
        val listener = firestore.collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Resource.Error(error.message ?: "Error cargando chat"))
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val allMessages = snapshot.toObjects(ChatMessage::class.java)
                    // Filtramos solo los mensajes entre estos dos usuarios
                    val chatMessages = allMessages.filter {
                        (it.senderId == userId1 && it.receiverId == userId2) ||
                                (it.senderId == userId2 && it.receiverId == userId1)
                    }
                    trySend(Resource.Success(chatMessages))
                }
            }

        // Si el ViewModel muere, cancelamos el listener para evitar fugas de memoria
        awaitClose { listener.remove() }
    }

    override fun getGroupChatsForUser(userId: String): Flow<Resource<List<GroupChat>>> = callbackFlow {
        trySend(Resource.Loading)
        val listener = firestore.collection("groupChats")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Resource.Error(error.message ?: "Error cargando chats grupales"))
                    return@addSnapshotListener
                }
                val all = snapshot?.toObjects(GroupChat::class.java) ?: emptyList()
                val filtered = all.filter { userId in it.members }
                trySend(Resource.Success(filtered))
            }
        awaitClose { listener.remove() }
    }

    override suspend fun sendGroupMessage(message: GroupMessage): Resource<Unit> = try {
        val docRef = firestore.collection("groupChats").document(message.groupChatId)
            .collection("messages").document()
        docRef.set(message.copy(id = docRef.id)).await()
        Resource.Success(Unit)
    } catch (e: Exception) {
        Resource.Error(e.message ?: "Error al enviar mensaje grupal")
    }

    override fun getGroupMessages(groupChatId: String): Flow<Resource<List<GroupMessage>>> = callbackFlow {
        trySend(Resource.Loading)
        val listener = firestore.collection("groupChats").document(groupChatId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Resource.Error(error.message ?: "Error cargando mensajes"))
                    return@addSnapshotListener
                }
                val list = snapshot?.toObjects(GroupMessage::class.java) ?: emptyList()
                trySend(Resource.Success(list))
            }
        awaitClose { listener.remove() }
    }
}
