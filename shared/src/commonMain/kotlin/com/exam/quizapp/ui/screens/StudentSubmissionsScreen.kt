package com.exam.quizapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.exam.quizapp.data.quizQuestions3
import com.exam.quizapp.model.AnswerOption
import com.exam.quizapp.model.QuestionResult
import com.exam.quizapp.platform.fetchTextFromUrl3
import com.exam.quizapp.platform.getBannerAdHeight
import com.exam.quizapp.platform.getLoggedInPhone
import com.exam.quizapp.platform.getNavigationBarHeight
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.stringResource

private data class AnswerStyle(
    val backgroundColor: Color,
    val textColor: Color,
    val border: BorderStroke,
    val statusIcon: String
)

@Serializable
data class DriveStudentsResponse(
    val status: String = "",
    val totalStudents: Int = 0,
    val students: List<StudentDriveFolder> = emptyList(),
    val message: String? = null
)

@Serializable
data class StudentDriveFolder(
    val phone: String = "",
    val folderId: String = "",
    val fileCount: Int = 0,
    val files: List<StudentDriveFile> = emptyList()
) {
    val effectiveFileCount: Int
        get() = if (fileCount > 0) fileCount else files.size
}

@Serializable
data class DriveFilesResponse(
    val status: String = "",
    val totalFiles: Int = 0,
    val files: List<StudentDriveFile> = emptyList(),
    val message: String? = null
)

@Serializable
data class StudentDriveFile(
    val fileName: String = "",
    val fileId: String = "",
    val lastUpdated: String = "",
    val content: String = ""
)

@Serializable
data class DriveFileContentResponse(
    val status: String = "",
    val fileName: String = "",
    val fileId: String = "",
    val content: String = "",
    val message: String? = null
)

class StudentSubmissionsRepository {
    companion object {
        private const val SCRIPT_URL =
            "https://script.google.com/macros/s/AKfycbwPaTel2KIAi1cOFsEWdeRkRzW6CnGB61yY81_5_XWg9yDZhidkde4YnsNaKNk2E5-yAg/exec"
    }

    private val json = Json {
        ignoreUnknownKeys = true
    }

    private fun buildUrl(
        action: String,
        parameterName: String? = null,
        parameterValue: String? = null
    ): String {
        return buildString {
            append(SCRIPT_URL)
            append("?action=").append(action)
            if (parameterName != null && parameterValue != null) {
                append("&").append(parameterName).append("=").append(parameterValue)
            }
        }
    }

    suspend fun getStudents(): DriveStudentsResponse = withContext(Dispatchers.Default) {
        val response = fetchTextFromUrl3(buildUrl(action = "getStudents"))
        json.decodeFromString<DriveStudentsResponse>(response)
    }

    suspend fun getStudentFiles(
        folderId: String
    ): DriveFilesResponse = withContext(Dispatchers.Default) {
        val response = fetchTextFromUrl3(
            buildUrl(
                action = "getStudentFiles",
                parameterName = "folderId",
                parameterValue = folderId
            )
        )
        json.decodeFromString<DriveFilesResponse>(response)
    }

    suspend fun getFileContent(
        fileId: String
    ): DriveFileContentResponse = withContext(Dispatchers.Default) {
        val response = fetchTextFromUrl3(
            buildUrl(
                action = "getFileContent",
                parameterName = "fileId",
                parameterValue = fileId
            )
        )
        json.decodeFromString<DriveFileContentResponse>(response)
    }
}

data class ParsedSubmission(
    val quizName: String = "",
    val score: Int = 0,
    val totalQuestions: Int = 0,
    val history: List<QuestionResult> = emptyList()
)

