package com.pdm0126.tutorconnectproyect.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.pdm0126.tutorconnectproyect.data.model.User
import com.pdm0126.tutorconnectproyect.domain.Resource

import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseTutorRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) : TutorRepository {

    override suspend fun getAllTutors(): Resource<List<User>> {
        return try {
            // Consulta real a Firestore: Solo traemos los que tienen rol TUTOR
            val snapshot = firestore.collection("users")
                .whereEqualTo("role", "TUTOR")
                .get()
                .await()

            val tutors = snapshot.toObjects(User::class.java)
            Resource.Success(tutors)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error al cargar tutores")
        }
    }

    override suspend fun getTutorById(tutorId: String): Resource<User> {
        return try {
            val snapshot = firestore.collection("users").document(tutorId).get().await()
            val tutor = snapshot.toObject(User::class.java)

            if (tutor != null) {
                Resource.Success(tutor)
            } else {
                Resource.Error("Tutor no encontrado")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error al cargar el perfil del tutor")
        }
    }
}
