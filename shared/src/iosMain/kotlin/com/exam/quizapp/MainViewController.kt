package com.exam.quizapp

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController
import com.exam.quizapp.db.initQuizDatabase
import com.exam.quizapp.ui.screens.AllQuestionsScreen
import com.exam.quizapp.ui.screens.ResultScreen
import com.exam.quizapp.viewmodel.QuizViewModel

fun MainViewController(): UIViewController {
    try {
        initQuizDatabase()
    } catch (e: Exception) {
        println("Error initializing database in MainViewController: ${e.message}")
    }
    return ComposeUIViewController {
        val viewModel = remember { QuizViewModel() }

        LaunchedEffect(Unit) {
            viewModel.startChapter("VẬT LÍ NHIỆT")
            viewModel.startQuiz("Thử nghiệm 01")
        }

        val questions = viewModel.questions

        if (viewModel.showResult) {
            ResultScreen(
                score = viewModel.score,
                totalQuestions = questions.size,
                history = viewModel.history,
                quizName = viewModel.selectedLesson ?: "Thử nghiệm 01",
                onBackQuiz = {
                    viewModel.resetTest()
                    viewModel.startQuiz("Thử nghiệm 01")
                },
                onBackTest = {
                    viewModel.resetTest()
                    viewModel.startQuiz("Thử nghiệm 01")
                },
                onBackTest2 = {
                    viewModel.resetTest()
                    viewModel.startQuiz("Thử nghiệm 01")
                },
                onSaveResult = {},
                onHomePage = {
                    viewModel.resetTest()
                    viewModel.startQuiz("Thử nghiệm 01")
                },
                onQuizReviewSelected = {},
                onQuizReviewSelected2 = {}
            )
        } else if (questions.isNotEmpty()) {
            AllQuestionsScreen(
                questions = questions,
                quizName = viewModel.selectedLesson ?: "Thử nghiệm 01",
                onSubmitQuiz = { calculatedScore, calculatedHistory ->
                    viewModel.score = calculatedScore
                    viewModel.history = calculatedHistory
                    viewModel.showResult = true
                },
                onBackQuiz = {
                    viewModel.resetTest()
                    viewModel.startQuiz("Thử nghiệm 01")
                },
                onHomePage = {
                    viewModel.resetTest()
                    viewModel.startQuiz("Thử nghiệm 01")
                }
            )
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Đang tải hoặc chưa có dữ liệu cho bài 'Thử nghiệm 01'")
            }
        }
    }
}