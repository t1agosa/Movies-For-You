package com.tiago.kmpauthflows.data.local

import android.content.Context
import androidx.room3.Room

fun buildMoviesDatabase(context: Context): MoviesDatabase {
    val appContext = context.applicationContext
    val dbFile = appContext.getDatabasePath("movies.db")
    val builder = Room.databaseBuilder<MoviesDatabase>(
        context = appContext,
        name = dbFile.absolutePath
    )
    return getMoviesDatabase(builder)
}