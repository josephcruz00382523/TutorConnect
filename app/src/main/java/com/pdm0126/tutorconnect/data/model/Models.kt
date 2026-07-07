package com.pdm0126.tutorconnect.data.model

import com.google.firebase.firestore.DocumentId
import java.util.Date

enum class UserRole { TUTOR, TUTORADO }

enum class TutorStatus(val label: String) {
    ONLINE("En línea"),
    BUSY("Ocupado"),
    AVAILABLE("Disponible"),
}

// Modelo visual para el Dashboard y Listas
data class FeaturedPost(
    val id: String = "",
    val authorName: String = "",
    val handle: String = "@uca.edu.sv",
    val question: String = "",
    val photoUrl: String? = null,
)

data class Tutor(
    val id: String = "",
    val name: String = "",
    val specialty: String = "Tutor UCA",
    val faculty: String = "Facultad de Ingeniería",
    val subjects: List<String> = emptyList(),
    val bio: String = "Estudiante de excelencia académica dispuesto a ayudarte.",
    val schedule: List<String> = listOf("Lunes 10:00 - 12:00", "Miércoles 14:00 - 16:00"),
    val rating: Double = 0.0,
    val price: String = "Gratis",
    val status: TutorStatus = TutorStatus.ONLINE,
    val photoUrl: String? = null,
    val imageUrl: String? = null // Para compatibilidad
)

data class UserProfile(
    val id: String = "",
    val fullName: String = "",
    val institutionalEmail: String = "",
    val career: String = "",
    val role: UserRole = UserRole.TUTORADO,
    val photoUrl: String? = null
)

// Usuario genérico (Firebase Auth / Firestore)
data class User(
    @DocumentId val id: String = "",
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val role: String = "TUTORADO", // "TUTOR" o "TUTORADO"
    val profileImageUrl: String = "",
    val bio: String = "",
    val subjects: List<String> = emptyList(),
    val rating: Double = 0.0,
    val isTutor: Boolean = false,
    val teachingSubjects: List<String> = emptyList(),   // materias que imparte
    val enrolledSubjects: List<String> = emptyList()     // materias que lleva como tutorado
)

data class Post(
    @DocumentId val id: String = "",
    val authorId: String = "",
    val authorName: String = "",
    val title: String = "",
    val content: String = "",
    val fileUrl: String = "",
    val fileType: String = "",   // "image" o "pdf"
    val timestamp: Date = Date(),
    val tags: List<String> = emptyList()
)

data class Comment(
    @DocumentId val id: String = "",
    val postId: String = "",
    val authorId: String = "",
    val authorName: String = "",
    val text: String = "",
    val timestamp: Date = Date()
)

data class Booking(
    @DocumentId val id: String = "",
    val studentId: String = "",
    val tutorId: String = "",
    val tutorName: String = "",
    val subject: String = "",
    val date: String = "",
    val time: String = "",
    val status: String = "PENDING",
    val notes: String = ""
)

data class ChatMessage(
    @DocumentId val id: String = "",
    val senderId: String = "",
    val receiverId: String = "",
    val message: String = "",
    val timestamp: Date = Date(),
    val fromMe: Boolean = false // Auxiliar para UI
)

data class UiChatMessage(
    val id: String = "",
    val text: String = "",
    val fromMe: Boolean = false,
    val timestamp: String = ""
)

data class TutoringSession(
    val id: String = "",
    val title: String = "",
    val subject: String = "", // Added subject
    val tutorName: String = "",
    val date: String = "",
    val time: String = "",
    val status: String = "PENDING",
    val confirmed: Boolean = false // Added confirmed
)

data class GroupChat(
    @DocumentId val id: String = "",
    val name: String = "",      // ej: "Cálculo Avanzado - Tutor Juan"
    val subject: String = "",   // ej: "Cálculo Avanzado"
    val tutorId: String = "",
    val tutorName: String = "",
    val members: List<String> = emptyList(),   // IDs de los miembros (incluye tutorId)
    val createdAt: Date = Date()
)

data class GroupMessage(
    @DocumentId val id: String = "",
    val groupChatId: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val text: String = "",
    val timestamp: Date = Date()
)
