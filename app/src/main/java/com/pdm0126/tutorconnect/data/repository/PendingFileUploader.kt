package com.pdm0126.tutorconnectproyect.data.repository

import android.net.Uri
import com.pdm0126.tutorconnectproyect.domain.Resource
import javax.inject.Inject

class PendingFileUploader @Inject constructor() : FileUploader {
    override suspend fun upload(uri: Uri, fileName: String): Resource<String> =
        Resource.Error("La subida de archivos está pendiente: el backend aún no está conectado.")
}
