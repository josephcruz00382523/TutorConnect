package com.pdm0126.tutorconnect.data.repository

import android.net.Uri
import com.pdm0126.tutorconnect.domain.Resource

interface FileUploader {
    suspend fun upload(uri: Uri, fileName: String): Resource<String>
}
