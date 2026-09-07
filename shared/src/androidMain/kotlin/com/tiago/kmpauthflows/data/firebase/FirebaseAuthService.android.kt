package com.tiago.kmpauthflows.data.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.OAuthProvider
import com.tiago.kmpauthflows.data.model.FirebaseUserData
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

actual class FirebaseAuthService {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    actual suspend fun signInWithEmail(email: String, password: String): FirebaseUserData =
        runCatchingFirebase {
            auth.signInWithEmailAndPassword(email, password).await().user.toFirebaseUserData()
        }

    actual suspend fun signUpWithEmail(email: String, password: String): FirebaseUserData =
        runCatchingFirebase {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            result.user?.sendEmailVerification()?.await()
            result.user.toFirebaseUserData()
        }

    actual suspend fun signInWithGoogleIdToken(idToken: String): FirebaseUserData =
        runCatchingFirebase {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            auth.signInWithCredential(credential).await().user.toFirebaseUserData()
        }

    actual suspend fun signInWithAppleCredential(idToken: String, rawNonce: String): FirebaseUserData =
        runCatchingFirebase {
            val credential = OAuthProvider.newCredentialBuilder("apple.com")
                .setIdTokenWithRawNonce(idToken, rawNonce)
                .build()
            auth.signInWithCredential(credential).await().user.toFirebaseUserData()
        }

    actual suspend fun signOut() {
        auth.signOut()
    }

    actual suspend fun deleteCurrentUser(){
        auth.currentUser?.delete()?.await()
    }

    actual fun observeAuthState(): Flow<FirebaseUserData?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            trySend(firebaseAuth.currentUser?.toFirebaseUserData())
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }
}