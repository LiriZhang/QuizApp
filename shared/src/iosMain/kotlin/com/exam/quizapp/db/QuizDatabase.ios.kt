@file:OptIn(ExperimentalForeignApi::class)

package com.exam.quizapp.db

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask
import platform.Foundation.NSTemporaryDirectory

fun getDatabaseBuilder(): RoomDatabase.Builder<QuizDatabase> {
    val documentDirectory = try {
        NSFileManager.defaultManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = true,
            error = null
        )
    } catch (e: Exception) {
        null
    }
    val docPath = documentDirectory?.path ?: NSTemporaryDirectory()
    val dbFilePath = "$docPath/quiz_database.db"
    return Room.databaseBuilder<QuizDatabase>(
        name = dbFilePath,
        factory = { QuizDatabaseConstructor.initialize() }
    )
        .fallbackToDestructiveMigration(dropAllTables = true)
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
}

actual fun initQuizDatabase(): QuizDatabase {
    return try {
        val builder = getDatabaseBuilder()
        val db = builder.build()
        QuizDatabase.setInstance(db)
        db
    } catch (e: Throwable) {
        println("Lỗi khởi tạo QuizDatabase trên iOS: ${e.message}")
        try {
            val fallbackDb = Room.inMemoryDatabaseBuilder<QuizDatabase>(
                factory = { QuizDatabaseConstructor.initialize() }
            )
                .setDriver(BundledSQLiteDriver())
                .setQueryCoroutineContext(Dispatchers.IO)
                .build()
            QuizDatabase.setInstance(fallbackDb)
            fallbackDb
        } catch (e2: Throwable) {
            println("Lỗi tạo fallback DB: ${e2.message}")
            throw e
        }
    }
}