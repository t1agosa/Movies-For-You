package com.tiago.kmpauthflows.presentation.discover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tiago.kmpauthflows.domain.model.Movie
import com.tiago.kmpauthflows.domain.model.MovieException
import com.tiago.kmpauthflows.domain.usecase.GetPopularMoviesUseCase
import com.tiago.kmpauthflows.domain.usecase.ObserveFavoritesUseCase
import com.tiago.kmpauthflows.domain.usecase.ObserveWatchedUseCase
import com.tiago.kmpauthflows.domain.usecase.ObserveWatchlistUseCase
import com.tiago.kmpauthflows.domain.usecase.SearchMoviesUseCase
import com.tiago.kmpauthflows.domain.usecase.ToggleFavoriteUseCase
import com.tiago.kmpauthflows.domain.usecase.ToggleWatchedUseCase
import com.tiago.kmpauthflows.domain.usecase.ToggleWatchlistUseCase
import com.tiago.kmpauthflows.domain.util.Result
import com.tiago.kmpauthflows.presentation.common.MovieCardUiState
import com.tiago.kmpauthflows.presentation.common.toMessageRes
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DiscoverViewModel(
    private val getPopularMoviesUseCase: GetPopularMoviesUseCase,
    private val searchMoviesUseCase: SearchMoviesUseCase,
    private val observeFavoritesUseCase: ObserveFavoritesUseCase,
    private val observeWatchedUseCase: ObserveWatchedUseCase,
    private val observeWatchlistUseCase: ObserveWatchlistUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val toggleWatchedUseCase: ToggleWatchedUseCase,
    private val toggleWatchlistUseCase: ToggleWatchlistUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(DiscoverState())
    val state: StateFlow<DiscoverState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<DiscoverEffect>()
    val effect: SharedFlow<DiscoverEffect> = _effect.asSharedFlow()

    // null = mostrando populares. no-null = ultima busqueda confirmada.
    private val activeQuery = MutableStateFlow<String?>(null)
    private val popularMovies = MutableStateFlow<List<Movie>>(emptyList())
    private val baseMovies = MutableStateFlow<List<Movie>>(emptyList())

    init {
        viewModelScope.launch {
            getPopularMoviesUseCase().collect { movies ->
                popularMovies.value = movies
                if (activeQuery.value == null) {
                    baseMovies.value == movies
                }
            }
        }
        observeToggleStates()
    }

    private fun observeToggleStates() {
        viewModelScope.launch {
            combine(
                baseMovies,
                observeFavoritesUseCase(),
                observeWatchedUseCase(),
                observeWatchlistUseCase()
            ) { movies, favorites, watched, watchlist ->
                val favoriteIds = favorites.map { it.movieId }.toSet()
                val watchedIds = watched.map { it.movieId }.toSet()
                val watchlistIds = watchlist.map { it.movieId }.toSet()
                movies.map { movie ->
                    MovieCardUiState(
                        movie = movie,
                        isFavorite = movie.id in favoriteIds,
                        isWatched = movie.id in watchedIds,
                        isInWatchlist = movie.id in watchlistIds
                    )
                }
            }.collect { cards ->
                _state.update { it.copy(movies = cards) }
            }
        }
    }

    fun onEvent(event: DiscoverEvent) {
        when (event) {
            is DiscoverEvent.OnSearchQueryChanged -> _state.update { it.copy(searchQuery = event.query) }
            DiscoverEvent.OnSearchSubmit -> onSearchSubmit()
            is DiscoverEvent.OnMovieClick -> onMovieClick(event.movieId)
            is DiscoverEvent.OnFavoriteToggle -> onFavoriteToggle(event.movieId)
            is DiscoverEvent.OnWatchedToggle -> onWatchedToggle(event.movieId)
            is DiscoverEvent.OnWatchlistToggle -> onWatchlistToggle(event.movieId)

        }
    }

    private fun onSearchSubmit() {
        val query = _state.value.searchQuery.trim()

        if (query.isEmpty()) {
            activeQuery.value = null
            baseMovies.value = popularMovies.value
            _state.update { it.copy(error = null) }
            return
        }

        activeQuery.value = query
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            when (val result = searchMoviesUseCase(query)) {
                is Result.Success -> {
                    baseMovies.value = result.data
                    _state.update { it.copy(isLoading = false) }
                }

                is Result.Error -> {
                    val movieException = result.exception as? MovieException
                        ?: MovieException.Unknown(result.exception)
                    _state.update {
                        it.copy(
                            isLoading = false, error = movieException.toMessageRes()
                        )
                    }
                }
            }
        }
    }

    private fun onMovieClick(movieId: Int) {
        viewModelScope.launch { _effect.emit(DiscoverEffect.NavigateToDetail(movieId)) }
    }

    private fun onFavoriteToggle(movieId: Int) {
        viewModelScope.launch {
            val result = toggleFavoriteUseCase(movieId)
            if (result is Result.Error) {
                val movieException =
                    result.exception as? MovieException ?: MovieException.Unknown(result.exception)
                _effect.emit(DiscoverEffect.ShowError(movieException.toMessageRes()))
            }
        }
    }

    private fun onWatchedToggle(movieId: Int) {
        viewModelScope.launch {
            val result = toggleWatchedUseCase(movieId)
            if (result is Result.Error) {
                val movieException =
                    result.exception as? MovieException ?: MovieException.Unknown(result.exception)
                _effect.emit(DiscoverEffect.ShowError(movieException.toMessageRes()))
            }
        }
    }

    private fun onWatchlistToggle(movieId: Int) {
        viewModelScope.launch {
            val result = toggleWatchlistUseCase(movieId)
            if (result is Result.Error) {
                val movieException =
                    result.exception as? MovieException ?: MovieException.Unknown(result.exception)
                _effect.emit(DiscoverEffect.ShowError(movieException.toMessageRes()))
            }
        }
    }
}