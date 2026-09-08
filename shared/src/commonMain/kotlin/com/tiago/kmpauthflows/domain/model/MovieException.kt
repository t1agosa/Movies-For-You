package com.tiago.kmpauthflows.domain.model

sealed class MovieException(cause: Throwable? = null) : Exception(cause) {
    data object  NetworkError : MovieException()
    data object NotFound : MovieException()
    data class Unknown(val original: Throwable) : MovieException(original)
}