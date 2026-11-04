@file:OptIn(ExperimentalForeignApi::class)

package com.exam.quizapp.db

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask
import platform.Foundation.NSTemporaryDirectory

fun getDatabaseBuilder(): RoomDatabase.Builder<QuizDatabase> {
    val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = true,
        error = null
    )
    val docPath = documentDirectory?.path ?: NSTemporaryDirectory()
    val dbFilePath = "$docPath/quiz_database.db"
    return Room.databaseBuilder<QuizDatabase>(
        name = dbFilePath
    )
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
}

actual fun initQuizDatabase(): QuizDatabase {
    return QuizDatabase.getDatabase(getDatabaseBuilder())
}