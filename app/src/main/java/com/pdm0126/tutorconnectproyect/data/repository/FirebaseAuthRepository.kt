package com.pdm0126.tutorconnectproyect.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pdm0126.tutorconnectproyect.data.model.User
import com.pdm0126.tutorconnectproyect.data.model.UserRole
import com.pdm0126.tutorconnectproyect.domain.Resource

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseAuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {

    // Listener reactivo para saber si el usuario está logueado en toda la app
    override val currentUser: Flow<User?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val firebaseUser = firebaseAuth.currentUser
            if (firebaseUser != null) {
                // Si hay usuario en Auth, buscamos sus datos en Firestore
                firestore.collection("users").document(firebaseUser.uid)
                    .get()
                    .addOnSuccessListener { snapshot ->
                        val user = snapshot.toObject(User::class.java)
                        trySend(user)
                    }
                    .addOnFailureListener { trySend(null) }
            } else {
                trySend(null)
            }
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    override suspend fun login(email: String, pass: String): Resource<User> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, pass).await()
            val uid = authResult.user?.uid ?: throw Exception("User not found")

            val snapshot = firestore.collection("users").document(uid).get().await()
            val user = snapshot.toObject(User::class.java) ?: throw Exception("User data missing")

            Resource.Success(user)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error al iniciar sesión")
        }
    }

    override suspend fun register(email: String, pass: String, name: String, isTutor: Boolean): Resource<User> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, pass).await()
            val uid = authResult.user?.uid ?: throw Exception("Error creating user")

            val role = if (isTutor) UserRole.TUTOR.name else UserRole.TUTORADO.name
            val newUser = User(
                id = uid,
                uid = uid,
                name = name,
                email = email,
                role = role,
                isTutor = isTutor
            )

            // Guardamos el perfil en Firestore
            firestore.collection("users").document(uid).set(newUser).await()

            Resource.Success(newUser)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error al registrarse")
        }
    }

    override suspend fun logout(): Resource<Unit> {
        return try {
            auth.signOut()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error al cerrar sesión")
        }
    }
}
