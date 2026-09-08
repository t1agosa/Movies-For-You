package com.tiago.kmpauthflows.data.local

import androidx.room3.Database
import androidx.room3.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver

@Database(entities = [MovieEntity::class], version = 1)
abstract class MoviesDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
}

fun getMoviesDatabase(builder: RoomDatabase.Builder<MoviesDatabase>): MoviesDatabase =
    builder.setDriver(BundledSQLiteDriver()).build()