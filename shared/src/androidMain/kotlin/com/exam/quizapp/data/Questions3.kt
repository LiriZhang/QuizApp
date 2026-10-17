package com.exam.quizapp.data

import com.exam.quizapp.model.*
import com.exam.quizapp.shared.resources.Res
import com.exam.quizapp.shared.resources.*

actual val quizQuestions3: Map<String, List<Question>> by lazy {
    mapOf(
        "Thử nghiệm 01" to listOf(
            Question(
                mainContent = QuestionContent.ClipUrl("https://s3.ap-southeast-1.amazonaws.com/com.exam.quizapp/I+%E1%BB%91t+th%C4%83ng+hoa.mp4"),
                //correctAnswer = 3 // Đáp án đúng là D
                extraContents = listOf(
                    QuestionContent.TextRes(Res.string.mau_1),
                    QuestionContent.ImageRes(Res.drawable.anhmau)
                ),
                correctTextAnswers = listOf(
                    AnswerOption.Text("Hè"),
                    AnswerOption.TextUrl("https://s3.ap-southeast-1.amazonaws.com/com.exam.quizapp/TXTTH/NH6C1.txt"),
                    AnswerOption.TextRes(Res.string.mau_1),
                    AnswerOption.ImageRes(Res.drawable.anhmau)
                ),
                detailedCorrectAnswers = listOf(
                    AnswerOption.TextRes(Res.string.mau_1),
                    AnswerOption.ImageRes(Res.drawable.anhmau),
                    AnswerOption.Text("Thu"),
                    AnswerOption.Text("Đông"),
                    AnswerOption.Text("Xuân"),
                )
            ),
            Question(
                mainContent = QuestionContent.TextUrl("https://s3.ap-southeast-1.amazonaws.com/com.exam.quizapp/TXTTH/NH6C2.txt"),
                answers = listOf(
                    AnswerOption.Text("Xuân"),
                    AnswerOption.TextRes(Res.string.mau_1),
                    AnswerOption.ImageRes(Res.drawable.anhmau),
                    AnswerOption.Text("Đông")
                ),
                correctAnswer = 1,
                detailedCorrectAnswers = listOf(
                    AnswerOption.Text("Xuân hè thu"),
                    AnswerOption.TextRes(Res.string.mau_1),
                    AnswerOption.ImageRes(Res.drawable.anhmau),

                )// Đáp án đúng là B
            ),
            Question(
                mainContent = QuestionContent.TextUrl("https://s3.ap-southeast-1.amazonaws.com/com.exam.quizapp/TXTTH/NH6C1.txt"),
                //correctAnswer = 3 // Đáp án đúng là D
                extraContents = listOf(
                    QuestionContent.Text("Các chất rắn khác nhau nở vì nhiệt khác nhau được ứng dụng, để tạo ra"),
                   QuestionContent.ImageRes(Res.drawable.anhmau),
                ),
                correctTextAnswers = listOf(
                    AnswerOption.TextUrl("https://s3.ap-southeast-1.amazonaws.com/com.exam.quizapp/TXTTH/NH6C1.txt"),
                    AnswerOption.Text("Hè"),
                    ),
                detailedCorrectAnswers = listOf(
                    AnswerOption.Text("Hè"),
                    AnswerOption.Text("Thu"),
                    AnswerOption.Text("Đông"),
                    AnswerOption.Text("你好"),
                    AnswerOption.ImageRes(Res.drawable.anhmau),
                )
            ),

            Question(
                mainContent = QuestionContent.TextUrl("https://s3.ap-southeast-1.amazonaws.com/com.exam.quizapp/TXTTH/NH6C2.txt"),
                answers = listOf(
                    AnswerOption.Text("Xuân"),
                    AnswerOption.TextRes(Res.string.mau_1),
                    AnswerOption.Text("Thu"),
                    AnswerOption.Text("Đông")
                ),
                correctAnswer = 1 // Đáp án đúng là B
            ),
            Question(
                mainContent = QuestionContent.TextRes(Res.string.mau_1),
                answers = listOf(
                    AnswerOption.Text("Nhiệt kế y tế"),
                    AnswerOption.ImageRes(Res.drawable.anhmau),
                ),
                correctAnswer = 1 // Đáp án đúng là B
            ),
            Question(
                mainContent = QuestionContent.ClipUrl("https://drive.google.com/uc?export=download&id=1NV5HHPo4MYMiGf7fXcICKTuWSBX-msw7"),
                //correctAnswer = 3 // Đáp án đúng là D
                answers = listOf(
                    AnswerOption.Text("Nhiệt kế y tế"),
                    AnswerOption.Text("Nhiệt | kế, lưỡng; kim"),
                    AnswerOption.Text("Nhiệt kế thủy ngân"),
                    AnswerOption.Text("Nhiệt kế rượu")
                ),
                correctAnswer = 1 // Đáp án đúng là B
            ),
            Question(
                mainContent = QuestionContent.AudioUrl("https://drive.google.com/uc?export=download&id=1fhUf0joDFAiGtg2ou3Mks0aD_6YQiHm4"),
                //correctAnswer = 3 // Đáp án đúng là D
                answers = listOf(
                    AnswerOption.Text("Nhiệt kế y tế"),
                    AnswerOption.Text("Nhiệt kế, lưỡng; kim"),
                    AnswerOption.Text("Nhiệt kế thủy ngân"),
                    AnswerOption.Text("Nhiệt kế rượu")
                ),
                correctAnswer = 1 // Đáp án đúng là B
            ),
        ),
    )
}
