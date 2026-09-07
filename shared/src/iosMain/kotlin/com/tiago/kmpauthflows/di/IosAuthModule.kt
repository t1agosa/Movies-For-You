package com.tiago.kmpauthflows.di

import com.tiago.kmpauthflows.data.apple.AppleSignInProvider
import com.tiago.kmpauthflows.data.google.GoogleSignInProvider
import com.tiago.kmpauthflows.data.repository.AuthRepositoryImpl
import com.tiago.kmpauthflows.domain.repository.AuthRepository
import com.tiago.kmpauthflows.domain.usecase.*
import org.koin.dsl.module

val iosAuthModule = module {
    single { GoogleSignInProvider() }
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
}