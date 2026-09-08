package com.tiago.kmpauthflows.data.firestore

import kotlinx.coroutines.flow.Flow

data class PersonalListItemDto(
    val movieId: Int,
    val createdAtMillis: Long
)

expect class PersonalListFirestoreService(collectionName: String) {
    fun observeItems(): Flow<List<PersonalListItemDto>>
    suspend fun toggleItem(movieId: Int)
}