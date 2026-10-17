package com.exam.quizapp.db

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.exam.quizapp.platform.AppContextHolder

fun getDatabaseBuilder(context: Context): RoomDatabase.Builder<QuizDatabase> {
    AppContextHolder.context = context.applicationContext
    val dbFile = context.applicationContext.getDatabasePath("quiz_database")
    return Room.databaseBuilder(
        context.applicationContext,
        QuizDatabase::class.java,
        dbFile.absolutePath
    )
}

fun initQuizDatabase(context: Context): QuizDatabase {
    return QuizDatabase.getDatabase(getDatabaseBuilder(context))
}

actual fun initQuizDatabase(): QuizDatabase {
    val context = AppContextHolder.context ?: throw IllegalStateException("AppContextHolder.context is not initialized")
    return initQuizDatabase(context)
}
