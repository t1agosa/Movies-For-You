package com.tiago.kmpauthflows.di

import com.tiago.kmpauthflows.data.local.MoviesDatabase
import com.tiago.kmpauthflows.data.local.buildMoviesDatabase
import com.tiago.kmpauthflows.presentation.discover.DiscoverViewModel
import com.tiago.kmpauthflows.presentation.mymovies.MyMoviesViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val androidMoviesModule = module {
    single<MoviesDatabase> { buildMoviesDatabase(androidContext()) }

    viewModel {
        DiscoverViewModel(get(), get(), get(), get(), get(), get(), get(), get())
    }
    viewModel {
        MyMoviesViewModel(get(), get(), get(), get(), get(), get(), get())
    }
}