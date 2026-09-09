package com.tiago.kmpauthflows.presentation.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface Destination : NavKey {
    @Serializable
    data object Login : Destination

    @Serializable
    data object Register : Destination

    @Serializable
    data object Home : Destination
    @Serializable
    data class Detail(val movieId: Int) : Destination
}