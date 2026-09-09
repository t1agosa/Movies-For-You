package com.tiago.kmpauthflows.presentation.common

import com.tiago.kmpauthflows.domain.model.MovieException
import com.tiago.kmpauthflows.shared.generated.resources.Res
import com.tiago.kmpauthflows.shared.generated.resources.movie_error_network
import com.tiago.kmpauthflows.shared.generated.resources.movie_error_not_found
import com.tiago.kmpauthflows.shared.generated.resources.movie_error_unknown
import org.jetbrains.compose.resources.StringResource

fun MovieException.toMessageRes(): StringResource = when (this) {
    is MovieException.NetworkError -> Res.string.movie_error_network
    is MovieException.NotFound -> Res.string.movie_error_not_found
    is MovieException.Unknown -> Res.string.movie_error_unknown
}
