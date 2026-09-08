package com.tiago.kmpauthflows.data.firestore

import kotlinx.coroutines.flow.Flow

actual class PersonalListFirestoreService actual constructor(private val collectionName: String) {

    actual fun observeItems(): Flow<List<PersonalListItemDto>> {
        throw NotImplementedError("Firestore en iOS pendiente - ver README (sin acceso a Mac)")
    }

    actual suspend fun toggleItem(movieId: Int) {
        throw NotImplementedError("Firestore en iOS pendiente - ver README (sin acceso a Mac)")
    }
}