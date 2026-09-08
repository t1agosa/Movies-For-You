package com.tiago.kmpauthflows.data.local

import androidx.room3.Room
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

@OptIn(ExperimentalForeignApi::class)
fun buildMoviesDatabase(): MoviesDatabase {
    val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null
    )
    val dbFilePath = requireNotNull(documentDirectory?.path) + "/movies.db"
    val builder = Room.databaseBuilder<MoviesDatabase>(name = dbFilePath)
    return getMoviesDatabase(builder)
}