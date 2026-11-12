package com.exam.quizapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.exam.quizapp.model.*
import com.exam.quizapp.platform.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

//Màn hình lý thuyết và bài tập dùng chung được nhé
@Composable
fun TheoryAndExerciseScreen(
    onExerciseSelected: () -> Unit,
    onTheorySelected: () -> Unit,
    onSavedResultsSelected: () -> Unit = {},
    onStudentSubmissionsSelected: () -> Unit = {},
    expiredCheckScriptUrl: String = "https://script.google.com/macros/s/AKfycbwlkRLT0WqlrrWgPnUSuCPolPuRS28GBQcJgVjQBL0rHwWSSvsoxe1qXNQtjh-Usor2/exec",
    teacherCheckScriptUrl: String = "https://script.google.com/macros/s/AKfycbzmMJ18Yzq_Tl5Mp2vvDCmsmw9OSDMIVfQ8AGDkb8cS6qB89slkMIiWKWKChWGYcUmC/exec"
) {
    val topPadding = getBannerAdHeight()
    val bottomPadding = getNavigationBarHeight()
    val coroutineScope = rememberCoroutineScope()

    var isChecking by remember { mutableStateOf(false) }
    var isTeacherRole by remember { mutableStateOf(false) }
    var expiredDialogMessage by remember { mutableStateOf<String?>(null) }

    // Kiểm tra quyền Giáo Viên khi mở màn hình (Có mạng + SĐT nằm trong danh sách Giáo Viên)
    LaunchedEffect(Unit) {
        val phone = getLoggedInPhone()
        if (phone.isNotBlank()) {
            isTeacherRole = checkIsTeacherAccount(phone, teacherCheckScriptUrl)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 8.dp, end = 8.dp, top = topPadding, bottom = bottomPadding)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "CẬP NHẬT NGÀY 25/06/25",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Dòng 1: Nút Làm bài tập
            Button(
                onClick = {
                    val phone = getLoggedInPhone()
                    if (isVipAccount(phone)) {
                        onExerciseSelected()
                        return@Button
                    }
                    if (isChecking) return@Button
                    isChecking = true
                    coroutineScope.launch {
                        try {
                            val checkResult = checkAccountAccessStatus(phone, expiredCheckScriptUrl)
                            when (checkResult) {
                                AccountCheckResult.AUTHORIZED -> {
                                    onExerciseSelected()
                                }
                                AccountCheckResult.EXPIRED -> {
                                    expiredDialogMessage = "Tài khoản của bạn đã hết hạn, liên hệ 0972712415 để được gia hạn"
                                }
                                AccountCheckResult.NO_NETWORK -> {
                                    expiredDialogMessage = "Không thể kết nối mạng. Vui lòng kiểm tra kết nối Internet để tiếp tục!"
                                }
                            }
                        } catch (e: Exception) {
                            expiredDialogMessage = "Không thể kết nối mạng. Vui lòng kiểm tra kết nối Internet để tiếp tục!"
                        } finally {
                            isChecking = false
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                enabled = !isChecking
            ) {
                if (isChecking) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Đang kiểm tra...",
                        style = MaterialTheme.typography.titleMedium
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = "Làm Bài Tập",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            // Dòng 2: Nút Lý thuyết
            OutlinedButton(
                onClick = { onTheorySelected() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Build,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = "Lý Thuyết (Đang cập nhật)",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            // Dòng 3: Nút Bài đã lưu
            OutlinedButton(
                onClick = { onSavedResultsSelected() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Folder,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = "Bài Đã Lưu",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            // Dòng 4 (Chỉ hiển thị đối với tài khoản Giáo Viên có kết nối mạng): Nút Bài làm học sinh
            if (isTeacherRole) {
                OutlinedButton(
                    onClick = { onStudentSubmissionsSelected() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = "Bài Làm Học Sinh",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }

    if (expiredDialogMessage != null) {
        AlertDialog(
            onDismissRequest = { expiredDialogMessage = null },
            title = { Text("Thông báo", fontWeight = FontWeight.Bold) },
            text = { Text(expiredDialogMessage ?: "") },
            confirmButton = {
                TextButton(
                    onClick = { expiredDialogMessage = null }
                ) {
                    Text("OK", fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

// Màn hình chọn chương dùng chung được nhé
@Composable
fun ChapterSelectionScreen(
    onChapterSelected: (String) -> Unit,
    onList: () -> Unit,
    onStudentSubmissions: () -> Unit,
    onBackApp: () -> Unit,
) {
    val topPadding = getBannerAdHeight()

    val bottomPadding = getNavigationBarHeight()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = topPadding,
                bottom = bottomPadding
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.Center
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Text(
                            text = "BÀI TẬP (cập nhật 25/01/2025)",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                val chapters = listOf(
                    "VẬT LÍ NHIỆT",
                    "ÔN TẬP CHƯƠNG I",
                    "KHÍ LÍ TƯỞNG",
                    "ÔN TẬP HỌC KÌ I",
                    "TỪ TRƯỜNG",
                    "ÔN TẬP CHƯƠNG III (đang cập nhật)",
                    "VẬT LÍ HẠT NHÂN (đang cập nhật)",
                    "ÔN TẬP CHƯƠNG IV (đang cập nhật)",
                    "ÔN TẬP HỌC KÌ II (đang cập nhật)",
                    "TỔNG ÔN TẬP (đang cập nhật)"
                )

                items(chapters) { chapter ->
                    val isUpdating = chapter.contains("đang cập nhật")

                    if (!isUpdating) {
                        Button(
                            onClick = { onChapterSelected(chapter) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF00668C),
                                contentColor = Color.White
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Book,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = chapter,
                                    textAlign = TextAlign.Start,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    } else {
                        OutlinedButton(
                            onClick = {},
                            enabled = false,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Build,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = chapter,
                                    textAlign = TextAlign.Start,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }
        }

        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 3.dp,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onBackApp) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Filled.Home,
                            contentDescription = "Trang chủ",
                            modifier = Modifier.size(20.dp)
                        )
                        Text(text = "Trang chủ", fontSize = 10.sp)
                    }
                }
            }
        }
    }
}

// MÀN HÌNH CHỌN BÀI THI THEO TỪNG CHƯƠNG HOÀN TOÀN DÙNG CHUNG ĐƯỢC NHÉ
@Composable
fun LessonSelectionScreen(
    chapter: String,
    onQuizSelected: (String) -> Unit,
    onQuizSelectedAllQuestions: (String) -> Unit,
    onQuizReviewSelected: (String) -> Unit,
    onBack: () -> Unit)
{
    var boxHeight by remember { mutableIntStateOf(0) } // Lưu chiều cao của Box
    //var iconWidth by remember { mutableIntStateOf(0) } Kiểm tra thử có thế thay đổi Biến lưu độ rộng của icon
    // Lấy khoảng đệm theo chiều cao thành trạng thái và thanh điều khiển
    val topPadding = getBannerAdHeight()
    val bottomPadding = getNavigationBarHeight()
    val quizzes = when (chapter) {
        "VẬT LÍ NHIỆT" -> listOf(
            "Thử nghiệm 01",
            "Thử nghiệm 02",
            "Thử nghiệm 03",
            "Thử nghiệm 04",
            "Thử nghiệm 05",
            "Ôn lại Nhiệt học 8 (1)","Ôn lại Nhiệt học 8 (2)",
            "Nhiệt kế","Thang nhiệt độ",
            "Cấu tạo vật chất","Mô hình động học phân tử",
            "Cấu trúc chất rắn, lỏng, khí", "Hiện tượng chuyển thể",
            "Điểm ba và hiện tượng chuyển thể của nước",
            "Nội năng và biến thiên nội năng", "Nhiệt dung riêng, nhiệt nóng chảy riêng và nhiệt hóa hơi riêng",
        )
        "ÔN TẬP CHƯƠNG I" -> listOf (
            "Bài kiểm tra 1.1","Bài kiểm tra 1.2","Bài kiểm tra 1.3",
            "Bài kiểm tra 1.4"
        )
        "KHÍ LÍ TƯỞNG" -> listOf (
            "Ôn lại Nhiệt học 6","Ôn lại chất khí",
            "Định luật Boyle (lý thuyết)","Định luật Boyle (bài tập)",
            "Định luật Charles (lý thuyết)","Định luật Charles (bài tập)",
            "Định luật Gay-Lussac","Phương trình trạng thái khí lý tưởng (lý thuyết)",
            "Phương trình trạng thái khí lý tưởng (bài tập)",
            "Mối liên hệ áp suất và động năng tịnh tiến phân tử khí (lý thuyết)",
            "Mối liên hệ áp suất và động năng tịnh tiến phân tử khí (bài tập)",
        )
        "ÔN TẬP CHƯƠNG II" -> listOf (
            "Ôn lại Nhiệt học 6",
            "Ôn lại chất khí"
        )
        "ÔN TẬP HỌC KÌ I" -> listOf (
            "HKI-01","HKI-02","HKI-03"
        )
        "TỪ TRƯỜNG" -> listOf (
            "Ôn lại Điện học 9","Ôn lại Từ học 9","Tương tác từ, từ trường, từ phổ và đường sức từ",
            "Lực từ tác dụng lên dây dẫn mang dòng điện (lý thuyết)",
            //"Sẽ cập nhật sau"
        )
        "ÔN TẬP CHƯƠNG III" -> listOf (
            "Sẽ cập nhật sau"
        )
        "VẬT LÍ HẠT NHÂN" -> listOf (
        )
        "ÔN TẬP CHƯƠNG IV" -> listOf (
        )
        "ÔN TẬP HỌC KÌ II" -> listOf (
        )
        "TỔNG ÔN TẬP" -> listOf (
        )
        else -> emptyList()
    }
    Box (
        modifier = Modifier
            .fillMaxSize()
            //.background(Color.White) // Đặt màu nền trắng
            .padding(start = 0.dp, end = 0.dp, top = topPadding, bottom = bottomPadding)
    )  {
        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 0.dp, end = 0.dp, top = 0.dp, bottom = with(LocalDensity.current) { boxHeight.toDp() }) // Dùng chiều cao Box làm padding
        )   {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // TIÊU ĐỀ
                item {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = chapter,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "CHỌN BÀI TRẮC NGHIỆM",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // DANH SÁCH BÀI TẬP
                if (quizzes.isEmpty() || (quizzes.size == 1 && quizzes[0] == "Sẽ cập nhật sau")) {
                    item {
                        OutlinedButton(
                            onClick = {},
                            enabled = false,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Build, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = "Nội dung chương này đang được cập nhật...")
                            }
                        }
                    }
                } else {
                    items(quizzes) { quiz ->
                        val isUpdating = quiz == "Sẽ cập nhật sau"

                        if (!isUpdating) {
                            // THẺ BÀI TẬP (Đã tách riêng vùng bấm làm bài và vùng bấm xem Review)
                            Surface(
                                color = Color(0xFF00668C), // Màu xanh
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Cột bên trái: bao gồm 2 mục nằm đè lên nhau thành cột
                                    Column(
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        // Mục 1 (hàng trên): Bấm vào tên bài để LÀM LẦN LƯỢT TỪNG CÂU
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable { onQuizSelected(quiz) }
                                                .padding(start = 12.dp, end = 12.dp, top = 10.dp, bottom = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "$quiz (làm từng câu)",
                                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                                color = Color.White,
                                                modifier = Modifier.weight(1f)
                                            )
                                        }

                                        // Mục 2 (hàng dưới): Bấm vào dải dấu chấm để LÀM TOÀN BỘ CÂU HỎI MỘT LƯỢT
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable { onQuizSelectedAllQuestions(quiz) }
                                                .padding(start = 12.dp, end = 12.dp, top = 4.dp, bottom = 10.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "$quiz (làm nhiều câu。。。。。。。。)",//"。。。。。。。。。。。。",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = Color.White.copy(alpha = 0.8f),
                                                modifier = Modifier.weight(1f)
                                            )
                                        }
                                    }

                                    // Vùng 2 (bên phải): Nút Mắt riêng biệt để XEM REVIEW
                                    IconButton(
                                        onClick = { onQuizReviewSelected(quiz) },
                                        modifier = Modifier.padding(end = 4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.Visibility,
                                            contentDescription = "Xem review bài $quiz",
                                            tint = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        Box (
            modifier = Modifier
                .fillMaxWidth() // Đảm bảo Box chiếm toàn bộ chiều rộng
                .align(Alignment.BottomCenter)
                .background(MaterialTheme.colorScheme.surface)
                .onGloballyPositioned { coordinates ->
                    boxHeight = coordinates.size.height // Cập nhật chiều cao của Box
                }
                .padding(start = 8.dp, end = 8.dp, top = 0.dp, bottom = 0.dp)
        ) {
            Button(
                onClick = { onBack() },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .wrapContentSize(),
                contentPadding = PaddingValues(0.dp), // Loại bỏ padding mặc định
                shape = RoundedCornerShape(4.dp), // Bo góc nhẹ
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f), // Màu nền của nút
                    contentColor = Color.Black // Màu của icon và chữ
                ),
            ) {
                Column (horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(imageVector = Icons.Outlined.Home , contentDescription = "Trang đầu", modifier = Modifier.size(20.dp))
                    Text(text = "Trang đầu", fontSize = 10.sp, fontWeight = FontWeight.Light,
                        color = Color.Black)
                }
            }
            Button(
                onClick = {
                    onBack()
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 4.dp),
                //.wrapContentSize()
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f), // Màu nền của nút
                    contentColor = Color.Black // Màu của icon và chữ
                ),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack, // Sử dụng phiên bản AutoMirrored
                    contentDescription = "Quay lại",
                    tint = Color.Black // Màu icon đen
                )
            }
        }
    }
}

//MÀN HÌNH HIỂN THỊ LẦN LƯỢT TỪNG CÂU HỎI TRẮC NGHIỆM VÀ TỰ LUẬN ĐỂ LÀM BÀI TẬP
@Composable
fun QuestionScreen(
    question: Question,
    currentQuestionIndex: Int,
    totalQuestions: Int,
    onTextAnswerSubmitted: (userInput: String, isCorrect: Boolean) -> Unit,
    onAnswerSelected: (Int) -> Unit,
    onBackQuiz: () -> Unit,
    onHomePage: () -> Unit,
    onBackQuestion: () -> Unit,
) {// Lấy khoảng đệm theo chiều cao thành trạng thái và thanh điều khiển
    val bottomPadding = getNavigationBarHeight()
    val topPadding = getBannerAdHeight()
    // Tạo LazyListState
    val lazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    // Biến trạng thái để kiểm soát hiển thị tiêu đề và nút
    val isToolbarVisible = remember { mutableStateOf(true) }
    //val context = LocalContext.current // <-- THÊM DÒNG NÀY
    // Sử dụng nestedScroll để phát hiện khi người dùng cuộn
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

    // Sử dụng LaunchedEffect với currentQuestion Index để đưa câu hỏi mới về đầu câu
    LaunchedEffect(currentQuestionIndex) {
        coroutineScope.launch {
            lazyListState.scrollToItem(0)
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            //.background(Color.White)
            .padding(top = topPadding, bottom = bottomPadding)
            .imePadding()
    ) {
        val horizontalPadding = if (question.mainContent !is QuestionContent.ClipUrl) {
            8.dp
        } else {
            0.dp
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(nestedScrollConnection) // Theo dõi sự kiện cuộn
                .padding(horizontal = horizontalPadding)
        ) {
            if (question.mainContent !is QuestionContent.ClipUrl) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                    //.background(Color.White.copy(alpha = 1.0f))
                ) {
                    val progress = if (totalQuestions > 0) (currentQuestionIndex + 1) / totalQuestions
                        .toFloat() else 0f
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 0.dp, horizontal = 4.dp),
                        color = Color(0xFF00668C).copy(alpha = 1.0f)
                    )
                    Text(
                        text = "${currentQuestionIndex + 1} / $totalQuestions",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = Color.Black, // Màu chữ đen
                        modifier = Modifier
                            .align(Alignment.BottomEnd),
                        //color = Color.Blue.copy(alpha = 1.0f)
                    )
                }
            }
            val questionBackground = Color(0xFFE8F1F8) // xanh rất nhạt
            val questionTextColor = Color(0xFF172B3A)   // xanh đen, dễ đọc
            val questionErrorColor = Color(0xFF8A4B00)  // cam nâu đậm
            LazyColumn(
                state = lazyListState, // Sử dụng LazyListState ở đây
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                //.padding(bottom = 0.dp)
            ) {
                item {
                    when (val content = question.mainContent) {

                        is QuestionContent.AudioUrl -> {
                            AudioPlayerFromUrl(
                                audioUrl = content.audioUrl, // hoặc it.audioUrl tùy tên biến trong khối when của bạn
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        is QuestionContent.ClipUrl -> {
                            VideoPlayerFromUrl(
                                videoUrl = content.clipUrl,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        is QuestionContent.TextRes -> Text(
                            text = stringResource(content.textRes),
                            color = questionTextColor,
                            style = MaterialTheme.typography.titleLarge,
                            textAlign = TextAlign.Justify,
                            //color = Color.Black, // Màu chữ đen
                            modifier = Modifier
                                .background(questionBackground)
                                .padding(horizontal = 4.dp)
                                .fillMaxWidth(),
                        )
                        is QuestionContent.Text -> Text(
                            text = content.text,
                            color = questionTextColor,
                            style = MaterialTheme.typography.titleLarge,
                            textAlign = TextAlign.Justify,
                            //color = Color.Black, // Màu chữ đen
                            modifier = Modifier
                                .background(questionBackground)
                                .padding(horizontal = 4.dp)
                                .fillMaxWidth()
                        )
                        is QuestionContent.ImageRes -> Image(
                            painter = painterResource(content.imageRes),
                            contentDescription = "Hình ảnh câu hỏi", // Thêm mô tả cho hình ảnh câu hỏi
                            modifier = Modifier
                                .fillMaxWidth(),
                            contentScale = ContentScale.FillWidth
                            //.background(Color.Unspecified.copy(alpha = 1.0f))
                        )
                        is QuestionContent.ImageUrl -> {//đoạn code này khác các đoạn kia nhưng không sao nhé
                            if (content.imageUrl.contains("long") || content.imageUrl.contains("big")) {
                                // Nếu url chứa từ "long" hoặc "big", gọi hàm ImageFromUrl để tải và hiển thị ảnh không có phóng to
                                ImageFromUrl(
                                    imageUrl = content.imageUrl,
                                    contentDescription = "Hình ảnh câu hỏi"
                                )
                            } else
                            {
                                // Các trường hợp còn lại, dùng ZoomableImageFromUrl để tải và hiển thị ảnh có thể phóng to
                                ZoomableImageFromUrl(
                                    imageUrl = content.imageUrl,
                                    contentDescription = "Hình ảnh câu hỏi"
                                )
                            }
                        }

                        is QuestionContent.TextUrl -> {

                            var textContent by remember(content.textUrl) {
                                mutableStateOf("Đang tải...")
                            }

                            var isError by remember(content.textUrl) {
                                mutableStateOf(false)
                            }

                            LaunchedEffect(content.textUrl) {

                                textContent = "Đang tải..."
                                isError = false

                                try {
                                    println("COMPOSE_TEST_A: BAT DAU FETCH")

                                    val result = fetchTextFromUrl(content.textUrl)

                                    println("COMPOSE_TEST_B: FETCH HOAN TAT")
                                    println("COMPOSE_TEST_B_LENGTH: ${result.length}")

                                    textContent = result
                                    isError = false

                                    println("COMPOSE_TEST_C: CAP NHAT GIAO DIEN")

                                } catch (e: CancellationException) {

                                    // Không xử lý như lỗi tải dữ liệu
                                    throw e

                                } catch (e: Exception) {

                                    println("COMPOSE_TEST_ERROR: ${e.message}")
                                    e.printStackTrace()

                                    textContent =
                                        "Không thể tải nội dung, vui lòng kiểm tra kết nối mạng. " +
                                                "Nếu nội dung bị lỗi, phản hồi qua Zalo: 0972712415."

                                    isError = true
                                }
                            }

                            Text(
                                text = textContent,
                                color = if (isError) questionErrorColor else questionTextColor,
                                style = if (isError)
                                    MaterialTheme.typography.bodyLarge
                                else
                                    MaterialTheme.typography.titleLarge,
                                textAlign = if (isError)
                                    TextAlign.Center
                                else
                                    TextAlign.Justify,
                                modifier = Modifier
                                    .background(
                                        if (isError) Color.LightGray
                                        else questionBackground
                                    )
                                    .padding(horizontal = 4.dp)
                                    .fillMaxWidth()
                            )
                        }

                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }
                // hiển thị câu hỏi thêm
                items(question.extraContents.size) { index ->
                    val extraText = question.extraContents[index]
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .padding(vertical = 2.dp),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        when (extraText) {
                            is QuestionContent.AudioUrl -> {
                                AudioPlayerFromUrl(
                                    audioUrl = extraText.audioUrl, // hoặc it.audioUrl tùy tên biến trong khối when của bạn
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            is QuestionContent.ClipUrl -> {
                                VideoPlayerFromUrl(
                                    videoUrl = extraText.clipUrl,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            } //Luu y


                            is QuestionContent.TextRes -> Text(
                                text = stringResource(extraText.textRes),
                                color = questionTextColor,
                                style = MaterialTheme.typography.titleLarge,
                                textAlign = TextAlign.Justify,
                                //color = Color.Black, // Màu chữ đen
                                modifier = Modifier
                                    .background(questionBackground)
                                    .padding(horizontal = 4.dp)
                                    .fillMaxWidth(),
                            )
                            is QuestionContent.Text -> Text(
                                text = extraText.text,
                                color = questionTextColor,
                                style = MaterialTheme.typography.titleLarge,
                                textAlign = TextAlign.Justify,
                                //color = Color.Black, // Màu chữ đen
                                modifier = Modifier
                                    .background(questionBackground)
                                    .padding(horizontal = 4.dp)
                                    .fillMaxWidth()
                            )
                            is QuestionContent.ImageRes -> Image(
                                painter = painterResource(extraText.imageRes),
                                contentDescription = "Hình ảnh câu hỏi", // Thêm mô tả cho hình ảnh câu hỏi
                                modifier = Modifier
                                    .fillMaxWidth(),
                                contentScale = ContentScale.FillWidth
                                //.background(Color.Unspecified.copy(alpha = 1.0f))
                            )
                            is QuestionContent.ImageUrl -> {
                                if (extraText.imageUrl.contains("long") || extraText.imageUrl.contains("big")) {
                                    // Nếu url chứa từ "long" hoặc "big", gọi hàm ImageFromUrl để tải và hiển thị ảnh không có phóng to
                                    ImageFromUrl(
                                        imageUrl = extraText.imageUrl,
                                        contentDescription = "Hình ảnh câu hỏi"
                                    )
                                } else
                                {
                                    // Các trường hợp còn lại, dùng ZoomableImageFromUrl để tải và hiển thị ảnh có thể phóng to
                                    ZoomableImageFromUrl(
                                        imageUrl = extraText.imageUrl,
                                        contentDescription = "Hình ảnh câu hỏi"
                                    )
                                }
                            }
                            is QuestionContent.TextUrl -> {
                                var textContent by remember { mutableStateOf("Đang tải...") }
                                var isError by remember { mutableStateOf(false) }

                                LaunchedEffect(extraText.textUrl) {
                                    try {
                                        textContent = fetchTextFromUrl(extraText.textUrl)
                                        isError = false
                                    } catch (e: Exception) {
                                        textContent = "Không thể tải nội dung, chưa được kết nối mạng hoặc " +
                                                "nội dung bị lỗi. Nếu nội dụng bị lỗi phản hồi qua Zalo: 0972712415 để khắc phục"
                                        isError = true
                                    }
                                }

                                if (!isError) {
                                    Text(
                                        text = textContent,
                                        color = questionTextColor,
                                        style = MaterialTheme.typography.titleLarge,
                                        textAlign = TextAlign.Justify,
                                        //color = Color.Black, // Màu chữ đen
                                        modifier = Modifier
                                            .background(questionBackground)
                                            .padding(horizontal = 4.dp)
                                            .fillMaxWidth()
                                    )
                                } else {
                                    Text(
                                        text = "Lỗi khi tải nội dung, chưa được kết nối mạng hoặc nội dung bị " +
                                                "lỗi. Nếu nội dụng bị lỗi phản hồi qua Zalo: 0972712415 để khắc phục",
                                        style = MaterialTheme.typography.bodyLarge.copy(color = questionErrorColor),
                                        textAlign = TextAlign.Center,
                                        //color = Color.Black, // Màu chữ đen
                                        modifier = Modifier
                                            .background(questionBackground)
                                            .padding(horizontal = 4.dp)
                                            .fillMaxWidth()
                                    )
                                }
                            }
                        }
                    }
                }

                if (question.correctAnswer == null && question.correctTextAnswers.isNotEmpty()) {
                    item {
                        var userInput by remember(currentQuestionIndex, question) { mutableStateOf("") }

                        // 1. Lấy ngay các đáp án tĩnh (Text và TextRes) vì gọi được stringResource trực tiếp tại đây
                        val staticCorrectTexts = question.correctTextAnswers.mapNotNull {
                            when (it) {
                                is AnswerOption.Text -> it.text
                                is AnswerOption.TextRes -> stringResource(it.textRes) // Hỗ trợ TextRes
                                else -> null
                            }
                        }

                        // 2. Khai báo state để chứa các đáp án tải từ URL
                        var urlCorrectTexts by remember(currentQuestionIndex, question) { mutableStateOf<List<String>>(emptyList()) }
                        var isUrlLoading by remember(currentQuestionIndex, question) { mutableStateOf(false) }

                        // 3. Dùng LaunchedEffect để tải ngầm các TextUrl (nếu có)
                        LaunchedEffect(question) {
                            val urlTexts = mutableListOf<String>()
                            val textUrlOptions = question.correctTextAnswers.filterIsInstance<AnswerOption.TextUrl>()

                            if (textUrlOptions.isNotEmpty()) {
                                isUrlLoading = true
                                for (option in textUrlOptions) {
                                    try {
                                        val textContent = fetchTextFromUrl(option.textUrl)
                                        urlTexts.add(textContent)
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }
                                isUrlLoading = false
                            }
                            urlCorrectTexts = urlTexts
                        }

                        // 4. Gộp cả đáp án tĩnh và đáp án từ URL lại, sau đó chuẩn hóa
                        val allCorrectAnswers = (staticCorrectTexts + urlCorrectTexts).map {
                            normalizeText(
                                it
                            )
                        }
                        val isLoading = isUrlLoading
                        val keyboardController = LocalSoftwareKeyboardController.current
                        val focusManager = LocalFocusManager.current
                        // 5. Nút Gửi kiểm tra đáp án
                        Button(
                            onClick = {
                                if (userInput.isNotBlank() && !isLoading) {
                                    keyboardController?.hide()
                                    focusManager.clearFocus(force = true)

                                    val userTextNormalized =
                                        normalizeText(userInput)

                                    // So sánh user nhập với toàn bộ danh sách đáp án (bao gồm cả Text, TextRes và TextUrl)
                                    val isCorrect = allCorrectAnswers.any { it.isNotBlank() && it == userTextNormalized }

                                    onTextAnswerSubmitted(userInput, isCorrect)
                                }
                            },
                            enabled = !isLoading,
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(4.dp)
                        ) {
                            Text(if (isLoading) "Đang chuẩn bị..." else "Gửi")
                        }
                        // 6. Ô nhập liệu tự luận
                        OutlinedTextField(
                            value = userInput,
                            onValueChange = { userInput = it },
                            placeholder = { Text(if (isLoading) "Đang tải dữ liệu đáp án..." else "Nhập câu trả lời...") },
                            enabled = !isLoading,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    focusManager.clearFocus(force = true)
                                    keyboardController?.hide()
                                }
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp)
                        )

                    }
                }
                else {
                    // CÂU TRẮC NGHIỆM
                    items(question.answers.size) { index ->
                        val answer = question.answers[index]
                        Button(
                            onClick = {
                                onAnswerSelected(index)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp)
                                .semantics { contentDescription = "Answer option ${index + 1}" }, // Thêm mô tả trợ năng
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF00668C),//00668C màu xanh ô đáp án trắc nghiệm
                                //contentColor = Color.White
                            ),
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Start
                            ) {
                                when (answer) {
                                    is AnswerOption.ImageUrl -> {
                                        if (answer.imageUrl.contains("long") || answer.imageUrl.contains("big")) {
                                            // Nếu url chứa từ "long" hoặc "big", gọi hàm ImageFromUrl để tải và hiển thị ảnh không có phóng to
                                            ImageFromUrl(
                                                imageUrl = answer.imageUrl,
                                                contentDescription = "Hình ảnh câu hỏi"
                                            )
                                        } else
                                        {
                                            // Các trường hợp còn lại, dùng ZoomableImageFromUrl để tải và hiển thị ảnh có thể phóng to
                                            ZoomableImageFromUrl(
                                                imageUrl = answer.imageUrl,
                                                contentDescription = "Hình ảnh câu hỏi"
                                            )
                                        }
                                    }
                                    is AnswerOption.TextRes -> Text(
                                        text = stringResource(answer.textRes),
                                        //style = MaterialTheme.typography.titleLarge,
                                        textAlign = TextAlign.Justify,
                                        //modifier = Modifier.padding(horizontal = 4.dp).fillMaxWidth()
                                    )
                                    is AnswerOption.Text -> Text(text = answer.text,
                                        textAlign = TextAlign.Justify)
                                    is AnswerOption.TextUrl -> {
                                        var textContent by remember { mutableStateOf("Đang tải...") }
                                        var isError by remember { mutableStateOf(false) }

                                        LaunchedEffect(answer.textUrl) {
                                            try {
                                                textContent = fetchTextFromUrl(answer.textUrl)
                                                isError = false
                                            } catch (e: Exception) {
                                                textContent = "Không thể tải nội dung, kiểm tra kết nối mạng hoặc phản hồi nội dung lỗi qua Zalo: 0972721415"
                                                isError = true
                                            }
                                        }

                                        if (!isError) {
                                            Text(
                                                text = textContent,
                                                textAlign = TextAlign.Justify,
                                                //modifier = Modifier.padding(horizontal = 4.dp).fillMaxWidth()
                                            )
                                        } else {
                                            Text(
                                                text = "Lỗi khi tải nội dung, kiểm tra kết nối mạng hoặc phản hồi lỗi nội dụng qua Zalo: 0972712415",
                                                textAlign = TextAlign.Justify,
                                                //modifier = Modifier.padding(horizontal = 4.dp).fillMaxWidth()
                                            )
                                        }
                                    }
                                    is AnswerOption.ImageRes -> Box(
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Image(
                                            painter = painterResource(answer.imageRes),
                                            contentDescription = "Hình ảnh đáp án", // Thêm mô tả cho hình ảnh đáp án
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .align(Alignment.Center),
                                            contentScale = ContentScale.FillWidth
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (isToolbarVisible.value) {
            Box(
                modifier = Modifier
                    //.padding(bottom = bottomPadding)
                    .fillMaxSize()
            ) {
                if (question.mainContent !is QuestionContent.ClipUrl) {
                    Box (
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .padding(horizontal = 0.dp)
                            .align(Alignment.BottomCenter),
                    )
                    {   // Nút quay lại câu hỏi trước
                        if (currentQuestionIndex != 0) {
                            Button(
                                onClick = {
                                    onBackQuestion()
                                },
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(start = 4.dp, bottom = 0.dp) // Căn chỉnh lề
                                    .wrapContentSize(),
                                contentPadding = PaddingValues(0.dp), // Loại bỏ padding mặc định
                                shape = RoundedCornerShape(4.dp), // Bo góc nhẹ
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.LightGray.copy(0.3f),
                                    contentColor = Color.Black
                                ),
                            ) {
                                Column (horizontalAlignment = Alignment.CenterHorizontally)  {
                                    Icon(imageVector = Icons.Filled.ChevronLeft, contentDescription = "Câu trước", modifier = Modifier.size(20.dp))
                                    Text(text = "Câu trước", fontSize = 10.sp, fontWeight = FontWeight.Light)
                                }
                            }
                        }
                        // Nút quay về trang chủ
                        Button(
                            onClick = { onHomePage() },
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .wrapContentSize(),
                            contentPadding = PaddingValues(0.dp), // Loại bỏ padding mặc định
                            shape = RoundedCornerShape(4.dp), // Bo góc nhẹ
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.LightGray.copy(0.3f), // Màu nền của nút
                                contentColor = Color.Black // Màu của icon và chữ
                            ),
                        ) {
                            Column (horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(imageVector = Icons.Outlined.Home , contentDescription = "Trang đầu", modifier = Modifier.size(20.dp))
                                Text(text = "Trang đầu", fontSize = 10.sp, fontWeight = FontWeight.Light,
                                    color = Color.Black)
                            }
                        }
                        // Nút "QUAY LẠI (chọn bài thi khác)" góc dưới bên phải
                        Button(
                            onClick = {
                                onBackQuiz()
                            },
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(end = 4.dp, bottom = 0.dp)
                                .wrapContentSize(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.LightGray.copy(0.3f),
                                contentColor = Color.Black
                            )
                        )
                        {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack, // Sử dụng phiên bản AutoMirrored
                                contentDescription = "Quay lại"
                            )
                        }
                    }

                }

            }
        }
    }
}
