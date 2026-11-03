@file:OptIn(ExperimentalForeignApi::class)

package com.exam.quizapp.db

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

fun getDatabaseBuilder(): RoomDatabase.Builder<QuizDatabase> {
    val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = true,
        error = null
    )
    val dbFilePath = requireNotNull(documentDirectory?.path) + "/quiz_database"
    return Room.databaseBuilder<QuizDatabase>(
        name = dbFilePath,
        factory = { QuizDatabaseConstructor.initialize() }
    ).setDriver(BundledSQLiteDriver())
}

actual fun initQuizDatabase(): QuizDatabase {
    return QuizDatabase.getDatabase(getDatabaseBuilder())
}
