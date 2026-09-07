package com.tiago.kmpauthflows.domain.usecase

import com.tiago.kmpauthflows.domain.fake.FakeAuthRepository
import com.tiago.kmpauthflows.domain.model.AuthException
import com.tiago.kmpauthflows.domain.model.AuthProvider
import com.tiago.kmpauthflows.domain.model.User
import com.tiago.kmpauthflows.domain.util.Result
import com.tiago.kmpauthflows.platform.currentTimeMillis
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.assertTrue

class EnforceEmailVerificationUseCaseTest {

    private val fakeRepository = FakeAuthRepository()
    private val enforceEmailVerification = EnforceEmailVerificationUseCase(fakeRepository)

    @Test
    fun `usuario verificado pasa sin tocar el repositorio`() = runTest {
        val user = User(
            id = "1", email = "tiago@example.com", displayName = null, photoUrl = null, provider = AuthProvider.EMAIL, isEmailVerified = true, createdAtMillis = 0L
        )

        val result = enforceEmailVerification(user)

        assertIs<Result.Success<User>>(result)
        assertTrue(!fakeRepository.deleteCurrentUserCalled)
    }

    @Test
    fun `usuario sin verificar dentro de las 24hs pasa igual`() = runTest {
        val recentlyCreated = currentTimeMillis() - (2*60*60*1000L) // hace 2hs
        val user = User(
            id = "1", email = "tiago@example.com", displayName = null, photoUrl = null, provider = AuthProvider.EMAIL, isEmailVerified = false, createdAtMillis = recentlyCreated
        )

        val result = enforceEmailVerification(user)

        assertIs<Result.Success<User>>(result)
        assertTrue(!fakeRepository.deleteCurrentUserCalled)
    }

    @Test
    fun `usuario sin verificar despues de 24hs se elimina y devuelve error`() = runTest {
        val expiredCreation = currentTimeMillis() - (25*60*60*1000L) // hace 25hs
        val user = User(
            id = "1", email = "tiago@example.com", displayName = null, photoUrl = null, provider = AuthProvider.EMAIL, isEmailVerified = false, createdAtMillis = expiredCreation
        )

        val result = enforceEmailVerification(user)

        assertIs<Result.Error>(result)
        assertIs<AuthException.UnverifiedEmailExpired>(result.exception)
        assertTrue(fakeRepository.deleteCurrentUserCalled)
    }
}