package com.exam.quizapp.model

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

// Dữ liệu câu hỏi, đáp án, đáp án chi tiết đầu vào (chưa có làm bài tập)
data class Question(
    val mainContent: QuestionContent, //(bắt buộc phải có, không có không được)
    val extraContents: List<QuestionContent> = emptyList(),
    val answers: List<AnswerOption> = emptyList(), // trắc nghiệm mới có phần này
    val correctAnswer: Int? = null,
    val correctTextAnswers: List<AnswerOption> = emptyList(), // tự luận mới có, nhiều đáp án đưa ra để đối chiếu câu tự luận, trả lời ngắn
    val detailedCorrectAnswers: List<AnswerOption> = emptyList(), // Chi tiết bài giải, hướng dẫn cho người đọc biết đáp án đúng sai
)

// Phân lớp câu hỏi có dạng dữ liệu đầu vào
sealed class QuestionContent {
    data class Text(val text: String) : QuestionContent() // câu hỏi văn bản đơn thuần
    data class TextRes(val textRes: StringResource) : QuestionContent() // Dùng StringResource thay vì Int
    data class TextUrl(val textUrl: String) : QuestionContent() // câu hỏi dạng văn bản lấy từ URL
    data class ImageRes(val imageRes: DrawableResource) : QuestionContent() // Dùng DrawableResource thay vì Int
    data class ImageUrl(val imageUrl: String) : QuestionContent() // hình ảnh lấy từ đám mây
    data class AudioUrl(val audioUrl: String) : QuestionContent()
    data class ClipUrl(val clipUrl: String) : QuestionContent() // clip lưu trên đám mây
}

// Phân lớp câu trả lời có dạng dữ liệu đầu vào, không có clip url
sealed class AnswerOption {
    data class Text(val text: String) : AnswerOption()
    data class TextRes(val textRes: StringResource) : AnswerOption() // Dùng StringResource thay vì Int
    data class TextUrl(val textUrl: String) : AnswerOption()
    data class ImageUrl(val imageUrl: String) : AnswerOption()
    data class ImageRes(val imageRes: DrawableResource) : AnswerOption() // Dùng DrawableResource thay vì Int
}

// Kết quả làm bài câu hỏi trắc nghiệm sau khi làm xong
data class QuestionResult(
    val mainQuestion: QuestionContent,
    val extraQuestions: List<QuestionContent> = emptyList(), // bổ sung thêm cho câu hỏi
    val answers: List<AnswerOption> = emptyList(), // trắc nghiệm mới có phần này
    val selectedAnswer: AnswerOption? = null, // dành cho trắc nghiệm chỉ hiển thị đáp án chọn chữ không hiển thị toàn bộ đáp án
    val correctAnswer: AnswerOption? = null,
    val userInputText: String? = null, // đáp án trả lời ngắn do người dùng cung cấp với câu hỏi tự luận chứ không có trong đề ra
    val correctTextAnswers: List<AnswerOption> = emptyList(), // Dành cho tự luận thôi, trắc nghiệm không có
    val isCorrect: Boolean,
    val detailedCorrectAnswers: List<AnswerOption> = emptyList() // phần giải thích câu trả lời đúng chi tiết
)

// Lớp lưu trữ kết quả làm bài tập, đây là cách tổ chức
@Entity(tableName = "quiz_results")
data class QuizResult(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val quizName: String,
    val score: Int,
    val totalQuestions: Int,
    val history: String,
    val timestamp: Long
)

@Dao
interface QuizResultDao {
    @Insert
    suspend fun insert(quizResult: QuizResult)

    @Query("SELECT * FROM quiz_results")
    suspend fun getAllResults(): List<QuizResult>

    @Query("SELECT * FROM quiz_results WHERE id = :id")
    suspend fun getResultById(id: Int): QuizResult?

    @Query("DELETE FROM quiz_results WHERE id = :id")
    suspend fun deleteResultById(id: Int)
}