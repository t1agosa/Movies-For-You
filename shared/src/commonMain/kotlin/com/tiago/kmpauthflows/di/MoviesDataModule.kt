package com.tiago.kmpauthflows.di

import com.tiago.kmpauthflows.data.firestore.PersonalListFirestoreService
import com.tiago.kmpauthflows.data.local.MoviesDatabase
import com.tiago.kmpauthflows.data.repository.FavoriteRepositoryImpl
import com.tiago.kmpauthflows.data.repository.MovieRepositoryImpl
import com.tiago.kmpauthflows.data.repository.WatchedRepositoryImpl
import com.tiago.kmpauthflows.data.repository.WatchlistRepositoryImpl
import com.tiago.kmpauthflows.data.tmdb.TmdbApiService
import com.tiago.kmpauthflows.domain.repository.FavoriteRepository
import com.tiago.kmpauthflows.domain.repository.MovieRepository
import com.tiago.kmpauthflows.domain.repository.WatchedRepository
import com.tiago.kmpauthflows.domain.repository.WatchlistRepository
import com.tiago.kmpauthflows.domain.usecase.GetMovieDetailUseCase
import com.tiago.kmpauthflows.domain.usecase.GetMoviesByIdsUseCase
import com.tiago.kmpauthflows.domain.usecase.GetPopularMoviesUseCase
import com.tiago.kmpauthflows.domain.usecase.ObserveFavoritesUseCase
import com.tiago.kmpauthflows.domain.usecase.ObserveWatchedUseCase
import com.tiago.kmpauthflows.domain.usecase.ObserveWatchlistUseCase
import com.tiago.kmpauthflows.domain.usecase.SearchMoviesUseCase
import com.tiago.kmpauthflows.domain.usecase.ToggleFavoriteUseCase
import com.tiago.kmpauthflows.domain.usecase.ToggleWatchedUseCase
import com.tiago.kmpauthflows.domain.usecase.ToggleWatchlistUseCase
import org.koin.core.qualifier.named
import org.koin.dsl.module

val moviesDataModule = module {
    single { TmdbApiService(get()) }
    single { get<MoviesDatabase>().movieDao() }
    single<MovieRepository> { MovieRepositoryImpl(get(), get()) }

    single(named("favorites")) { PersonalListFirestoreService("favorites") }
    single(named("watched")) { PersonalListFirestoreService("watched") }
    single(named("watchlist")) { PersonalListFirestoreService("watchlist") }

    single<FavoriteRepository> { FavoriteRepositoryImpl(get(named("favorites"))) }
    single<WatchedRepository> { WatchedRepositoryImpl(get(named("watched"))) }
    single<WatchlistRepository> { WatchlistRepositoryImpl(get(named("watchlist"))) }

    single { GetPopularMoviesUseCase(get()) }
    single { GetMovieDetailUseCase(get()) }
    single { SearchMoviesUseCase(get()) }
    single { ObserveFavoritesUseCase(get()) }
    single { ToggleFavoriteUseCase(get()) }
    single { ObserveWatchedUseCase(get()) }
    single { ToggleWatchedUseCase(get()) }
    single { ObserveWatchlistUseCase(get()) }
    single { ToggleWatchlistUseCase(get()) }
    single { GetMoviesByIdsUseCase(get()) }
}