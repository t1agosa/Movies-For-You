package com.tiago.kmpauthflows.domain.usecase

import com.tiago.kmpauthflows.domain.model.User
import com.tiago.kmpauthflows.domain.repository.AuthRepository
import com.tiago.kmpauthflows.domain.util.Result

class LoginWithEmailUseCase(
    private val repository: AuthRepository,
    private val validateEmail: ValidateEmailUseCase,
    private val validatePassword: ValidatePasswordUseCase,
    private val enforceEmailVerification: EnforceEmailVerificationUseCase
) {
    suspend operator fun invoke(email: String, password: String): Result<User> {
        val emailCheck = validateEmail(email)
        if (emailCheck is Result.Error) return emailCheck

        val passwordCheck = validatePassword(password)
        if (passwordCheck is Result.Error) return passwordCheck

        val loginResult = repository.loginWithEmail(email.trim(), password)
        if (loginResult is Result.Error) return loginResult

        val user = (loginResult as Result.Success).data
        return enforceEmailVerification(user)
    }
}