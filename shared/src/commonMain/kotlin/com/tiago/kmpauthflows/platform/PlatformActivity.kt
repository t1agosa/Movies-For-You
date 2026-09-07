package com.tiago.kmpauthflows.platform

import androidx.compose.runtime.Composable

/**
 * Representa "la pantalla nativa actual" de forma opaca — domain nunca ve
 * sus miembros, solo la recibe y la reenvía. Solo Google/Apple Sign-In
 * la necesitan de verdad (para lanzar su UI nativa); el resto de los
 * flujos de auth (email, logout, observeAuthState) no la tocan.
 */
expect class PlatformActivity()

@Composable
expect fun rememberCurrentPlatformActivity(): PlatformActivity