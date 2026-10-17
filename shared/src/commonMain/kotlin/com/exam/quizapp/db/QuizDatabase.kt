package com.exam.quizapp.db

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import com.exam.quizapp.model.QuizResult
import com.exam.quizapp.model.QuizResultDao
import kotlin.concurrent.Volatile

expect fun initQuizDatabase(): QuizDatabase

@Database(entities = [QuizResult::class], version = 2)
@ConstructedBy(QuizDatabaseConstructor::class)
abstract class QuizDatabase : RoomDatabase() {
    abstract fun quizResultDao(): QuizResultDao

    companion object {
        @Volatile
        private var INSTANCE: QuizDatabase? = null
        /**
         * Hàm khởi tạo duy nhất nằm trọn vẹn trong commonMain.
         * Nơi gọi (Android / iOS) chỉ cần truyền Builder tương ứng vào.
         */

        fun getDatabase(builder: Builder<QuizDatabase>? = null): QuizDatabase {
            return INSTANCE ?: (builder?.build()
                ?: throw IllegalStateException("QuizDatabase has not been initialized. Pass a RoomDatabase.Builder to getDatabase() first."))
                .also { INSTANCE = it }
        }
    }
}

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object QuizDatabaseConstructor : RoomDatabaseConstructor<QuizDatabase>
