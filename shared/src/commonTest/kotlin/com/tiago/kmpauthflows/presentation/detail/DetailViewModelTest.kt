package com.tiago.kmpauthflows.presentation.detail

import com.tiago.kmpauthflows.domain.fake.FakeFavoriteRepository
import com.tiago.kmpauthflows.domain.fake.FakeMovieRepository
import com.tiago.kmpauthflows.domain.fake.FakeWatchedRepository
import com.tiago.kmpauthflows.domain.fake.FakeWatchlistRepository
import com.tiago.kmpauthflows.domain.model.Movie
import com.tiago.kmpauthflows.domain.usecase.GetMovieDetailUseCase
import com.tiago.kmpauthflows.domain.usecase.ObserveFavoritesUseCase
import com.tiago.kmpauthflows.domain.usecase.ObserveWatchedUseCase
import com.tiago.kmpauthflows.domain.usecase.ObserveWatchlistUseCase
import com.tiago.kmpauthflows.domain.usecase.ToggleFavoriteUseCase
import com.tiago.kmpauthflows.domain.usecase.ToggleWatchedUseCase
import com.tiago.kmpauthflows.domain.usecase.ToggleWatchlistUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class DetailViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val fakeMovieRepository = FakeMovieRepository()
    private val fakeFavoriteRepository = FakeFavoriteRepository()
    private val fakeWatchedRepository = FakeWatchedRepository()
    private val fakeWatchlistRepository = FakeWatchlistRepository()

    private val movie = Movie(
        id = 1,
        title = "Interstellar",
        overview = "overview",
        posterUrl = null,
        releaseDate = "2014-11-06",
        voteAverage = 8.6
    )

    private fun createViewModel() = DetailViewModel(
        movieId = movie.id,
        getMovieDetailUseCase = GetMovieDetailUseCase(fakeMovieRepository),
        observeFavoritesUseCase = ObserveFavoritesUseCase(fakeFavoriteRepository),
        observeWatchedUseCase = ObserveWatchedUseCase(fakeWatchedRepository),
        observeWatchlistUseCase = ObserveWatchlistUseCase(fakeWatchlistRepository),
        toggleFavoriteUseCase = ToggleFavoriteUseCase(fakeFavoriteRepository),
        toggleWatchedUseCase = ToggleWatchedUseCase(fakeWatchedRepository),
        toggleWatchlistUseCase = ToggleWatchlistUseCase(fakeWatchlistRepository)
    )

    @BeforeTest
    fun setup() { Dispatchers.setMain(testDispatcher) }

    @AfterTest
    fun tearDown() { Dispatchers.resetMain() }

    @Test
    fun `marcar como favorita actualiza isFavorite en el state`() = runTest {
        fakeMovieRepository.setMovieDetail(movie)
        val viewModel = createViewModel()
        advanceUntilIdle()
        assertFalse(viewModel.state.value.isFavorite)

        viewModel.onEvent(DetailEvent.OnFavoriteToggle)
        advanceUntilIdle()

        assertTrue(viewModel.state.value.isFavorite)
    }
}