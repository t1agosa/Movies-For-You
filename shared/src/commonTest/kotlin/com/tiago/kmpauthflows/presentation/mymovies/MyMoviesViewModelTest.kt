package com.tiago.kmpauthflows.presentation.mymovies

import com.tiago.kmpauthflows.domain.fake.FakeFavoriteRepository
import com.tiago.kmpauthflows.domain.fake.FakeMovieRepository
import com.tiago.kmpauthflows.domain.fake.FakeWatchedRepository
import com.tiago.kmpauthflows.domain.fake.FakeWatchlistRepository
import com.tiago.kmpauthflows.domain.model.Movie
import com.tiago.kmpauthflows.domain.usecase.GetMoviesByIdsUseCase
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
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class MyMoviesViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val fakeMovieRepository = FakeMovieRepository()
    private val fakeFavoriteRepository = FakeFavoriteRepository()
    private val fakeWatchedRepository = FakeWatchedRepository()
    private val fakeWatchlistRepository = FakeWatchlistRepository()

    private fun createViewModel() = MyMoviesViewModel(
        getMoviesByIdsUseCase = GetMoviesByIdsUseCase(fakeMovieRepository),
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
    fun `una pelicula favoriteada y despues marcada como vista actualiza las 3 flags en ambas secciones`() = runTest {
        val movieA = Movie(
            id = 1,
            title = "Interstellar",
            overview = "overview",
            posterUrl = null,
            releaseDate = "2014-11-06",
            voteAverage = 8.6
        )
        fakeMovieRepository.setMoviesByIds(listOf(movieA))

        val viewModel = createViewModel()
        advanceUntilIdle()
        assertEquals(emptyList(), viewModel.state.value.favorites)
        assertEquals(emptyList(), viewModel.state.value.watched)

        viewModel.onEvent(MyMoviesEvent.OnFavoriteToggle(movieA.id))
        advanceUntilIdle()

        val favoritesAfterFirstToggle = viewModel.state.value.favorites
        assertEquals(1, favoritesAfterFirstToggle.size)
        assertTrue(favoritesAfterFirstToggle.first().isFavorite)
        assertTrue(favoritesAfterFirstToggle.none { it.isWatched })

        viewModel.onEvent(MyMoviesEvent.OnWatchedToggle(movieA.id))
        advanceUntilIdle()

        val favoritesAfterSecondToggle = viewModel.state.value.favorites
        assertTrue(
            favoritesAfterSecondToggle.first().isWatched,
            "la seccion de favoritos deberia reflejar que la misma pelicula tambien quedo marcada como vista"
        )

        val watchedSection = viewModel.state.value.watched
        assertEquals(1, watchedSection.size)
        assertTrue(watchedSection.first().isFavorite)
        assertTrue(watchedSection.first().isWatched)
    }
}