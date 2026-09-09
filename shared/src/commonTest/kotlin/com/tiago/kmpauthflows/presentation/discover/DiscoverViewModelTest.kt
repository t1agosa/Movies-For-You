package com.tiago.kmpauthflows.presentation.discover

import app.cash.turbine.test
import com.tiago.kmpauthflows.domain.fake.FakeFavoriteRepository
import com.tiago.kmpauthflows.domain.fake.FakeMovieRepository
import com.tiago.kmpauthflows.domain.fake.FakeWatchedRepository
import com.tiago.kmpauthflows.domain.fake.FakeWatchlistRepository
import com.tiago.kmpauthflows.domain.model.Movie
import com.tiago.kmpauthflows.domain.usecase.GetPopularMoviesUseCase
import com.tiago.kmpauthflows.domain.usecase.ObserveFavoritesUseCase
import com.tiago.kmpauthflows.domain.usecase.ObserveWatchedUseCase
import com.tiago.kmpauthflows.domain.usecase.ObserveWatchlistUseCase
import com.tiago.kmpauthflows.domain.usecase.SearchMoviesUseCase
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
import kotlin.test.assertIs

@OptIn(ExperimentalCoroutinesApi::class)
class DiscoverViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val fakeMovieRepository = FakeMovieRepository()
    private val fakeFavoriteRepository = FakeFavoriteRepository()
    private val fakeWatchedRepository = FakeWatchedRepository()
    private val fakeWatchlistRepository = FakeWatchlistRepository()

    private fun createViewModel() = DiscoverViewModel(
        getPopularMoviesUseCase = GetPopularMoviesUseCase(fakeMovieRepository),
        searchMoviesUseCase = SearchMoviesUseCase(fakeMovieRepository),
        observeFavoritesUseCase = ObserveFavoritesUseCase(fakeFavoriteRepository),
        observeWatchedUseCase = ObserveWatchedUseCase(fakeWatchedRepository),
        observeWatchlistUseCase = ObserveWatchlistUseCase(fakeWatchlistRepository),
        toggleFavoriteUseCase = ToggleFavoriteUseCase(fakeFavoriteRepository),
        toggleWatchedUseCase = ToggleWatchedUseCase(fakeWatchedRepository),
        toggleWatchlistUseCase = ToggleWatchlistUseCase(fakeWatchlistRepository)
    )

    private fun movie(id: Int, title: String) = Movie(
        id = id,
        title = title,
        overview = "overview",
        posterUrl = null,
        releaseDate = "2020-01-01",
        voteAverage = 7.0
    )

    @BeforeTest
    fun setup() { Dispatchers.setMain(testDispatcher) }

    @AfterTest
    fun tearDown() { Dispatchers.resetMain() }

    @Test
    fun `una busqueda activa no se pisa con una actualizacion del catalogo en segundo plano`() = runTest {
        val popular = movie(id = 1, title = "Popular")
        val searchResult = movie(id = 2, title = "Encontrada")
        fakeMovieRepository.setPopularMovies(listOf(popular))

        val viewModel = createViewModel()
        advanceUntilIdle()
        assertEquals(listOf("Popular"), viewModel.state.value.movies.map { it.movie.title })

        fakeMovieRepository.searchResults = listOf(searchResult)
        viewModel.onEvent(DiscoverEvent.OnSearchQueryChanged("Encontrada"))
        viewModel.onEvent(DiscoverEvent.OnSearchSubmit)
        advanceUntilIdle()
        assertEquals(listOf("Encontrada"), viewModel.state.value.movies.map { it.movie.title })

        fakeMovieRepository.setPopularMovies(listOf(popular, movie(id = 3, title = "Nueva popular")))
        advanceUntilIdle()

        assertEquals(listOf("Encontrada"), viewModel.state.value.movies.map { it.movie.title })
    }

    @Test
    fun `borrar la busqueda restaura el catalogo sin esperar una nueva emision`() = runTest {
        val popular = movie(id = 1, title = "Popular")
        val searchResult = movie(id = 2, title = "Encontrada")
        fakeMovieRepository.setPopularMovies(listOf(popular))
        fakeMovieRepository.searchResults = listOf(searchResult)

        val viewModel = createViewModel()
        advanceUntilIdle()
        viewModel.onEvent(DiscoverEvent.OnSearchQueryChanged("Encontrada"))
        viewModel.onEvent(DiscoverEvent.OnSearchSubmit)
        advanceUntilIdle()

        viewModel.onEvent(DiscoverEvent.OnSearchQueryChanged(""))
        viewModel.onEvent(DiscoverEvent.OnSearchSubmit)
        advanceUntilIdle()

        assertEquals(listOf("Popular"), viewModel.state.value.movies.map { it.movie.title })
    }

    @Test
    fun `un toggle fallido emite ShowError`() = runTest {
        fakeFavoriteRepository.shouldThrowOnToggle = RuntimeException("firestore down")
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.effect.test {
            viewModel.onEvent(DiscoverEvent.OnFavoriteToggle(1))
            advanceUntilIdle()

            assertIs<DiscoverEffect.ShowError>(awaitItem())
        }
    }
}