package com.exam.quizapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.exam.quizapp.viewmodel.QuizViewModel

@Composable
actual fun QuizApp(
    viewModel: QuizViewModel,
    onQuizCompleted: () -> Unit
) {
    // Phần giao diện và điều hướng riêng cho iOS (bạn có thể tự chỉnh sửa tại đây)
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "QuizApp iOS Version",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Bạn có thể viết code điều hướng hoặc thay đổi giao diện cho iOS tại file này.",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
