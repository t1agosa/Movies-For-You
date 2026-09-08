package com.tiago.kmpauthflows.di

import com.tiago.kmpauthflows.data.local.MoviesDatabase
import com.tiago.kmpauthflows.data.local.buildMoviesDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val androidMoviesModule = module {
    single<MoviesDatabase> { buildMoviesDatabase(androidContext()) }
}