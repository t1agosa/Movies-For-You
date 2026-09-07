package com.tiago.kmpauthflows.domain.model

data class User(
    val id: String,
    val email: String?,
    val displayName: String?,
    val photoUrl: String?,
    val provider: AuthProvider,
    val isEmailVerified: Boolean = true,
    val createdAtMillis: Long = 0L
)