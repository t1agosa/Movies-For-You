package com.tiago.kmpauthflows.ui.mymovies

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tiago.kmpauthflows.presentation.mymovies.MyMoviesEffect
import com.tiago.kmpauthflows.presentation.mymovies.MyMoviesEvent
import com.tiago.kmpauthflows.presentation.mymovies.MyMoviesViewModel
import com.tiago.kmpauthflows.ui.common.MovieCard
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.koin.compose.viewmodel.koinViewModel

private enum class MyMoviesTab(val title: String) {
    FAVORITES("Favoritas"),
    WATCHED("Vistas"),
    WATCHLIST("Por ver")
}

@Composable
fun MyMoviesScreen(
    onNavigateToDetail: (Int) -> Unit,
    viewModel: MyMoviesViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var selectedTab by remember { mutableStateOf(MyMoviesTab.FAVORITES) }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is MyMoviesEffect.NavigateToDetail -> onNavigateToDetail(effect.movieId)
                is MyMoviesEffect.ShowError -> {
                    val message = getString(effect.messageRes)
                    scope.launch { snackbarHostState.showSnackbar(message) }
                }
            }
        }
    }

    val movies = when (selectedTab) {
        MyMoviesTab.FAVORITES -> state.favorites
        MyMoviesTab.WATCHED -> state.watched
        MyMoviesTab.WATCHLIST -> state.watchlist
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            TabRow(selectedTabIndex = selectedTab.ordinal) {
                MyMoviesTab.entries.forEach { tab ->
                    Tab(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        text = { Text(tab.title) }
                    )
                }
            }

            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (movies.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "Todavía no agregaste ninguna película acá",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 140.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize().padding(16.dp)
                ) {
                    items(movies, key = { it.movie.id }) { movieCard ->
                        MovieCard(
                            uiState = movieCard,
                            onClick = { viewModel.onEvent(MyMoviesEvent.OnMovieClick(movieCard.movie.id)) },
                            onFavoriteToggle = { viewModel.onEvent(MyMoviesEvent.OnFavoriteToggle(movieCard.movie.id)) },
                            onWatchedToggle = { viewModel.onEvent(MyMoviesEvent.OnWatchedToggle(movieCard.movie.id)) },
                            onWatchlistToggle = { viewModel.onEvent(MyMoviesEvent.OnWatchlistToggle(movieCard.movie.id)) }
                        )
                    }
                }
            }
        }
        SnackbarHost(hostState = snackbarHostState, modifier = Modifier.align(Alignment.BottomCenter))
    }
}