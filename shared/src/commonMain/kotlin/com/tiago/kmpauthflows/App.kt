package com.tiago.kmpauthflows

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.tiago.kmpauthflows.presentation.navigation.Destination
import com.tiago.kmpauthflows.ui.detail.DetailScreen
import com.tiago.kmpauthflows.ui.home.HomeScreen
import com.tiago.kmpauthflows.ui.login.LoginScreen
import com.tiago.kmpauthflows.ui.register.RegisterScreen
import com.tiago.kmpauthflows.ui.theme.KmpAuthFlowsTheme
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

// Configuración explícita de serialización — obligatoria en multiplatform
// (la sobrecarga simple de rememberNavBackStack solo funciona en Android, vía reflection).
private val navConfig = SavedStateConfiguration {
    serializersModule = SerializersModule {
        polymorphic(NavKey::class) {
            subclass(Destination.Login::class, Destination.Login.serializer())
            subclass(Destination.Register::class, Destination.Register.serializer())
            subclass(Destination.Home::class, Destination.Home.serializer())
            subclass(Destination.Detail::class, Destination.Detail.serializer())
        }
    }
}

@Composable
fun App() {
    KmpAuthFlowsTheme {
        val backStack = rememberNavBackStack(navConfig, Destination.Login)

        NavDisplay(
            backStack = backStack,
            onBack = { backStack.removeLastOrNull() },
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator()
            ),
            entryProvider = entryProvider {
                entry<Destination.Login> {
                    LoginScreen(
                        onNavigateToHome = { backStack.clear(); backStack.add(Destination.Home) },
                        onNavigateToRegister = { backStack.add(Destination.Register) }
                    )
                }
                entry<Destination.Register> {
                    RegisterScreen(
                        onNavigateToHome = { backStack.clear(); backStack.add(Destination.Home) },
                        onNavigateToLogin = { backStack.removeLastOrNull() }
                    )
                }
                entry<Destination.Home> {
                    HomeScreen(
                        onNavigateToLogin = { backStack.clear(); backStack.add(Destination.Login) },
                        onNavigateToDetail = { movieId -> backStack.add(Destination.Detail(movieId)) }
                    )
                }
                entry<Destination.Detail> { destination ->
                    DetailScreen(
                        movieId = destination.movieId,
                        onNavigateBack = { backStack.removeLastOrNull() }
                    )
                }
            }
        )
    }
}