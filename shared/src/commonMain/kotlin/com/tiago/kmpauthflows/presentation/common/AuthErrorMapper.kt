package com.tiago.kmpauthflows.presentation.common

import com.tiago.kmpauthflows.domain.model.AuthException
import com.tiago.kmpauthflows.shared.generated.resources.Res
import com.tiago.kmpauthflows.shared.generated.resources.auth_error_email_already_in_use
import com.tiago.kmpauthflows.shared.generated.resources.auth_error_invalid_credentials
import com.tiago.kmpauthflows.shared.generated.resources.auth_error_invalid_email_format
import com.tiago.kmpauthflows.shared.generated.resources.auth_error_network
import com.tiago.kmpauthflows.shared.generated.resources.auth_error_sign_in_cancelled
import com.tiago.kmpauthflows.shared.generated.resources.auth_error_unknown
import com.tiago.kmpauthflows.shared.generated.resources.auth_error_unverified_email_expired
import com.tiago.kmpauthflows.shared.generated.resources.auth_error_user_not_found
import com.tiago.kmpauthflows.shared.generated.resources.auth_error_weak_password
import org.jetbrains.compose.resources.StringResource

fun AuthException.toMessageRes(): StringResource = when (this) {
    is AuthException.InvalidCredentials -> Res.string.auth_error_invalid_credentials
    is AuthException.UserNotFound -> Res.string.auth_error_user_not_found
    is AuthException.EmailAlreadyInUse -> Res.string.auth_error_email_already_in_use
    is AuthException.WeakPassword -> Res.string.auth_error_weak_password
    is AuthException.InvalidEmailFormat -> Res.string.auth_error_invalid_email_format
    is AuthException.NetworkError -> Res.string.auth_error_network
    is AuthException.SignInCancelled -> Res.string.auth_error_sign_in_cancelled
    is AuthException.UnverifiedEmailExpired -> Res.string.auth_error_unverified_email_expired
    is AuthException.Unknown -> Res.string.auth_error_unknown
}