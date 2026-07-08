package com.pdm0126.tutorconnectproyect.data.repository

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.pdm0126.tutorconnectproyect.domain.Resource
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseFileUploader @Inject constructor(
    private val storage: FirebaseStorage,
    private val auth: FirebaseAuth
) : FileUploader {

    override suspend fun upload(uri: Uri, fileName: String): Resource<String> {
        return try {
            val userId = auth.currentUser?.uid
                ?: return Resource.Error("Usuario no autenticado")
            val timestamp = System.currentTimeMillis()
            val safeName = fileName.replace(" ", "_")
            val ref = storage.reference.child("posts/$userId/${timestamp}_$safeName")

            ref.putFile(uri).await()
            val downloadUrl = ref.downloadUrl.await().toString()
            Resource.Success(downloadUrl)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error al subir el archivo")
        }
    }
}