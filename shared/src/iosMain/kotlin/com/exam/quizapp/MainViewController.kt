package com.exam.quizapp

import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController
import com.exam.quizapp.ui.screens.QuizApp
import com.exam.quizapp.viewmodel.QuizViewModel

fun MainViewController(): UIViewController {
    return ComposeUIViewController {
        val viewModel = QuizViewModel()
        QuizApp(
            viewModel = viewModel,
            onQuizCompleted = {}
        )
    }
}
