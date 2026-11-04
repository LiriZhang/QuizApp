package com.exam.quizapp

//import androidx.compose.runtime.*
import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController
import com.exam.quizapp.ui.screens.QuizApp
import com.exam.quizapp.db.initQuizDatabase
import com.exam.quizapp.ui.screens.TheoryAndExerciseScreen

fun MainViewController(): UIViewController {
    try {
        initQuizDatabase()
    } catch (e: Exception) {
        println("Error initializing database in MainViewController: ${e.message}")
    }
    return ComposeUIViewController {
        TheoryAndExerciseScreen(
            onExerciseSelected = {
                // Xử lý khi chọn bài tập trên iOS
            },
            onTheorySelected = {
                // Xử lý khi chọn lý thuyết trên iOS
            }
        )
        /*QuizApp(
            onQuizCompleted = {
                // Xử lý khi hoàn thành quiz trên iOS
            }
        )*/
    }
}