fun parseStudentSubmissionContent(fileContent: String): ParsedSubmission? {
    if (fileContent.isBlank()) return null

    val lines = fileContent.lines().map { it.trim() }

    var quizName = ""
    var score = 0
    var totalQuestions = 0

    for (line in lines) {
        if (line.startsWith("Quiz:")) {
            quizName = line.substringAfter("Quiz:").trim()
        } else if (line.startsWith("Điểm số:")) {
            val scoreStr = line.substringAfter("Điểm số:").trim()
            val parts = scoreStr.split("/")
            if (parts.size >= 2) {
                score = parts[0].trim().toIntOrNull() ?: 0
                totalQuestions = parts[1].trim().toIntOrNull() ?: 0
            }
        }
    }

    if (quizName.isBlank()) return null

    val originalQuestions = quizQuestions3[quizName] ?: return null
    if (originalQuestions.isEmpty()) return null

    data class ParsedQuestionState(
        val isCorrect: Boolean = false,
        val selectedIndex: Int? = null,
        val userInputText: String? = null
    )

    val parsedStates = mutableMapOf<Int, ParsedQuestionState>()

    var currentQNum: Int? = null
    var currentIsCorrect = false

    for (line in lines) {
        if (line.startsWith("Câu ")) {
            val qNumStr = line.substringAfter("Câu ").substringBefore(":").trim()
            val qNum = qNumStr.toIntOrNull()
            if (qNum != null) {
                currentQNum = qNum
                currentIsCorrect = line.contains("ĐÚNG") || line.contains("✓")
                parsedStates[qNum] = ParsedQuestionState(isCorrect = currentIsCorrect)
            }
        } else if (currentQNum != null) {
            if (line.contains("Học sinh điền (Tự luận):")) {
                val inputStr = line.substringAfter("Học sinh điền (Tự luận):").trim()
                parsedStates[currentQNum!!] = ParsedQuestionState(
                    isCorrect = currentIsCorrect,
                    userInputText = inputStr
                )
            } else if (line.contains("Đã chọn:")) {
                val selectedPart = line.substringAfter("Đã chọn:").substringBefore("|").trim()
                val selectedIdx = when {
                    selectedPart.toIntOrNull() != null -> selectedPart.toInt()
                    selectedPart.length == 1 && selectedPart[0] in 'A'..'Z' -> selectedPart[0] - 'A'
                    selectedPart.length == 1 && selectedPart[0] in 'a'..'z' -> selectedPart[0] - 'a'
                    else -> -1
                }
                parsedStates[currentQNum!!] = ParsedQuestionState(
                    isCorrect = currentIsCorrect,
                    selectedIndex = if (selectedIdx >= 0) selectedIdx else null
                )
            }
        }
    }

    val historyList = mutableListOf<QuestionResult>()

    originalQuestions.forEachIndexed { index, question ->
        val qNum = index + 1
        val state = parsedStates[qNum]

        val isCorrect = state?.isCorrect ?: false
        val userInputText = state?.userInputText
        val selectedIdx = state?.selectedIndex

        val selectedAnswer = if (selectedIdx != null && selectedIdx >= 0) {
            question.answers.getOrNull(selectedIdx)
        } else null

        val correctAnswer = if (question.correctAnswer != null) {
            question.answers.getOrNull(question.correctAnswer)
        } else null

        historyList.add(
            QuestionResult(
                mainQuestion = question.mainContent,
                extraQuestions = question.extraContents,
                answers = question.answers,
                selectedAnswer = selectedAnswer,
                correctAnswer = correctAnswer,
                userInputText = userInputText,
                correctTextAnswers = question.correctTextAnswers,
                isCorrect = isCorrect,
                detailedCorrectAnswers = question.detailedCorrectAnswers
            )
        )
    }

    val finalScore = if (score > 0) score else historyList.count { it.isCorrect }
    val finalTotal = if (totalQuestions > 0) totalQuestions else historyList.size

    return ParsedSubmission(
        quizName = quizName,
        score = finalScore,
        totalQuestions = finalTotal,
        history = historyList
    )
}

