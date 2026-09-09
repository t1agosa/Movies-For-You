package com.tiago.kmpauthflows.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.tiago.kmpauthflows.presentation.home.HomeEffect
import com.tiago.kmpauthflows.presentation.home.HomeEvent
import com.tiago.kmpauthflows.presentation.home.HomeViewModel
import com.tiago.kmpauthflows.ui.discover.DiscoverScreen
import com.tiago.kmpauthflows.ui.mymovies.MyMoviesScreen
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.koin.compose.viewmodel.koinViewModel

private enum class HomeTab { DISCOVER, MY_MOVIES }

@Composable
fun HomeScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToDetail: (Int) -> Unit,
    viewModel: HomeViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var showAccount by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf(HomeTab.DISCOVER) }
    var showMenu by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                HomeEffect.NavigateToLogin -> onNavigateToLogin()
                is HomeEffect.ShowError -> {
                    val message = getString(effect.messageRes)
                    scope.launch { snackbarHostState.showSnackbar(message) }
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(if (showAccount) "Mi cuenta" else "Movies For You") },
                navigationIcon = {
                    if (showAccount) {
                        IconButton(onClick = { showAccount = false }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                        }
                    }
                },
                actions = {
                    if (!showAccount) {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Filled.Menu, contentDescription = "Menú")
                        }
                        DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                            DropdownMenuItem(
                                text = { Text("Mi cuenta") },
                                onClick = {
                                    showMenu = false
                                    showAccount = true
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Cerrar sesión") },
                                onClick = {
                                    showMenu = false
                                    viewModel.onEvent(HomeEvent.OnLogoutClicked)
                                }
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            if (!showAccount) {
                NavigationBar {
                    NavigationBarItem(
                        selected = selectedTab == HomeTab.DISCOVER,
                        onClick = { selectedTab = HomeTab.DISCOVER },
                        icon = { Icon(Icons.Filled.Search, contentDescription = null) },
                        label = { Text("Descubrir") }
                    )
                    NavigationBarItem(
                        selected = selectedTab == HomeTab.MY_MOVIES,
                        onClick = { selectedTab = HomeTab.MY_MOVIES },
                        icon = { Icon(Icons.Filled.Movie, contentDescription = null) },
                        label = { Text("Mis Películas") }
                    )
                }
            }
        }
    ) { paddingValues ->
        if (state.isCheckingAuthState) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        val user = state.user ?: return@Scaffold // sin sesión: el Effect ya está navegando a Login

        if (showAccount) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (user.photoUrl != null) {
                    AsyncImage(
                        model = user.photoUrl,
                        contentDescription = "Foto de perfil",
                        modifier = Modifier.size(96.dp).clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Text(
                    text = user.displayName ?: "¡Bienvenido!",
                    style = MaterialTheme.typography.headlineSmall
                )

                Spacer(modifier = Modifier.height(4.dp))

                if (user.email != null) {
                    Text(
                        text = user.email,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (!user.isEmailVerified) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "⚠️ Verificá tu email dentro de las 24hs\no tu cuenta será eliminada.\nRevisa tu casilla de spam.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Ingresaste con ${user.provider.name.lowercase().replaceFirstChar { it.uppercase() }}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                when (selectedTab) {
                    HomeTab.DISCOVER -> DiscoverScreen(onNavigateToDetail = onNavigateToDetail)
                    HomeTab.MY_MOVIES -> MyMoviesScreen(onNavigateToDetail = onNavigateToDetail)
                }
            }
        }
    }
}