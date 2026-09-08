package com.tiago.kmpauthflows.di

import com.tiago.kmpauthflows.data.local.MoviesDatabase
import com.tiago.kmpauthflows.data.local.buildMoviesDatabase
import org.koin.dsl.module

val iosMoviesModule = module {
    single<MoviesDatabase> { buildMoviesDatabase() }
}