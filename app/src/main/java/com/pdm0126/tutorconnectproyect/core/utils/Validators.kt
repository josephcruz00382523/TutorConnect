package com.pdm0126.tutorconnectproyect.core.utils

/** Simple, reusable input validation used across forms. */
object Validators {

    fun isInstitutionalEmail(email: String): Boolean =
        email.trim().lowercase().let {
            it.matches(Regex("^[a-z0-9._%+-]+@uca\\.edu\\.(sv|ni)$"))
        }

    fun isValidPassword(password: String): Boolean = password.length >= 6

    fun isNotBlank(value: String): Boolean = value.trim().isNotEmpty()
}
