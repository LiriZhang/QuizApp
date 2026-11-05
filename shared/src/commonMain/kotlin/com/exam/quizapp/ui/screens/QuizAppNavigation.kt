package com.exam.quizapp.ui.screens

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.geometry.Offset
import kotlinx.coroutines.launch
import com.exam.quizapp.model.*
import com.exam.quizapp.db.*
import com.exam.quizapp.viewmodel.*
import com.exam.quizapp.platform.*
import androidx.compose.runtime.remember
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import coil3.compose.AsyncImage
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Build
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import kotlin.math.abs
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.sp
import com.exam.quizapp.shared.resources.Res
import com.exam.quizapp.shared.resources.allStringResources
import com.exam.quizapp.shared.resources.allDrawableResources
import com.exam.quizapp.shared.resources.*
import org.jetbrains.compose.resources.painterResource

// Multiplatform QuizApp Composable
// Hàm điều phối màn hình dùng riêng cho từng nền tảng
@Composable
expect fun QuizApp(
    viewModel: QuizViewModel = viewModel(),
    onQuizCompleted: () -> Unit
)

//Màn hình lý thuyết và bài tập dùng chung được nhé
@Composable
fun TheoryAndExerciseScreen(
    onExerciseSelected: () -> Unit,
    onTheorySelected: () -> Unit
) {
    val topPadding = getBannerAdHeight()
    val bottomPadding = getNavigationBarHeight()
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

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { onExerciseSelected() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
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

            OutlinedButton(
                onClick = { onTheorySelected() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
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
        }
    }
}
// Màn hình chọn chương dùng chung được nhé
@Composable
fun ChapterSelectionScreen(
    onChapterSelected: (String) -> Unit,
    onList: () -> Unit,
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
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onList) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Outlined.Folder,
                            contentDescription = "Bài đã lưu",
                            modifier = Modifier.size(20.dp)
                        )
                        Text(text = "Bài đã lưu", fontSize = 10.sp)
                    }
                }

                TextButton(onClick = onBackApp) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Filled.Home,
                            contentDescription = "Trang đầu",
                            modifier = Modifier.size(20.dp)
                        )
                        Text(text = "Trang đầu", fontSize = 10.sp)
                    }
                }

                TextButton(onClick = onBackApp) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Quay lại",
                            modifier = Modifier.size(20.dp)
                        )
                        Text(text = "Quay lại", fontSize = 10.sp)
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

