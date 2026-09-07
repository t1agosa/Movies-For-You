package com.tiago.kmpauthflows.domain.usecase

import com.tiago.kmpauthflows.domain.model.AuthException
import com.tiago.kmpauthflows.domain.model.User
import com.tiago.kmpauthflows.domain.repository.AuthRepository
import com.tiago.kmpauthflows.domain.util.Result
import com.tiago.kmpauthflows.platform.currentTimeMillis

class EnforceEmailVerificationUseCase(private val repository: AuthRepository){
    suspend operator fun invoke(user: User): Result<User> {
        if (user.isEmailVerified) return Result.Success(user)

        val elapsedMillis = currentTimeMillis() - user.createdAtMillis
        if (elapsedMillis < VERIFICATION_WINDOW_MILLIS) {
            return Result.Success(user) // todavía dentro de la ventana de 24hs
        }
        repository.deleteCurrentUser()
        return Result.Error(AuthException.UnverifiedEmailExpired)
    }

    private companion object {
        const val VERIFICATION_WINDOW_MILLIS = 24 * 60 * 60 * 1000L // 60 * 1000L (TEMPORAL PARA TESTING) — Volver a 24 * 60 * 60 * 1000L antes de commitear
    }
}