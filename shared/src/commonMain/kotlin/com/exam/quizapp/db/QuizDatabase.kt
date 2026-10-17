package com.exam.quizapp.db

import com.exam.quizapp.model.QuizResultDao
import kotlin.concurrent.Volatile

/**
 * Platform-agnostic declaration of the database that lives in commonMain.
 * Platform-specific implementations (Android / iOS) should provide a concrete
 * subclass and set the instance via getDatabase(...) or by assigning the
 * INSTANCE through other platform initialization code.
 *
 * This file purposely avoids any Android-specific Room imports/annotations so
 * it can compile for non-Android targets (e.g. iOS) when building the IPA.
 */
abstract class QuizDatabase {
    abstract fun quizResultDao(): QuizResultDao

    companion object {
        @Volatile
        private var INSTANCE: QuizDatabase? = null

        /**
         * Return the initialized database instance, or throw if it hasn't been
         * initialized. Platform code should initialize the INSTANCE before use.
         *
         * Note: the parameter is kept as Any? to avoid depending on Android types
         * from commonMain. Platform code can call getDatabase() after setting
         * INSTANCE (or you can add a small platform helper that sets INSTANCE).
         */
        fun getDatabase(initializer: Any? = null): QuizDatabase {
            return INSTANCE ?: (initializer ?: throw IllegalStateException(
                "QuizDatabase has not been initialized. Initialize the platform-specific database before calling getDatabase()."
            )).also { INSTANCE = it as QuizDatabase }
        }

        /**
         * Platform initialization helper (call from Android/iOS startup code).
         * Use this to set the platform-specific QuizDatabase instance.
         */
        fun setInstance(db: QuizDatabase) {
            INSTANCE = db
        }
    }
}
