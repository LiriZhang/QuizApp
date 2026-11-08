package com.exam.quizapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.exam.quizapp.platform.getBannerAdHeight
import com.exam.quizapp.platform.getDeviceId
import com.exam.quizapp.platform.getNavigationBarHeight
import com.exam.quizapp.platform.playVoice
import com.exam.quizapp.platform.setUserLoggedIn
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.coroutines.launch

@Serializable
data class LoginResponse(val status: String, val message: String)

private val loginHttpClient by lazy {
    HttpClient {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }
}

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit
) {
    var phoneNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val topPadding = getBannerAdHeight()
    val bottomPadding = getNavigationBarHeight()

    // 🔗 Dán URL Web App (có đuôi /exec) của Google Apps Script vào đây:
    val googleSheetApiUrl = "https://script.google.com/macros/s/AKfycbxwqack9635Nt9ltp62HVsCrqg5N8YTNQLE7zKqv0kIdW5vxMUmF7xUy88r8HFaEBZL/exec"

    fun performLogin() {
        val trimmedPhone = phoneNumber.trim()
        val trimmedPassword = password.trim()

        if (trimmedPhone.length != 10 || !trimmedPhone.all { it.isDigit() }) {
            errorMessage = "Số điện thoại phải gồm đúng 10 chữ số!"
            playVoice(isCorrect = false)
            return
        }

        if (trimmedPassword.length != 5 || !trimmedPassword.all { it.isDigit() }) {
            errorMessage = "Mật khẩu phải gồm đúng 5 chữ số!"
            playVoice(isCorrect = false)
            return
        }

        // 5 chữ số tương ứng các vị trí thứ 2, 4, 6, 8, 10 (chỉ số 1, 3, 5, 7, 9)
        val expectedPassword = buildString {
            append(trimmedPhone[1])
            append(trimmedPhone[3])
            append(trimmedPhone[5])
            append(trimmedPhone[7])
            append(trimmedPhone[9])
        }

        if (trimmedPassword != expectedPassword) {
            errorMessage = "Mật khẩu không chính xác!"
            playVoice(isCorrect = false)
            return
        }

        isLoading = true
        errorMessage = null

        scope.launch {
            try {
                val deviceId = getDeviceId()
                val url = "$googleSheetApiUrl?phone=$trimmedPhone&deviceId=$deviceId"
                val responseText = loginHttpClient.get(url).bodyAsText()

                val response = Json.decodeFromString<LoginResponse>(responseText)

                if (response.status == "success") {
                    errorMessage = null
                    playVoice(isCorrect = true)
                    setUserLoggedIn(true)
                    onLoginSuccess()
                } else {
                    errorMessage = response.message
                    playVoice(isCorrect = false)
                }
            } catch (e: Exception) {
                errorMessage = "Lỗi: ${e.message ?: e.toString()}"
                playVoice(isCorrect = false)
            } finally {
                isLoading = false
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp, top = topPadding, bottom = bottomPadding),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Hiển thị thông báo lỗi
                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = errorMessage ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center
                    )
                }
                Text(
                    text = "ĐĂNG NHẬP",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Vui lòng nhập Số điện thoại và Mật khẩu để tiếp tục",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Trường nhập Số điện thoại
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { input ->
                        if (input.length <= 10 && input.all { it.isDigit() }) {
                            phoneNumber = input
                            errorMessage = null
                        }
                    },
                    label = { Text("Số điện thoại (10 số)") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = "Phone Icon"
                        )
                    },
                    singleLine = true,
                    enabled = !isLoading,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Trường nhập Mật khẩu
                OutlinedTextField(
                    value = password,
                    onValueChange = { input ->
                        if (input.length <= 5 && input.all { it.isDigit() }) {
                            password = input
                            errorMessage = null
                        }
                    },
                    label = { Text("Mật khẩu (5 số)") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Lock Icon"
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                            Icon(
                                imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (isPasswordVisible) "Hide Password" else "Show Password"
                            )
                        }
                    },
                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    singleLine = true,
                    enabled = !isLoading,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.NumberPassword,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { performLogin() }
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Hiển thị thông báo lỗi
                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = errorMessage ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Nút Đăng nhập
                Button(
                    onClick = { performLogin() },
                    enabled = !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "ĐĂNG NHẬP",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
