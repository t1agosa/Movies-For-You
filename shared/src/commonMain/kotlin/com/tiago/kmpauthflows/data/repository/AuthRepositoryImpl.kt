package com.tiago.kmpauthflows.data.repository

import com.tiago.kmpauthflows.data.apple.AppleSignInProvider
import com.tiago.kmpauthflows.data.firebase.FirebaseAuthService
import com.tiago.kmpauthflows.data.google.GoogleSignInProvider
import com.tiago.kmpauthflows.data.model.FirebaseUserData
import com.tiago.kmpauthflows.domain.model.AuthException
import com.tiago.kmpauthflows.domain.model.AuthProvider
import com.tiago.kmpauthflows.domain.model.User
import com.tiago.kmpauthflows.domain.repository.AuthRepository
import com.tiago.kmpauthflows.domain.util.Result
import com.tiago.kmpauthflows.platform.PlatformActivity
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AuthRepositoryImpl(
    private val firebaseAuthService: FirebaseAuthService,
    private val googleSignInProvider: GoogleSignInProvider,
    private val appleSignInProvider: AppleSignInProvider
) : AuthRepository {

    override suspend fun loginWithEmail(email: String, password: String): Result<User> =
        runCatchingAuth {
            firebaseAuthService.signInWithEmail(email, password).toUser(AuthProvider.EMAIL)
        }

    override suspend fun registerWithEmail(email: String, password: String): Result<User> =
        runCatchingAuth {
            firebaseAuthService.signUpWithEmail(email, password).toUser(AuthProvider.EMAIL)
        }

    override suspend fun loginWithGoogle(activity: PlatformActivity): Result<User> = runCatchingAuth {
        val idToken = googleSignInProvider.requestIdToken(activity)
        firebaseAuthService.signInWithGoogleIdToken(idToken).toUser(AuthProvider.GOOGLE)
    }

    override suspend fun loginWithApple(activity: PlatformActivity): Result<User> = runCatchingAuth {
        appleSignInProvider.signIn(activity).toUser(AuthProvider.APPLE)
    }

    override suspend fun logout(): Result<Unit> = runCatchingAuth {
        firebaseAuthService.signOut()
    }

    override suspend fun deleteCurrentUser(): Result<Unit> = runCatchingAuth {
        firebaseAuthService.deleteCurrentUser()
    }

    override fun observeAuthState(): Flow<User?> =
        firebaseAuthService.observeAuthState().map { data -> data?.toUser(data.providerId.toAuthProvider()) }

    private inline fun <T> runCatchingAuth(block: () -> T): Result<T> = try {
        Result.Success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: AuthException) {
        Result.Error(e)
    } catch (e: Exception) {
        Result.Error(AuthException.Unknown(e))
    }
}

private fun FirebaseUserData.toUser(provider: AuthProvider): User = User(
    id = id,
    email = email,
    displayName = displayName,
    photoUrl = photoUrl,
    provider = provider,
    isEmailVerified = isEmailVerified,
    createdAtMillis = createdAtMillis
)

private fun String?.toAuthProvider(): AuthProvider = when (this) {
    "google.com" -> AuthProvider.GOOGLE
    "apple.com" -> AuthProvider.APPLE
    else -> AuthProvider.EMAIL
}