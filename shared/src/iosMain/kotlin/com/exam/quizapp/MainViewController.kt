package com.exam.quizapp

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController
import com.exam.quizapp.ui.screens.QuizApp
import com.exam.quizapp.viewmodel.QuizViewModel
import com.exam.quizapp.platform.PlatformBannerAd

fun MainViewController(): UIViewController {
    return ComposeUIViewController {
        val viewModel = QuizViewModel()
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            PlatformBannerAd(modifier = Modifier)
            QuizApp(
                viewModel = viewModel,
                onQuizCompleted = {}
            )
        }
    }
}
