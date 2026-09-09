package com.tiago.kmpauthflows.ui.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.tiago.kmpauthflows.presentation.detail.DetailEffect
import com.tiago.kmpauthflows.presentation.detail.DetailEvent
import com.tiago.kmpauthflows.presentation.detail.DetailViewModel
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun DetailScreen(
    movieId: Int,
    onNavigateBack: () -> Unit,
    viewModel: DetailViewModel = koinViewModel(parameters = { parametersOf(movieId) })
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is DetailEffect.ShowError -> {
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
                title = { Text(state.movie?.title.orEmpty()) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            state.error != null -> {
                Column(
                    modifier = Modifier.fillMaxSize().padding(paddingValues).padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(state.error!!),
                        color = MaterialTheme.colorScheme.error
                    )
                    Button(onClick = { viewModel.onEvent(DetailEvent.OnRetry) }) {
                        Text("Reintentar")
                    }
                }
            }
            else -> {
                val movie = state.movie ?: return@Scaffold
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                ) {
                    AsyncImage(
                        model = movie.backdropUrl ?: movie.posterUrl,
                        contentDescription = movie.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxWidth().height(240.dp)
                    )

                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = movie.title, style = MaterialTheme.typography.headlineSmall)

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Filled.Star,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary
                            )
                            Text(
                                text = " ${movie.voteAverage} · ${movie.releaseDate?.take(4).orEmpty()}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        Row(modifier = Modifier.padding(vertical = 8.dp)) {
                            IconButton(onClick = { viewModel.onEvent(DetailEvent.OnFavoriteToggle) }) {
                                Icon(
                                    imageVector = if (state.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                    contentDescription = "Favorita"
                                )
                            }
                            IconButton(onClick = { viewModel.onEvent(DetailEvent.OnWatchedToggle) }) {
                                Icon(
                                    imageVector = if (state.isWatched) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                    contentDescription = "Vista"
                                )
                            }
                            IconButton(onClick = { viewModel.onEvent(DetailEvent.OnWatchlistToggle) }) {
                                Icon(
                                    imageVector = if (state.isInWatchlist) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                                    contentDescription = "Por ver"
                                )
                            }
                        }

                        Text(text = movie.overview, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}