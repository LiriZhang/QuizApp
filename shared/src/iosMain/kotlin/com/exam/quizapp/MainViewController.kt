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
        ChapterSelectionScreen(
            onChapterSelected = { chapter ->
                // Xử lý khi người dùng chọn chương (ví dụ: chuyển màn hình hoặc log)
                println("Đã chọn chương: $chapter")
            },
            onList = {
                // Xử lý khi nhấn nút xem danh sách
            },
            onBackApp = {
                // Xử lý khi nhấn nút Back
            }
        )
    }
}