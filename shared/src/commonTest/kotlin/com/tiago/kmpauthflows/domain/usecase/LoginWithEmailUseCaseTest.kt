package com.tiago.kmpauthflows.domain.usecase

import com.tiago.kmpauthflows.domain.fake.FakeAuthRepository
import com.tiago.kmpauthflows.domain.model.AuthException
import com.tiago.kmpauthflows.domain.model.AuthProvider
import com.tiago.kmpauthflows.domain.model.User
import com.tiago.kmpauthflows.domain.util.Result
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.assertNull

class LoginWithEmailUseCaseTest {

    private val fakeRepository = FakeAuthRepository()
    private val loginWithEmail = LoginWithEmailUseCase(
        repository = fakeRepository,
        validateEmail = ValidateEmailUseCase(),
        validatePassword = ValidatePasswordUseCase(),
        enforceEmailVerification = EnforceEmailVerificationUseCase(fakeRepository)
    )

    @Test
    fun `con credenciales validas y repositorio exitoso devuelve Success con el User`() = runTest {
        fakeRepository.userToReturn = User(
            id = "1",
            email = "tiago@example.com",
            displayName = "Tiago",
            photoUrl = null,
            provider = AuthProvider.EMAIL
        )

        val result = loginWithEmail("tiago@example.com", "password123")

        assertIs<Result.Success<User>>(result)
    }

    @Test
    fun `con email invalido no llega a llamar al repositorio`() = runTest {
        val result = loginWithEmail("email-invalido", "password123")

        assertIs<Result.Error>(result)
        assertIs<AuthException.InvalidEmailFormat>(result.exception)
        assertNull(fakeRepository.lastEmailUsed) // el repositorio nunca fue invocado
    }

    @Test
    fun `con password invalida no llega a llamar al repositorio`() = runTest {
        val result = loginWithEmail("tiago@example.com", "123")

        assertIs<Result.Error>(result)
        assertIs<AuthException.WeakPassword>(result.exception)
        assertNull(fakeRepository.lastEmailUsed)
    }

    @Test
    fun `si el repositorio falla con InvalidCredentials el UseCase propaga ese error`() = runTest {
        fakeRepository.exceptionToThrow = AuthException.InvalidCredentials

        val result = loginWithEmail("tiago@example.com", "password123")

        assertIs<Result.Error>(result)
        assertIs<AuthException.InvalidCredentials>(result.exception)
    }
}