package com.pdm0126.tutorconnectproyect.di

import com.pdm0126.tutorconnectproyect.data.repository.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: FirebaseAuthRepository
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindTutorRepository(
        tutorRepositoryImpl: FirebaseTutorRepository
    ): TutorRepository

    @Binds
    @Singleton
    abstract fun bindPostRepository(
        postRepositoryImpl: FirebasePostRepository
    ): PostRepository

    @Binds
    @Singleton
    abstract fun bindBookingRepository(
        bookingRepositoryImpl: FirebaseBookingRepository
    ): BookingRepository

    @Binds
    @Singleton
    abstract fun bindChatRepository(
        chatRepositoryImpl: FirebaseChatRepository
    ): ChatRepository

    @Binds
    abstract fun bindFileUploader(impl: PendingFileUploader): FileUploader
}
