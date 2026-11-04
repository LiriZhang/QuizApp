package com.exam.quizapp

import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController
import com.exam.quizapp.db.initQuizDatabase
import com.exam.quizapp.ui.screens.LessonSelectionScreen

fun MainViewController(): UIViewController {
    try {
        initQuizDatabase()
    } catch (e: Exception) {
        println("Error initializing database in MainViewController: ${e.message}")
    }
    return ComposeUIViewController {
        LessonSelectionScreen(
            chapter = "VẬT LÍ NHIỆT",
            onQuizSelected = { quiz ->
                println("Đã chọn bài: $quiz")
            },
            onQuizSelectedAllQuestions = { quiz ->
                println("Đã chọn tất cả câu hỏi: $quiz")
            },
            onQuizReviewSelected = { quiz ->
                println("Đã chọn ôn tập: $quiz")
            },
            onBack = {
                println("Nhấn nút quay lại")
            }
        )
    }
}