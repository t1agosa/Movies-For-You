package com.tiago.kmpauthflows.domain.usecase

import com.tiago.kmpauthflows.domain.fake.FakeAuthRepository
import com.tiago.kmpauthflows.domain.model.AuthProvider
import com.tiago.kmpauthflows.domain.model.User
import com.tiago.kmpauthflows.domain.util.Result
import com.tiago.kmpauthflows.platform.PlatformActivity
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.assertTrue

class LoginWithAppleUseCaseTest {

    private val fakeRepository = FakeAuthRepository()
    private val loginWithApple = LoginWithAppleUseCase(fakeRepository)

    @Test
    fun `delega en el repositorio y devuelve el resultado tal cual`() = runTest {
        fakeRepository.userToReturn = User(
            id = "1",
            email = "tiago@icloud.com",
            displayName = "Tiago",
            photoUrl = null,
            provider = AuthProvider.APPLE
        )

        val fakeActivity = PlatformActivity()
        val result = loginWithApple(fakeActivity)

        assertIs<Result.Success<User>>(result)
        assertTrue(fakeRepository.appleLoginCalled)
    }
}