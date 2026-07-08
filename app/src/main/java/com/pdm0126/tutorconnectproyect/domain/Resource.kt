package com.pdm0126.tutorconnectproyect.domain

/**
 * Generic wrapper for asynchronous results, used by repositories/ViewModels
 * to model Loading / Success / Error states cleanly.
 */

sealed interface Resource<out T> {
    data object Loading : Resource<Nothing>
    data class Success<T>(val data: T) : Resource<T>
    data class Error(val message: String) : Resource<Nothing>
}
