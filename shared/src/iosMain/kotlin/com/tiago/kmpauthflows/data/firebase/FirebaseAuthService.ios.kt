package com.tiago.kmpauthflows.data.firebase

import com.tiago.kmpauthflows.data.model.FirebaseUserData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * TODO (requiere Mac): implementar con el SDK nativo de Firebase Auth para iOS.
 *
 * Pasos pendientes, documentados para retomar:
 * 1. Agregar el plugin `kotlin("native.cocoapods")` en shared/build.gradle.kts.
 * 2. Declarar el bloque `cocoapods { pod("FirebaseAuth") }`.
 * 3. Correr `pod install` desde iosApp/ (requiere CocoaPods instalado, solo en macOS).
 * 4. Reemplazar este archivo con la implementación real, usando
 *    suspendCancellableCoroutine para envolver los callbacks de
 *    Auth.auth().signIn(withEmail:password:completion:), etc.
 * 5. Mapear los NSError de Firebase iOS a AuthException, mismo criterio
 *    que la versión Android (ver FirebaseAuthService.android.kt).
 */
actual class FirebaseAuthService {

    actual suspend fun signInWithEmail(email: String, password: String): FirebaseUserData =
        throw NotImplementedError("Pendiente: implementar con Mac. Ver TODO en este archivo.")

    actual suspend fun signUpWithEmail(email: String, password: String): FirebaseUserData =
        throw NotImplementedError("Pendiente: implementar con Mac. Ver TODO en este archivo.")

    actual suspend fun signInWithGoogleIdToken(idToken: String): FirebaseUserData =
        throw NotImplementedError("Pendiente: implementar con Mac. Ver TODO en este archivo.")

    actual suspend fun signInWithAppleCredential(idToken: String, rawNonce: String): FirebaseUserData =
        throw NotImplementedError("Pendiente: implementar con Mac. Ver TODO en este archivo.")

    actual suspend fun signOut() {
        throw NotImplementedError("Pendiente: implementar con Mac. Ver TODO en este archivo.")
    }

    actual suspend fun deleteCurrentUser() {
        throw NotImplementedError("Pendiente: implementar con Mac. Ver TODO en este archivo.")
    }

    actual fun observeAuthState(): Flow<FirebaseUserData?> = flowOf(null)
}