// MÀN HÌNH HIỂN THỊ DANH SÁCH MỘT LƯỢT THEO DẠNG REVIEW CÂN NHẮC DÙNG CHUNG HOẶC RIÊNG NHÉ
@Composable
fun ReviewQuizScreen(
    lessonName: String, // Tên bài thi
    questions: List<Question>, // Danh sách các câu hỏi
    onStartQuiz: () -> Unit, // Hàm điều hướng sang màn hình làm bài trắc nghiệm
    onHomePage: () -> Unit,
    onBack: () -> Unit, // Hàm quay lại màn hình trước

) {
    val topPadding = getBannerAdHeight()
    val bottomPadding = getNavigationBarHeight()
    // Biến trạng thái để kiểm soát hiển thị tiêu đề và nút
    val isToolbarVisible = remember { mutableStateOf(true) }
    // Sử dụng nestedScroll để phát hiện khi người dùng cuộn
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (available.y < 0) {
                    // Cuộn xuống, ẩn tiêu đề và nút
                    isToolbarVisible.value = false
                } else if (available.y > 0) {
                    // Cuộn lên, hiện tiêu đề và nút
                    isToolbarVisible.value = true
                }
                return Offset.Zero
            }
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = topPadding)
            .nestedScroll(nestedScrollConnection), // Theo dõi sự kiện cuộn
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Hiển thị tiêu đề và điểm số nếu isToolbarVisible là true
        if (isToolbarVisible.value) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.LightGray.copy(alpha = 0.5f))
            )
            {
                Text(
                    text = lessonName,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)

                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = topPadding, bottom = bottomPadding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp)
                    .nestedScroll(nestedScrollConnection), // Theo dõi sự kiện cuộn

            ) {
                itemsIndexed(questions) { index, question ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        val currentQuestionIndex = questions.indexOf(question) + 1                    // Hiển thị câu hỏi
                        Text(
                            text = "Câu $currentQuestionIndex",
                            color = Color(0xFF0D47A1),
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        // Hiển thị câu hỏi
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
                            } //Luu y

                            is QuestionContent.TextRes -> Text(
                                text = org.jetbrains.compose.resources.stringResource(content.textRes),
                                style = MaterialTheme.typography.titleLarge,
                                textAlign = TextAlign.Justify,
                                color = Color.Black, // Màu chữ đen
                                modifier = Modifier
                                    .background(Color.LightGray)
                                    // modifier = Modifier
                                    .padding(horizontal = 4.dp)
                                    .fillMaxWidth()
                            )

                            is QuestionContent.Text -> Text(
                                text = content.text,
                                style = MaterialTheme.typography.titleLarge,
                                textAlign = TextAlign.Justify,
                                color = Color.Black, // Màu chữ đen
                                modifier = Modifier
                                    .background(Color.LightGray)
                                    //modifier = Modifier
                                    .padding(horizontal = 4.dp)
                                    .fillMaxWidth()
                            )

                            is QuestionContent.ImageRes -> Image(
                                painter = org.jetbrains.compose.resources.painterResource(content.imageRes),
                                contentDescription = "Hình ảnh câu hỏi", // Thêm mô tả cho hình ảnh câu hỏi
                                modifier = Modifier
                                    .fillMaxWidth(),
                                contentScale = ContentScale.FillWidth
                                //.background(Color.Unspecified.copy(alpha = 1.0f))
                            )

                            is QuestionContent.ImageUrl -> {
                                if (content.imageUrl.contains("long") || content.imageUrl.contains("big")) {
                                    // Nếu url chứa từ "long" hoặc "big", gọi hàm ImageFromUrl để tải và hiển thị ảnh không có phóng to
                                    ImageFromUrl(
                                        imageUrl = content.imageUrl,
                                        contentDescription = "Hình ảnh câu hỏi"
                                    )
                                } else {
                                    // Các trường hợp còn lại, dùng ZoomableImageFromUrl để tải và hiển thị ảnh có thể phóng to
                                    ZoomableImageFromUrl(
                                        imageUrl = content.imageUrl,
                                        contentDescription = "Hình ảnh câu hỏi"
                                    )
                                }
                            }
                            is QuestionContent.TextUrl -> {
                                var textContent by remember { mutableStateOf("Đang tải...") }
                                var isError by remember { mutableStateOf(false) }

                                LaunchedEffect(content.textUrl) {
                                    try {
                                        textContent =
                                            fetchTextFromUrl(content.textUrl)
                                        isError = false
                                    } catch (e: Exception) {
                                        textContent =
                                            "Không thể tải nội dung, chưa được kết nối mạng hoặc " +
                                                    "nội dung bị lỗi. Nếu nội dụng bị lỗi phản hồi qua Zalo: 0972712415 để khắc phục"
                                        isError = true
                                    }
                                }

                                if (!isError) {
                                    Text(
                                        text = textContent,
                                        style = MaterialTheme.typography.titleLarge,
                                        textAlign = TextAlign.Justify,
                                        color = Color.Black, // Màu chữ đen
                                        modifier = Modifier
                                            .background(Color.LightGray)
                                            //modifier = Modifier
                                            .padding(horizontal = 4.dp)
                                            .fillMaxWidth()
                                    )
                                } else {
                                    Text(
                                        text = "Lỗi khi tải nội dung, chưa được kết nối mạng hoặc nội dung bị " +
                                                "lỗi. Nếu nội dụng bị lỗi phản hồi qua Zalo: 0972712415 để khắc phục",
                                        style = MaterialTheme.typography.bodyLarge.copy(color = Color.Red),
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier
                                            .padding(horizontal = 4.dp)
                                            .fillMaxWidth()
                                    )
                                }
                            }
                        }
                        // Hiển thị phần câu hỏi thêm
                        question.extraContents.forEachIndexed { extraTextIndex, extraText ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                /*Text(
                                    text = "${'A' + answerIndex}: ",
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.padding(end = 4.dp)
                                )*/
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
                                        text = org.jetbrains.compose.resources.stringResource(extraText.textRes),
                                        style = MaterialTheme.typography.titleLarge,
                                        textAlign = TextAlign.Justify,
                                        color = Color.Black, // Màu chữ đen
                                        modifier = Modifier
                                            .background(Color.LightGray)
                                            // modifier = Modifier
                                            .padding(horizontal = 4.dp)
                                            .fillMaxWidth()
                                    )

                                    is QuestionContent.Text -> Text(
                                        text = extraText.text,
                                        style = MaterialTheme.typography.titleLarge,
                                        textAlign = TextAlign.Justify,
                                        color = Color.Black, // Màu chữ đen
                                        modifier = Modifier
                                            .background(Color.LightGray)
                                            //modifier = Modifier
                                            .padding(horizontal = 4.dp)
                                            .fillMaxWidth()
                                    )

                                    is QuestionContent.ImageRes -> Image(
                                        painter = org.jetbrains.compose.resources.painterResource(extraText.imageRes),
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
                                        } else {
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
                                                textContent =
                                                    fetchTextFromUrl(
                                                        extraText.textUrl
                                                    )
                                                isError = false
                                            } catch (e: Exception) {
                                                textContent =
                                                    "Không thể tải nội dung, chưa được kết nối mạng hoặc " +
                                                            "nội dung bị lỗi. Nếu nội dụng bị lỗi phản hồi qua Zalo: 0972712415 để khắc phục"
                                                isError = true
                                            }
                                        }

                                        if (!isError) {
                                            Text(
                                                text = textContent,
                                                style = MaterialTheme.typography.titleLarge,
                                                textAlign = TextAlign.Justify,
                                                color = Color.Black, // Màu chữ đen
                                                modifier = Modifier
                                                    .background(Color.LightGray)
                                                    //modifier = Modifier
                                                    .padding(horizontal = 4.dp)
                                                    .fillMaxWidth()
                                            )
                                        } else {
                                            Text(
                                                text = "Lỗi khi tải nội dung, chưa được kết nối mạng hoặc nội dung bị " +
                                                        "lỗi. Nếu nội dụng bị lỗi phản hồi qua Zalo: 0972712415 để khắc phục",
                                                style = MaterialTheme.typography.bodyLarge.copy(color = Color.Red),
                                                textAlign = TextAlign.Center,
                                                modifier = Modifier
                                                    .padding(horizontal = 4.dp)
                                                    .fillMaxWidth()
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        // Hiển thị đáp án
                        question.answers.forEachIndexed { answerIndex, answer ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                /*Text(
                                    text = "${'A' + answerIndex}: ",
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.padding(end = 4.dp)
                                )*/
                                when (answer) {
                                    is AnswerOption.Text -> Text(
                                        text = answer.text,
                                        textAlign = TextAlign.Justify
                                    )
                                    is AnswerOption.TextRes -> Text(
                                        text = org.jetbrains.compose.resources.stringResource(answer.textRes),
                                        //style = MaterialTheme.typography.titleLarge,
                                        textAlign = TextAlign.Justify,
                                        //modifier = Modifier.padding(horizontal = 4.dp).fillMaxWidth()
                                    )

                                    is AnswerOption.ImageUrl -> AsyncImage(
                                        model = answer.imageUrl,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .fillMaxWidth(),//.background(Color.Unspecified.copy(alpha = 1.0f)),
                                        placeholder = org.jetbrains.compose.resources.painterResource(Res.drawable.placeholder1),
                                        error = org.jetbrains.compose.resources.painterResource(Res.drawable.error_image),
                                        contentScale = ContentScale.FillWidth
                                    )
                                    is AnswerOption.TextUrl -> {
                                        var textContent by remember { mutableStateOf("Đang tải...") }
                                        var isError by remember { mutableStateOf(false) }

                                        LaunchedEffect(answer.textUrl) {
                                            try {
                                                textContent =
                                                    fetchTextFromUrl(
                                                        answer.textUrl
                                                    )
                                                isError = false
                                            } catch (e: Exception) {
                                                textContent =
                                                    "Không thể tải nội dung, kiểm tra kết nối mạng hoặc phản hồi nội dung lỗi qua Zaolo: 0972721415"
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
                                            painter = org.jetbrains.compose.resources.painterResource(answer.imageRes),
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
                        HorizontalDivider(
                            color = Color.Gray,
                            thickness = 1.dp,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
            }
            // Nút bắt đầu làm bài trắc nghiệm
            // Hiển thị nút khi cuộn lên (isToolbarVisible == true)
            if (isToolbarVisible.value) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 4.dp)
                ) {
                    Box (
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .padding(horizontal = 0.dp)
                            .align(Alignment.BottomCenter),
                    ) {
                        Button(
                            onClick = {
                                if (lessonName != "Bài thi") {
                                    onStartQuiz ()
                                }
                            },
                            modifier = Modifier
                                .align(Alignment.BottomStart) // Căn chỉnh ở dưới bên trái
                                //.padding(bottom = bottomPadding)
                                .wrapContentSize(),
                            contentPadding = PaddingValues(0.dp), // Loại bỏ padding mặc định
                            shape = RoundedCornerShape(4.dp), // Bo góc nhẹ
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.LightGray.copy(0.3f),
                                contentColor = Color.Black
                            ),
                        ) {
                            Column (horizontalAlignment = Alignment.CenterHorizontally)  {
                                Icon(imageVector = Icons.Outlined.PlayArrow, contentDescription = "Làm bài", modifier = Modifier.size(20.dp))
                                Text(text = "Làm bài", fontSize = 10.sp, fontWeight = FontWeight.Light)
                            }
                        }
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
                        // Nút "Quay lại"
                        Button(
                            onClick = onBack,
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                //.padding(bottom = bottomPadding)
                                .wrapContentSize(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.LightGray.copy(0.3f),
                                contentColor = Color.Black
                            ),
                        ) {
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
// HÀM HIỂN THỊ NỘI DUNG CÂU HỎI
@Composable
fun RenderQuestionContent(content: QuestionContent) {
    val questionBackground = Color(0xFFE8F1F8)
    val questionTextColor = Color(0xFF172B3A)
    val questionErrorColor = Color(0xFF8A4B00)

    when (content) {
        is QuestionContent.AudioUrl -> {
            AudioPlayerFromUrl(
                audioUrl = content.audioUrl,
                modifier = Modifier.fillMaxWidth()
            )
        }
        is QuestionContent.ClipUrl -> {
            VideoPlayerFromUrl(
                videoUrl = content.clipUrl,
                modifier = Modifier.fillMaxWidth()
            )
        }
        is QuestionContent.TextRes -> {
            Text(
                text = org.jetbrains.compose.resources.stringResource(content.textRes),
                color = questionTextColor,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Justify,
                modifier = Modifier
                    .background(questionBackground)
                    .padding(horizontal = 4.dp, vertical = 2.dp)
                    .fillMaxWidth()
            )
        }
        is QuestionContent.Text -> {
            Text(
                text = content.text,
                color = questionTextColor,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Justify,
                modifier = Modifier
                    .background(questionBackground)
                    .padding(horizontal = 4.dp, vertical = 2.dp)
                    .fillMaxWidth()
            )
        }
        is QuestionContent.ImageRes -> {Image(
            painter = org.jetbrains.compose.resources.painterResource(content.imageRes),
            contentDescription = "Hình ảnh câu hỏi",
            modifier = Modifier.fillMaxWidth(),
            contentScale = ContentScale.FillWidth
        )
        }
        is QuestionContent.ImageUrl -> {
            if (content.imageUrl.contains("long") || content.imageUrl.contains("big")) {
                ImageFromUrl(
                    imageUrl = content.imageUrl,
                    contentDescription = "Hình ảnh câu hỏi"
                )
            } else {
                ZoomableImageFromUrl(
                    imageUrl = content.imageUrl,
                    contentDescription = "Hình ảnh câu hỏi"
                )
            }
        }
        is QuestionContent.TextUrl -> {
            var textContent by remember { mutableStateOf("Đang tải...") }
            var isError by remember { mutableStateOf(false) }

            LaunchedEffect(content.textUrl) {
                try {
                    textContent = fetchTextFromUrl(content.textUrl)
                    isError = false
                } catch (e: Exception) {
                    textContent = "Không thể tải nội dung"
                    isError = true
                }
            }

            if (!isError) {
                Text(
                    text = textContent,
                    color = questionTextColor,
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Justify,
                    modifier = Modifier
                        .background(questionBackground)
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                        .fillMaxWidth()
                )
            } else {
                Text(
                    text = "Lỗi khi tải nội dung!",
                    style = MaterialTheme.typography.bodyLarge.copy(color = questionErrorColor),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .background(Color.LightGray)
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                        .fillMaxWidth()
                )
            }
        }
    }
}

// HÀM HIỂN THỊ NỘI DUNG ĐÁP ÁN TRẮC NGHIỆM
@Composable
fun RenderAnswerOptionContent(
    answer: AnswerOption,
    textColor: Color = Color.Black
) {
    when (answer) {
        is AnswerOption.Text -> Text(
            text = answer.text,
            color = textColor,
            textAlign = TextAlign.Justify
        )
        is AnswerOption.TextRes -> Text(
            text = org.jetbrains.compose.resources.stringResource(answer.textRes),
            color = textColor,
            textAlign = TextAlign.Justify
        )
        is AnswerOption.ImageUrl -> AsyncImage(
            model = answer.imageUrl,
            contentDescription = null,
            modifier = Modifier.fillMaxWidth(),
            placeholder = org.jetbrains.compose.resources.painterResource(Res.drawable.placeholder1),
            error = org.jetbrains.compose.resources.painterResource(Res.drawable.error_image),
            contentScale = ContentScale.FillWidth
        )
        is AnswerOption.TextUrl -> {
            var textContent by remember { mutableStateOf("Đang tải...") }
            var isError by remember { mutableStateOf(false) }

            LaunchedEffect(answer.textUrl) {
                try {
                    textContent = fetchTextFromUrl(answer.textUrl)
                    isError = false
                } catch (e: Exception) {
                    textContent = "Không thể tải nội dung"
                    isError = true
                }
            }

            Text(
                text = if (!isError) textContent else "Lỗi khi tải nội dung",
                color = textColor,
                textAlign = TextAlign.Justify
            )
        }
        is AnswerOption.ImageRes -> Box(modifier = Modifier.fillMaxWidth()) {
            Image(
                painter = org.jetbrains.compose.resources.painterResource(answer.imageRes),
                contentDescription = "Hình ảnh đáp án",
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center),
                contentScale = ContentScale.FillWidth
            )
        }
    }
}

// MÀN HÌNH HIỂN THỊ TOÀN BỘ CÂU HỎI TRẮC NGHIỆM VÀ TỰ LUẬN ĐỂ LÀM BÀI TẬP CÙNG LÚC
@Composable
fun AllQuestionsScreen(
    questions: List<Question>,
    quizName: String,
    onSubmitQuiz: (score: Int, history: List<QuestionResult>) -> Unit,
    onBackQuiz: () -> Unit,
    onHomePage: () -> Unit
) {
    val bottomPadding = getNavigationBarHeight()
    val topPadding = getBannerAdHeight()

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val isToolbarVisible = remember { mutableStateOf(true) }
    val lazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Chỉ dùng NestedScrollConnection để điều khiển Toolbar.
    // KHÔNG ẩn bàn phím ở đây nữa.
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(
                available: Offset,
                source: NestedScrollSource
            ): Offset {

                if (available.y < 0) {
                    isToolbarVisible.value = false
                } else if (available.y > 0) {
                    isToolbarVisible.value = true
                }

                return Offset.Zero
            }
        }
    }

    // State lưu đáp án trắc nghiệm được chọn
    val selectedAnswers = remember {
        mutableStateMapOf<Int, Int>()
    }

    // State lưu câu trả lời tự luận
    val userInputTexts = remember {
        mutableStateMapOf<Int, String>()
    }

    // State cảnh báo câu chưa hoàn thành
    var warningMessage by remember {
        mutableStateOf<String?>(null)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                top = topPadding,
                bottom = bottomPadding
            )
            .imePadding()
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(nestedScrollConnection)
                .padding(horizontal = 8.dp)
        ) {

            // =====================================================
            // THANH TIÊU ĐỀ
            // =====================================================

            if (isToolbarVisible.value) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Color.LightGray.copy(alpha = 0.5f)
                        )
                        .padding(
                            vertical = 4.dp,
                            horizontal = 8.dp
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Text(
                        text = buildAnnotatedString {
                            withStyle(
                                style = SpanStyle(
                                    color = Color.Black
                                )
                            ) {
                                append(quizName)
                            }

                            withStyle(
                                style = SpanStyle(
                                    color = Color.Red
                                )
                            ) {
                                append(" (${questions.size} câu)")
                            }
                        },
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                lazyListState.animateScrollToItem(0)
                            }
                        }
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Top",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Light
                            )

                            Icon(
                                imageVector = Icons.Default.KeyboardArrowUp,
                                contentDescription = "Lên đầu trang",
                                tint = Color.Black
                            )
                        }
                    }
                }
            }

            // =====================================================
            // DANH SÁCH CÂU HỎI
            // =====================================================

            LazyColumn(
                state = lazyListState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)

                    // =================================================
                    // PHÁT HIỆN NGAY KHI NGƯỜI DÙNG BẮT ĐẦU VUỐT
                    // =================================================
                    .pointerInput(Unit) {

                        awaitEachGesture {

                            // Chờ ngón tay chạm xuống
                            awaitFirstDown(
                                requireUnconsumed = false
                            )

                            var hasDragged = false

                            do {
                                val event = awaitPointerEvent()

                                // Chỉ coi là vuốt khi di chuyển > 6 px
                                val isDragging = event.changes.any {
                                    abs(it.positionChange().y) > 6f
                                }

                                // Chỉ thực hiện 1 lần trong mỗi lần vuốt
                                if (isDragging && !hasDragged) {

                                    hasDragged = true

                                    // Xóa focus khỏi ô nhập
                                    focusManager.clearFocus()

                                    // Ẩn bàn phím
                                    keyboardController?.hide()
                                }

                            } while (
                                event.changes.any { it.pressed }
                            )
                        }
                    }

            ) {

                // =================================================
                // HIỂN THỊ TỪNG CÂU HỎI
                // =================================================

                itemsIndexed(questions) { index, question ->

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {

                        // -------------------------------------------------
                        // SỐ CÂU
                        // -------------------------------------------------

                        Text(
                            text = "Câu ${index + 1}",
                            color = Color(0xFF0D47A1),
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.padding(
                                bottom = 4.dp
                            )
                        )

                        // -------------------------------------------------
                        // NỘI DUNG CÂU HỎI
                        // -------------------------------------------------

                        RenderQuestionContent(
                            content = question.mainContent
                        )

                        // -------------------------------------------------
                        // CÂU HỎI PHỤ
                        // -------------------------------------------------

                        question.extraContents.forEach { extra ->
                            RenderQuestionContent(
                                content = extra
                            )
                        }

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )

                        // =================================================
                        // 1. CÂU HỎI TRẮC NGHIỆM
                        // =================================================

                        if (question.answers.isNotEmpty()) {

                            question.answers.forEachIndexed {
                                    answerIndex,
                                    answer ->

                                val isSelected =
                                    selectedAnswers[index] == answerIndex

                                val backgroundColor =
                                    if (isSelected) {
                                        Color(0xFFD6E8FF)
                                    } else {
                                        Color(0xFFF2F2F2)
                                    }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = backgroundColor,

                                    border =
                                        if (isSelected) {
                                            BorderStroke(
                                                1.dp,
                                                Color(0xFF1565C0)
                                            )
                                        } else {
                                            null
                                        },

                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 3.dp)
                                        .clickable {

                                            selectedAnswers[index] =
                                                answerIndex

                                            warningMessage = null
                                        }
                                ) {

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(
                                                horizontal = 12.dp,
                                                vertical = 10.dp
                                            ),
                                        verticalAlignment =
                                            Alignment.CenterVertically
                                    ) {

                                        RenderAnswerOptionContent(
                                            answer = answer
                                        )
                                    }
                                }
                            }
                        }

                        // =================================================
                        // 2. CÂU HỎI TỰ LUẬN
                        // =================================================

                        if (
                            question.answers.isEmpty() &&
                            question.correctTextAnswers.isNotEmpty()
                        ) {

                            val userText =
                                userInputTexts[index] ?: ""



                            OutlinedTextField(
                                value = userText,

                                onValueChange = { newValue ->
                                    userInputTexts[index] = newValue
                                    warningMessage = null
                                },

                                placeholder = {
                                    Text(
                                        "Nhập câu trả lời..."
                                    )
                                },

                                keyboardOptions =
                                    KeyboardOptions(
                                        imeAction = ImeAction.Done
                                    ),

                                keyboardActions =
                                    KeyboardActions(
                                        onDone = {
                                            focusManager.clearFocus()
                                            keyboardController?.hide()
                                        }
                                    ),

                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),

                                shape = RoundedCornerShape(8.dp)
                            )
                        }

                        // -------------------------------------------------
                        // ĐƯỜNG KẺ
                        // -------------------------------------------------

                        HorizontalDivider(
                            modifier = Modifier.padding(
                                top = 12.dp
                            ),
                            color = Color.LightGray.copy(
                                alpha = 0.5f
                            )
                        )
                    }
                }

                // =================================================
                // NÚT NỘP BÀI & BẢNG SỐ CÂU HỎI
                // =================================================

                item {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        horizontalAlignment =
                            Alignment.CenterHorizontally
                    ) {

                        warningMessage?.let { msg ->

                            Text(
                                text = msg,
                                color = Color.Red,
                                style =
                                    MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight =
                                            FontWeight.Bold
                                    ),
                                modifier =
                                    Modifier.padding(
                                        bottom = 8.dp
                                    ),
                                textAlign =
                                    TextAlign.Center
                            )
                        }

                        // Bảng số thứ tự các câu hỏi (tối đa 8 ô mỗi hàng) ngay phía trên nút nộp bài
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            questions.indices.chunked(8).forEach { rowIndices ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    rowIndices.forEach { index ->
                                        val question = questions[index]
                                        val isAnswered = if (question.answers.isNotEmpty()) {
                                            selectedAnswers.containsKey(index)
                                        } else {
                                            !userInputTexts[index]?.trim().isNullOrEmpty()
                                        }

                                        val backgroundColor = if (isAnswered) Color(0xFFD6E8FF) else Color(0xFFF2F2F2)
                                        val borderStroke = if (isAnswered) BorderStroke(1.dp, Color(0xFF1565C0)) else null
                                        val textColor = if (isAnswered) Color(0xFF1565C0) else Color.Black

                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = backgroundColor,
                                            border = borderStroke,
                                            modifier = Modifier
                                                .padding(horizontal = 3.dp)
                                                .size(38.dp)
                                                .clickable {
                                                    coroutineScope.launch {
                                                        lazyListState.animateScrollToItem(index)
                                                    }
                                                }
                                        ) {
                                            Box(
                                                contentAlignment = Alignment.Center,
                                                modifier = Modifier.fillMaxSize()
                                            ) {
                                                Text(
                                                    text = "${index + 1}",
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = textColor
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        Button(
                            onClick = {

                                val unfinishedList =
                                    mutableListOf<Int>()

                                questions.forEachIndexed { idx, q ->

                                    if (q.answers.isNotEmpty()) {

                                        if (
                                            !selectedAnswers
                                                .containsKey(idx)
                                        ) {
                                            unfinishedList.add(
                                                idx + 1
                                            )
                                        }

                                    } else if (
                                        q.correctTextAnswers
                                            .isNotEmpty()
                                    ) {

                                        if (
                                            userInputTexts[idx]
                                                ?.trim()
                                                .isNullOrEmpty()
                                        ) {
                                            unfinishedList.add(
                                                idx + 1
                                            )
                                        }
                                    }
                                }

                                if (unfinishedList.isNotEmpty()) {

                                    warningMessage =
                                        "Có câu ${
                                            unfinishedList.joinToString(", ")
                                        } chưa hoàn thành"

                                } else {

                                    warningMessage = null

                                    val historyList =
                                        mutableListOf<QuestionResult>()

                                    var calculatedScore = 0

                                    questions.forEachIndexed { idx, q ->

                                        // ---------------------------------
                                        // TRẮC NGHIỆM
                                        // ---------------------------------

                                        if (q.answers.isNotEmpty()) {

                                            val selIdx =
                                                selectedAnswers[idx]
                                                    ?: -1

                                            val correctIdx =
                                                q.correctAnswer

                                            val isCorrect =
                                                selIdx == correctIdx

                                            if (isCorrect) {
                                                calculatedScore++
                                            }

                                            historyList.add(
                                                QuestionResult(
                                                    mainQuestion = q.mainContent,
                                                    extraQuestions =
                                                        q.extraContents,
                                                    answers = q.answers,
                                                    selectedAnswer =
                                                        q.answers.getOrNull(
                                                            selIdx
                                                        ),
                                                    correctAnswer =
                                                        q.answers.getOrNull(
                                                            correctIdx ?: -1
                                                        ),
                                                    isCorrect =
                                                        isCorrect,
                                                    detailedCorrectAnswers =
                                                        q.detailedCorrectAnswers
                                                )
                                            )

                                        }

                                        // ---------------------------------
                                        // TỰ LUẬN
                                        // ---------------------------------

                                        else if (
                                            q.correctTextAnswers
                                                .isNotEmpty()
                                        ) {

                                            val userStr =
                                                userInputTexts[idx]
                                                    ?: ""

                                            val userTextNormalized =
                                                com.exam.quizapp.ui.screens.normalizeText(
                                                    userStr
                                                )

                                            val staticCorrectTexts =
                                                q.correctTextAnswers
                                                    .mapNotNull { opt ->

                                                        when (opt) {

                                                            is AnswerOption.Text ->
                                                                opt.text

                                                            else ->
                                                                null
                                                        }
                                                    }

                                            val isCorrect =
                                                staticCorrectTexts
                                                    .map {
                                                        com.exam.quizapp.ui.screens.normalizeText(it)
                                                    }
                                                    .any {
                                                        it.isNotBlank() &&
                                                                it ==
                                                                userTextNormalized
                                                    }

                                            if (isCorrect) {
                                                calculatedScore++
                                            }

                                            historyList.add(
                                                QuestionResult(
                                                    mainQuestion = q.mainContent,
                                                    extraQuestions =
                                                        q.extraContents,
                                                    isCorrect =
                                                        isCorrect,
                                                    userInputText =
                                                        userStr,
                                                    correctTextAnswers =
                                                        q.correctTextAnswers,
                                                    detailedCorrectAnswers =
                                                        q.detailedCorrectAnswers
                                                )
                                            )
                                        }
                                    }

                                    onSubmitQuiz(
                                        calculatedScore,
                                        historyList
                                    )
                                }
                            },

                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),

                            shape = RoundedCornerShape(8.dp),

                            colors =
                                ButtonDefaults.buttonColors(
                                    containerColor =
                                        Color(0xFF00668C),
                                    contentColor =
                                        Color.White
                                )
                        ) {

                            Text(
                                text = "Nộp bài",
                                style =
                                    MaterialTheme.typography.titleMedium.copy(
                                        fontWeight =
                                            FontWeight.Bold
                                    )
                            )
                        }
                    }
                }
            }
        }

        // =========================================================
        // THANH NÚT PHÍA DƯỚI
        // =========================================================

        if (isToolbarVisible.value) {

            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .align(Alignment.BottomCenter)
                ) {

                    // -------------------------------------------------
                    // TRANG CHỦ
                    // -------------------------------------------------

                    Button(
                        onClick = {
                            onHomePage()
                        },

                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .wrapContentSize(),

                        contentPadding =
                            PaddingValues(0.dp),

                        shape =
                            RoundedCornerShape(4.dp),

                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor =
                                    Color.LightGray.copy(
                                        alpha = 0.3f
                                    ),
                                contentColor =
                                    Color.Black
                            )
                    ) {

                        Column(
                            horizontalAlignment =
                                Alignment.CenterHorizontally
                        ) {

                            Icon(
                                imageVector =
                                    Icons.Outlined.Home,
                                contentDescription =
                                    "Trang đầu",
                                modifier =
                                    Modifier.size(20.dp)
                            )

                            Text(
                                text = "Trang đầu",
                                fontSize = 10.sp,
                                fontWeight =
                                    FontWeight.Light,
                                color = Color.Black
                            )
                        }
                    }

                    // -------------------------------------------------
                    // QUAY LẠI
                    // -------------------------------------------------

                    Button(
                        onClick = {
                            onBackQuiz()
                        },

                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(end = 4.dp)
                            .wrapContentSize(),

                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor =
                                    Color.LightGray.copy(
                                        alpha = 0.3f
                                    ),
                                contentColor =
                                    Color.Black
                            )
                    ) {

                        Icon(
                            imageVector =
                                Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription =
                                "Quay lại"
                        )
                    }
                }
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
                            text = org.jetbrains.compose.resources.stringResource(content.textRes),
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
                            painter = org.jetbrains.compose.resources.painterResource(content.imageRes),
                            contentDescription = "Hình ảnh câu hỏi", // Thêm mô tả cho hình ảnh câu hỏi
                            modifier = Modifier
                                .fillMaxWidth(),
                            contentScale = ContentScale.FillWidth
                            //.background(Color.Unspecified.copy(alpha = 1.0f))
                        )
                        is QuestionContent.ImageUrl -> {
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

                                } catch (e: kotlinx.coroutines.CancellationException) {

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
                                text = org.jetbrains.compose.resources.stringResource(extraText.textRes),
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
                                painter = org.jetbrains.compose.resources.painterResource(extraText.imageRes),
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
                                is AnswerOption.TextRes -> org.jetbrains.compose.resources.stringResource(it.textRes) // Hỗ trợ TextRes
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
                                    focusManager.clearFocus()

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
                                    is AnswerOption.ImageUrl -> AsyncImage(
                                        model = answer.imageUrl,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .fillMaxWidth(),//.background(Color.Unspecified.copy(alpha = 1.0f)),
                                        placeholder = org.jetbrains.compose.resources.painterResource(Res.drawable.placeholder1),
                                        error = org.jetbrains.compose.resources.painterResource(Res.drawable.error_image),
                                        contentScale = ContentScale.FillWidth
                                    )
                                    is AnswerOption.TextRes -> Text(
                                        text = org.jetbrains.compose.resources.stringResource(answer.textRes),
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
                                            painter = org.jetbrains.compose.resources.painterResource(answer.imageRes),
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
// BẢNG NÚT VUÔNG CHỈ SỐ CÂU HỎI VỚI MÀU TRẠNG THÁI (XANH: ĐÚNG, NÂU XÁM: SAI)
@Composable
fun QuestionGridButtons(
    questionResults: List<QuestionResult>,
    onQuestionClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    if (questionResults.isEmpty()) return

    val correctColor = Color(0xFF1565C0) // Màu xanh (đúng)
    val wrongColor = Color(0xFFA04B00)   // Màu nâu xám (sai)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F4F8)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Text(
                text = "Danh sách câu hỏi (chọn câu để chuyển đến):",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0D47A1)
                ),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            val chunks = questionResults.chunked(5)
            chunks.forEachIndexed { rowIndex, chunk ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    chunk.forEachIndexed { colIndex, result ->
                        val questionIndex = rowIndex * 5 + colIndex
                        val isCorrect = result.isCorrect
                        val bgColor = if (isCorrect) correctColor else wrongColor

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .background(bgColor, shape = RoundedCornerShape(6.dp))
                                .clickable { onQuestionClick(questionIndex) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${questionIndex + 1}",
                                color = Color.White,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }

                    // Nếu hàng cuối cùng không đủ 5 nút, điền Spacer để không bị lệch kích thước
                    val emptySlots = 5 - chunk.size
                    for (i in 0 until emptySlots) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

// MÀN HÌNH HIỂN THỊ CHI TIẾT KẾT QUẢ VÀ LỊCH SỬ LÀM BÀI DUNG CHUNG NHÉ
@Composable
fun ResultScreen(
    score: Int,
    totalQuestions: Int,
    history: List<QuestionResult>,
    quizName: String,
    onBackQuiz: () -> Unit,
    onBackTest: () -> Unit,
    onBackTest2: () -> Unit,
    onSaveResult: () -> Unit,
    onHomePage: () -> Unit,
    onQuizReviewSelected: () -> Unit,
    onQuizReviewSelected2: () -> Unit,

    ) {
    // Lấy khoảng đệm theo chiều cao thành trạng thái và thanh điều khiển
    val bottomPadding = getNavigationBarHeight()
    val topPadding = getBannerAdHeight()
    // Biến trạng thái để kiểm soát hiển thị tiêu đề và nút
    val isToolbarVisible = remember { mutableStateOf(true) }

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Sử dụng nestedScroll để phát hiện khi người dùng cuộn
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (available.y < 0) {
                    // Cuộn xuống, ẩn tiêu đề và nút
                    isToolbarVisible.value = false
                } else if (available.y > 0) {
                    // Cuộn lên, hiện tiêu đề và nút
                    isToolbarVisible.value = true
                }
                return Offset.Zero
            }
        }
    }
    // Ẩn nút khi danh sách lịch sử rỗng hoặc điểm số chưa có
    val testShowButtons = history.isNotEmpty() && score > 0
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = topPadding, bottom = bottomPadding)
            .nestedScroll(nestedScrollConnection), // Theo dõi sự kiện cuộn
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Hiển thị tiêu đề và điểm số nếu isToolbarVisible là true
        if (isToolbarVisible.value) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.LightGray.copy(alpha = 0.5f))
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(color = Color.Black)) {
                            append(quizName) // Phần quizName có màu đen
                        }
                        withStyle(style = SpanStyle(color = Color.Red)) {
                            append(" (đúng: $score / $totalQuestions)") // Phần kết quả có màu đỏ
                        }
                    },
                    style = MaterialTheme
                        .typography
                        .headlineSmall
                        .copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp, end = 4.dp)
                )

                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            listState.animateScrollToItem(0)
                        }
                    },
                    modifier = Modifier.padding(end = 4.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Top", fontSize = 10.sp, fontWeight = FontWeight.Light)
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowUp,
                            contentDescription = "Lên đầu trang",
                            tint = Color.Black
                        )
                    }

                }
            }
        }
        LazyColumn(state = listState) {
            if (history.isNotEmpty()) {
                item {
                    QuestionGridButtons(
                        questionResults = history,
                        onQuestionClick = { index ->
                            coroutineScope.launch {
                                listState.animateScrollToItem(index + 1)
                            }
                        }
                    )
                }
            }
            items(history) { result ->
                Column(
                    modifier = Modifier
                        .padding(
                            start = 8.dp,
                            end = 8.dp,
                            top = 8.dp
                        )
                ) {

                    // ==============================
                    // MÀU SẮC PHẦN CÂU HỎI
                    // ==============================
                    val questionBackground = Color(0xFFE8F1F8)
                    val questionTextColor = Color(0xFF172B3A)
                    val questionTitleColor = Color(0xFF0D47A1)

                    // Hiển thị số câu
                    val currentQuestionIndex = history.indexOf(result) + 1

                    Text(
                        text = "Câu $currentQuestionIndex",
                        color = questionTitleColor,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(
                            bottom = 4.dp
                        )
                    )

                    // ==============================
                    // HIỂN THỊ CÂU HỎI CHÍNH
                    // ==============================
                    when (val question = result.mainQuestion) {

                        is QuestionContent.AudioUrl -> {
                            AudioPlayerFromUrl(
                                audioUrl = question.audioUrl,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        is QuestionContent.ClipUrl -> {
                            VideoPlayerFromUrl(
                                videoUrl = question.clipUrl,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        is QuestionContent.TextRes -> {
                            Text(
                                text = org.jetbrains.compose.resources.stringResource(question.textRes),
                                color = questionTextColor,
                                textAlign = TextAlign.Justify,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(questionBackground)
                                    .padding(
                                        horizontal = 10.dp,
                                        vertical = 10.dp
                                    )
                            )
                        }

                        is QuestionContent.Text -> {
                            Text(
                                text = question.text,
                                color = questionTextColor,
                                textAlign = TextAlign.Justify,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(questionBackground)
                                    .padding(
                                        horizontal = 10.dp,
                                        vertical = 10.dp
                                    )
                            )
                        }

                        is QuestionContent.ImageRes -> {
                            Image(
                                painter = org.jetbrains.compose.resources.painterResource(question.imageRes),
                                contentDescription = null,
                                modifier = Modifier.fillMaxWidth(),
                                contentScale = ContentScale.FillWidth
                            )
                        }

                        is QuestionContent.ImageUrl -> {
                            AsyncImage(
                                model = question.imageUrl,
                                contentDescription = null,
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = org.jetbrains.compose.resources.painterResource(Res.drawable.placeholder1
                                ),
                                error = org.jetbrains.compose.resources.painterResource(Res.drawable.error_image
                                ),
                                contentScale = ContentScale.FillWidth
                            )
                        }

                        is QuestionContent.TextUrl -> {

                            var textContent by remember {
                                mutableStateOf("Đang tải...")
                            }

                            var isError by remember {
                                mutableStateOf(false)
                            }

                            LaunchedEffect(question.textUrl) {
                                try {
                                    textContent =
                                        fetchTextFromUrl(question.textUrl)
                                    isError = false
                                } catch (e: Exception) {
                                    textContent =
                                        "Không thể tải nội dung"
                                    isError = true
                                }
                            }

                            if (!isError) {

                                Text(
                                    text = textContent,
                                    color = questionTextColor,
                                    textAlign = TextAlign.Justify,
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(questionBackground)
                                        .padding(
                                            horizontal = 10.dp,
                                            vertical = 10.dp
                                        )
                                )

                            } else {

                                Text(
                                    text = "Lỗi khi tải nội dung từ URL, kiểm tra kết nối mạng!",
                                    color = Color(0xFF8A4B00),
                                    style = MaterialTheme.typography.bodyLarge,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(
                                            horizontal = 10.dp,
                                            vertical = 10.dp
                                        )
                                )
                            }
                        }
                    }


                    // ==============================
                    // HIỂN THỊ EXTRA QUESTIONS
                    // ==============================
                    result.extraQuestions.forEachIndexed { extraTextIndex, extraText ->

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            when (extraText) {

                                is QuestionContent.AudioUrl -> {
                                    AudioPlayerFromUrl(
                                        audioUrl = extraText.audioUrl,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }

                                is QuestionContent.ClipUrl -> {
                                    VideoPlayerFromUrl(
                                        videoUrl = extraText.clipUrl,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }

                                is QuestionContent.TextRes -> {
                                    Text(
                                        text = org.jetbrains.compose.resources.stringResource(extraText.textRes),
                                        color = questionTextColor,
                                        style = MaterialTheme.typography.bodyLarge,
                                        textAlign = TextAlign.Justify,
                                        modifier = Modifier
                                            .background(questionBackground)
                                            .padding(
                                                horizontal = 10.dp,
                                                vertical = 10.dp
                                            )
                                            .fillMaxWidth()
                                    )
                                }

                                is QuestionContent.Text -> {
                                    Text(
                                        text = extraText.text,
                                        color = questionTextColor,
                                        style = MaterialTheme.typography.bodyLarge,
                                        textAlign = TextAlign.Justify,
                                        modifier = Modifier
                                            .background(questionBackground)
                                            .padding(
                                                horizontal = 10.dp,
                                                vertical = 10.dp
                                            )
                                            .fillMaxWidth()
                                    )
                                }

                                is QuestionContent.ImageRes -> {
                                    Image(
                                        painter = org.jetbrains.compose.resources.painterResource(extraText.imageRes),
                                        contentDescription = "Hình ảnh câu hỏi",
                                        modifier = Modifier.fillMaxWidth(),
                                        contentScale = ContentScale.FillWidth
                                    )
                                }

                                is QuestionContent.ImageUrl -> {

                                    if (
                                        extraText.imageUrl.contains("long") ||
                                        extraText.imageUrl.contains("big")
                                    ) {

                                        ImageFromUrl(
                                            imageUrl = extraText.imageUrl,
                                            contentDescription = "Hình ảnh câu hỏi"
                                        )

                                    } else {

                                        ZoomableImageFromUrl(
                                            imageUrl = extraText.imageUrl,
                                            contentDescription = "Hình ảnh câu hỏi"
                                        )
                                    }
                                }

                                is QuestionContent.TextUrl -> {

                                    var textContent by remember {
                                        mutableStateOf("Đang tải...")
                                    }

                                    var isError by remember {
                                        mutableStateOf(false)
                                    }

                                    LaunchedEffect(extraText.textUrl) {
                                        try {
                                            textContent =
                                                fetchTextFromUrl(extraText.textUrl)
                                            isError = false
                                        } catch (e: Exception) {
                                            textContent =
                                                "Không thể tải nội dung, chưa được kết nối mạng hoặc " +
                                                        "nội dung bị lỗi. Nếu nội dung bị lỗi phản hồi qua Zalo: " +
                                                        "0972712415 để khắc phục"

                                            isError = true
                                        }
                                    }

                                    if (!isError) {

                                        Text(
                                            text = textContent,
                                            color = questionTextColor,
                                            style = MaterialTheme.typography.bodyLarge,
                                            textAlign = TextAlign.Justify,
                                            modifier = Modifier
                                                .background(questionBackground)
                                                .padding(
                                                    horizontal = 10.dp,
                                                    vertical = 10.dp
                                                )
                                                .fillMaxWidth()
                                        )

                                    } else {

                                        Text(
                                            text =
                                                "Lỗi khi tải nội dung, chưa được kết nối mạng " +
                                                        "hoặc nội dung bị lỗi. Nếu nội dung bị lỗi " +
                                                        "phản hồi qua Zalo: 0972712415 để khắc phục",

                                            color = Color(0xFF8A4B00),

                                            style = MaterialTheme.typography.bodyLarge,

                                            textAlign = TextAlign.Center,

                                            modifier = Modifier
                                                .padding(
                                                    horizontal = 10.dp,
                                                    vertical = 10.dp
                                                )
                                                .fillMaxWidth()
                                        )
                                    }
                                }
                            }
                        }
                    }
                    // Hiển thị danh sách đáp án trắc nghiệm
                    if (result.answers.isNotEmpty()) {
                        val selectedAnswer = result.selectedAnswer
                        val correctAnswer = result.correctAnswer

                        result.answers.forEach { answer ->

                            val isCorrectAnswer = answer == correctAnswer
                            val isSelected = answer == selectedAnswer

                            // 4 trạng thái:
                            // 1. Đúng + đã chọn
                            // 2. Sai + đã chọn
                            // 3. Đúng nhưng không chọn
                            // 4. Không đúng + không chọn

                            val backgroundColor = when {
                                // 1. Người dùng chọn đúng
                                isSelected && isCorrectAnswer ->
                                    Color(0xFF1565C0) // Xanh dương đậm

                                // 2. Người dùng chọn sai
                                isSelected && !isCorrectAnswer ->
                                    Color(0xFFA04B00) // Cam/nâu đậm

                                // 3. Đáp án đúng nhưng người dùng không chọn
                                !isSelected && isCorrectAnswer ->
                                    Color(0xFFD6E8FF) // Xanh dương rất nhạt

                                // 4. Không đúng và cũng không chọn
                                else ->
                                    Color(0xFFF2F2F2) // Xám rất nhạt
                            }

                            val textColor = when {
                                // Nền đậm → chữ trắng
                                isSelected && isCorrectAnswer ->
                                    Color.White

                                isSelected && !isCorrectAnswer ->
                                    Color.White

                                // Nền sáng → chữ tối
                                !isSelected && isCorrectAnswer ->
                                    Color(0xFF0D47A1)

                                else ->
                                    Color(0xFF333333)
                            }

                            val statusIcon = when {
                                // Đúng + đã chọn
                                isSelected && isCorrectAnswer -> "✓"

                                // Sai + đã chọn
                                isSelected && !isCorrectAnswer -> "✕"

                                // Đúng nhưng không chọn
                                !isSelected && isCorrectAnswer -> "✓"

                                // Không đúng + không chọn
                                else -> "—"
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp)
                                    .background(backgroundColor)
                                    .padding(horizontal = 8.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                // Biểu tượng trạng thái
                                Text(
                                    text = statusIcon,
                                    color = textColor,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(end = 8.dp)
                                )

                                when (answer) {

                                    is AnswerOption.Text -> Text(
                                        text = answer.text,
                                        color = textColor,
                                        textAlign = TextAlign.Justify
                                    )

                                    is AnswerOption.TextRes -> Text(
                                        text = org.jetbrains.compose.resources.stringResource(answer.textRes),
                                        color = textColor,
                                        textAlign = TextAlign.Justify
                                    )

                                    is AnswerOption.ImageUrl -> AsyncImage(
                                        model = answer.imageUrl,
                                        contentDescription = null,
                                        modifier = Modifier.fillMaxWidth(),
                                        placeholder = org.jetbrains.compose.resources.painterResource(Res.drawable.placeholder1
                                        ),
                                        error = org.jetbrains.compose.resources.painterResource(Res.drawable.error_image
                                        ),
                                        contentScale = ContentScale.FillWidth
                                    )

                                    is AnswerOption.TextUrl -> {
                                        var textContent by remember {
                                            mutableStateOf("Đang tải...")
                                        }

                                        var isError by remember {
                                            mutableStateOf(false)
                                        }

                                        LaunchedEffect(answer.textUrl) {
                                            try {
                                                textContent =
                                                    fetchTextFromUrl(answer.textUrl)
                                                isError = false
                                            } catch (e: Exception) {
                                                textContent =
                                                    "Không thể tải nội dung, kiểm tra kết nối mạng hoặc phản hồi nội dung lỗi qua Zalo: 0972721415"
                                                isError = true
                                            }
                                        }

                                        Text(
                                            text = textContent,
                                            color = textColor,
                                            textAlign = TextAlign.Justify
                                        )
                                    }

                                    is AnswerOption.ImageRes -> Box(
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Image(
                                            painter = org.jetbrains.compose.resources.painterResource(answer.imageRes),
                                            contentDescription = "Hình ảnh đáp án",
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .align(Alignment.Center),
                                            contentScale = ContentScale.FillWidth
                                        )
                                    }
                                }
                            }
                        }
                    } else if (result.correctTextAnswers.isNotEmpty()) {

                        // hiện đáp án và tính đúng sai của câu tự luận
                        val userInput = result.userInputText
                        val isCorrect = result.isCorrect
                        if (isCorrect) {
                            // ==========================================
                            // TRƯỜNG HỢP ĐÚNG: Chỉ hiện đáp án của người dùng
                            // ==========================================
                            Surface(
                                color = Color(0xFF1565C0), // Xanh dương đậm
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 2.dp)
                                        //.background(backgroundColor)
                                        .padding(horizontal = 8.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {

                                    // Biểu tượng trạng thái
                                    Text(
                                        text = "✓",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(end = 8.dp)
                                    )
                                    Text(
                                        text = "Đáp án của bạn: $userInput",
                                        style = MaterialTheme.typography.bodyLarge.copy(
                                            color = Color.White
                                        ),
                                        modifier = Modifier.padding(12.dp)
                                    )
                                }
                            }
                        } else {
                            val randomCorrect = remember(result) { // nếu ưu tiên lấy đáp án đầu tiên thì đổi thành firstCorrect
                                result.correctTextAnswers.randomOrNull() // nếu ưu tiên lấy đáp án đầu tiên thì lấy firstOrNull()
                            }

                            if (randomCorrect != null) {
                                val backgroundColor = Color(0xFFA04B00) // Nâu xám

                                Surface(
                                    color = backgroundColor,
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 2.dp)
                                            .background(backgroundColor)
                                            .padding(horizontal = 8.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {

                                        // Biểu tượng trạng thái
                                        Text(
                                            text = "✕",
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(end = 8.dp)
                                        )
                                        // Xử lý lấy nội dung chữ tùy theo kiểu dữ liệu của AnswerOption
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
                                                        org.jetbrains.compose.resources.stringResource(randomCorrect.textRes)
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
                                                        textContent =
                                                            fetchTextFromUrl(randomCorrect.textUrl)
                                                        isError = false
                                                    } catch (e: Exception) {
                                                        textContent =
                                                            "Không thể tải nội dung, kiểm tra kết nối mạng!"
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
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(vertical = 2.dp)
                                                        .background(backgroundColor)
                                                        .padding(horizontal = 8.dp, vertical = 8.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {

                                                    // Biểu tượng trạng thái
                                                    Text(
                                                        text = "✕",
                                                        color = Color.White,
                                                        fontWeight = FontWeight.Bold,
                                                        modifier = Modifier.padding(end = 8.dp)
                                                    )
                                                    Text(
                                                        text = "Dạng đáp án tự luận không hỗ trợ kiểu này.",
                                                        style = MaterialTheme.typography.bodyMedium.copy(
                                                            color = Color.Red
                                                        ),
                                                        modifier = Modifier.padding(12.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    var showDetail by remember { mutableStateOf(false) }

                    if (result.detailedCorrectAnswers.isNotEmpty()) {

                        // Màu sắc phần giải thích
                        val explanationBackground = Color(0xFFFFF7E6)
                        val explanationTextColor = Color(0xFF3A3025)
                        val explanationTitleColor = Color(0xFF8A4B00)

                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {

                            // ==============================
                            // NÚT HIỂN THỊ / ẨN ĐÁP ÁN CHI TIẾT
                            // ==============================
                            TextButton(
                                onClick = {
                                    showDetail = !showDetail
                                }
                            ) {
                                Text(
                                    text = if (showDetail)
                                        "Ẩn đáp án chi tiết"
                                    else
                                        "Đáp án chi tiết",

                                    color = explanationTitleColor,

                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }

                            // ==============================
                            // HIỂN THỊ ĐÁP ÁN CHI TIẾT
                            // ==============================
                            if (showDetail) {

                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(explanationBackground)
                                        .padding(
                                            horizontal = 10.dp,
                                            vertical = 10.dp
                                        )
                                ) {

                                    // Tiêu đề phần giải thích
                                    Text(
                                        text = "Giải thích",
                                        color = explanationTitleColor,
                                        style = MaterialTheme.typography.bodyLarge.copy(
                                            fontWeight = FontWeight.Bold
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 6.dp)
                                    )

                                    result.detailedCorrectAnswers.forEachIndexed { index,
                                                                                   detailedCorrectAnswer ->

                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp),
                                            verticalAlignment = Alignment.Top
                                        ) {

                                            when (detailedCorrectAnswer) {

                                                // ==============================
                                                // TEXT
                                                // ==============================
                                                is AnswerOption.Text -> {
                                                    Text(
                                                        text = detailedCorrectAnswer.text,
                                                        color = explanationTextColor,
                                                        textAlign = TextAlign.Justify,
                                                        style = MaterialTheme.typography.bodyLarge,
                                                        modifier = Modifier.fillMaxWidth()
                                                    )
                                                }

                                                // ==============================
                                                // TEXT RESOURCE
                                                // ==============================
                                                is AnswerOption.TextRes -> {
                                                    Text(
                                                        text = org.jetbrains.compose.resources.stringResource(detailedCorrectAnswer.textRes),
                                                        color = explanationTextColor,
                                                        textAlign = TextAlign.Justify,
                                                        style = MaterialTheme.typography.bodyLarge,
                                                        modifier = Modifier.fillMaxWidth()
                                                    )
                                                }

                                                // ==============================
                                                // IMAGE RESOURCE
                                                // ==============================
                                                is AnswerOption.ImageRes -> {
                                                    Image(
                                                        painter = org.jetbrains.compose.resources.painterResource(detailedCorrectAnswer.imageRes),
                                                        contentDescription = "Hình ảnh đáp án",
                                                        modifier = Modifier.fillMaxWidth(),
                                                        contentScale = ContentScale.FillWidth
                                                    )
                                                }

                                                // ==============================
                                                // IMAGE URL
                                                // ==============================
                                                is AnswerOption.ImageUrl -> {
                                                    AsyncImage(
                                                        model = detailedCorrectAnswer.imageUrl,
                                                        contentDescription = null,
                                                        modifier = Modifier.fillMaxWidth(),
                                                        placeholder = org.jetbrains.compose.resources.painterResource(Res.drawable.placeholder1
                                                        ),
                                                        error = org.jetbrains.compose.resources.painterResource(Res.drawable.error_image
                                                        ),
                                                        contentScale = ContentScale.FillWidth
                                                    )
                                                }

                                                // ==============================
                                                // TEXT URL
                                                // ==============================
                                                is AnswerOption.TextUrl -> {

                                                    var textContent by remember(index) {
                                                        mutableStateOf("Đang tải...")
                                                    }

                                                    var isError by remember(index) {
                                                        mutableStateOf(false)
                                                    }

                                                    LaunchedEffect(
                                                        detailedCorrectAnswer.textUrl
                                                    ) {
                                                        try {
                                                            textContent =
                                                                fetchTextFromUrl(
                                                                    detailedCorrectAnswer.textUrl
                                                                )

                                                            isError = false

                                                        } catch (e: Exception) {

                                                            textContent =
                                                                "Không thể tải nội dung, kiểm tra kết nối mạng hoặc " +
                                                                        "phản hồi nội dung lỗi qua Zalo: 0972712415"

                                                            isError = true
                                                        }
                                                    }

                                                    if (!isError) {

                                                        Text(
                                                            text = textContent,
                                                            color = explanationTextColor,
                                                            textAlign = TextAlign.Justify,
                                                            style = MaterialTheme.typography.bodyLarge,
                                                            modifier = Modifier.fillMaxWidth()
                                                        )

                                                    } else {

                                                        Text(
                                                            text = "Lỗi khi tải nội dung, kiểm tra kết nối mạng!",
                                                            color = explanationTitleColor,
                                                            textAlign = TextAlign.Center,
                                                            style = MaterialTheme.typography.bodyLarge,
                                                            modifier = Modifier.fillMaxWidth()
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
        Spacer(modifier = Modifier.height(48.dp))
    }

    // Hiển thị nút khi cuộn lên (isToolbarVisible == true)
    if (isToolbarVisible.value) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 0.dp)
                    .align(Alignment.BottomCenter)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = bottomPadding),
                    horizontalArrangement = Arrangement.SpaceBetween, // Căn đều các nút
                    verticalAlignment = Alignment.Bottom
                ) { // Nút "Thi lại" (bên trái)
                    Button(
                        onClick = { if (testShowButtons) {
                            onBackTest()
                        } else {onBackTest2()}
                        },
                        modifier = Modifier.weight(1f), // Cân bằng khoảng cách
                        contentPadding = PaddingValues(0.dp),
                        shape = RoundedCornerShape(4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.LightGray.copy(0.3f),
                            contentColor = Color.Black
                        ),
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(imageVector = Icons.Outlined.Edit, contentDescription = "Thi lại", modifier = Modifier.size(20.dp))
                            Text(text = "Thi lại", fontSize = 10.sp, fontWeight = FontWeight.Light)
                        }
                    }
                    // Nút "Lưu lại" (ở giữa "Thi lại" và "Trang đầu")
                    Button(
                        onClick = { if (testShowButtons) {
                            onSaveResult()
                        }
                        },
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(0.dp),
                        shape = RoundedCornerShape(4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.LightGray.copy(0.3f),
                            contentColor = Color.Black
                        ),
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(imageVector = Icons.Outlined.Save, contentDescription = "Lưu lại", modifier = Modifier.size(20.dp))
                            Text(text = "Lưu lại", fontSize = 10.sp, fontWeight = FontWeight.Light)
                        }
                    }

                    // Nút "Trang đầu" (chính giữa)
                    Button(
                        onClick = { onHomePage() },
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(0.dp),
                        shape = RoundedCornerShape(4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.LightGray.copy(0.3f),
                            contentColor = Color.Black
                        ),
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(imageVector = Icons.Outlined.Home, contentDescription = "Trang đầu", modifier = Modifier.size(20.dp))
                            Text(text = "Trang đầu", fontSize = 10.sp, fontWeight = FontWeight.Light, color = Color.Black)
                        }
                    }
                    Button(
                        onClick = { if (testShowButtons) {
                            onQuizReviewSelected()
                        } else {onQuizReviewSelected2()}
                        },
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(0.dp),
                        shape = RoundedCornerShape(4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.LightGray.copy(0.3f),
                            contentColor = Color.Black
                        ),
                    ) {
                        Column (horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Outlined.Visibility,
                                contentDescription = "Xem review",
                                modifier = Modifier.size(20.dp)
                            )
                            Text(text = "Xem review", fontSize = 10.sp, fontWeight = FontWeight.Light, color = Color.Black)

                        }
                    }
                    // Nút "Quay lại" (bên phải)
                    Button(
                        onClick = { onBackQuiz() },
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(0.dp),
                        shape = RoundedCornerShape(4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.LightGray.copy(0.3f),
                            contentColor = Color.Black
                        ),
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
                    }
                }
            }
        }
    }
}
// MÀN HÌNH DANH SÁCH KẾT QUẢ LÀM BÀI ĐÃ ĐƯỢC LƯU LẠI THEO DẠNG MỤC LỤC
@Composable
fun ResultListScreen(onResultClick: (Int) -> Unit,
                     onBack: () -> Unit,
                     onHomePage: () -> Unit
) {
    var iconWidth by remember { mutableIntStateOf(0) } // Kiểm tra thử có thế thay đổi Biến lưu độ rộng của icon
    // Lấy khoảng đệm theo chiều cao thành trạng thái và thanh điều khiển
    val bottomPadding = getNavigationBarHeight()
    val topPadding = getBannerAdHeight()
    val resultViewModel: ResultViewModel = viewModel()
    var results by remember { mutableStateOf(resultViewModel.getAllResults()) }
    // Biến trạng thái để kiểm soát hiển thị tiêu đề và nút
    val isToolbarVisible = remember { mutableStateOf(true) }
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

    // Cập nhật danh sách kết quả khi có thay đổi
    LaunchedEffect(Unit) {
        results = resultViewModel.getAllResults()
    }
    Box (
        modifier = Modifier
            .fillMaxSize()
            .padding(top = topPadding, bottom = bottomPadding, start = 8.dp, end = 8.dp)
            .nestedScroll(nestedScrollConnection)
    )
    {
        Column {
            LazyColumn {
                items(results) { result ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 0.dp, top = 4.dp, bottom = 4.dp, end = 0.dp)
                            .clickable {
                                onResultClick(result.id)
                            }
                    ) {
                        Column(modifier = Modifier.padding(start = 8.dp, top = 2.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween // Chia đều giữa văn bản và icon
                            ) {
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(end = with(LocalDensity.current) { (iconWidth * 0.0625f).toDp() }) // Thêm đệm bằng % độ rộng của icon
                                    //.padding(end = with(LocalDensity.current) { iconWidth.toDp() }) // Thêm đệm dựa trên độ rộng của icon
                                ) {
                                    Text(
                                        text = result.quizName,
                                        textAlign = TextAlign.Justify,  // Căn đều văn bản
                                        style = MaterialTheme.typography.bodyLarge,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis // Thêm dấu '...' nếu văn bản quá dài
                                    )

                                    Text(
                                        text = "Đúng: ${result.score} / ${result.totalQuestions} - ${result.timestamp} - ${result.id}"
                                    )
                                }

                                // Thêm nút xóa
                                IconButton(
                                    onClick = {
                                        resultViewModel.deleteResultById(result.id)
                                        // Cập nhật lại danh sách kết quả sau khi xóa
                                        results = resultViewModel.getAllResults()
                                    },
                                    modifier = Modifier
                                        .onGloballyPositioned { coordinates ->
                                            iconWidth = coordinates.size.width // Lưu kích thước icon
                                        }
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Delete,
                                        contentDescription = "Xóa kết quả",
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        if (isToolbarVisible.value) {
            Box (
                modifier = Modifier
                    //.padding(bottom = bottomPadding)
                    .fillMaxSize()
            ) {
                Box (
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(horizontal = 0.dp)
                        .align(Alignment.BottomCenter),
                ) {
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
                    Button(
                        onClick = {
                            onBack()
                        },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .wrapContentSize(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.LightGray.copy(0.3f), // Màu nền của nút
                            contentColor = Color.Black // Màu của icon và chữ
                        ),

                        ) {
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
// MÀN HÌNH CHI TIẾT BÀI LÀM ĐÃ LƯU THEO TỪNG BÀI LÀM
@Composable
fun ResultDetailScreen(
    resultId: Int,
    viewModel: ResultViewModel = viewModel(),
    onHomePage: () -> Unit,
    onBack: () -> Unit,
    onBackTest: (String) -> Unit
) {
    // Lấy khoảng đệm theo chiều cao thành trạng thái và thanh điều khiển
    val bottomPadding = getNavigationBarHeight()
    val topPadding = getBannerAdHeight()
    // Lấy dữ liệu kết quả từ ViewModel
    val result = viewModel.getResultById(resultId)
    val questionResults =
        result?.let { viewModel.getQuestionResultsForResult(it.id) } ?: emptyList()
    // Biến trạng thái để kiểm soát hiển thị tiêu đề và nút
    val isToolbarVisible = remember { mutableStateOf(true) }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                top = topPadding,
                bottom = bottomPadding)
            .nestedScroll(nestedScrollConnection), // Theo dõi sự kiện cuộn
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Hiển thị tiêu đề và điểm số nếu isToolbarVisible là true
        if (isToolbarVisible.value) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.LightGray.copy(alpha = 0.5f))
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(color = Color.Black)) {
                            append("${result?.quizName}") // Phần quizName có màu đen
                        }
                        withStyle(style = SpanStyle(color = Color.Blue)) {
                            append(" (đúng: ${result?.score} / ${result?.totalQuestions})") // Phần kết quả có màu xanh/đỏ
                        }
                    },
                    style = MaterialTheme
                        .typography
                        .headlineSmall
                        .copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp, end = 4.dp)
                )

                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            listState.animateScrollToItem(0)
                        }
                    },
                    modifier = Modifier.padding(end = 4.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Top", fontSize = 10.sp, fontWeight = FontWeight.Light)
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowUp,
                            contentDescription = "Lên đầu trang",
                            tint = Color.Black
                        )
                    }
                }
            }
        }

        // Hiển thị lịch sử câu hỏi
        LazyColumn(state = listState) {
            if (questionResults.isNotEmpty()) {
                item {
                    QuestionGridButtons(
                        questionResults = questionResults,
                        onQuestionClick = { index ->
                            coroutineScope.launch {
                                listState.animateScrollToItem(index + 1)
                            }
                        }
                    )
                }
            }
            items(questionResults) { result ->
                Column(modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 8.dp)) {
                    val currentQuestionIndex = questionResults.indexOf(result)
                    // Hiển thị câu hỏi
                    Text(
                        text = "Câu ${currentQuestionIndex + 1}",
                        color = Color(0xFF0D47A1),
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    val questionBackground = Color(0xFFE8F1F8) // Xanh rất nhạt
                    val questionTextColor = Color(0xFF172B3A)   // Xanh đen
                    val questionErrorColor = Color(0xFF8A4B00)  // Cam nâu đậm

                    when (val question = result.mainQuestion) {
                        is QuestionContent.AudioUrl -> {}
                        is QuestionContent.Text -> {

                            when {
                                // Văn bản thường
                                question.text.startsWith("TEXTXT") -> {

                                    val text = question.text.removePrefix("TEXTXT")

                                    Text(
                                        text = text,
                                        color = questionTextColor,
                                        textAlign = TextAlign.Justify,
                                        modifier = Modifier
                                            .background(questionBackground),
                                        style = MaterialTheme.typography.bodyLarge.copy(
                                            fontWeight = if (result.isCorrect)
                                                FontWeight.Normal
                                            else
                                                FontWeight.Bold
                                        )
                                    )
                                }
                                // Text Resource
                                question.text.startsWith("RESTEX") -> {
                                    val textId = question.text.removePrefix("RESTEX")
                                    val stringRes = Res.allStringResources[textId]
                                    if (stringRes != null) {
                                        Text(
                                            text = org.jetbrains.compose.resources.stringResource(stringRes),
                                            color = questionTextColor,
                                            modifier = Modifier
                                                .background(questionBackground),
                                            style = MaterialTheme.typography.bodyLarge.copy(
                                                fontWeight = if (result.isCorrect)
                                                    FontWeight.Normal
                                                else
                                                    FontWeight.Bold
                                            )
                                        )
                                    }
                                }

                                // ==============================
                                // Image Resource
                                // ==============================
                                question.text.startsWith("RESIMA") -> {
                                    val imageKey = question.text.removePrefix("RESIMA")
                                    val drawableRes = Res.allDrawableResources[imageKey]
                                    if (drawableRes != null) {
                                        Image(
                                            painter = org.jetbrains.compose.resources.painterResource(drawableRes),
                                            contentDescription = null,
                                            modifier = Modifier.fillMaxWidth(),
                                            contentScale = ContentScale.FillWidth
                                        )
                                    }
                                }

                                // ==============================
                                // Text URL
                                // ==============================
                                question.text.startsWith("URLTEX") -> {

                                    val textUrl = question.text
                                        .removePrefix("URLTEX")

                                    var textContent by remember {
                                        mutableStateOf("Đang tải...")
                                    }

                                    var isError by remember {
                                        mutableStateOf(false)
                                    }

                                    LaunchedEffect(textUrl) {

                                        try {
                                            textContent = fetchTextFromUrl(textUrl)
                                            isError = false

                                        } catch (e: Exception) {

                                            textContent = "Không thể tải nội dung"
                                            isError = true
                                        }
                                    }

                                    if (!isError) {

                                        Text(
                                            text = textContent,
                                            color = questionTextColor,
                                            textAlign = TextAlign.Justify,
                                            modifier = Modifier
                                                .background(questionBackground),
                                            style = MaterialTheme.typography.bodyLarge.copy(
                                                fontWeight = if (result.isCorrect)
                                                    FontWeight.Normal
                                                else
                                                    FontWeight.Bold
                                            )
                                        )

                                    } else {

                                        Text(
                                            text = "Lỗi khi tải nội dung từ URL, kiểm tra kết nối mạng!",
                                            color = questionErrorColor,
                                            style = MaterialTheme.typography.bodyLarge.copy(
                                                fontWeight = if (result.isCorrect)
                                                    FontWeight.Normal
                                                else
                                                    FontWeight.Bold
                                            ),
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }

                                // ==============================
                                // Image URL
                                // ==============================
                                question.text.startsWith("URLIMA") -> {

                                    val imageUrl = question.text
                                        .removePrefix("URLIMA")

                                    AsyncImage(
                                        model = imageUrl,
                                        contentDescription = "Hình ảnh từ URL",
                                        modifier = Modifier.fillMaxWidth(),
                                        contentScale = ContentScale.FillWidth,
                                        placeholder = org.jetbrains.compose.resources.painterResource(Res.drawable.placeholder1
                                        ),
                                        error = org.jetbrains.compose.resources.painterResource(Res.drawable.error_image
                                        )
                                    )
                                }

                                // ==============================
                                // Clip / Video URL
                                // ==============================
                                question.text.startsWith("URLCLI") -> {

                                    val clipUrl = question.text
                                        .removePrefix("URLCLI")

                                    VideoPlayerFromUrl(
                                        videoUrl = clipUrl,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                                // ==============================
                                // AUDIO / AUDIO URL
                                // ==============================
                                question.text.startsWith("URLAUD") -> {

                                    val audioUrl = question.text
                                        .removePrefix("URLAUD")

                                    AudioPlayerFromUrl(
                                        audioUrl = audioUrl, // hoặc it.audioUrl tùy tên biến trong khối when của bạn
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }

                                // ==============================
                                // Dữ liệu không xác định
                                // ==============================
                                else -> {

                                    Text(
                                        text = question.text,
                                        textAlign = TextAlign.Justify,
                                        modifier = Modifier
                                            .background(Color.LightGray),
                                        style = MaterialTheme.typography.bodyLarge.copy(
                                            fontWeight = if (result.isCorrect)
                                                FontWeight.Normal
                                            else
                                                FontWeight.Bold
                                        )
                                    )
                                }
                            }
                        }
                        else -> {}
                    }
                    // Hiển thi phần câu hỏi thêm extraQuestions
                    result.extraQuestions.forEachIndexed { extraTextIndex, extraText ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            /*Text(
                                text = "${'A' + answerIndex}: ",
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(end = 4.dp)
                            )*/
                            when (extraText) {

                                is QuestionContent.Text -> {

                                    when {

                                        // ==============================
                                        // 1. TEXT THƯỜNG
                                        // ==============================
                                        extraText.text.startsWith("TEXTXT") -> {

                                            val text = extraText.text.removePrefix("TEXTXT")

                                            Text(
                                                text = text,
                                                color = questionTextColor,
                                                textAlign = TextAlign.Justify,
                                                modifier = Modifier
                                                    .background(questionBackground),
                                                style = MaterialTheme.typography.bodyLarge.copy(
                                                    fontWeight = if (result.isCorrect)
                                                        FontWeight.Normal
                                                    else
                                                        FontWeight.Bold
                                                )
                                            )
                                        }

                                        // ==============================
                                        // 2. TEXT RESOURCE
                                        // ==============================
                                        extraText.text.startsWith("RESTEX") -> {
                                            val textId = extraText.text.removePrefix("RESTEX")
                                            val stringRes = Res.allStringResources[textId]
                                            if (stringRes != null) {
                                                Text(
                                                    text = org.jetbrains.compose.resources.stringResource(stringRes),
                                                    color = questionTextColor,
                                                    modifier = Modifier
                                                        .background(questionBackground),
                                                    style = MaterialTheme.typography.bodyLarge.copy(
                                                        fontWeight = if (result.isCorrect)
                                                            FontWeight.Normal
                                                        else
                                                            FontWeight.Bold
                                                    )
                                                )
                                            }
                                        }

                                        // ==============================
                                        // 3. IMAGE RESOURCE
                                        // ==============================
                                        extraText.text.startsWith("RESIMA") -> {
                                            val imageKey = extraText.text.removePrefix("RESIMA")
                                            val drawableRes = Res.allDrawableResources[imageKey]
                                            if (drawableRes != null) {
                                                Image(
                                                    painter = org.jetbrains.compose.resources.painterResource(drawableRes),
                                                    contentDescription = null,
                                                    modifier = Modifier.fillMaxWidth(),
                                                    contentScale = ContentScale.FillWidth
                                                )
                                            }
                                        }

                                        // ==============================
                                        // 4. TEXT URL
                                        // ==============================
                                        extraText.text.startsWith("URLTEX") -> {

                                            val textUrl = extraText.text
                                                .removePrefix("URLTEX")

                                            var textContent by remember {
                                                mutableStateOf("Đang tải...")
                                            }

                                            var isError by remember {
                                                mutableStateOf(false)
                                            }

                                            LaunchedEffect(textUrl) {

                                                try {
                                                    textContent = fetchTextFromUrl(textUrl)
                                                    isError = false

                                                } catch (e: Exception) {

                                                    textContent = "Không thể tải nội dung"
                                                    isError = true
                                                }
                                            }

                                            if (!isError) {

                                                Text(
                                                    text = textContent,
                                                    color = questionTextColor,
                                                    textAlign = TextAlign.Justify,
                                                    modifier = Modifier
                                                        .background(questionBackground),
                                                    style = MaterialTheme.typography.bodyLarge.copy(
                                                        fontWeight = if (result.isCorrect)
                                                            FontWeight.Normal
                                                        else
                                                            FontWeight.Bold
                                                    )
                                                )

                                            } else {

                                                Text(
                                                    text = "Lỗi khi tải nội dung từ URL, kiểm tra kết nối mạng!",
                                                    color = questionErrorColor,
                                                    style = MaterialTheme.typography.bodyLarge.copy(
                                                        fontWeight = if (result.isCorrect)
                                                            FontWeight.Normal
                                                        else
                                                            FontWeight.Bold
                                                    ),
                                                    textAlign = TextAlign.Center
                                                )
                                            }
                                        }

                                        // ==============================
                                        // 5. IMAGE URL
                                        // ==============================
                                        extraText.text.startsWith("URLIMA") -> {

                                            val imageUrl = extraText.text
                                                .removePrefix("URLIMA")

                                            AsyncImage(
                                                model = imageUrl,
                                                contentDescription = "Hình ảnh từ URL",
                                                modifier = Modifier.fillMaxWidth(),
                                                contentScale = ContentScale.FillWidth,
                                                placeholder = org.jetbrains.compose.resources.painterResource(Res.drawable.placeholder1
                                                ),
                                                error = org.jetbrains.compose.resources.painterResource(Res.drawable.error_image
                                                )
                                            )
                                        }

                                        // ==============================
                                        // 6. VIDEO / CLIP URL
                                        // ==============================
                                        extraText.text.startsWith("URLCLI") -> {

                                            val clipUrl = extraText.text
                                                .removePrefix("URLCLI")

                                            VideoPlayerFromUrl(
                                                videoUrl = clipUrl,
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                        }
                                        // ==============================
                                        // 7. AUDIO / AUDIO URL
                                        // ==============================
                                        extraText.text.startsWith("URLAUD") -> {

                                            val audioUrl = extraText.text
                                                .removePrefix("URLAUD")

                                            AudioPlayerFromUrl(
                                                audioUrl = audioUrl, // hoặc it.audioUrl tùy tên biến trong khối when của bạn
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                        }

                                        // ==============================
                                        // 7. DỮ LIỆU KHÔNG CÓ TIỀN TỐ
                                        // ==============================
                                        else -> {

                                            Text(
                                                text = extraText.text,
                                                textAlign = TextAlign.Justify,
                                                modifier = Modifier
                                                    .background(Color.LightGray),
                                                style = MaterialTheme.typography.bodyLarge.copy(
                                                    fontWeight = if (result.isCorrect)
                                                        FontWeight.Normal
                                                    else
                                                        FontWeight.Bold
                                                )
                                            )
                                        }
                                    }
                                }
                                else -> {}
                            }
                        }
                    }

                    // Hiển thị danh sách đáp án trắc nghiệm
                    if (result.answers.isNotEmpty()) {
                        val selectedAnswer = result.selectedAnswer
                        val correctAnswer = result.correctAnswer

                        result.answers.forEach { answer ->

                            val isCorrectAnswer = answer == correctAnswer
                            val isSelected = answer == selectedAnswer

                            // 4 trạng thái:
                            // 1. Đúng + đã chọn
                            // 2. Sai + đã chọn
                            // 3. Đúng nhưng không chọn
                            // 4. Không đúng + không chọn

                            val backgroundColor = when {
                                // 1. Người dùng chọn đúng
                                isSelected && isCorrectAnswer ->
                                    Color(0xFF1565C0) // Xanh dương đậm

                                // 2. Người dùng chọn sai
                                isSelected && !isCorrectAnswer ->
                                    Color(0xFFA04B00) // Cam/nâu đậm

                                // 3. Đáp án đúng nhưng người dùng không chọn
                                !isSelected && isCorrectAnswer ->
                                    Color(0xFFD6E8FF) // Xanh dương rất nhạt

                                // 4. Không đúng và cũng không chọn
                                else ->
                                    Color(0xFFF2F2F2) // Xám rất nhạt
                            }

                            val textColor = when {
                                // Nền đậm → chữ trắng
                                isSelected && isCorrectAnswer ->
                                    Color.White

                                isSelected && !isCorrectAnswer ->
                                    Color.White

                                // Nền sáng → chữ tối
                                !isSelected && isCorrectAnswer ->
                                    Color(0xFF0D47A1)

                                else ->
                                    Color(0xFF333333)
                            }

                            val statusIcon = when {
                                // Đúng + đã chọn
                                isSelected && isCorrectAnswer -> "✓"

                                // Sai + đã chọn
                                isSelected && !isCorrectAnswer -> "✕"

                                // Đúng nhưng không chọn
                                !isSelected && isCorrectAnswer -> "✓"

                                // Không đúng + không chọn
                                else -> "—"
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp)
                                    .background(backgroundColor)
                                    .padding(horizontal = 8.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                // Biểu tượng trạng thái
                                Text(
                                    text = statusIcon,
                                    color = textColor,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(end = 8.dp)
                                )

                                when (answer) {

                                    is AnswerOption.Text -> {
                                        when {

                                            // ==============================
                                            // 1. TEXT THƯỜNG
                                            // ==============================
                                            answer.text.startsWith("TEXTXT") -> {

                                                val text = answer.text.removePrefix("TEXTXT")

                                                Text(
                                                    text = text,
                                                    color = textColor,
                                                    textAlign = TextAlign.Justify,
                                                    modifier = Modifier
                                                        .background(backgroundColor),
                                                    style = MaterialTheme.typography.bodyLarge.copy(
                                                        fontWeight = if (result.isCorrect)
                                                            FontWeight.Normal
                                                        else
                                                            FontWeight.Bold
                                                    )
                                                )
                                            }

                                            // ==============================
                                            // 2. TEXT RESOURCE
                                            // ==============================
                                            answer.text.startsWith("RESTEX") -> {
                                                val textId = answer.text.removePrefix("RESTEX")
                                                val stringRes = Res.allStringResources[textId]
                                                if (stringRes != null) {
                                                    Text(
                                                        text = org.jetbrains.compose.resources.stringResource(stringRes),
                                                        color = textColor,
                                                        modifier = Modifier
                                                            .background(backgroundColor),
                                                        style = MaterialTheme.typography.bodyLarge.copy(
                                                            fontWeight = if (result.isCorrect)
                                                                FontWeight.Normal
                                                            else
                                                                FontWeight.Bold
                                                        )
                                                    )
                                                }
                                            }

                                            // ==============================
                                            // 3. IMAGE RESOURCE
                                            // ==============================
                                            answer.text.startsWith("RESIMA") -> {
                                                val imageKey = answer.text.removePrefix("RESIMA")
                                                val drawableRes = Res.allDrawableResources[imageKey]
                                                if (drawableRes != null) {
                                                    Image(
                                                        painter = org.jetbrains.compose.resources.painterResource(drawableRes),
                                                        contentDescription = null,
                                                        modifier = Modifier
                                                            .background(backgroundColor)
                                                            .fillMaxWidth(),
                                                        contentScale = ContentScale.FillWidth
                                                    )
                                                }
                                            }

                                            // ==============================
                                            // 4. TEXT URL
                                            // ==============================
                                            answer.text.startsWith("URLTEX") -> {

                                                val textUrl = answer.text
                                                    .removePrefix("URLTEX")

                                                var textContent by remember {
                                                    mutableStateOf("Đang tải...")
                                                }

                                                var isError by remember {
                                                    mutableStateOf(false)
                                                }

                                                LaunchedEffect(textUrl) {

                                                    try {
                                                        textContent = fetchTextFromUrl(textUrl)
                                                        isError = false

                                                    } catch (e: Exception) {

                                                        textContent = "Không thể tải nội dung"
                                                        isError = true
                                                    }
                                                }

                                                if (!isError) {

                                                    Text(
                                                        text = textContent,
                                                        color = textColor,
                                                        textAlign = TextAlign.Justify,
                                                        modifier = Modifier
                                                            .background(backgroundColor),
                                                        style = MaterialTheme.typography.bodyLarge.copy(
                                                            fontWeight = if (result.isCorrect)
                                                                FontWeight.Normal
                                                            else
                                                                FontWeight.Bold
                                                        )
                                                    )

                                                } else {

                                                    Text(
                                                        text = "Lỗi khi tải nội dung từ URL, kiểm tra kết nối mạng!",
                                                        color = questionErrorColor,
                                                        style = MaterialTheme.typography.bodyLarge.copy(
                                                            fontWeight = if (result.isCorrect)
                                                                FontWeight.Normal
                                                            else
                                                                FontWeight.Bold
                                                        ),
                                                        textAlign = TextAlign.Center
                                                    )
                                                }
                                            }

                                            // ==============================
                                            // 5. IMAGE URL
                                            // ==============================
                                            answer.text.startsWith("URLIMA") -> {

                                                val imageUrl = answer.text
                                                    .removePrefix("URLIMA")

                                                AsyncImage(
                                                    model = imageUrl,
                                                    contentDescription = "Hình ảnh từ URL",
                                                    modifier = Modifier
                                                        .background(backgroundColor)
                                                        .fillMaxWidth(),
                                                    contentScale = ContentScale.FillWidth,
                                                    placeholder = org.jetbrains.compose.resources.painterResource(Res.drawable.placeholder1
                                                    ),
                                                    error = org.jetbrains.compose.resources.painterResource(Res.drawable.error_image
                                                    )
                                                )
                                            }

                                            // ==============================
                                            // 7. DỮ LIỆU KHÔNG CÓ TIỀN TỐ
                                            // ==============================
                                            else -> {

                                                Text(
                                                    text = answer.text,
                                                    textAlign = TextAlign.Justify,
                                                    modifier = Modifier
                                                        .background(Color.LightGray),
                                                    style = MaterialTheme.typography.bodyLarge.copy(
                                                        fontWeight = if (result.isCorrect)
                                                            FontWeight.Normal
                                                        else
                                                            FontWeight.Bold
                                                    )
                                                )
                                            }
                                        }
                                    }
                                    else -> {
                                        // viết như vậy cho gọn
                                        Text(
                                            text = "Logic đã bị lỗi, báo cáo Zalo 0972712415 để khắc phục nhé",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = Color.Gray
                                        )
                                    }
                                }
                            }
                        }
                    } else if (result.correctTextAnswers.isNotEmpty()) {

                        // hiện đáp án và tính đúng sai của câu tự luận
                        val userInput = result.userInputText
                        val isCorrect = result.isCorrect
                        if (isCorrect) {
                            // ==========================================
                            // TRƯỜNG HỢP ĐÚNG: Chỉ hiện đáp án của người dùng
                            // ==========================================
                            Surface(
                                color = Color(0xFF1565C0), // Xanh dương đậm
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 2.dp)
                                        //.background(backgroundColor)
                                        .padding(horizontal = 8.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {

                                    // Biểu tượng trạng thái
                                    Text(
                                        text = "✓",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(end = 8.dp)
                                    )
                                    Text(
                                        text = "Đáp án của bạn: $userInput",
                                        style = MaterialTheme.typography.bodyLarge.copy(
                                            color = Color.White
                                        ),
                                        modifier = Modifier.padding(12.dp)
                                    )
                                }
                            }
                        } else {
                            // ==========================================
                            // TRƯỜNG HỢP SAI: Hiện đáp án người chơi VÀ CHỈ 1 GỢI Ý ĐÁP ÁN ĐÚNG NGẪU NHIÊN
                            // ==========================================
                            // Dùng remember(result) để khi recompose không bị đổi ngẫu nhiên liên tục
                            val randomCorrect = remember(result) { // nếu ưu tiên lấy đáp án đầu tiên thì đổi thành firstCorrect
                                result.correctTextAnswers.randomOrNull() // nếu ưu tiên lấy đáp án đầu tiên thì lấy firstOrNull()
                            }

                            if (randomCorrect != null) {
                                val backgroundColor = Color(0xFFA04B00) // Nâu xám

                                Surface(
                                    color = backgroundColor,
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 2.dp)
                                            //.background(backgroundColor)
                                            .padding(horizontal = 8.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {

                                        // Biểu tượng trạng thái
                                        Text(
                                            text = "✕",
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(end = 8.dp)
                                        )
                                        when (randomCorrect) {
                                            is AnswerOption.Text -> {
                                                when {
                                                    // 1. TEXT THƯỜNG (có tiền tố TEXTXT)
                                                    randomCorrect.text.startsWith("TEXTXT") -> {
                                                        val text =
                                                            randomCorrect.text.removePrefix("TEXTXT")
                                                        Text(
                                                            text = "Bạn điền: '$userInput' (Gợi ý đáp án đúng: ${text})",
                                                            style = MaterialTheme.typography.bodyLarge.copy(
                                                                color = Color.White
                                                            ),
                                                            textAlign = TextAlign.Justify,
                                                            modifier = Modifier.padding(12.dp)
                                                        )
                                                    }

                                                    // 2. TEXT RESOURCE (có tiền tố RESTEX)
                                                    randomCorrect.text.startsWith("RESTEX") -> {
                                                        val textId = randomCorrect.text.removePrefix("RESTEX")
                                                        val stringRes = Res.allStringResources[textId]
                                                        if (stringRes != null) {
                                                            Text(
                                                                text = "Bạn điền: '$userInput' (Gợi ý đáp án đúng: ${
                                                                    org.jetbrains.compose.resources.stringResource(stringRes)
                                                                })",
                                                                style = MaterialTheme.typography.bodyLarge.copy(
                                                                    color = Color.White
                                                                ),
                                                                textAlign = TextAlign.Justify,
                                                                modifier = Modifier.padding(12.dp)
                                                            )
                                                        }
                                                    }

                                                    // 3. TEXT URL (có tiền tố URLTEX)
                                                    randomCorrect.text.startsWith("URLTEX") -> {
                                                        val textUrl =
                                                            randomCorrect.text.removePrefix("URLTEX")
                                                        var textContent by remember(textUrl) {
                                                            mutableStateOf(
                                                                "Đang tải..."
                                                            )
                                                        }
                                                        var isError by remember(textUrl) {
                                                            mutableStateOf(
                                                                false
                                                            )
                                                        }

                                                        LaunchedEffect(textUrl) {
                                                            try {
                                                                textContent =
                                                                    fetchTextFromUrl(textUrl)
                                                                isError = false
                                                            } catch (e: Exception) {
                                                                textContent =
                                                                    "Không thể tải nội dung"
                                                                isError = true
                                                            }
                                                        }

                                                        if (!isError) {
                                                            Text(
                                                                text = "Bạn điền: '$userInput' (Gợi ý đáp án đúng: ${textContent})",
                                                                style = MaterialTheme.typography.bodyLarge.copy(
                                                                    color = Color.White
                                                                ),
                                                                textAlign = TextAlign.Justify,
                                                                modifier = Modifier.padding(12.dp)
                                                            )
                                                        } else {
                                                            Text(
                                                                text = "Bạn điền: '$userInput' (Lỗi tải nội dung từ URL)",
                                                                color = Color.Red,
                                                                style = MaterialTheme.typography.bodyLarge,
                                                                modifier = Modifier.padding(12.dp)
                                                            )
                                                        }
                                                    }

                                                    // 4. MẶC ĐỊNH KHÔNG CÓ TIỀN TỐ
                                                    else -> {
                                                        Text(
                                                            text = "Bạn điền: '$userInput' (Gợi ý đáp án đúng: ${randomCorrect.text})",
                                                            style = MaterialTheme.typography.bodyLarge.copy(
                                                                color = Color.White
                                                            ),
                                                            modifier = Modifier.padding(12.dp)
                                                        )
                                                    }
                                                }
                                            }

                                            else -> {
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(vertical = 2.dp)
                                                        .background(backgroundColor)
                                                        .padding(
                                                            horizontal = 8.dp,
                                                            vertical = 8.dp
                                                        ),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {

                                                    // Biểu tượng trạng thái
                                                    Text(
                                                        text = "✕",
                                                        color = Color.White,
                                                        fontWeight = FontWeight.Bold,
                                                        modifier = Modifier.padding(end = 8.dp)
                                                    )
                                                    Text(
                                                        text = "Dạng đáp án tự luận không hỗ trợ kiểu này.",
                                                        style = MaterialTheme.typography.bodyMedium.copy(
                                                            color = Color.Red
                                                        ),
                                                        modifier = Modifier.padding(12.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                    }

                    var showDetail by remember { mutableStateOf(false) }
                    if (result.detailedCorrectAnswers.isNotEmpty()) {
                        Column {
                            // Nút bấm hiển thị/ẩn đáp án chi tiết
                            TextButton(onClick = { showDetail = !showDetail }) {
                                Text(
                                    text = if (showDetail) "Ẩn đáp án chi tiết" else "Đáp án chi tiết",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }

                            // Hiển thị danh sách đáp án chi tiết nếu đang bật
                            if (showDetail) {
                                result.detailedCorrectAnswers.forEachIndexed { index, detailedCorrectAnswer ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        when (detailedCorrectAnswer) {

                                            is AnswerOption.Text -> {

                                                when {

                                                    // 1. VĂN BẢN THƯỜNG
                                                    detailedCorrectAnswer.text.startsWith("TEXTXT") -> {
                                                        val text = detailedCorrectAnswer.text.removePrefix("TEXTXT")

                                                        Text(
                                                            text = text,
                                                            style = MaterialTheme.typography.bodyLarge,
                                                            textAlign = TextAlign.Justify
                                                        )
                                                    }

                                                    // 2. TEXT RESOURCE
                                                    detailedCorrectAnswer.text.startsWith("RESTEX") -> {
                                                        val textId = detailedCorrectAnswer.text.removePrefix("RESTEX")

                                                        // Thao tác trung gian: Lấy StringResource từ Res.allStringResources dựa vào key (textId)
                                                        val stringRes = Res.allStringResources[textId]

                                                        if (stringRes != null) {
                                                            Text(
                                                                text = org.jetbrains.compose.resources.stringResource(stringRes),
                                                                style = MaterialTheme.typography.bodyLarge
                                                            )
                                                        }
                                                    }

                                                    // 3. IMAGE RESOURCE
                                                    detailedCorrectAnswer.text.startsWith("RESIMA") -> {
                                                        val imageKey = detailedCorrectAnswer.text.removePrefix("RESIMA")
                                                        val drawableRes = Res.allDrawableResources[imageKey]
                                                        if (drawableRes != null) {
                                                            Image(
                                                                painter = org.jetbrains.compose.resources.painterResource(drawableRes),
                                                                contentDescription = null,
                                                                modifier = Modifier
                                                                    .fillMaxWidth(),
                                                                contentScale = ContentScale.FillWidth
                                                            )
                                                        }
                                                    }

                                                    // 4. TEXT URL
                                                    detailedCorrectAnswer.text.startsWith("URLTEX") -> {
                                                        val textUrl = detailedCorrectAnswer.text
                                                            .removePrefix("URLTEX")

                                                        var textContent by remember {
                                                            mutableStateOf("Đang tải...")
                                                        }

                                                        var isError by remember {
                                                            mutableStateOf(false)
                                                        }

                                                        LaunchedEffect(textUrl) {
                                                            try {
                                                                textContent = fetchTextFromUrl(textUrl)
                                                                isError = false
                                                            } catch (e: Exception) {
                                                                textContent = "Không thể tải nội dung"
                                                                isError = true
                                                            }
                                                        }

                                                        if (!isError) {
                                                            Text(
                                                                text = textContent,
                                                                style = MaterialTheme.typography.bodyLarge,
                                                                textAlign = TextAlign.Justify,
                                                                modifier = Modifier
                                                                    .padding(horizontal = 4.dp)
                                                                    .fillMaxWidth()
                                                            )
                                                        } else {
                                                            Text(
                                                                text = "Lỗi khi tải nội dung từ URL, kiểm tra kết nối mạng!",
                                                                style = MaterialTheme.typography.bodyMedium,
                                                                textAlign = TextAlign.Center,
                                                                modifier = Modifier
                                                                    .padding(horizontal = 4.dp)
                                                                    .fillMaxWidth()
                                                            )
                                                        }
                                                    }

                                                    // 5. IMAGE URL
                                                    detailedCorrectAnswer.text.startsWith("URLIMA") -> {
                                                        val imageUrl = detailedCorrectAnswer.text
                                                            .removePrefix("URLIMA")

                                                        AsyncImage(
                                                            model = imageUrl,
                                                            contentDescription = "Hình ảnh từ URL",
                                                            modifier = Modifier.fillMaxWidth(),
                                                            contentScale = ContentScale.FillWidth,
                                                            placeholder = org.jetbrains.compose.resources.painterResource(Res.drawable.placeholder1),
                                                            error = org.jetbrains.compose.resources.painterResource(Res.drawable.error_image)
                                                        )
                                                    }

                                                    // 6. KHÔNG XÁC ĐỊNH
                                                    else -> {
                                                        Text(
                                                            text = detailedCorrectAnswer.text,
                                                            style = MaterialTheme.typography.bodyLarge,
                                                            textAlign = TextAlign.Justify
                                                        )
                                                    }
                                                }
                                            }
                                            else -> {}
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
    // Hiển thị nút khi cuộn lên (isToolbarVisible == true)
    if (isToolbarVisible.value) {
        Box(
            modifier = Modifier
                .padding(bottom = bottomPadding)
                .fillMaxSize()
        ) {
            Box (
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 0.dp)
                    .align(Alignment.BottomCenter),
            ) {
                Button(
                    onClick = {
                        result?.quizName?.let { quizName ->
                            onBackTest(quizName)
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .wrapContentSize(),
                    contentPadding = PaddingValues(0.dp), // Loại bỏ padding mặc định
                    shape = RoundedCornerShape(4.dp), // Bo góc nhẹ
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.LightGray.copy(0.3f), // Màu nền của nút
                        contentColor = Color.Black // Màu của icon và chữ
                    ),
                ) {
                    Column (horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(imageVector = Icons.Outlined.Edit, contentDescription = "Thi lại", modifier = Modifier.size(20.dp))
                        Text(text = "Thi lại", fontSize = 10.sp, fontWeight = FontWeight.Light)
                    }
                }
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
                        Icon(imageVector = Icons.Outlined.Home, contentDescription = "Trang đầu", modifier = Modifier.size(20.dp))
                        Text(text = "Trang đầu", fontSize = 10.sp, fontWeight = FontWeight.Light,
                            color = Color.Black)
                    }
                }
                Button(
                    onClick = { onBack() },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .wrapContentSize(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.LightGray.copy(0.3f), // Màu nền của nút
                        contentColor = Color.Black // Màu của icon và chữ
                    ),
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack, // Sử dụng phiên bản AutoMirrored
                        contentDescription = "Quay lại"
                    )
                }
            }
        }
    }
}
// Các hàm phụ và hoàn toàn không liên quan đến việc hiển thị màn hình và điều hướng
@Composable
fun VideoPlayerFromUrl(videoUrl: String, modifier: Modifier = Modifier) {
    PlatformVideoPlayer(videoUrl = videoUrl, modifier = modifier)
}

// Hàm tải và phóng to ảnh từ URL
@Composable
fun ZoomableImageFromUrl(
    imageUrl: String,
    contentDescription: String, // Thêm tham số mô tả
    modifier: Modifier = Modifier
) {
    // Hiển thị ảnh tải từ URL
    AsyncImage(
        model = imageUrl,
        contentDescription = contentDescription,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 200.dp) // Cố định chiều cao tối thiểu tránh UIKit layout shift trên iOS
            .background(Color.LightGray.copy(alpha = 0.2f)),
        contentScale = ContentScale.FillWidth,
        placeholder = painterResource(Res.drawable.placeholder1), // Placeholder image
        error = painterResource(Res.drawable.error_image) // Hình ảnh lỗi
    )
}
//Hàm tải và hiển thị ảnh đơn thuần không phóng to hay di chuyển nhé
@Composable
fun ImageFromUrl(
    imageUrl: String,
    contentDescription: String, // Thêm tham số mô tả
    modifier: Modifier = Modifier
) {
    // Hiển thị ảnh tải từ URL
    AsyncImage(
        model = imageUrl,
        contentDescription = contentDescription,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 200.dp) // Cố định chiều cao tối thiểu tránh UIKit layout shift trên iOS
            .background(Color.LightGray.copy(alpha = 0.2f)),
        contentScale = ContentScale.FillWidth,
        placeholder = painterResource(Res.drawable.placeholder1), // Placeholder image
        error = painterResource(Res.drawable.error_image) // Hình ảnh lỗi
    )
}

// Hàm chuẩn hóa văn bản không phân biệt chữ hoa, chữ thưởng
// dấu, nhiều dấu cách dùng chung đa nền tảng
fun normalizeText(input: String): String {
    val srcM = "áàảãạăắằẳẵặâấầẩẫậéèẻẽẹêếềểễệíìỉĩịóòỏõọôốồổỗộơớờởỡợúùủũụưứừửữựýỳỷỹỵđ"
    val desM = "aaaaaaaaaaaaaaaaaeeeeeeeeeeeiiiiiooooooooooooooooouuuuuuuuuuuyyyyyyd"
    var result = input.lowercase()
    for (i in srcM.indices) {
        result = result.replace(srcM[i], desM[i])
        result = result.replace(srcM[i].uppercaseChar(), desM[i])
    }
    return result.replace(Regex("\\s+"), " ").trim()
}

// Hàm mở audio thông qua url
@Composable
fun AudioPlayerFromUrl(audioUrl: String, modifier: Modifier = Modifier) {
    PlatformAudioPlayer(audioUrl = audioUrl, modifier = modifier)
}

