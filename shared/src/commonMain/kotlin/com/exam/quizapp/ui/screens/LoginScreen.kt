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
import com.exam.quizapp.platform.fetchTextFromUrl
import com.exam.quizapp.platform.getBannerAdHeight
import com.exam.quizapp.platform.getNavigationBarHeight
import com.exam.quizapp.platform.playVoice
import com.exam.quizapp.platform.setUserLoggedIn

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit
) {
    var phoneNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val localPhoneNumbers = remember {
        listOf(
            "0900449008", "0987057417"
        )
    }

    var remotePhoneNumbers by remember {
        mutableStateOf<List<String>>(emptyList())
    }

    var lockedPhoneNumbers by remember {
        mutableStateOf<List<String>>(emptyList())
    }

    var isRemoteListLoaded by remember {
        mutableStateOf(false)
    }

    var remoteLoadError by remember {
        mutableStateOf(false)
    }

// Đường dẫn file TXT trên GitHub
    val githubUrl =
        "https://raw.githubusercontent.com/LiriZhang/HKI.01.github.io/HKI.01/AccActive.txt"

    val lockedGithubUrl =
        "https://raw.githubusercontent.com/LiriZhang/HKI.01.github.io/HKI.01/AccLock.txt"

// Tải danh sách số điện thoại từ GitHub
    LaunchedEffect(Unit) {
        try {
            val content = fetchTextFromUrl(githubUrl)

            remotePhoneNumbers = Regex("""\d{10}""")
                .findAll(content)
                .map { it.value }
                .distinct()
                .toList()

            try {
                val lockedContent = fetchTextFromUrl(lockedGithubUrl)
                lockedPhoneNumbers = Regex("""\d{10}""")
                    .findAll(lockedContent)
                    .map { it.value }
                    .distinct()
                    .toList()
            } catch (e: Exception) {
                println("Lỗi tải danh sách tài khoản đã đăng nhập: ${e.message}")
            }

            remoteLoadError = false

        } catch (_: Exception) {
            remoteLoadError = true
        } finally {
            isRemoteListLoaded = true
        }
    }

// Kết hợp danh sách cố định và danh sách GitHub
    val allowedPhoneNumbers = remember(remotePhoneNumbers) {
        (localPhoneNumbers + remotePhoneNumbers).distinct()
    }
    val topPadding = getBannerAdHeight()
    val bottomPadding = getNavigationBarHeight()

    fun performLogin() {
        val trimmedPhone = phoneNumber.trim()
        val trimmedPassword = password.trim()

        if (trimmedPhone.length != 10 || !trimmedPhone.all { it.isDigit() }) {
            errorMessage = "Số điện thoại phải gồm đúng 10 chữ số!"
            playVoice(isCorrect = false)
            return
        }

        if (trimmedPhone in lockedPhoneNumbers) {
            errorMessage = "Số điện thoại này đã được đăng nhập điện thoại khác nên không được cấp cho điện thoại này. Nếu có vấn đề liên hệ Zalo 0972712415 để được giải quyết nhé !"
            playVoice(isCorrect = false)
            return
        }

        if (trimmedPhone !in allowedPhoneNumbers) {

            if (!isRemoteListLoaded) {
                errorMessage = "Đang tải danh sách tài khoản, vui lòng thử lại!"
                playVoice(isCorrect = false)
                return
            }

            if (remoteLoadError) {
                errorMessage = "Không thể tải danh sách tài khoản! Vui lòng kiểm tra Internet."
                playVoice(isCorrect = false)
                return
            }

            errorMessage = "Số điện thoại này chưa được cấp quyền truy cập ứng dụng!"
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

        if (trimmedPassword == expectedPassword) {
            errorMessage = null
            playVoice(isCorrect = true)
            setUserLoggedIn(true)
            onLoginSuccess()
        } else {
            errorMessage = "Mật khẩu không chính xác!"
            playVoice(isCorrect = false)
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
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.NumberPassword,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { performLogin() }
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Hiển thị gợi ý quy tắc mật khẩu
                Spacer(modifier = Modifier.height(8.dp))
                /*Text(
                    //text = "* Mật khẩu là 5 chữ số ở vị trí thứ 2, 4, 6, 8, 10 của SĐT",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    fontSize = 11.sp,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )*/

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
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
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
