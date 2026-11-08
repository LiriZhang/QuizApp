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
/*Text(
                    text = "* Mật khẩu là 5 chữ số ở vị trí thứ 2, 4, 6, 8, 10 của SĐT",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    fontSize = 11.sp,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )*/