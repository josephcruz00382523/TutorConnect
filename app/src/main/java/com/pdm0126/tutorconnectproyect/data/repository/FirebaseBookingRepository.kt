package com.pdm0126.tutorconnectproyect.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.pdm0126.tutorconnectproyect.data.model.Booking
import com.pdm0126.tutorconnectproyect.domain.Resource
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseBookingRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) : BookingRepository {

    override suspend fun createBooking(booking: Booking): Resource<Unit> {
        return try {
            // Generamos un ID automático para el documento en Firestore
            val documentRef = firestore.collection("bookings").document()
            val newBooking = booking.copy(id = documentRef.id)

            documentRef.set(newBooking).await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error al crear la reserva")
        }
    }

    override suspend fun getBookingsForUser(userId: String, isTutor: Boolean): Resource<List<Booking>> {
        return try {
            val fieldToSearch = if (isTutor) "tutorId" else "studentId"

            val snapshot = firestore.collection("bookings")
                .whereEqualTo(fieldToSearch, userId)
                .orderBy("date", Query.Direction.DESCENDING) // Ordenamos de más reciente a más antigua
                .get()
                .await()

            val bookings = snapshot.toObjects(Booking::class.java)
            Resource.Success(bookings)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error al cargar las reservas")
        }
    }

    override suspend fun updateBookingStatus(bookingId: String, newStatus: String): Resource<Unit> {
        return try {
            firestore.collection("bookings")
                .document(bookingId)
                .update("status", newStatus)
                .await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error al actualizar el estado")
        }
    }
}