package com.tiago.kmpauthflows.data.local

import android.content.Context
import androidx.room3.Room
import androidx.room3.RoomDatabase

fun getMoviesDatabaseBuilder(context: Context): RoomDatabase.Builder<MoviesDatabase> {
    val appContext = context.applicationContext
    val dbFile = appContext.getDatabasePath("movies.db")
    return Room.databaseBuilder<MoviesDatabase>(
        context = appContext,
        name = dbFile.absolutePath
    )
}