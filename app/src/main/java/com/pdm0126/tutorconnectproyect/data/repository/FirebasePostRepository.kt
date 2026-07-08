package com.pdm0126.tutorconnectproyect.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.pdm0126.tutorconnectproyect.data.model.Comment
import com.pdm0126.tutorconnectproyect.data.model.Post
import com.pdm0126.tutorconnectproyect.domain.Resource
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebasePostRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) : PostRepository {

    override fun getAllPosts(): Flow<Resource<List<Post>>> = callbackFlow {
        trySend(Resource.Loading)
        val registration = firestore.collection("posts")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Resource.Error(error.message ?: "Error al cargar las publicaciones"))
                    return@addSnapshotListener
                }
                val posts = snapshot?.toObjects(Post::class.java) ?: emptyList()
                trySend(Resource.Success(posts))
            }
        awaitClose { registration.remove() }
    }

    override suspend fun createPost(post: Post): Resource<Unit> {
        return try {
            // Generamos una referencia con un ID único y automático de Firestore
            val documentRef = firestore.collection("posts").document()

            // Adjuntamos ese ID al modelo antes de guardarlo
            val newPost = post.copy(id = documentRef.id)

            documentRef.set(newPost).await()

            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error al crear la publicación")
        }
    }

    override suspend fun addComment(comment: Comment): Resource<Unit> = try {
        val docRef = firestore.collection("comments").document()
        docRef.set(comment.copy(id = docRef.id)).await()
        Resource.Success(Unit)
    } catch (e: Exception) {
        Resource.Error(e.message ?: "Error al comentar")
    }

    override fun getComments(postId: String): Flow<Resource<List<Comment>>> = callbackFlow {
        trySend(Resource.Loading)
        val registration = firestore.collection("comments")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Resource.Error(error.message ?: "Error cargando comentarios"))
                    return@addSnapshotListener
                }
                val list = snapshot?.toObjects(Comment::class.java)?.filter { it.postId == postId } ?: emptyList()
                trySend(Resource.Success(list))
            }
        awaitClose { registration.remove() }
    }
}
