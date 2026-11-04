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

        fun setInstance(database: QuizDatabase) {
            INSTANCE = database
        }

        fun getDatabase(builder: Builder<QuizDatabase>? = null): QuizDatabase {
            val current = INSTANCE
            if (current != null) return current
            if (builder != null) {
                val newDb = builder.build()
                INSTANCE = newDb
                return newDb
            }
            return initQuizDatabase()
        }
    }
}
// Chú thích này báo cho Kotlin Compiler biết:
// "Hãy bỏ qua việc kiểm tra file actual viết tay vì Room KSP sẽ tự sinh ra nó lúc build!"
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object QuizDatabaseConstructor : RoomDatabaseConstructor<QuizDatabase> {
    override fun initialize(): QuizDatabase
}