package com.tiago.kmpauthflows.data.firebase

import com.tiago.kmpauthflows.data.model.FirebaseUserData
import kotlinx.coroutines.flow.Flow

expect class FirebaseAuthService() {
    suspend fun signInWithEmail(email: String, password: String): FirebaseUserData
    suspend fun signUpWithEmail(email: String, password: String): FirebaseUserData
    suspend fun signInWithGoogleIdToken(idToken: String): FirebaseUserData
    suspend fun signInWithAppleCredential(idToken: String, rawNonce: String): FirebaseUserData
    suspend fun signOut()
    suspend fun deleteCurrentUser()
    fun observeAuthState(): Flow<FirebaseUserData?>
}