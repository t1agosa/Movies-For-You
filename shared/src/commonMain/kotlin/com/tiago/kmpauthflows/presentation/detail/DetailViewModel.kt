package com.tiago.kmpauthflows.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tiago.kmpauthflows.domain.model.MovieException
import com.tiago.kmpauthflows.domain.usecase.GetMovieDetailUseCase
import com.tiago.kmpauthflows.domain.usecase.ObserveFavoritesUseCase
import com.tiago.kmpauthflows.domain.usecase.ObserveWatchedUseCase
import com.tiago.kmpauthflows.domain.usecase.ObserveWatchlistUseCase
import com.tiago.kmpauthflows.domain.usecase.ToggleFavoriteUseCase
import com.tiago.kmpauthflows.domain.usecase.ToggleWatchedUseCase
import com.tiago.kmpauthflows.domain.usecase.ToggleWatchlistUseCase
import com.tiago.kmpauthflows.domain.util.Result
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

class DetailViewModel(
    private val movieId: Int,
    private val getMovieDetailUseCase: GetMovieDetailUseCase,
    private val observeFavoritesUseCase: ObserveFavoritesUseCase,
    private val observeWatchedUseCase: ObserveWatchedUseCase,
    private val observeWatchlistUseCase: ObserveWatchlistUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val toggleWatchedUseCase: ToggleWatchedUseCase,
    private val toggleWatchlistUseCase: ToggleWatchlistUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(DetailState())
    val state: StateFlow<DetailState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<DetailEffect>()
    val effect: SharedFlow<DetailEffect> = _effect.asSharedFlow()

    init {
        loadMovie()
        observeToggleStates()
    }

    private fun loadMovie() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            when (val result = getMovieDetailUseCase(movieId)) {
                is Result.Success -> _state.update { it.copy(movie = result.data, isLoading = false) }
                is Result.Error -> {
                    val movieException = result.exception as? MovieException ?: MovieException.Unknown(result.exception)
                    _state.update { it.copy(isLoading = false, error = movieException.toMessageRes()) }
                }
            }
        }
    }

    private fun observeToggleStates() {
        viewModelScope.launch {
            combine(
                observeFavoritesUseCase(),
                observeWatchedUseCase(),
                observeWatchlistUseCase()
            ) { favorites, watched, watchlist ->
                Triple(
                    favorites.any { it.movieId == movieId },
                    watched.any { it.movieId == movieId },
                    watchlist.any { it.movieId == movieId }
                )
            }.collect { (isFavorite, isWatched, isInWatchlist) ->
                _state.update { it.copy(isFavorite = isFavorite, isWatched = isWatched, isInWatchlist = isInWatchlist) }
            }
        }
    }

    fun onEvent(event: DetailEvent) {
        when (event) {
            DetailEvent.OnFavoriteToggle -> onFavoriteToggle()
            DetailEvent.OnWatchedToggle -> onWatchedToggle()
            DetailEvent.OnWatchlistToggle -> onWatchlistToggle()
            DetailEvent.OnRetry -> loadMovie()
        }
    }

    private fun onFavoriteToggle() {
        viewModelScope.launch {
            val result = toggleFavoriteUseCase(movieId)
            if (result is Result.Error) {
                val movieException = result.exception as? MovieException ?: MovieException.Unknown(result.exception)
                _effect.emit(DetailEffect.ShowError(movieException.toMessageRes()))
            }
        }
    }

    private fun onWatchedToggle() {
        viewModelScope.launch {
            val result = toggleWatchedUseCase(movieId)
            if (result is Result.Error) {
                val movieException = result.exception as? MovieException ?: MovieException.Unknown(result.exception)
                _effect.emit(DetailEffect.ShowError(movieException.toMessageRes()))
            }
        }
    }

    private fun onWatchlistToggle() {
        viewModelScope.launch {
            val result = toggleWatchlistUseCase(movieId)
            if (result is Result.Error) {
                val movieException = result.exception as? MovieException ?: MovieException.Unknown(result.exception)
                _effect.emit(DetailEffect.ShowError(movieException.toMessageRes()))
            }
        }
    }
}