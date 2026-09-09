package com.tiago.kmpauthflows.data.firestore

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.tiago.kmpauthflows.domain.model.MovieException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.tasks.await

actual class PersonalListFirestoreService actual constructor(private val collectionName: String) {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private fun collection() =
        firestore.collection("users")
            .document(requireNotNull(auth.currentUser?.uid) { "No hay usuario autenticado" })
            .collection(collectionName)

    actual fun observeItems(): Flow<List<PersonalListItemDto>> = callbackFlow {
        val registration = collection().addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error.toMovieException())
                return@addSnapshotListener
            }
            val items = snapshot?.documents.orEmpty().map { doc ->
                PersonalListItemDto(
                    movieId = doc.id.toInt(),
                    createdAtMillis = doc.getLong("createdAtMillis") ?: 0L
                )
            }
            trySend(items)
        }
        awaitClose { registration.remove() }
    }
        .retryWhen { _, attempt ->
            // el listener murio (posible race entre logout y un listener
            // todavia activo). si hay sesion, reintentamos con un listener
            // NUEVO (no revivimos el viejo, Firestore no lo permite) hasta
            // 3 veces. sin sesion, no tiene sentido reintentar.
            val hasUser = auth.currentUser != null
            if (hasUser && attempt < 3) {
                delay(1000)
                true
            } else {
                false
            }
        }
        .catch { emit(emptyList()) } // si se agotaron los reintentos, no crashea: se ve vacio

    actual suspend fun toggleItem(movieId: Int) {
        try {
            val docRef = collection().document(movieId.toString())
            val snapshot = docRef.get().await()
            if (snapshot.exists()) {
                docRef.delete().await()
            } else {
                docRef.set(mapOf("createdAtMillis" to System.currentTimeMillis())).await()
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            throw e.toMovieException()
        }
    }

    private fun Exception.toMovieException(): MovieException = when (this) {
        is FirebaseFirestoreException -> when (code) {
            FirebaseFirestoreException.Code.UNAVAILABLE -> MovieException.NetworkError
            FirebaseFirestoreException.Code.NOT_FOUND -> MovieException.NotFound
            else -> MovieException.Unknown(this)
        }
        else -> MovieException.Unknown(this)
    }
}