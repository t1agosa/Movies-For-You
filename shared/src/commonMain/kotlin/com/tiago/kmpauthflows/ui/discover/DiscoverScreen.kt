package com.tiago.kmpauthflows.ui.discover

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tiago.kmpauthflows.presentation.discover.DiscoverEffect
import com.tiago.kmpauthflows.presentation.discover.DiscoverEvent
import com.tiago.kmpauthflows.presentation.discover.DiscoverViewModel
import com.tiago.kmpauthflows.ui.common.MovieCard
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DiscoverScreen(
    onNavigateToDetail: (Int) -> Unit,
    viewModel: DiscoverViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is DiscoverEffect.NavigateToDetail -> onNavigateToDetail(effect.movieId)
                is DiscoverEffect.ShowError -> {
                    val message = getString(effect.messageRes)
                    scope.launch { snackbarHostState.showSnackbar(message) }
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = { viewModel.onEvent(DiscoverEvent.OnSearchQueryChanged(it)) },
                label = { Text("Buscar películas") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = { viewModel.onEvent(DiscoverEvent.OnSearchSubmit) }
                ),
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            )

            if (state.isLoading && state.movies.isNotEmpty()) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            state.error?.let { errorRes ->
                Text(
                    text = stringResource(errorRes),
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            if (state.isLoading && state.movies.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (state.movies.isEmpty() && state.error == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = if (state.searchQuery.isBlank()) "Sin películas para mostrar" else "No encontramos resultados",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 140.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)
                ) {
                    items(state.movies, key = { it.movie.id }) { movieCard ->
                        MovieCard(
                            uiState = movieCard,
                            onClick = { viewModel.onEvent(DiscoverEvent.OnMovieClick(movieCard.movie.id)) },
                            onFavoriteToggle = { viewModel.onEvent(DiscoverEvent.OnFavoriteToggle(movieCard.movie.id)) },
                            onWatchedToggle = { viewModel.onEvent(DiscoverEvent.OnWatchedToggle(movieCard.movie.id)) },
                            onWatchlistToggle = { viewModel.onEvent(DiscoverEvent.OnWatchlistToggle(movieCard.movie.id)) }
                        )
                    }
                }
            }
        }
        SnackbarHost(hostState = snackbarHostState, modifier = Modifier.align(Alignment.BottomCenter))
    }
}