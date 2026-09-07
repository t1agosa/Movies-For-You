package com.tiago.kmpauthflows.data.model

data class FirebaseUserData(
    val id: String,
    val email: String?,
    val displayName: String?,
    val photoUrl: String?,
    val providerId: String?,
    val isEmailVerified: Boolean,
    val createdAtMillis: Long
)