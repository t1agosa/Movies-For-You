package com.tiago.kmpauthflows.presentation.login

import app.cash.turbine.test
import com.tiago.kmpauthflows.domain.fake.FakeAuthRepository
import com.tiago.kmpauthflows.domain.model.AuthException
import com.tiago.kmpauthflows.domain.model.AuthProvider
import com.tiago.kmpauthflows.domain.model.User
import com.tiago.kmpauthflows.domain.usecase.EnforceEmailVerificationUseCase
import com.tiago.kmpauthflows.domain.usecase.LoginWithAppleUseCase
import com.tiago.kmpauthflows.domain.usecase.LoginWithEmailUseCase
import com.tiago.kmpauthflows.domain.usecase.LoginWithGoogleUseCase
import com.tiago.kmpauthflows.domain.usecase.ValidateEmailUseCase
import com.tiago.kmpauthflows.domain.usecase.ValidatePasswordUseCase
import com.tiago.kmpauthflows.platform.PlatformActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val fakeRepository = FakeAuthRepository()

    private fun createViewModel() = LoginViewModel(
        validateEmailUseCase = ValidateEmailUseCase(),
        validatePasswordUseCase = ValidatePasswordUseCase(),
        loginWithEmailUseCase = LoginWithEmailUseCase(
            fakeRepository, ValidateEmailUseCase(), ValidatePasswordUseCase(),
            EnforceEmailVerificationUseCase(fakeRepository)
        ),
        loginWithGoogleUseCase = LoginWithGoogleUseCase(fakeRepository),
        loginWithAppleUseCase = LoginWithAppleUseCase(fakeRepository)
    )

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `email invalido muestra emailError y no llama al repositorio`() = runTest {
        val viewModel = createViewModel()

        viewModel.onEvent(LoginEvent.OnEmailChanged("email-invalido"))
        viewModel.onEvent(LoginEvent.OnPasswordChanged("password123"))
        viewModel.onEvent(LoginEvent.OnLoginClicked)

        assertNotNull(viewModel.state.value.emailError)
        assertNull(fakeRepository.lastEmailUsed)
    }

    @Test
    fun `login exitoso con email emite NavigateToHome`() = runTest {
        fakeRepository.userToReturn = User(
            id = "1", email = "tiago@example.com", displayName = "Tiago",
            photoUrl = null, provider = AuthProvider.EMAIL
        )
        val viewModel = createViewModel()

        viewModel.effect.test {
            viewModel.onEvent(LoginEvent.OnEmailChanged("tiago@example.com"))
            viewModel.onEvent(LoginEvent.OnPasswordChanged("password123"))
            viewModel.onEvent(LoginEvent.OnLoginClicked)

            assertEquals(LoginEffect.NavigateToHome, awaitItem())
        }
    }

    @Test
    fun `login fallido con credenciales invalidas emite ShowError`() = runTest {
        fakeRepository.exceptionToThrow = AuthException.InvalidCredentials
        val viewModel = createViewModel()

        viewModel.effect.test {
            viewModel.onEvent(LoginEvent.OnEmailChanged("tiago@example.com"))
            viewModel.onEvent(LoginEvent.OnPasswordChanged("password123"))
            viewModel.onEvent(LoginEvent.OnLoginClicked)

            val effect = awaitItem()
            assertIs<LoginEffect.ShowError>(effect)
        }
    }

    @Test
    fun `cancelar Google Sign-In no emite ningun effect`() = runTest {
        fakeRepository.exceptionToThrow = AuthException.SignInCancelled
        val viewModel = createViewModel()
        val fakeActivity = PlatformActivity()

        viewModel.effect.test {
            viewModel.onEvent(LoginEvent.OnGoogleSignInClicked(fakeActivity))
            expectNoEvents() // ningún Effect — SignInCancelled se ignora a propósito
        }

        assertEquals(null, viewModel.state.value.loadingTarget) // el loading sí se apaga
    }

    @Test
    fun `OnGoToRegisterClicked emite NavigateToRegister`() = runTest {
        val viewModel = createViewModel()

        viewModel.effect.test {
            viewModel.onEvent(LoginEvent.OnGoToRegisterClicked)
            assertEquals(LoginEffect.NavigateToRegister, awaitItem())
        }
    }
}