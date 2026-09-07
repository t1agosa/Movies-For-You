package com.tiago.kmpauthflows.domain.repository

import com.tiago.kmpauthflows.domain.model.User
import com.tiago.kmpauthflows.domain.util.Result
import com.tiago.kmpauthflows.platform.PlatformActivity
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun loginWithEmail(email: String, password: String): Result<User>
    suspend fun registerWithEmail(email: String, password: String): Result<User>
    suspend fun loginWithGoogle(activity: PlatformActivity): Result<User>
    suspend fun loginWithApple(activity: PlatformActivity): Result<User>
    suspend fun logout(): Result<Unit>
    suspend fun deleteCurrentUser():Result<Unit>
    fun observeAuthState(): Flow<User?>
}