@Composable
fun SubmissionResultView(
    submission: ParsedSubmission
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val isToolbarVisible = remember { mutableStateOf(true) }
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (available.y < 0) {
                    isToolbarVisible.value = false // Cuộn xuống
                } else if (available.y > 0) {
                    isToolbarVisible.value = true // Cuộn lên
                }
                return Offset.Zero
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
            .nestedScroll(nestedScrollConnection)
    ) {
        if (isToolbarVisible.value) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.LightGray.copy(alpha = 0.5f))
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(color = Color.Black)) {
                            append(submission.quizName)
                        }
                        withStyle(style = SpanStyle(color = Color.Red)) {
                            append(" (đúng: ${submission.score} / ${submission.totalQuestions})")
                        }
                    },
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.weight(1f)
                )

                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            listState.animateScrollToItem(0)
                        }
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowUp,
                        contentDescription = "Lên đầu trang",
                        tint = Color.Black
                    )
                }
            }
        }

        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize()
        ) {
            if (submission.history.isNotEmpty()) {
                item {
                    QuestionGridButtons(
                        questionResults = submission.history,
                        onQuestionClick = { index ->
                            coroutineScope.launch {
                                listState.animateScrollToItem(index + 1)
                            }
                        }
                    )
                }
            }

            itemsIndexed(submission.history) { index, result ->
                Column(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
                ) {
                    val questionTitleColor = Color(0xFF0D47A1)

                    Text(
                        text = "Câu ${index + 1}",
                        color = questionTitleColor,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )

                    // NỘI DUNG CÂU HỎI (Màu nền RẤT NHẠT hơn phần câu trả lời)
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFF7F9FC),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            RenderQuestionContent(content = result.mainQuestion)

                            result.extraQuestions.forEach { extra ->
                                RenderQuestionContent(content = extra)
                            }
                        }
                    }

                    if (result.answers.isNotEmpty()) {
                        val selectedAnswer = result.selectedAnswer
                        val correctAnswer = result.correctAnswer

                        result.answers.forEach { answer ->
                            val isCorrectAnswer = answer == correctAnswer
                            val isSelected = answer == selectedAnswer

                            // Bảng màu thiết kế rõ ràng, thẩm mỹ dành cho giáo viên chấm/xem bài:
                            // 1. Chọn & Đúng: Nền Xanh Dương Đậm (0xFF1565C0), Chữ Trắng, Icon ✓ [ĐÚNG]
                            // 2. Chọn & Sai: Nền Cam Nâu Đậm (0xFFA04B00), Chữ Trắng, Icon ✕ [SAI]
                            // 3. Không chọn & Đáp Án Đúng: Nền Xanh Dương Nhạt (0xFFD6E8FF), Chữ Xanh Đậm (0xFF0D47A1), Icon ✓ [ĐÁP ÁN ĐÚNG]
                            // 4. Không chọn (Khác): Nền Trắng/Xám Nhạt (0xFFF2F2F2), Chữ Xám Đen (0xFF333333), Icon —
                            val (backgroundColor, textColor, border, statusIcon) = when {
                                isSelected && isCorrectAnswer -> AnswerStyle(
                                    Color(0xFF1565C0),
                                    Color.White,
                                    BorderStroke(1.5.dp, Color(0xFF0D47A1)),
                                    "✓ [ĐÚNG] "
                                )
                                isSelected && !isCorrectAnswer -> AnswerStyle(
                                    Color(0xFFA04B00),
                                    Color.White,
                                    BorderStroke(1.5.dp, Color(0xFF803800)),
                                    "✕ [SAI] "
                                )
                                !isSelected && isCorrectAnswer -> AnswerStyle(
                                    Color(0xFFD6E8FF),
                                    Color(0xFF0D47A1),
                                    BorderStroke(1.5.dp, Color(0xFF1565C0)),
                                    "✓ [ĐÁP ÁN ĐÚNG] "
                                )
                                else -> AnswerStyle(
                                    Color(0xFFF2F2F2),
                                    Color(0xFF333333),
                                    BorderStroke(1.dp, Color(0xFFE0E0E0)),
                                    "— "
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = backgroundColor,
                                border = border,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = statusIcon,
                                        color = textColor,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        modifier = Modifier.padding(end = 6.dp)
                                    )

                                    RenderAnswerOptionContent(
                                        answer = answer,
                                        textColor = textColor
                                    )
                                }
                            }
                        }
                    } else if (result.correctTextAnswers.isNotEmpty() || !result.userInputText.isNullOrBlank()) {
                        val userInput = result.userInputText ?: ""
                        val isCorrect = result.isCorrect

                        if (isCorrect) {
                            Surface(
                                color = Color(0xFF1565C0),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.5.dp, Color(0xFF0D47A1)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "✓ [ĐÚNG]",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(end = 8.dp)
                                    )
                                    Text(
                                        text = "Đáp án của học sinh: $userInput",
                                        style = MaterialTheme.typography.bodyLarge.copy(color = Color.White)
                                    )
                                }
                            }
                        } else {
                            val randomCorrect = remember(result) {
                                result.correctTextAnswers.randomOrNull()
                            }
                            val backgroundColor = Color(0xFFA04B00)

                            Surface(
                                color = backgroundColor,
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.5.dp, Color(0xFF803800)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "✕ [SAI]",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(end = 8.dp)
                                    )

                                    when (randomCorrect) {
                                        is AnswerOption.Text -> {
                                            Text(
                                                text = "$userInput (Đáp án đúng là: ${randomCorrect.text})",
                                                style = MaterialTheme.typography.bodyLarge.copy(
                                                    color = Color.White
                                                ),
                                                textAlign = TextAlign.Justify,
                                                modifier = Modifier.padding(12.dp)
                                            )
                                        }

                                        is AnswerOption.TextRes -> {
                                            Text(
                                                text = "$userInput (Đáp án đúng là: ${
                                                    stringResource(randomCorrect.textRes)
                                                })",
                                                style = MaterialTheme.typography.bodyLarge.copy(
                                                    color = Color.White
                                                ),
                                                textAlign = TextAlign.Justify,
                                                modifier = Modifier.padding(12.dp)
                                            )
                                        }

                                        is AnswerOption.TextUrl -> {
                                            var textContent by remember(randomCorrect.textUrl) {
                                                mutableStateOf("Đang tải...")
                                            }
                                            var isError by remember(randomCorrect.textUrl) {
                                                mutableStateOf(false)
                                            }

                                            LaunchedEffect(randomCorrect.textUrl) {
                                                try {
                                                    textContent = fetchTextFromUrl3(randomCorrect.textUrl)
                                                    isError = false
                                                } catch (e: Exception) {
                                                    textContent = "Không thể tải nội dung, kiểm tra kết nối mạng!"
                                                    isError = true
                                                }
                                            }

                                            Text(
                                                text = if (!isError) {
                                                    "$userInput (Đáp án đúng là: ${textContent})"
                                                } else {
                                                    "Lỗi khi tải nội dung từ URL, kiểm tra kết nối mạng!"
                                                },
                                                style = MaterialTheme.typography.bodyLarge.copy(
                                                    color = Color.White
                                                ),
                                                textAlign = TextAlign.Justify,
                                                modifier = Modifier.padding(12.dp)
                                            )
                                        }

                                        else -> {
                                            Text(
                                                text = "$userInput (Chưa đúng)",
                                                style = MaterialTheme.typography.bodyLarge.copy(
                                                    color = Color.White
                                                ),
                                                modifier = Modifier.padding(12.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (result.detailedCorrectAnswers.isNotEmpty()) {
                        var showDetail by remember { mutableStateOf(false) }
                        val explanationBackground = Color(0xFFFFF7E6)
                        val explanationTextColor = Color(0xFF3A3025)
                        val explanationTitleColor = Color(0xFF8A4B00)

                        Column(modifier = Modifier.fillMaxWidth()) {
                            TextButton(onClick = { showDetail = !showDetail }) {
                                Text(
                                    text = if (showDetail) "Ẩn đáp án chi tiết" else "Đáp án chi tiết",
                                    color = explanationTitleColor,
                                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                                )
                            }

                            if (showDetail) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(explanationBackground, shape = RoundedCornerShape(8.dp))
                                        .padding(10.dp)
                                ) {
                                    Text(
                                        text = "Giải thích",
                                        color = explanationTitleColor,
                                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                        modifier = Modifier.padding(bottom = 6.dp)
                                    )

                                    result.detailedCorrectAnswers.forEach { detail ->
                                        RenderAnswerOptionContent(
                                            answer = detail,
                                            textColor = explanationTextColor
                                        )
                                    }
                                }
                            }
                        }
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(top = 12.dp),
                        color = Color.LightGray.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}

// Phân quyền hiển thị bài làm học sinh theo SĐT Giáo viên đăng nhập
private val TEACHER_STUDENT_PERMISSIONS: Map<String, Set<String>> = mapOf(
    "0972712415" to setOf("0972712415", "0987057417"),
    "0976037854" to setOf("0364634010", "0384801966"),
    "0666666666" to setOf("0555555555", "0444444444")
)

@Composable
fun StudentSubmissionsScreen(
    onBack: () -> Unit
) {
    val isToolbarVisible = remember { mutableStateOf(true) }
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (available.y < 0) {
                    isToolbarVisible.value = false
                } else if (available.y > 0) {
                    isToolbarVisible.value = true
                }
                return Offset.Zero
            }
        }
    }
    val repository = remember {
        StudentSubmissionsRepository()
    }

    var studentsList by remember {
        mutableStateOf<List<StudentDriveFolder>>(emptyList())
    }

    var filesList by remember {
        mutableStateOf<List<StudentDriveFile>>(emptyList())
    }

    var selectedStudent by remember {
        mutableStateOf<StudentDriveFolder?>(null)
    }

    var selectedFile by remember {
        mutableStateOf<StudentDriveFile?>(null)
    }

    var fileContent by remember {
        mutableStateOf("")
    }

    var isStudentsLoading by remember {
        mutableStateOf(true)
    }

    var isFilesLoading by remember {
        mutableStateOf(false)
    }

    var isContentLoading by remember {
        mutableStateOf(false)
    }

    var errorMessage by remember {
        mutableStateOf<String?>(null)
    }

    var studentsRetry by remember {
        mutableIntStateOf(0)
    }

    var filesRetry by remember {
        mutableIntStateOf(0)
    }

    var contentRetry by remember {
        mutableIntStateOf(0)
    }

    LaunchedEffect(studentsRetry) {
        isStudentsLoading = true
        errorMessage = null

        try {
            val response = withContext(Dispatchers.Main) {
                repository.getStudents()
            }

            if (response.status == "success") {
                val allStudents = response.students
                val loggedInTeacherPhone = getLoggedInPhone().trim()
                val allowedStudentPhones = TEACHER_STUDENT_PERMISSIONS[loggedInTeacherPhone]

                studentsList = if (allowedStudentPhones != null) {
                    allStudents.filter { student ->
                        val cleanPhone = student.phone.trim()
                        allowedStudentPhones.contains(cleanPhone)
                    }
                } else {
                    allStudents
                }
            } else {
                errorMessage = response.message ?: "Không thể tải danh sách học sinh."
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            errorMessage = "Lỗi kết nối: ${e.message}"
        } finally {
            isStudentsLoading = false
        }
    }

    LaunchedEffect(
        selectedStudent,
        filesRetry
    ) {
        val student = selectedStudent ?: return@LaunchedEffect

        if (student.files.isNotEmpty()) {
            filesList = student.files
            isFilesLoading = false
            return@LaunchedEffect
        }

        val folderId = student.folderId.ifEmpty { student.phone }
        if (folderId.isEmpty()) {
            filesList = emptyList()
            isFilesLoading = false
            return@LaunchedEffect
        }

        isFilesLoading = true
        errorMessage = null
        filesList = emptyList()

        try {
            val response = repository.getStudentFiles(folderId)

            if (response.status == "success") {
                filesList = response.files
            } else {
                errorMessage = response.message
                    ?: "Không thể tải danh sách bài nộp."
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            errorMessage = "Lỗi tải danh sách file: ${e.message}"
        } finally {
            isFilesLoading = false
        }
    }

    LaunchedEffect(
        selectedFile,
        contentRetry
    ) {
        val file = selectedFile ?: return@LaunchedEffect

        if (file.content.isNotBlank()) {
            fileContent = file.content
            isContentLoading = false
            return@LaunchedEffect
        }

        if (file.fileId.isEmpty()) {
            fileContent = "Không tìm thấy nội dung bài làm."
            isContentLoading = false
            return@LaunchedEffect
        }

        isContentLoading = true
        errorMessage = null
        fileContent = ""

        try {
            val response = repository.getFileContent(file.fileId)

            if (response.status == "success") {
                fileContent = response.content
            } else {
                errorMessage = response.message
                    ?: "Không thể tải nội dung bài làm."
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            errorMessage = "Lỗi đọc nội dung file: ${e.message}"
        } finally {
            isContentLoading = false
        }
    }

    val topPadding = getBannerAdHeight()
    val bottomPadding = getNavigationBarHeight()

    val isLoading = when {
        selectedFile != null -> isContentLoading
        selectedStudent != null -> isFilesLoading
        else -> isStudentsLoading
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(nestedScrollConnection)
            .padding(
                top = topPadding,
                bottom = bottomPadding
            )
            .background(Color(0xFFF7F9FC))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(nestedScrollConnection)
                .padding(vertical = 4.dp)
        ) {

            if (isToolbarVisible.value) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "‹",
                        modifier = Modifier
                            .clickable {
                                when {
                                    selectedFile != null -> {
                                        selectedFile = null
                                        fileContent = ""
                                        errorMessage = null
                                    }

                                    selectedStudent != null -> {
                                        selectedStudent = null
                                        selectedFile = null
                                        errorMessage = null
                                    }

                                    else -> onBack()
                                }
                            }
                            .padding(
                                horizontal = 12.dp,
                                vertical = 4.dp
                            ),
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color(0xFF1565C0)
                    )

                    Text(
                        text = when {
                            selectedFile != null ->
                                selectedFile?.fileName ?: "Chi tiết bài làm"

                            selectedStudent != null ->
                                "Học sinh: ${selectedStudent?.phone}"

                            else ->
                                "Bài làm học sinh"
                        },
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.width(40.dp))
                }
            }

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (errorMessage != null) {

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = errorMessage ?: "Có lỗi xảy ra.",
                            color = Color.Red,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                when {
                                    selectedFile != null ->
                                        contentRetry++

                                    selectedStudent != null ->
                                        filesRetry++

                                    else ->
                                        studentsRetry++
                                }
                            }
                        ) {
                            Text("Thử lại")
                        }
                    }
                }

            } else {

                when {

                    // MÀN HÌNH 3: NỘI DUNG FILE
                    selectedFile != null -> {

                        val parsedSubmission = remember(fileContent) {
                            parseStudentSubmissionContent(fileContent)
                        }

                        if (parsedSubmission != null) {
                            SubmissionResultView(submission = parsedSubmission)
                        } else {
                            Card(
                                modifier = Modifier.fillMaxSize(),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color.White
                                )
                            ) {
                                LazyColumn(
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    item {
                                        Text(
                                            text = fileContent,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = Color.DarkGray
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // MÀN HÌNH 2: DANH SÁCH FILE
                    selectedStudent != null -> {

                        if (filesList.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "Học sinh này chưa có bài nộp nào."
                                )
                            }
                        } else {

                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                itemsIndexed(
                                    items = filesList,
                                    key = { index, file ->
                                        val base = file.fileId.ifEmpty { file.fileName }
                                        if (base.isNotBlank()) "${base}_$index" else "file_$index"
                                    }
                                ) { index, file ->

                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                selectedFile = file
                                            },
                                        colors = CardDefaults.cardColors(
                                            containerColor = Color.White
                                        )
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp)
                                        ) {
                                            Text(
                                                text = file.fileName,
                                                style = MaterialTheme.typography.bodyLarge,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF0D47A1)
                                            )

                                            Spacer(
                                                modifier = Modifier.height(4.dp)
                                            )

                                            Text(
                                                text = "🕒 Nộp lúc: ${file.lastUpdated}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color.Gray
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // MÀN HÌNH 1: DANH SÁCH HỌC SINH
                    else -> {

                        if (studentsList.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "Chưa có dữ liệu bài làm nào trên Google Drive."
                                )
                            }
                        } else {

                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                itemsIndexed(
                                    items = studentsList,
                                    key = { index, student ->
                                        val base = student.phone.ifEmpty { student.folderId }
                                        if (base.isNotBlank()) "${base}_$index" else "student_$index"
                                    }
                                ) { index, student ->

                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                filesList = emptyList()
                                                selectedFile = null
                                                selectedStudent = student
                                            },
                                        colors = CardDefaults.cardColors(
                                            containerColor = Color.White
                                        )
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text(
                                                    text = "SĐT: ${student.phone}",
                                                    style = MaterialTheme.typography.bodyLarge,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }

                                            Surface(
                                                color = Color(0xFFE8F1F8),
                                                shape = RoundedCornerShape(12.dp)
                                            ) {
                                                Text(
                                                    text = "${student.effectiveFileCount} bài nộp",
                                                    modifier = Modifier.padding(
                                                        horizontal = 8.dp,
                                                        vertical = 4.dp
                                                    ),
                                                    style = MaterialTheme.typography.bodySmall,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFF0D47A1)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
