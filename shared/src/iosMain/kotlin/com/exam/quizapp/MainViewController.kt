package com.exam.quizapp

//import androidx.compose.runtime.*
import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController
import com.exam.quizapp.ui.screens.QuizApp
import com.exam.quizapp.db.initQuizDatabase
import com.exam.quizapp.platform.playVoice
import com.exam.quizapp.ui.screens.ChapterSelectionScreen
import com.exam.quizapp.ui.screens.TheoryAndExerciseScreen

fun MainViewController(): UIViewController {
    try {
        initQuizDatabase()
    } catch (e: Exception) {
        println("Error initializing database in MainViewController: ${e.message}")
    }
    return ComposeUIViewController {
        QuizApp(
            /*onQuizCompleted = {
                // Xử lý khi hoàn thành quiz trên iOS
            }*/
        )
    }
}