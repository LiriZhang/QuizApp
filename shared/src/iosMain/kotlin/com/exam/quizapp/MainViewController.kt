package com.exam.quizapp

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController
import com.exam.quizapp.db.initQuizDatabase
import com.exam.quizapp.platform.playVoice
import com.exam.quizapp.ui.screens.QuestionScreen
import com.exam.quizapp.ui.screens.ResultScreen
import com.exam.quizapp.viewmodel.QuizViewModel

fun MainViewController(): UIViewController {
    return ComposeUIViewController {
        var errorMessage by remember { mutableStateOf<String?>(null) }
        val viewModel = remember { QuizViewModel() }

        LaunchedEffect(Unit) {
            try {
                initQuizDatabase()
                viewModel.startChapter("VẬT LÍ NHIỆT")
                viewModel.startQuiz("Thử nghiệm 01")
            } catch (e: Throwable) {
                println("❌ LỖI KHỞI TẠO: ${e.stackTraceToString()}")
                errorMessage = "Lỗi hệ thống/Cơ sở dữ liệu:\n" + e.stackTraceToString()
            }
        }

        if (errorMessage != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🚨 PHÁT HIỆN LỖI CRASH:\n\n$errorMessage",
                    color = Color.Red,
                    fontSize = 13.sp
                )
            }
        } else {
            val questions = viewModel.questions
            val currentIdx = viewModel.currentQuestion

            if (viewModel.showResult) {
                // MÀN HÌNH 2: Màn hình kết quả hiển thị khi làm xong câu cuối cùng
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
            } else if (questions.isNotEmpty() && currentIdx in questions.indices) {
                val currentQuestion = questions[currentIdx]
                // MÀN HÌNH 1: Màn hình hiển thị từng câu hỏi một
                QuestionScreen(
                    question = currentQuestion,
                    currentQuestionIndex = currentIdx,
                    totalQuestions = questions.size,
                    onAnswerSelected = { answerIndex ->
                        val isCorrect = currentQuestion.correctAnswer == answerIndex
                        playVoice(isCorrect = isCorrect)
                        viewModel.selectAnswer(answerIndex)
                    },
                    onTextAnswerSubmitted = { userInput, isCorrect ->
                        playVoice(isCorrect = isCorrect)
                        viewModel.submitTextAnswer(userInput, isCorrect)
                    },
                    onBackQuestion = {
                        playVoice(isCorrect = false)
                        viewModel.onBackQuestion()
                    },
                    onHomePage = {
                        playVoice(isCorrect = false)
                        viewModel.resetTest()
                        viewModel.startQuiz("Thử nghiệm 01")
                    },
                    onBackQuiz = {
                        playVoice(isCorrect = false)
                        viewModel.resetTest()
                        viewModel.startQuiz("Thử nghiệm 01")
                    }
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Đang tải dữ liệu câu hỏi bài 'Thử nghiệm 01'...")
                }
            }
        }
    }
}