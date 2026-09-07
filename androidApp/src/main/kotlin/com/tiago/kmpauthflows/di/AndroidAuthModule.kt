package com.tiago.kmpauthflows.di

import com.tiago.kmpauthflows.R
import com.tiago.kmpauthflows.data.apple.AppleSignInProvider
import com.tiago.kmpauthflows.data.google.GoogleSignInProvider
import com.tiago.kmpauthflows.data.repository.AuthRepositoryImpl
import com.tiago.kmpauthflows.domain.repository.AuthRepository
import com.tiago.kmpauthflows.domain.usecase.*
import com.tiago.kmpauthflows.presentation.home.HomeViewModel
import com.tiago.kmpauthflows.presentation.login.LoginViewModel
import com.tiago.kmpauthflows.presentation.register.RegisterViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val androidAuthModule = module {
    single { GoogleSignInProvider(webClientId = androidContext().getString(R.string.default_web_client_id)) }
    single { AppleSignInProvider() }
    single<AuthRepository> { AuthRepositoryImpl(get(), get(), get()) }

    single { ValidateEmailUseCase() }
    single { ValidatePasswordUseCase() }
    single { EnforceEmailVerificationUseCase(get()) }
    single { LoginWithEmailUseCase(get(), get(), get(), get()) }
    single { RegisterWithEmailUseCase(get(), get(), get()) }
    single { LoginWithGoogleUseCase(get()) }
    single { LoginWithAppleUseCase(get()) }
    single { LogoutUseCase(get()) }
    single { ObserveAuthStateUseCase(get()) }

    viewModel { LoginViewModel(get(), get(), get(), get(), get()) }
    viewModel { RegisterViewModel(get(), get(), get()) }
    viewModel { HomeViewModel(get(), get()) }
}