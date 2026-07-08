package com.pdm0126.tutorconnectproyect.data.repository

import android.net.Uri
import com.pdm0126.tutorconnectproyect.domain.Resource

interface FileUploader {
    suspend fun upload(uri: Uri, fileName: String): Resource<String>
}
