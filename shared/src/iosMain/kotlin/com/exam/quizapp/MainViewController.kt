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
import com.exam.quizapp.ui.screens.QuizApp

fun MainViewController(): UIViewController {
    return ComposeUIViewController {
        var errorMessage by remember { mutableStateOf<String?>(null) }

        LaunchedEffect(Unit) {
            try {
                initQuizDatabase()
            } catch (e: Throwable) {
                println("❌ LỖI KHỞI TẠO DB: ${e.stackTraceToString()}")
                errorMessage = "Lỗi khởi tạo Database:\n" + e.stackTraceToString()
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
                    text = "🚨 PHÁT HIỆN LỖI:\n\n$errorMessage",
                    color = Color.Red,
                    fontSize = 13.sp
                )
            }
        } else {
            QuizApp(
                onQuizCompleted = {
                    println("Quiz completed")
                }
            )
        }
    }
}