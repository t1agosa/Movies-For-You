package com.tiago.kmpauthflows.domain.model

sealed class AuthException(cause: Throwable? = null) : Exception(cause) {
    data object InvalidCredentials : AuthException()
    data object UserNotFound : AuthException()
    data object EmailAlreadyInUse : AuthException()
    data object WeakPassword : AuthException()
    data object InvalidEmailFormat : AuthException()
    data object NetworkError : AuthException()
    data object SignInCancelled : AuthException()
    data object UnverifiedEmailExpired : AuthException()
    data class Unknown(val original: Throwable) : AuthException(original)
}
