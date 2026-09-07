package com.tiago.kmpauthflows.data.firebase

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import com.tiago.kmpauthflows.data.model.FirebaseUserData
import com.tiago.kmpauthflows.domain.model.AuthException
import kotlinx.coroutines.CancellationException

internal inline fun <T> runCatchingFirebase(block: () -> T): T = try {
    block()
} catch (e: CancellationException) {
    throw e // nunca capturar la cancelación — ver result_pattern.md
} catch (e: FirebaseAuthInvalidCredentialsException) {
    throw AuthException.InvalidCredentials
} catch (e: FirebaseAuthInvalidUserException) {
    throw AuthException.UserNotFound
} catch (e: FirebaseAuthUserCollisionException) {
    throw AuthException.EmailAlreadyInUse
} catch (e: FirebaseAuthWeakPasswordException) {
    throw AuthException.WeakPassword
} catch (e: FirebaseNetworkException) {
    throw AuthException.NetworkError
} catch (e: Exception) {
    throw AuthException.Unknown(e)
}

internal fun FirebaseUser?.toFirebaseUserData(): FirebaseUserData {
    val user = this ?: throw AuthException.Unknown(IllegalStateException("Firebase no devolvió un usuario"))
    val providerId = user.providerData
        .map { it.providerId }
        .firstOrNull { it != "firebase" }

    return FirebaseUserData(
        id = user.uid,
        email = user.email,
        displayName = user.displayName,
        photoUrl = user.photoUrl?.toString(),
        providerId = providerId,
        isEmailVerified = user.isEmailVerified,
        createdAtMillis = user.metadata?.creationTimestamp ?: 0L
    )
}