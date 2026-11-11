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
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Send
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.geometry.Offset
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import com.exam.quizapp.model.*
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
import com.exam.quizapp.data.quizQuestions3
import com.exam.quizapp.data.*
import com.exam.quizapp.shared.resources.Res
import com.exam.quizapp.shared.resources.allStringResources
import com.exam.quizapp.shared.resources.allDrawableResources
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.painterResource
import kotlinx.coroutines.CancellationException
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.URLBuilder
import org.jetbrains.compose.resources.stringResource

// Multiplatform QuizApp Composable
// Hàm điều phối màn hình dùng riêng cho từng nền tảng
@Composable
expect fun QuizApp(
    viewModel: QuizViewModel = viewModel(),
    onQuizCompleted: () -> Unit
)



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
                                painter = painterResource(content.imageRes),
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

                                    is AnswerOption.ImageUrl -> ImageFromUrl(
                                        imageUrl = answer.imageUrl,
                                        contentDescription = "Đáp án dạng ảnh",
                                        modifier = Modifier.fillMaxWidth()
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
            painter = painterResource(content.imageRes),
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
        is AnswerOption.ImageUrl -> ImageFromUrl(
            imageUrl = answer.imageUrl,
            contentDescription = "Đáp án dạng ảnh",
            modifier = Modifier.fillMaxWidth()
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
                painter = painterResource(answer.imageRes),
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
                                                normalizeText(
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
                                                        normalizeText(it)
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

    var isSubmitting by remember { mutableStateOf(false) }
    var submitDialogMessage by remember { mutableStateOf<String?>(null) }

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
                                painter = painterResource(question.imageRes),
                                contentDescription = null,
                                modifier = Modifier.fillMaxWidth(),
                                contentScale = ContentScale.FillWidth
                            )
                        }

                        is QuestionContent.ImageUrl -> {
                            ImageFromUrl(
                                imageUrl = question.imageUrl,
                                contentDescription = "Hình ảnh câu hỏi",
                                modifier = Modifier.fillMaxWidth()
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
                                        painter = painterResource(extraText.imageRes),
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
                                            painter = painterResource(answer.imageRes),
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
                                                        painter = painterResource(detailedCorrectAnswer.imageRes),
                                                        contentDescription = "Hình ảnh đáp án",
                                                        modifier = Modifier.fillMaxWidth(),
                                                        contentScale = ContentScale.FillWidth
                                                    )
                                                }

                                                // ==============================
                                                // IMAGE URL
                                                // ==============================
                                                is AnswerOption.ImageUrl -> {
                                                    if (detailedCorrectAnswer.imageUrl.contains("long") || detailedCorrectAnswer.imageUrl.contains("big")) {
                                                        // Nếu url chứa từ "long" hoặc "big", gọi hàm ImageFromUrl để tải và hiển thị ảnh không có phóng to
                                                        ImageFromUrl(
                                                            imageUrl = detailedCorrectAnswer.imageUrl,
                                                            contentDescription = "Hình ảnh câu hỏi"
                                                        )
                                                    } else
                                                    {
                                                        // Các trường hợp còn lại, dùng ZoomableImageFromUrl để tải và hiển thị ảnh có thể phóng to
                                                        ZoomableImageFromUrl(
                                                            imageUrl = detailedCorrectAnswer.imageUrl,
                                                            contentDescription = "Hình ảnh câu hỏi"
                                                        )
                                                    }
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
                    // Nút "Lưu & Nộp" (vừa lưu local vừa nộp lên Google Drive cho giáo viên)
                    Button(
                        onClick = {
                            if (!testShowButtons) return@Button
                            // 1. Lưu local trên máy học sinh
                            onSaveResult()

                            // 2. Nộp bài lên Google Drive cho giáo viên
                            if (isSubmitting) return@Button
                            isSubmitting = true
                            coroutineScope.launch {
                                try {
                                    val phone = getLoggedInPhone()
                                    if (phone.isBlank()) {
                                        submitDialogMessage = "Lỗi: Không tìm thấy số điện thoại đăng nhập. Vui lòng đăng nhập lại!"
                                        isSubmitting = false
                                        return@launch
                                    }
                                    val rawContent = buildString {
                                        appendLine("=== KẾT QUẢ BÀI LÀM ===")
                                        appendLine("Quiz: $quizName")
                                        appendLine("Điểm số: $score / $totalQuestions")
                                        appendLine("----------------------------------------")
                                        history.forEachIndexed { index, qr ->
                                            val status = if (qr.isCorrect) "ĐÚNG (✓)" else "SAI (✕)"
                                            appendLine("Câu ${index + 1}: $status")
                                            if (!qr.userInputText.isNullOrBlank()) {
                                                appendLine("  - Học sinh điền (Tự luận): ${qr.userInputText.trim()}")
                                            } else if (qr.answers.isNotEmpty()) {
                                                val selectedIdx = qr.answers.indexOf(qr.selectedAnswer)
                                                val selectedStr = if (selectedIdx >= 0) "$selectedIdx" else "Chưa chọn"

                                                val correctIdx = qr.answers.indexOf(qr.correctAnswer)
                                                val correctStr = if (correctIdx >= 0) "$correctIdx" else "N/A"

                                                appendLine("  - Đã chọn: $selectedStr | Đáp án đúng: $correctStr")
                                            }
                                            appendLine()
                                        }
                                    }
                                    val rawFileName = "KetQua_${quizName}.txt"
                                    val contentText = encodeURLComponent(rawContent)
                                    val fileNameText = encodeURLComponent(rawFileName)
                                    val url = "https://script.google.com/macros/s/AKfycbxxde1KsjDnLl-7EoOLmDtsi9WU0ECbY8Yxi8s-vnR7kZn4bL7cKOMJmxvXUjS64fr3/exec?phone=$phone&content=$contentText&fileName=$fileNameText"
                                    val responseText = fetchTextFromUrl(url)
                                    val json = Json.parseToJsonElement(responseText).jsonObject
                                    val status = json["status"]?.jsonPrimitive?.content ?: "error"
                                    val msg = json["message"]?.jsonPrimitive?.content ?: "Lỗi"
                                    submitDialogMessage = if (status == "success") "Lưu máy & Nộp bài Google Drive thành công!" else msg
                                } catch (e: Exception) {
                                    submitDialogMessage = "Đã lưu máy nhưng nộp Drive thất bại: ${e.message}"
                                } finally {
                                    isSubmitting = false
                                }
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
                            if (isSubmitting) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                            } else {
                                Icon(imageVector = Icons.Outlined.Save, contentDescription = "Lưu & Nộp", modifier = Modifier.size(20.dp))
                                Text(text = "Lưu & Nộp", fontSize = 10.sp, fontWeight = FontWeight.Light, color = Color.Black)
                            }
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
                    // Nút "Xem review" (khôi phục lại như cũ)
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

    if (submitDialogMessage != null) {
        AlertDialog(
            onDismissRequest = { submitDialogMessage = null },
            title = { Text("Thông báo") },
            text = { Text(submitDialogMessage ?: "") },
            confirmButton = {
                TextButton(onClick = { submitDialogMessage = null }) {
                    Text("OK")
                }
            }
        )
    }
}
// MÀN HÌNH DANH SÁCH KẾT QUẢ LÀM BÀI ĐÃ ĐƯỢC LƯU LẠI THEO DẠNG MỤC LỤC
@Composable
fun ResultListScreen(
    onResultClick: (Int) -> Unit,
    onBack: () -> Unit,
    onHomePage: () -> Unit,
    resultViewModel: ResultViewModel = viewModel()
) {
    var iconWidth by remember { mutableIntStateOf(0) } // Kiểm tra thử có thế thay đổi Biến lưu độ rộng của icon
    // Lấy khoảng đệm theo chiều cao thành trạng thái và thanh điều khiển
    val bottomPadding = getNavigationBarHeight()
    val topPadding = getBannerAdHeight()
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


/*@Serializable
data class SubmissionResponse(val status: String, val message: String)
*/

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
                                            painter = painterResource(drawableRes),
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

                                    ImageFromUrl(
                                        imageUrl = imageUrl,
                                        contentDescription = "Hình ảnh từ URL",
                                        modifier = Modifier.fillMaxWidth()
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
                                                    painter = painterResource(drawableRes),
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

                                            ImageFromUrl(
                                                imageUrl = imageUrl,
                                                contentDescription = "Hình ảnh từ URL",
                                                modifier = Modifier.fillMaxWidth()
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
                                                        painter = painterResource(drawableRes),
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

                                                ImageFromUrl(
                                                    imageUrl = imageUrl,
                                                    contentDescription = "Hình ảnh từ URL",
                                                    modifier = Modifier
                                                        .background(backgroundColor)
                                                        .fillMaxWidth()
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
                                                                painter = painterResource(drawableRes),
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

                                                        ImageFromUrl(
                                                            imageUrl = imageUrl,
                                                            contentDescription = "Hình ảnh từ URL",
                                                            modifier = Modifier.fillMaxWidth()
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
// Hàm không phân biệt chữ hoa, chữ thưởng
// dấu, nhiều dấu cách dùng chung đa nền tảng
fun normalizeText(input: String): String {
    val srcM = "áàảãạăắằẳẵặâấầẩẫậéèẻẽẹêếềểễệíìỉĩịóòỏõọôốồổỗộơớờởỡợúùủũụưứừửữựýỳỷỹỵđ"
    val desM = "aaaaaaaaaaaaaaaaaee@Serializable\n"+
            "data class SubmissionResponse(val status: String, val message: String)\n"+
            "\n"+
            "// MÀN HÌNH CHI TIẾT BÀI LÀM ĐÃ LƯU THEO TỪNG BÀI LÀM\n"+
            "@Composable\n"+
            "fun ResultDetailScreen(\n"+
            "    resultId: Int,\n"+
            "    viewModel: ResultViewModel = viewModel(),\n"+
            "    onHomePage: () -> Unit,\n"+
            "    onBack: () -> Unit,\n"+
            "    onBackTest: (String) -> Unit\n"+
            ") {\n"+
            "    // Lấy khoảng đệm theo chiều cao thành trạng thái và thanh điều khiển\n"+
            "    val bottomPadding = getNavigationBarHeight()\n"+
            "    val topPadding = getBannerAdHeight()\n"+
            "    // Lấy dữ liệu kết quả từ ViewModel\n"+
            "    val result = viewModel.getResultById(resultId)\n"+
            "    val questionResults =\n"+
            "        result?.let { viewModel.getQuestionResultsForResult(it.id) } ?: emptyList()\n"+
            "    // Biến trạng thái để kiểm soát hiển thị tiêu đề và nút\n"+
            "    val isToolbarVisible = remember { mutableStateOf(true) }\n"+
            "    val listState = rememberLazyListState()\n"+
            "    val coroutineScope = rememberCoroutineScope()\n"+
            "\n"+
            "    // Sử dụng nestedScroll để phát hiện khi người dùng cuộn\n"+
            "    val nestedScrollConnection = remember {\n"+
            "        object : NestedScrollConnection {\n"+
            "            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {\n"+
            "                if (available.y < 0) {\n"+
            "                    isToolbarVisible.value = false // Cuộn xuống\n"+
            "                } else if (available.y > 0) {\n"+
            "                    isToolbarVisible.value = true // Cuộn lên\n"+
            "                }\n"+
            "                return Offset.Zero\n"+
            "            }\n"+
            "        }\n"+
            "    }\n"+
            "\n"+
            "    Column(\n"+
            "        modifier = Modifier\n"+
            "            .fillMaxSize()\n"+
            "            .padding(\n"+
            "                top = topPadding,\n"+
            "                bottom = bottomPadding)\n"+
            "            .nestedScroll(nestedScrollConnection), // Theo dõi sự kiện cuộn\n"+
            "        verticalArrangement = Arrangement.Center,\n"+
            "        horizontalAlignment = Alignment.CenterHorizontally\n"+
            "    ) {\n"+
            "        // Hiển thị tiêu đề và điểm số nếu isToolbarVisible là true\n"+
            "        if (isToolbarVisible.value) {\n"+
            "            Row(\n"+
            "                modifier = Modifier\n"+
            "                    .fillMaxWidth()\n"+
            "                    .background(Color.LightGray.copy(alpha = 0.5f))\n"+
            "                    .padding(vertical = 4.dp),\n"+
            "                verticalAlignment = Alignment.CenterVertically,\n"+
            "                horizontalArrangement = Arrangement.SpaceBetween\n"+
            "            ) {\n"+
            "                Text(\n"+
            "                    text = buildAnnotatedString {\n"+
            "                        withStyle(style = SpanStyle(color = Color.Black)) {\n"+
            "                            append(\"\${result?.quizName}\") // Phần quizName có màu đen\n"+
            "                        }\n"+
            "                        withStyle(style = SpanStyle(color = Color.Blue)) {\n"+
            "                            append(\" (đúng: \${result?.score} / \${result?.totalQuestions})\") // Phần kết quả có màu xanh/đỏ\n"+
            "                        }\n"+
            "                    },\n"+
            "                    style = MaterialTheme\n"+
            "                        .typography\n"+
            "                        .headlineSmall\n"+
            "                        .copy(fontWeight = FontWeight.Bold),\n"+
            "                    modifier = Modifier\n"+
            "                        .weight(1f)\n"+
            "                        .padding(start = 8.dp, end = 4.dp)\n"+
            "                )\n"+
            "\n"+
            "                IconButton(\n"+
            "                    onClick = {\n"+
            "                        coroutineScope.launch {\n"+
            "                            listState.animateScrollToItem(0)\n"+
            "                        }\n"+
            "                    },\n"+
            "                    modifier = Modifier.padding(end = 4.dp)\n"+
            "                ) {\n"+
            "                    Column(horizontalAlignment = Alignment.CenterHorizontally) {\n"+
            "                        Text(text = \"Top\", fontSize = 10.sp, fontWeight = FontWeight.Light)\n"+
            "                        Icon(\n"+
            "                            imageVector = Icons.Default.KeyboardArrowUp,\n"+
            "                            contentDescription = \"Lên đầu trang\",\n"+
            "                            tint = Color.Black\n"+
            "                        )\n"+
            "                    }\n"+
            "                }\n"+
            "            }\n"+
            "        }\n"+
            "\n"+
            "        // Hiển thị lịch sử câu hỏi\n"+
            "        LazyColumn(state = listState) {\n"+
            "            if (questionResults.isNotEmpty()) {\n"+
            "                item {\n"+
            "                    QuestionGridButtons(\n"+
            "                        questionResults = questionResults,\n"+
            "                        onQuestionClick = { index ->\n"+
            "                            coroutineScope.launch {\n"+
            "                                listState.animateScrollToItem(index + 1)\n"+
            "                            }\n"+
            "                        }\n"+
            "                    )\n"+
            "                }\n"+
            "            }\n"+
            "            items(questionResults) { result ->\n"+
            "                Column(modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 8.dp)) {\n"+
            "                    val currentQuestionIndex = questionResults.indexOf(result)\n"+
            "                    // Hiển thị câu hỏi\n"+
            "                    Text(\n"+
            "                        text = \"Câu \${currentQuestionIndex + 1}\",\n"+
            "                        color = Color(0xFF0D47A1),\n"+
            "                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)\n"+
            "                    )\n"+
            "                    val questionBackground = Color(0xFFE8F1F8) // Xanh rất nhạt\n"+
            "                    val questionTextColor = Color(0xFF172B3A)   // Xanh đen\n"+
            "                    val questionErrorColor = Color(0xFF8A4B00)  // Cam nâu đậm\n"+
            "\n"+
            "                    when (val question = result.mainQuestion) {\n"+
            "                        is QuestionContent.AudioUrl -> {}\n"+
            "                        is QuestionContent.Text -> {\n"+
            "\n"+
            "                            when {\n"+
            "                                // Văn bản thường\n"+
            "                                question.text.startsWith(\"TEXTXT\") -> {\n"+
            "\n"+
            "                                    val text = question.text.removePrefix(\"TEXTXT\")\n"+
            "\n"+
            "                                    Text(\n"+
            "                                        text = text,\n"+
            "                                        color = questionTextColor,\n"+
            "                                        textAlign = TextAlign.Justify,\n"+
            "                                        modifier = Modifier\n"+
            "                                            .background(questionBackground),\n"+
            "                                        style = MaterialTheme.typography.bodyLarge.copy(\n"+
            "                                            fontWeight = if (result.isCorrect)\n"+
            "                                                FontWeight.Normal\n"+
            "                                            else\n"+
            "                                                FontWeight.Bold\n"+
            "                                        )\n"+
            "                                    )\n"+
            "                                }\n"+
            "                                // Text Resource\n"+
            "                                question.text.startsWith(\"RESTEX\") -> {\n"+
            "                                    val textId = question.text.removePrefix(\"RESTEX\")\n"+
            "                                    val stringRes = Res.allStringResources[textId]\n"+
            "                                    if (stringRes != null) {\n"+
            "                                        Text(\n"+
            "                                            text = org.jetbrains.compose.resources.stringResource(stringRes),\n"+
            "                                            color = questionTextColor,\n"+
            "                                            modifier = Modifier\n"+
            "                                                .background(questionBackground),\n"+
            "                                            style = MaterialTheme.typography.bodyLarge.copy(\n"+
            "                                                fontWeight = if (result.isCorrect)\n"+
            "                                                    FontWeight.Normal\n"+
            "                                                else\n"+
            "                                                    FontWeight.Bold\n"+
            "                                            )\n"+
            "                                        )\n"+
            "                                    }\n"+
            "                                }\n"+
            "\n"+
            "                                // ==============================\n"+
            "                                // Image Resource\n"+
            "                                // ==============================\n"+
            "                                question.text.startsWith(\"RESIMA\") -> {\n"+
            "                                    val imageKey = question.text.removePrefix(\"RESIMA\")\n"+
            "                                    val drawableRes = Res.allDrawableResources[imageKey]\n"+
            "                                    if (drawableRes != null) {\n"+
            "                                        Image(\n"+
            "                                            painter = painterResource(drawableRes),\n"+
            "                                            contentDescription = null,\n"+
            "                                            modifier = Modifier.fillMaxWidth(),\n"+
            "                                            contentScale = ContentScale.FillWidth\n"+
            "                                        )\n"+
            "                                    }\n"+
            "                                }\n"+
            "\n"+
            "                                // ==============================\n"+
            "                                // Text URL\n"+
            "                                // ==============================\n"+
            "                                question.text.startsWith(\"URLTEX\") -> {\n"+
            "\n"+
            "                                    val textUrl = question.text\n"+
            "                                        .removePrefix(\"URLTEX\")\n"+
            "\n"+
            "                                    var textContent by remember {\n"+
            "                                        mutableStateOf(\"Đang tải...\")\n"+
            "                                    }\n"+
            "\n"+
            "                                    var isError by remember {\n"+
            "                                        mutableStateOf(false)\n"+
            "                                    }\n"+
            "\n"+
            "                                    LaunchedEffect(textUrl) {\n"+
            "\n"+
            "                                        try {\n"+
            "                                            textContent = fetchTextFromUrl(textUrl)\n"+
            "                                            isError = false\n"+
            "\n"+
            "                                        } catch (e: Exception) {\n"+
            "\n"+
            "                                            textContent = \"Không thể tải nội dung\"\n"+
            "                                            isError = true\n"+
            "                                        }\n"+
            "                                    }\n"+
            "\n"+
            "                                    if (!isError) {\n"+
            "\n"+
            "                                        Text(\n"+
            "                                            text = textContent,\n"+
            "                                            color = questionTextColor,\n"+
            "                                            textAlign = TextAlign.Justify,\n"+
            "                                            modifier = Modifier\n"+
            "                                                .background(questionBackground),\n"+
            "                                            style = MaterialTheme.typography.bodyLarge.copy(\n"+
            "                                                fontWeight = if (result.isCorrect)\n"+
            "                                                    FontWeight.Normal\n"+
            "                                                else\n"+
            "                                                    FontWeight.Bold\n"+
            "                                            )\n"+
            "                                        )\n"+
            "\n"+
            "                                    } else {\n"+
            "\n"+
            "                                        Text(\n"+
            "                                            text = \"Lỗi khi tải nội dung từ URL, kiểm tra kết nối mạng!\",\n"+
            "                                            color = questionErrorColor,\n"+
            "                                            style = MaterialTheme.typography.bodyLarge.copy(\n"+
            "                                                fontWeight = if (result.isCorrect)\n"+
            "                                                    FontWeight.Normal\n"+
            "                                                else\n"+
            "                                                    FontWeight.Bold\n"+
            "                                            ),\n"+
            "                                            textAlign = TextAlign.Center\n"+
            "                                        )\n"+
            "                                    }\n"+
            "                                }\n"+
            "\n"+
            "                                // ==============================\n"+
            "                                // Image URL\n"+
            "                                // ==============================\n"+
            "                                question.text.startsWith(\"URLIMA\") -> {\n"+
            "\n"+
            "                                    val imageUrl = question.text\n"+
            "                                        .removePrefix(\"URLIMA\")\n"+
            "\n"+
            "                                    ImageFromUrl(\n"+
            "                                        imageUrl = imageUrl,\n"+
            "                                        contentDescription = \"Hình ảnh từ URL\",\n"+
            "                                        modifier = Modifier.fillMaxWidth()\n"+
            "                                    )\n"+
            "                                }\n"+
            "\n"+
            "                                // ==============================\n"+
            "                                // Clip / Video URL\n"+
            "                                // ==============================\n"+
            "                                question.text.startsWith(\"URLCLI\") -> {\n"+
            "\n"+
            "                                    val clipUrl = question.text\n"+
            "                                        .removePrefix(\"URLCLI\")\n"+
            "\n"+
            "                                    VideoPlayerFromUrl(\n"+
            "                                        videoUrl = clipUrl,\n"+
            "                                        modifier = Modifier.fillMaxWidth()\n"+
            "                                    )\n"+
            "                                }\n"+
            "                                // ==============================\n"+
            "                                // AUDIO / AUDIO URL\n"+
            "                                // ==============================\n"+
            "                                question.text.startsWith(\"URLAUD\") -> {\n"+
            "\n"+
            "                                    val audioUrl = question.text\n"+
            "                                        .removePrefix(\"URLAUD\")\n"+
            "\n"+
            "                                    AudioPlayerFromUrl(\n"+
            "                                        audioUrl = audioUrl, // hoặc it.audioUrl tùy tên biến trong khối when của bạn\n"+
            "                                        modifier = Modifier.fillMaxWidth()\n"+
            "                                    )\n"+
            "                                }\n"+
            "\n"+
            "                                // ==============================\n"+
            "                                // Dữ liệu không xác định\n"+
            "                                // ==============================\n"+
            "                                else -> {\n"+
            "\n"+
            "                                    Text(\n"+
            "                                        text = question.text,\n"+
            "                                        textAlign = TextAlign.Justify,\n"+
            "                                        modifier = Modifier\n"+
            "                                            .background(Color.LightGray),\n"+
            "                                        style = MaterialTheme.typography.bodyLarge.copy(\n"+
            "                                            fontWeight = if (result.isCorrect)\n"+
            "                                                FontWeight.Normal\n"+
            "                                            else\n"+
            "                                                FontWeight.Bold\n"+
            "                                        )\n"+
            "                                    )\n"+
            "                                }\n"+
            "                            }\n"+
            "                        }\n"+
            "                        else -> {}\n"+
            "                    }\n"+
            "                    // Hiển thi phần câu hỏi thêm extraQuestions\n"+
            "                    result.extraQuestions.forEachIndexed { extraTextIndex, extraText ->\n"+
            "                        Row(\n"+
            "                            modifier = Modifier\n"+
            "                                .fillMaxWidth()\n"+
            "                                .padding(vertical = 2.dp),\n"+
            "                            verticalAlignment = Alignment.CenterVertically\n"+
            "                        ) {\n"+
            "                            /*Text(\n"+
            "                                text = \"\${'A' + answerIndex}: \",\n"+
            "                                style = MaterialTheme.typography.bodyLarge,\n"+
            "                                modifier = Modifier.padding(end = 4.dp)\n"+
            "                            )*/\n"+
            "                            when (extraText) {\n"+
            "\n"+
            "                                is QuestionContent.Text -> {\n"+
            "\n"+
            "                                    when {\n"+
            "\n"+
            "                                        // ==============================\n"+
            "                                        // 1. TEXT THƯỜNG\n"+
            "                                        // ==============================\n"+
            "                                        extraText.text.startsWith(\"TEXTXT\") -> {\n"+
            "\n"+
            "                                            val text = extraText.text.removePrefix(\"TEXTXT\")\n"+
            "\n"+
            "                                            Text(\n"+
            "                                                text = text,\n"+
            "                                                color = questionTextColor,\n"+
            "                                                textAlign = TextAlign.Justify,\n"+
            "                                                modifier = Modifier\n"+
            "                                                    .background(questionBackground),\n"+
            "                                                style = MaterialTheme.typography.bodyLarge.copy(\n"+
            "                                                    fontWeight = if (result.isCorrect)\n"+
            "                                                        FontWeight.Normal\n"+
            "                                                    else\n"+
            "                                                        FontWeight.Bold\n"+
            "                                                )\n"+
            "                                            )\n"+
            "                                        }\n"+
            "\n"+
            "                                        // ==============================\n"+
            "                                        // 2. TEXT RESOURCE\n"+
            "                                        // ==============================\n"+
            "                                        extraText.text.startsWith(\"RESTEX\") -> {\n"+
            "                                            val textId = extraText.text.removePrefix(\"RESTEX\")\n"+
            "                                            val stringRes = Res.allStringResources[textId]\n"+
            "                                            if (stringRes != null) {\n"+
            "                                                Text(\n"+
            "                                                    text = org.jetbrains.compose.resources.stringResource(stringRes),\n"+
            "                                                    color = questionTextColor,\n"+
            "                                                    modifier = Modifier\n"+
            "                                                        .background(questionBackground),\n"+
            "                                                    style = MaterialTheme.typography.bodyLarge.copy(\n"+
            "                                                        fontWeight = if (result.isCorrect)\n"+
            "                                                            FontWeight.Normal\n"+
            "                                                        else\n"+
            "                                                            FontWeight.Bold\n"+
            "                                                    )\n"+
            "                                                )\n"+
            "                                            }\n"+
            "                                        }\n"+
            "\n"+
            "                                        // ==============================\n"+
            "                                        // 3. IMAGE RESOURCE\n"+
            "                                        // ==============================\n"+
            "                                        extraText.text.startsWith(\"RESIMA\") -> {\n"+
            "                                            val imageKey = extraText.text.removePrefix(\"RESIMA\")\n"+
            "                                            val drawableRes = Res.allDrawableResources[imageKey]\n"+
            "                                            if (drawableRes != null) {\n"+
            "                                                Image(\n"+
            "                                                    painter = painterResource(drawableRes),\n"+
            "                                                    contentDescription = null,\n"+
            "                                                    modifier = Modifier.fillMaxWidth(),\n"+
            "                                                    contentScale = ContentScale.FillWidth\n"+
            "                                                )\n"+
            "                                            }\n"+
            "                                        }\n"+
            "\n"+
            "                                        // ==============================\n"+
            "                                        // 4. TEXT URL\n"+
            "                                        // ==============================\n"+
            "                                        extraText.text.startsWith(\"URLTEX\") -> {\n"+
            "\n"+
            "                                            val textUrl = extraText.text\n"+
            "                                                .removePrefix(\"URLTEX\")\n"+
            "\n"+
            "                                            var textContent by remember {\n"+
            "                                                mutableStateOf(\"Đang tải...\")\n"+
            "                                            }\n"+
            "\n"+
            "                                            var isError by remember {\n"+
            "                                                mutableStateOf(false)\n"+
            "                                            }\n"+
            "\n"+
            "                                            LaunchedEffect(textUrl) {\n"+
            "\n"+
            "                                                try {\n"+
            "                                                    textContent = fetchTextFromUrl(textUrl)\n"+
            "                                                    isError = false\n"+
            "\n"+
            "                                                } catch (e: Exception) {\n"+
            "\n"+
            "                                                    textContent = \"Không thể tải nội dung\"\n"+
            "                                                    isError = true\n"+
            "                                                }\n"+
            "                                            }\n"+
            "\n"+
            "                                            if (!isError) {\n"+
            "\n"+
            "                                                Text(\n"+
            "                                                    text = textContent,\n"+
            "                                                    color = questionTextColor,\n"+
            "                                                    textAlign = TextAlign.Justify,\n"+
            "                                                    modifier = Modifier\n"+
            "                                                        .background(questionBackground),\n"+
            "                                                    style = MaterialTheme.typography.bodyLarge.copy(\n"+
            "                                                        fontWeight = if (result.isCorrect)\n"+
            "                                                            FontWeight.Normal\n"+
            "                                                        else\n"+
            "                                                            FontWeight.Bold\n"+
            "                                                    )\n"+
            "                                                )\n"+
            "\n"+
            "                                            } else {\n"+
            "\n"+
            "                                                Text(\n"+
            "                                                    text = \"Lỗi khi tải nội dung từ URL, kiểm tra kết nối mạng!\",\n"+
            "                                                    color = questionErrorColor,\n"+
            "                                                    style = MaterialTheme.typography.bodyLarge.copy(\n"+
            "                                                        fontWeight = if (result.isCorrect)\n"+
            "                                                            FontWeight.Normal\n"+
            "                                                        else\n"+
            "                                                            FontWeight.Bold\n"+
            "                                                    ),\n"+
            "                                                    textAlign = TextAlign.Center\n"+
            "                                                )\n"+
            "                                            }\n"+
            "                                        }\n"+
            "\n"+
            "                                        // ==============================\n"+
            "                                        // 5. IMAGE URL\n"+
            "                                        // ==============================\n"+
            "                                        extraText.text.startsWith(\"URLIMA\") -> {\n"+
            "\n"+
            "                                            val imageUrl = extraText.text\n"+
            "                                                .removePrefix(\"URLIMA\")\n"+
            "\n"+
            "                                            ImageFromUrl(\n"+
            "                                                imageUrl = imageUrl,\n"+
            "                                                contentDescription = \"Hình ảnh từ URL\",\n"+
            "                                                modifier = Modifier.fillMaxWidth()\n"+
            "                                            )\n"+
            "                                        }\n"+
            "\n"+
            "                                        // ==============================\n"+
            "                                        // 6. VIDEO / CLIP URL\n"+
            "                                        // ==============================\n"+
            "                                        extraText.text.startsWith(\"URLCLI\") -> {\n"+
            "\n"+
            "                                            val clipUrl = extraText.text\n"+
            "                                                .removePrefix(\"URLCLI\")\n"+
            "\n"+
            "                                            VideoPlayerFromUrl(\n"+
            "                                                videoUrl = clipUrl,\n"+
            "                                                modifier = Modifier.fillMaxWidth()\n"+
            "                                            )\n"+
            "                                        }\n"+
            "                                        // ==============================\n"+
            "                                        // 7. AUDIO / AUDIO URL\n"+
            "                                        // ==============================\n"+
            "                                        extraText.text.startsWith(\"URLAUD\") -> {\n"+
            "\n"+
            "                                            val audioUrl = extraText.text\n"+
            "                                                .removePrefix(\"URLAUD\")\n"+
            "\n"+
            "                                            AudioPlayerFromUrl(\n"+
            "                                                audioUrl = audioUrl, // hoặc it.audioUrl tùy tên biến trong khối when của bạn\n"+
            "                                                modifier = Modifier.fillMaxWidth()\n"+
            "                                            )\n"+
            "                                        }\n"+
            "\n"+
            "                                        // ==============================\n"+
            "                                        // 7. DỮ LIỆU KHÔNG CÓ TIỀN TỐ\n"+
            "                                        // ==============================\n"+
            "                                        else -> {\n"+
            "\n"+
            "                                            Text(\n"+
            "                                                text = extraText.text,\n"+
            "                                                textAlign = TextAlign.Justify,\n"+
            "                                                modifier = Modifier\n"+
            "                                                    .background(Color.LightGray),\n"+
            "                                                style = MaterialTheme.typography.bodyLarge.copy(\n"+
            "                                                    fontWeight = if (result.isCorrect)\n"+
            "                                                        FontWeight.Normal\n"+
            "                                                    else\n"+
            "                                                        FontWeight.Bold\n"+
            "                                                )\n"+
            "                                            )\n"+
            "                                        }\n"+
            "                                    }\n"+
            "                                }\n"+
            "                                else -> {}\n"+
            "                            }\n"+
            "                        }\n"+
            "                    }\n"+
            "\n"+
            "                    // Hiển thị danh sách đáp án trắc nghiệm\n"+
            "                    if (result.answers.isNotEmpty()) {\n"+
            "                        val selectedAnswer = result.selectedAnswer\n"+
            "                        val correctAnswer = result.correctAnswer\n"+
            "\n"+
            "                        result.answers.forEach { answer ->\n"+
            "\n"+
            "                            val isCorrectAnswer = answer == correctAnswer\n"+
            "                            val isSelected = answer == selectedAnswer\n"+
            "\n"+
            "                            // 4 trạng thái:\n"+
            "                            // 1. Đúng + đã chọn\n"+
            "                            // 2. Sai + đã chọn\n"+
            "                            // 3. Đúng nhưng không chọn\n"+
            "                            // 4. Không đúng + không chọn\n"+
            "\n"+
            "                            val backgroundColor = when {\n"+
            "                                // 1. Người dùng chọn đúng\n"+
            "                                isSelected && isCorrectAnswer ->\n"+
            "                                    Color(0xFF1565C0) // Xanh dương đậm\n"+
            "\n"+
            "                                // 2. Người dùng chọn sai\n"+
            "                                isSelected && !isCorrectAnswer ->\n"+
            "                                    Color(0xFFA04B00) // Cam/nâu đậm\n"+
            "\n"+
            "                                // 3. Đáp án đúng nhưng người dùng không chọn\n"+
            "                                !isSelected && isCorrectAnswer ->\n"+
            "                                    Color(0xFFD6E8FF) // Xanh dương rất nhạt\n"+
            "\n"+
            "                                // 4. Không đúng và cũng không chọn\n"+
            "                                else ->\n"+
            "                                    Color(0xFFF2F2F2) // Xám rất nhạt\n"+
            "                            }\n"+
            "\n"+
            "                            val textColor = when {\n"+
            "                                // Nền đậm → chữ trắng\n"+
            "                                isSelected && isCorrectAnswer ->\n"+
            "                                    Color.White\n"+
            "\n"+
            "                                isSelected && !isCorrectAnswer ->\n"+
            "                                    Color.White\n"+
            "\n"+
            "                                // Nền sáng → chữ tối\n"+
            "                                !isSelected && isCorrectAnswer ->\n"+
            "                                    Color(0xFF0D47A1)\n"+
            "\n"+
            "                                else ->\n"+
            "                                    Color(0xFF333333)\n"+
            "                            }\n"+
            "\n"+
            "                            val statusIcon = when {\n"+
            "                                // Đúng + đã chọn\n"+
            "                                isSelected && isCorrectAnswer -> \"✓\"\n"+
            "\n"+
            "                                // Sai + đã chọn\n"+
            "                                isSelected && !isCorrectAnswer -> \"✕\"\n"+
            "\n"+
            "                                // Đúng nhưng không chọn\n"+
            "                                !isSelected && isCorrectAnswer -> \"✓\"\n"+
            "\n"+
            "                                // Không đúng + không chọn\n"+
            "                                else -> \"—\"\n"+
            "                            }\n"+
            "\n"+
            "                            Row(\n"+
            "                                modifier = Modifier\n"+
            "                                    .fillMaxWidth()\n"+
            "                                    .padding(vertical = 2.dp)\n"+
            "                                    .background(backgroundColor)\n"+
            "                                    .padding(horizontal = 8.dp, vertical = 8.dp),\n"+
            "                                verticalAlignment = Alignment.CenterVertically\n"+
            "                            ) {\n"+
            "\n"+
            "                                // Biểu tượng trạng thái\n"+
            "                                Text(\n"+
            "                                    text = statusIcon,\n"+
            "                                    color = textColor,\n"+
            "                                    fontWeight = FontWeight.Bold,\n"+
            "                                    modifier = Modifier.padding(end = 8.dp)\n"+
            "                                )\n"+
            "\n"+
            "                                when (answer) {\n"+
            "\n"+
            "                                    is AnswerOption.Text -> {\n"+
            "                                        when {\n"+
            "\n"+
            "                                            // ==============================\n"+
            "                                            // 1. TEXT THƯỜNG\n"+
            "                                            // ==============================\n"+
            "                                            answer.text.startsWith(\"TEXTXT\") -> {\n"+
            "\n"+
            "                                                val text = answer.text.removePrefix(\"TEXTXT\")\n"+
            "\n"+
            "                                                Text(\n"+
            "                                                    text = text,\n"+
            "                                                    color = textColor,\n"+
            "                                                    textAlign = TextAlign.Justify,\n"+
            "                                                    modifier = Modifier\n"+
            "                                                        .background(backgroundColor),\n"+
            "                                                    style = MaterialTheme.typography.bodyLarge.copy(\n"+
            "                                                        fontWeight = if (result.isCorrect)\n"+
            "                                                            FontWeight.Normal\n"+
            "                                                        else\n"+
            "                                                            FontWeight.Bold\n"+
            "                                                    )\n"+
            "                                                )\n"+
            "                                            }\n"+
            "\n"+
            "                                            // ==============================\n"+
            "                                            // 2. TEXT RESOURCE\n"+
            "                                            // ==============================\n"+
            "                                            answer.text.startsWith(\"RESTEX\") -> {\n"+
            "                                                val textId = answer.text.removePrefix(\"RESTEX\")\n"+
            "                                                val stringRes = Res.allStringResources[textId]\n"+
            "                                                if (stringRes != null) {\n"+
            "                                                    Text(\n"+
            "                                                        text = org.jetbrains.compose.resources.stringResource(stringRes),\n"+
            "                                                        color = textColor,\n"+
            "                                                        modifier = Modifier\n"+
            "                                                            .background(backgroundColor),\n"+
            "                                                        style = MaterialTheme.typography.bodyLarge.copy(\n"+
            "                                                            fontWeight = if (result.isCorrect)\n"+
            "                                                                FontWeight.Normal\n"+
            "                                                            else\n"+
            "                                                                FontWeight.Bold\n"+
            "                                                        )\n"+
            "                                                    )\n"+
            "                                                }\n"+
            "                                            }\n"+
            "\n"+
            "                                            // ==============================\n"+
            "                                            // 3. IMAGE RESOURCE\n"+
            "                                            // ==============================\n"+
            "                                            answer.text.startsWith(\"RESIMA\") -> {\n"+
            "                                                val imageKey = answer.text.removePrefix(\"RESIMA\")\n"+
            "                                                val drawableRes = Res.allDrawableResources[imageKey]\n"+
            "                                                if (drawableRes != null) {\n"+
            "                                                    Image(\n"+
            "                                                        painter = painterResource(drawableRes),\n"+
            "                                                        contentDescription = null,\n"+
            "                                                        modifier = Modifier\n"+
            "                                                            .background(backgroundColor)\n"+
            "                                                            .fillMaxWidth(),\n"+
            "                                                        contentScale = ContentScale.FillWidth\n"+
            "                                                    )\n"+
            "                                                }\n"+
            "                                            }\n"+
            "\n"+
            "                                            // ==============================\n"+
            "                                            // 4. TEXT URL\n"+
            "                                            // ==============================\n"+
            "                                            answer.text.startsWith(\"URLTEX\") -> {\n"+
            "\n"+
            "                                                val textUrl = answer.text\n"+
            "                                                    .removePrefix(\"URLTEX\")\n"+
            "\n"+
            "                                                var textContent by remember {\n"+
            "                                                    mutableStateOf(\"Đang tải...\")\n"+
            "                                                }\n"+
            "\n"+
            "                                                var isError by remember {\n"+
            "                                                    mutableStateOf(false)\n"+
            "                                                }\n"+
            "\n"+
            "                                                LaunchedEffect(textUrl) {\n"+
            "\n"+
            "                                                    try {\n"+
            "                                                        textContent = fetchTextFromUrl(textUrl)\n"+
            "                                                        isError = false\n"+
            "\n"+
            "                                                    } catch (e: Exception) {\n"+
            "\n"+
            "                                                        textContent = \"Không thể tải nội dung\"\n"+
            "                                                        isError = true\n"+
            "                                                    }\n"+
            "                                                }\n"+
            "\n"+
            "                                                if (!isError) {\n"+
            "\n"+
            "                                                    Text(\n"+
            "                                                        text = textContent,\n"+
            "                                                        color = textColor,\n"+
            "                                                        textAlign = TextAlign.Justify,\n"+
            "                                                        modifier = Modifier\n"+
            "                                                            .background(backgroundColor),\n"+
            "                                                        style = MaterialTheme.typography.bodyLarge.copy(\n"+
            "                                                            fontWeight = if (result.isCorrect)\n"+
            "                                                                FontWeight.Normal\n"+
            "                                                            else\n"+
            "                                                                FontWeight.Bold\n"+
            "                                                        )\n"+
            "                                                    )\n"+
            "\n"+
            "                                                } else {\n"+
            "\n"+
            "                                                    Text(\n"+
            "                                                        text = \"Lỗi khi tải nội dung từ URL, kiểm tra kết nối mạng!\",\n"+
            "                                                        color = questionErrorColor,\n"+
            "                                                        style = MaterialTheme.typography.bodyLarge.copy(\n"+
            "                                                            fontWeight = if (result.isCorrect)\n"+
            "                                                                FontWeight.Normal\n"+
            "                                                            else\n"+
            "                                                                FontWeight.Bold\n"+
            "                                                        ),\n"+
            "                                                        textAlign = TextAlign.Center\n"+
            "                                                    )\n"+
            "                                                }\n"+
            "                                            }\n"+
            "\n"+
            "                                            // ==============================\n"+
            "                                            // 5. IMAGE URL\n"+
            "                                            // ==============================\n"+
            "                                            answer.text.startsWith(\"URLIMA\") -> {\n"+
            "\n"+
            "                                                val imageUrl = answer.text\n"+
            "                                                    .removePrefix(\"URLIMA\")\n"+
            "\n"+
            "                                                ImageFromUrl(\n"+
            "                                                    imageUrl = imageUrl,\n"+
            "                                                    contentDescription = \"Hình ảnh từ URL\",\n"+
            "                                                    modifier = Modifier\n"+
            "                                                        .background(backgroundColor)\n"+
            "                                                        .fillMaxWidth()\n"+
            "                                                )\n"+
            "                                            }\n"+
            "\n"+
            "                                            // ==============================\n"+
            "                                            // 7. DỮ LIỆU KHÔNG CÓ TIỀN TỐ\n"+
            "                                            // ==============================\n"+
            "                                            else -> {\n"+
            "\n"+
            "                                                Text(\n"+
            "                                                    text = answer.text,\n"+
            "                                                    textAlign = TextAlign.Justify,\n"+
            "                                                    modifier = Modifier\n"+
            "                                                        .background(Color.LightGray),\n"+
            "                                                    style = MaterialTheme.typography.bodyLarge.copy(\n"+
            "                                                        fontWeight = if (result.isCorrect)\n"+
            "                                                            FontWeight.Normal\n"+
            "                                                        else\n"+
            "                                                            FontWeight.Bold\n"+
            "                                                    )\n"+
            "                                                )\n"+
            "                                            }\n"+
            "                                        }\n"+
            "                                    }\n"+
            "                                    else -> {\n"+
            "                                        // viết như vậy cho gọn\n"+
            "                                        Text(\n"+
            "                                            text = \"Logic đã bị lỗi, báo cáo Zalo 0972712415 để khắc phục nhé\",\n"+
            "                                            style = MaterialTheme.typography.bodyMedium,\n"+
            "                                            color = Color.Gray\n"+
            "                                        )\n"+
            "                                    }\n"+
            "                                }\n"+
            "                            }\n"+
            "                        }\n"+
            "                    } else if (result.correctTextAnswers.isNotEmpty()) {\n"+
            "\n"+
            "                        // hiện đáp án và tính đúng sai của câu tự luận\n"+
            "                        val userInput = result.userInputText\n"+
            "                        val isCorrect = result.isCorrect\n"+
            "                        if (isCorrect) {\n"+
            "                            // ==========================================\n"+
            "                            // TRƯỜNG HỢP ĐÚNG: Chỉ hiện đáp án của người dùng\n"+
            "                            // ==========================================\n"+
            "                            Surface(\n"+
            "                                color = Color(0xFF1565C0), // Xanh dương đậm\n"+
            "                                shape = RoundedCornerShape(8.dp),\n"+
            "                                modifier = Modifier\n"+
            "                                    .fillMaxWidth()\n"+
            "                                    .padding(vertical = 4.dp)\n"+
            "                            ) {\n"+
            "                                Row(\n"+
            "                                    modifier = Modifier\n"+
            "                                        .fillMaxWidth()\n"+
            "                                        .padding(vertical = 2.dp)\n"+
            "                                        //.background(backgroundColor)\n"+
            "                                        .padding(horizontal = 8.dp, vertical = 8.dp),\n"+
            "                                    verticalAlignment = Alignment.CenterVertically\n"+
            "                                ) {\n"+
            "\n"+
            "                                    // Biểu tượng trạng thái\n"+
            "                                    Text(\n"+
            "                                        text = \"✓\",\n"+
            "                                        color = Color.White,\n"+
            "                                        fontWeight = FontWeight.Bold,\n"+
            "                                        modifier = Modifier.padding(end = 8.dp)\n"+
            "                                    )\n"+
            "                                    Text(\n"+
            "                                        text = \"Đáp án của bạn: \$userInput\",\n"+
            "                                        style = MaterialTheme.typography.bodyLarge.copy(\n"+
            "                                            color = Color.White\n"+
            "                                        ),\n"+
            "                                        modifier = Modifier.padding(12.dp)\n"+
            "                                    )\n"+
            "                                }\n"+
            "                            }\n"+
            "                        } else {\n"+
            "                            // ==========================================\n"+
            "                            // TRƯỜNG HỢP SAI: Hiện đáp án người chơi VÀ CHỈ 1 GỢI Ý ĐÁP ÁN ĐÚNG NGẪU NHIÊN\n"+
            "                            // ==========================================\n"+
            "                            // Dùng remember(result) để khi recompose không bị đổi ngẫu nhiên liên tục\n"+
            "                            val randomCorrect = remember(result) { // nếu ưu tiên lấy đáp án đầu tiên thì đổi thành firstCorrect\n"+
            "                                result.correctTextAnswers.randomOrNull() // nếu ưu tiên lấy đáp án đầu tiên thì lấy firstOrNull()\n"+
            "                            }\n"+
            "\n"+
            "                            if (randomCorrect != null) {\n"+
            "                                val backgroundColor = Color(0xFFA04B00) // Nâu xám\n"+
            "\n"+
            "                                Surface(\n"+
            "                                    color = backgroundColor,\n"+
            "                                    shape = RoundedCornerShape(8.dp),\n"+
            "                                    modifier = Modifier\n"+
            "                                        .fillMaxWidth()\n"+
            "                                        .padding(vertical = 4.dp)\n"+
            "                                ) {\n"+
            "                                    Row(\n"+
            "                                        modifier = Modifier\n"+
            "                                            .fillMaxWidth()\n"+
            "                                            .padding(vertical = 2.dp)\n"+
            "                                            //.background(backgroundColor)\n"+
            "                                            .padding(horizontal = 8.dp, vertical = 8.dp),\n"+
            "                                        verticalAlignment = Alignment.CenterVertically\n"+
            "                                    ) {\n"+
            "\n"+
            "                                        // Biểu tượng trạng thái\n"+
            "                                        Text(\n"+
            "                                            text = \"✕\",\n"+
            "                                            color = Color.White,\n"+
            "                                            fontWeight = FontWeight.Bold,\n"+
            "                                            modifier = Modifier.padding(end = 8.dp)\n"+
            "                                        )\n"+
            "                                        when (randomCorrect) {\n"+
            "                                            is AnswerOption.Text -> {\n"+
            "                                                when {\n"+
            "                                                    // 1. TEXT THƯỜNG (có tiền tố TEXTXT)\n"+
            "                                                    randomCorrect.text.startsWith(\"TEXTXT\") -> {\n"+
            "                                                        val text =\n"+
            "                                                            randomCorrect.text.removePrefix(\"TEXTXT\")\n"+
            "                                                        Text(\n"+
            "                                                            text = \"Bạn điền: '\$userInput' (Gợi ý đáp án đúng: \${text})\",\n"+
            "                                                            style = MaterialTheme.typography.bodyLarge.copy(\n"+
            "                                                                color = Color.White\n"+
            "                                                            ),\n"+
            "                                                            textAlign = TextAlign.Justify,\n"+
            "                                                            modifier = Modifier.padding(12.dp)\n"+
            "                                                        )\n"+
            "                                                    }\n"+
            "\n"+
            "                                                    // 2. TEXT RESOURCE (có tiền tố RESTEX)\n"+
            "                                                    randomCorrect.text.startsWith(\"RESTEX\") -> {\n"+
            "                                                        val textId = randomCorrect.text.removePrefix(\"RESTEX\")\n"+
            "                                                        val stringRes = Res.allStringResources[textId]\n"+
            "                                                        if (stringRes != null) {\n"+
            "                                                            Text(\n"+
            "                                                                text = \"Bạn điền: '\$userInput' (Gợi ý đáp án đúng: \${\n                                                                    org.jetbrains.compose.resources.stringResource(stringRes)\n                                                                })\",\n"+
            "                                                                style = MaterialTheme.typography.bodyLarge.copy(\n"+
            "                                                                    color = Color.White\n"+
            "                                                                ),\n"+
            "                                                                textAlign = TextAlign.Justify,\n"+
            "                                                                modifier = Modifier.padding(12.dp)\n"+
            "                                                            )\n"+
            "                                                        }\n"+
            "                                                    }\n"+
            "\n"+
            "                                                    // 3. TEXT URL (có tiền tố URLTEX)\n"+
            "                                                    randomCorrect.text.startsWith(\"URLTEX\") -> {\n"+
            "                                                        val textUrl =\n"+
            "                                                            randomCorrect.text.removePrefix(\"URLTEX\")\n"+
            "                                                        var textContent by remember(textUrl) {\n"+
            "                                                            mutableStateOf(\n"+
            "                                                                \"Đang tải...\"\n"+
            "                                                            )\n"+
            "                                                        }\n"+
            "                                                        var isError by remember(textUrl) {\n"+
            "                                                            mutableStateOf(\n"+
            "                                                                false\n"+
            "                                                            )\n"+
            "                                                        }\n"+
            "\n"+
            "                                                        LaunchedEffect(textUrl) {\n"+
            "                                                            try {\n"+
            "                                                                textContent =\n"+
            "                                                                    fetchTextFromUrl(textUrl)\n"+
            "                                                                isError = false\n"+
            "                                                            } catch (e: Exception) {\n"+
            "                                                                textContent =\n"+
            "                                                                    \"Không thể tải nội dung\"\n"+
            "                                                                isError = true\n"+
            "                                                            }\n"+
            "                                                        }\n"+
            "\n"+
            "                                                        if (!isError) {\n"+
            "                                                            Text(\n"+
            "                                                                text = \"Bạn điền: '\$userInput' (Gợi ý đáp án đúng: \${textContent})\",\n"+
            "                                                                style = MaterialTheme.typography.bodyLarge.copy(\n"+
            "                                                                    color = Color.White\n"+
            "                                                                ),\n"+
            "                                                                textAlign = TextAlign.Justify,\n"+
            "                                                                modifier = Modifier.padding(12.dp)\n"+
            "                                                            )\n"+
            "                                                        } else {\n"+
            "                                                            Text(\n"+
            "                                                                text = \"Bạn điền: '\$userInput' (Lỗi tải nội dung từ URL)\",\n"+
            "                                                                color = Color.Red,\n"+
            "                                                                style = MaterialTheme.typography.bodyLarge,\n"+
            "                                                                modifier = Modifier.padding(12.dp)\n"+
            "                                                            )\n"+
            "                                                        }\n"+
            "                                                    }\n"+
            "\n"+
            "                                                    // 4. MẶC ĐỊNH KHÔNG CÓ TIỀN TỐ\n"+
            "                                                    else -> {\n"+
            "                                                        Text(\n" +
            "                                                            text = \"Bạn điền: '\$userInput' (Gợi ý đáp án đúng: \${randomCorrect.text})\",\n" +
            "                                                            style = MaterialTheme.typography.bodyLarge.copy(\n" +
            "                                                                color = Color.White\n" +
            "                                                            ),\n" +
            "                                                            modifier = Modifier.padding(12.dp)\n" +
            "                                                        )\n" +
            "                                                    }\n" +
            "                                                }\n" +
            "                                            }\n" +
            "\n" +
            "                                            else -> {\n" +
            "                                                Row(\n" +
            "                                                    modifier = Modifier\n" +
            "                                                        .fillMaxWidth()\n" +
            "                                                        .padding(vertical = 2.dp)\n" +
            "                                                        .background(backgroundColor)\n" +
            "                                                        .padding(\n" +
            "                                                            horizontal = 8.dp,\n" +
            "                                                            vertical = 8.dp\n" +
            "                                                        ),\n" +
            "                                                    verticalAlignment = Alignment.CenterVertically\n" +
            "                                                ) {\n" +
            "\n" +
            "                                                    // Biểu tượng trạng thái\n" +
            "                                                    Text(\n" +
            "                                                        text = \"✕\",\n" +
            "                                                        color = Color.White,\n" +
            "                                                        fontWeight = FontWeight.Bold,\n" +
            "                                                        modifier = Modifier.padding(end = 8.dp)\n" +
            "                                                    )\n" +
            "                                                    Text(\n" +
            "                                                        text = \"Dạng đáp án tự luận không hỗ trợ kiểu này.\",\n" +
            "                                                        style = MaterialTheme.typography.bodyMedium.copy(\n" +
            "                                                            color = Color.Red\n" +
            "                                                        ),\n" +
            "                                                        modifier = Modifier.padding(12.dp)\n" +
            "                                                    )\n" +
            "                                                }\n" +
            "                                            }\n" +
            "                                        }\n" +
            "                                    }\n" +
            "                                }\n" +
            "                            }\n" +
            "                        }\n" +
            "\n" +
            "                    }\n" +
            "\n" +
            "                    var showDetail by remember { mutableStateOf(false) }\n" +
            "                    if (result.detailedCorrectAnswers.isNotEmpty()) {\n" +
            "                        Column {\n" +
            "                            // Nút bấm hiển thị/ẩn đáp án chi tiết\n" +
            "                            TextButton(onClick = { showDetail = !showDetail }) {\n" +
            "                                Text(\n" +
            "                                    text = if (showDetail) \"Ẩn đáp án chi tiết\" else \"Đáp án chi tiết\",\n" +
            "                                    style = MaterialTheme.typography.bodyLarge\n" +
            "                                )\n" +
            "                            }\n" +
            "\n" +
            "                            // Hiển thị danh sách đáp án chi tiết nếu đang bật\n" +
            "                            if (showDetail) {\n" +
            "                                result.detailedCorrectAnswers.forEachIndexed { index, detailedCorrectAnswer ->\n" +
            "                                    Row(\n" +
            "                                        modifier = Modifier\n" +
            "                                            .fillMaxWidth()\n" +
            "                                            .padding(vertical = 4.dp),\n" +
            "                                        verticalAlignment = Alignment.Top\n" +
            "                                    ) {\n" +
            "                                        when (detailedCorrectAnswer) {\n" +
            "\n" +
            "                                            is AnswerOption.Text -> {\n" +
            "\n" +
            "                                                when {\n" +
            "\n" +
            "                                                    // 1. VĂN BẢN THƯỜNG\n" +
            "                                                    detailedCorrectAnswer.text.startsWith(\"TEXTXT\") -> {\n" +
            "                                                        val text = detailedCorrectAnswer.text.removePrefix(\"TEXTXT\")\n" +
            "\n" +
            "                                                        Text(\n" +
            "                                                            text = text,\n" +
            "                                                            style = MaterialTheme.typography.bodyLarge,\n" +
            "                                                            textAlign = TextAlign.Justify\n" +
            "                                                        )\n" +
            "                                                    }\n" +
            "\n" +
            "                                                    // 2. TEXT RESOURCE\n" +
            "                                                    detailedCorrectAnswer.text.startsWith(\"RESTEX\") -> {\n" +
            "                                                        val textId = detailedCorrectAnswer.text.removePrefix(\"RESTEX\")\n" +
            "\n" +
            "                                                        // Thao tác trung gian: Lấy StringResource từ Res.allStringResources dựa vào key (textId)\n" +
            "                                                        val stringRes = Res.allStringResources[textId]\n" +
            "\n" +
            "                                                        if (stringRes != null) {\n" +
            "                                                            Text(\n" +
            "                                                                text = org.jetbrains.compose.resources.stringResource(stringRes),\n" +
            "                                                                style = MaterialTheme.typography.bodyLarge\n" +
            "                                                            )\n" +
            "                                                        }\n" +
            "                                                    }\n" +
            "\n" +
            "                                                    // 3. IMAGE RESOURCE\n" +
            "                                                    detailedCorrectAnswer.text.startsWith(\"RESIMA\") -> {\n" +
            "                                                        val imageKey = detailedCorrectAnswer.text.removePrefix(\"RESIMA\")\n" +
            "                                                        val drawableRes = Res.allDrawableResources[imageKey]\n" +
            "                                                        if (drawableRes != null) {\n" +
            "                                                            Image(\n" +
            "                                                                painter = painterResource(drawableRes),\n" +
            "                                                                contentDescription = null,\n" +
            "                                                                modifier = Modifier\n" +
            "                                                                    .fillMaxWidth(),\n" +
            "                                                                contentScale = ContentScale.FillWidth\n" +
            "                                                            )\n" +
            "                                                        }\n" +
            "                                                    }\n" +
            "\n" +
            "                                                    // 4. TEXT URL\n" +
            "                                                    detailedCorrectAnswer.text.startsWith(\"URLTEX\") -> {\n" +
            "                                                        val textUrl = detailedCorrectAnswer.text\n" +
            "                                                            .removePrefix(\"URLTEX\")\n" +
            "\n" +
            "                                                        var textContent by remember {\n" +
            "                                                            mutableStateOf(\"Đang tải...\")\n" +
            "                                                        }\n" +
            "\n" +
            "                                                        var isError by remember {\n" +
            "                                                            mutableStateOf(false)\n" +
            "                                                        }\n" +
            "\n" +
            "                                                        LaunchedEffect(textUrl) {\n" +
            "                                                            try {\n" +
            "                                                                textContent = fetchTextFromUrl(textUrl)\n" +
            "                                                                isError = false\n" +
            "                                                            } catch (e: Exception) {\n" +
            "                                                                textContent = \"Không thể tải nội dung\"\n" +
            "                                                                isError = true\n" +
            "                                                            }\n" +
            "                                                        }\n" +
            "\n" +
            "                                                        if (!isError) {\n" +
            "                                                            Text(\n" +
            "                                                                text = textContent,\n" +
            "                                                                style = MaterialTheme.typography.bodyLarge,\n" +
            "                                                                textAlign = TextAlign.Justify,\n" +
            "                                                                modifier = Modifier\n" +
            "                                                                    .padding(horizontal = 4.dp)\n" +
            "                                                                    .fillMaxWidth()\n" +
            "                                                            )\n" +
            "                                                        } else {\n" +
            "                                                            Text(\n" +
            "                                                                text = \"Lỗi khi tải nội dung từ URL, kiểm tra kết nối mạng!\",\n" +
            "                                                                style = MaterialTheme.typography.bodyMedium,\n" +
            "                                                                textAlign = TextAlign.Center,\n" +
            "                                                                modifier = Modifier\n" +
            "                                                                    .padding(horizontal = 4.dp)\n" +
            "                                                                    .fillMaxWidth()\n" +
            "                                                            )\n" +
            "                                                        }\n" +
            "                                                    }\n" +
            "\n" +
            "                                                    // 5. IMAGE URL\n" +
            "                                                    detailedCorrectAnswer.text.startsWith(\"URLIMA\") -> {\n" +
            "                                                        val imageUrl = detailedCorrectAnswer.text\n" +
            "                                                            .removePrefix(\"URLIMA\")\n" +
            "\n" +
            "                                                        ImageFromUrl(\n" +
            "                                                            imageUrl = imageUrl,\n" +
            "                                                            contentDescription = \"Hình ảnh từ URL\",\n" +
            "                                                            modifier = Modifier.fillMaxWidth()\n" +
            "                                                        )\n" +
            "                                                    }\n" +
            "\n" +
            "                                                    // 6. KHÔNG XÁC ĐỊNH\n" +
            "                                                    else -> {\n" +
            "                                                        Text(\n" +
            "                                                            text = detailedCorrectAnswer.text,\n" +
            "                                                            style = MaterialTheme.typography.bodyLarge,\n" +
            "                                                            textAlign = TextAlign.Justify\n" +
            "                                                        )\n" +
            "                                                    }\n" +
            "                                                }\n" +
            "                                            }\n" +
            "                                            else -> {}\n" +
            "                                        }\n" +
            "                                    }\n" +
            "                                }\n" +
            "                            }\n" +
            "                        }\n" +
            "                    }\n" +
            "                }\n" +
            "            }\n" +
            "        }\n" +
            "    }\n" +
            "    // Hiển thị nút khi cuộn lên (isToolbarVisible == true)\n" +
            "    if (isToolbarVisible.value) {\n" +
            "        Box(\n" +
            "            modifier = Modifier\n" +
            "                .padding(bottom = bottomPadding)\n" +
            "                .fillMaxSize()\n" +
            "        ) {\n" +
            "            Box (\n" +
            "                modifier = Modifier\n" +
            "                    .fillMaxWidth()\n" +
            "                    .background(Color.White)\n" +
            "                    .padding(horizontal = 0.dp)\n" +
            "                    .align(Alignment.BottomCenter),\n" +
            "            ) {\n" +
            "                Button(\n" +
            "                    onClick = {\n" +
            "                        result?.quizName?.let { quizName ->\n" +
            "                            onBackTest(quizName)\n" +
            "                        }\n" +
            "                    },\n" +
            "                    modifier = Modifier\n" +
            "                        .align(Alignment.BottomStart)\n" +
            "                        .wrapContentSize(),\n" +
            "                    contentPadding = PaddingValues(0.dp), // Loại bỏ padding mặc định\n" +
            "                    shape = RoundedCornerShape(4.dp), // Bo góc nhẹ\n" +
            "                    colors = ButtonDefaults.buttonColors(\n" +
            "                        containerColor = Color.LightGray.copy(0.3f), // Màu nền của nút\n" +
            "                        contentColor = Color.Black // Màu của icon và chữ\n" +
            "                    ),\n" +
            "                ) {\n" +
            "                    Column (horizontalAlignment = Alignment.CenterHorizontally) {\n" +
            "                        Icon(imageVector = Icons.Outlined.Edit, contentDescription = \"Thi lại\", modifier = Modifier.size(20.dp))\n" +
            "                        Text(text = \"Thi lại\", fontSize = 10.sp, fontWeight = FontWeight.Light)\n" +
            "                    }\n" +
            "                }\n" +
            "                Button(\n" +
            "                    onClick = { onHomePage() },\n" +
            "                    modifier = Modifier\n" +
            "                        .align(Alignment.BottomCenter)\n" +
            "                        .wrapContentSize(),\n" +
            "                    contentPadding = PaddingValues(0.dp), // Loại bỏ padding mặc định\n" +
            "                    shape = RoundedCornerShape(4.dp), // Bo góc nhẹ\n" +
            "                    colors = ButtonDefaults.buttonColors(\n" +
            "                        containerColor = Color.LightGray.copy(0.3f), // Màu nền của nút\n" +
            "                        contentColor = Color.Black // Màu của icon và chữ\n" +
            "                    ),\n" +
            "                ) {\n" +
            "                    Column (horizontalAlignment = Alignment.CenterHorizontally) {\n" +
            "                        Icon(imageVector = Icons.Outlined.Home, contentDescription = \"Trang đầu\", modifier = Modifier.size(20.dp))\n" +
            "                        Text(text = \"Trang đầu\", fontSize = 10.sp, fontWeight = FontWeight.Light,\n" +
            "                            color = Color.Black)\n" +
            "                    }\n" +
            "                }\n" +
            "                Button(\n" +
            "                    onClick = { onBack() },\n" +
            "                    modifier = Modifier\n" +
            "                        .align(Alignment.BottomEnd)\n" +
            "                        .wrapContentSize(),\n" +
            "                    colors = ButtonDefaults.buttonColors(\n" +
            "                        containerColor = Color.LightGray.copy(0.3f), // Màu nền của nút\n" +
            "                        contentColor = Color.Black // Màu của icon và chữ\n" +
            "                    ),\n" +
            "                ) {\n" +
            "                    Icon(\n" +
            "                        imageVector = Icons.AutoMirrored.Filled.ArrowBack, // Sử dụng phiên bản AutoMirrored\n" +
            "                        contentDescription = \"Quay lại\"\n" +
            "                    )\n" +
            "                }\n" +
            "            }\n" +
            "        }\n" +
            "    }\n" +
            "}\n" +
            "// Các hàm phụ và hoàn toàn không liên quan đến việc hiển thị màn hình và điều hướng\n" +
            "@Composable\n" +
            "fun VideoPlayerFromUrl(videoUrl: String, modifier: Modifier = Modifier) {\n" +
            "    PlatformVideoPlayer(videoUrl = videoUrl, modifier = modifier)\n" +
            "}\n" +
            "\n" +
            "// Hàm chuẩn hóa văeeeeeeeeeiiiiiooooooooooooooooouuuuuuuuuuuyyyyyyd"
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


class StudentSubmissionsRepository(
    private val client: HttpClient
) {
    companion object {
        private const val SCRIPT_URL =
            "https://script.google.com/macros/s/AKfycbwPaTel2KIAi1cOFsEWdeRkRzW6CnGB61yY81_5_XWg9yDZhidkde4YnsNaKNk2E5-yAg/exec"
        //   https://script.google.com/macros/s/AKfycbwPaTel2KIAi1cOFsEWdeRkRzW6CnGB61yY81_5_XWg9yDZhidkde4YnsNaKNk2E5-yAg/exec
    }

    private val json = Json {
        ignoreUnknownKeys = true
    }


    private fun buildUrl(
        action: String,
        parameterName: String? = null,
        parameterValue: String? = null
    ): String {
        return URLBuilder(SCRIPT_URL).apply {
            parameters.append("action", action)

            if (parameterName != null && parameterValue != null) {
                parameters.append(parameterName, parameterValue)
            }
        }.buildString()
    }

    suspend fun getStudents(): DriveStudentsResponse {
        val response = client.get(
            buildUrl(action = "getStudents")
        ).bodyAsText()

        return json.decodeFromString<DriveStudentsResponse>(response)
    }

    suspend fun getStudentFiles(
        folderId: String
    ): DriveFilesResponse {
        val response = client.get(
            buildUrl(
                action = "getStudentFiles",
                parameterName = "folderId",
                parameterValue = folderId
            )
        ).bodyAsText()

        return json.decodeFromString<DriveFilesResponse>(response)
    }

    suspend fun getFileContent(
        fileId: String
    ): DriveFileContentResponse {
        val response = client.get(
            buildUrl(
                action = "getFileContent",
                parameterName = "fileId",
                parameterValue = fileId
            )
        ).bodyAsText()

        return json.decodeFromString<DriveFileContentResponse>(response)
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
            .nestedScroll(nestedScrollConnection) // Theo dõi sự kiện cuộn


    ) {
        // Sub-header displaying quiz name and score
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
                    val questionBackground = Color(0xFFE8F1F8)
                    val questionTextColor = Color(0xFF172B3A)

                    Text(
                        text = "Câu ${index + 1}",
                        color = questionTitleColor,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )

                    // Render main question
                    RenderQuestionContent(content = result.mainQuestion)

                    // Render extra questions
                    result.extraQuestions.forEach { extra ->
                        RenderQuestionContent(content = extra)
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Multiple choice answers
                    if (result.answers.isNotEmpty()) {
                        val selectedAnswer = result.selectedAnswer
                        val correctAnswer = result.correctAnswer

                        result.answers.forEach { answer ->
                            val isCorrectAnswer = answer == correctAnswer
                            val isSelected = answer == selectedAnswer

                            val backgroundColor = when {
                                isSelected && isCorrectAnswer -> Color(0xFF1565C0) // Xanh dương đậm
                                isSelected && !isCorrectAnswer -> Color(0xFFA04B00) // Cam/nâu đậm
                                !isSelected && isCorrectAnswer -> Color(0xFFD6E8FF) // Xanh dương nhạt
                                else -> Color(0xFFF2F2F2)
                            }

                            val textColor = when {
                                isSelected -> Color.White
                                !isSelected && isCorrectAnswer -> Color(0xFF0D47A1)
                                else -> Color(0xFF333333)
                            }

                            val statusIcon = when {
                                isSelected && isCorrectAnswer -> "✓"
                                isSelected && !isCorrectAnswer -> "✕"
                                !isSelected && isCorrectAnswer -> "✓"
                                else -> "—"
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp)
                                    .background(backgroundColor, shape = RoundedCornerShape(6.dp))
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = statusIcon,
                                    color = textColor,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(end = 8.dp)
                                )

                                RenderAnswerOptionContent(
                                    answer = answer,
                                    textColor = textColor
                                )
                            }
                        }
                    }
                    // Essay question
                    else if (result.correctTextAnswers.isNotEmpty() || !result.userInputText.isNullOrBlank()) {
                        val userInput = result.userInputText ?: ""
                        val isCorrect = result.isCorrect

                        if (isCorrect) {
                            Surface(
                                color = Color(0xFF1565C0),
                                shape = RoundedCornerShape(8.dp),
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
                                        text = "✓",
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
                                        text = "✕",
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
                                                    textContent = fetchTextFromUrl(randomCorrect.textUrl)
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

                    // Detailed answer explanation
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

@Composable
fun StudentSubmissionsScreen(
    onBack: () -> Unit
) {
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
    // Tạo HTTP Client dùng chung cho màn hình.
    val httpClient = remember {
        HttpClient()
    }

    DisposableEffect(httpClient) {
        onDispose {
            httpClient.close()
        }
    }

    val repository = remember(httpClient) {
        StudentSubmissionsRepository(httpClient)
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

    // Bước 1: Tải danh sách học sinh.
    LaunchedEffect(studentsRetry) {
        isStudentsLoading = true
        errorMessage = null

        try {
            // withContext(Dispatchers.Main) ép iOS phải chạy trên Main Thread để khỏi bị đá văng
            val response = withContext(Dispatchers.Main) {
                repository.getStudents()
            }

            if (response.status == "success") {
                studentsList = response.students
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

    // Bước 2: Hiển thị/tải danh sách file khi chọn một học sinh.
    LaunchedEffect(
        selectedStudent,
        filesRetry
    ) {
        val student = selectedStudent ?: return@LaunchedEffect

        // Nếu danh sách file đã có sẵn trong object student (từ API getStudents)
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

    // Bước 3: Hiển thị/tải nội dung khi chọn một file.
    LaunchedEffect(
        selectedFile,
        contentRetry
    ) {
        val file = selectedFile ?: return@LaunchedEffect

        // Nếu nội dung file đã có sẵn
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

    // Giữ cách tính padding của màn hình cũ.
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
            .nestedScroll(nestedScrollConnection) // Theo dõi sự kiện cuộn
            .padding(
                top = topPadding,
                bottom = bottomPadding
            )
            .background(Color(0xFFF7F9FC))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(nestedScrollConnection) // Theo dõi sự kiện cuộn
                .padding(vertical = 4.dp)
        ) {

            if (isToolbarVisible.value) {// Thanh tiêu đề.
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

            // Trạng thái tải dữ liệu.
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (errorMessage != null) {

                // Hiển thị lỗi và nút thử lại.
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

                    // --------------------------------
                    // MÀN HÌNH 3: NỘI DUNG FILE
                    // --------------------------------
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
                                    modifier = Modifier
                                        .fillMaxSize()
                                        //.padding(16.dp)
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

                    // --------------------------------
                    // MÀN HÌNH 2: DANH SÁCH FILE
                    // --------------------------------
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
                                items(
                                    items = filesList,
                                    key = { it.fileId.ifEmpty { it.fileName } }
                                ) { file ->

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

                    // --------------------------------
                    // MÀN HÌNH 1: DANH SÁCH HỌC SINH
                    // --------------------------------
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
                                items(
                                    items = studentsList,
                                    key = { it.phone.ifEmpty { it.folderId } }
                                ) { student ->

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
