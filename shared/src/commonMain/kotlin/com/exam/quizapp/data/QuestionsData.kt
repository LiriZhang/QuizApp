package com.exam.quizapp.data

import com.exam.quizapp.model.Question
import com.exam.quizapp.model.*
import com.exam.quizapp.shared.resources.Res
import com.exam.quizapp.shared.resources.*


expect val quizQuestions1: Map<String, List<Question>>
expect val quizQuestions2: Map<String, List<Question>>
val quizQuestions3: Map<String, List<Question>> by lazy {
    mapOf(
        "Thử nghiệm 01" to listOf(
            Question(
                mainContent = QuestionContent.Text(" câu hỏi thuần văn bản Nhiệt kế y tế"),
                //correctAnswer = 3 // Đáp án đúng là D
                answers = listOf(
                    AnswerOption.Text("Nhiệt kế y tế"),
                    AnswerOption.Text("Nhiệt kế, lưỡng; kim"),
                    AnswerOption.Text("Nhiệt kế thủy ngân"),
                    AnswerOption.Text("Nhiệt kế rượu")
                ),
                correctAnswer = 1 // Đáp án đúng là B
            ),
            Question(
                mainContent = QuestionContent.TextRes(Res.string.mau_1),
                //correctAnswer = 3 // Đáp án đúng là D
                answers = listOf(
                    AnswerOption.Text("câu này có câu hỏi là tài nguyên chuỗi, cầu trả lời có hình ảnh và cả chuỗi"),
                    AnswerOption.Text("Nhiệt kế, lưỡng; kim"),
                    AnswerOption.TextRes(Res.string.mau_1),
                    AnswerOption.ImageRes(Res.drawable.anhmau)
                ),
                correctAnswer = 1 // Đáp án đúng là B
            ),
            Question( // câu 3
                mainContent = QuestionContent.TextUrl("https://s3.ap-southeast-1.amazonaws.com/com.exam.quizapp/TXTTH/NH6C1.txt"),
                //correctAnswer = 3 // Đáp án đúng là D
                extraContents = listOf(
                    QuestionContent.TextUrl("https://s3.ap-southeast-1.amazonaws.com/com.exam.quizapp/TXTTH/NH6C1.txt"),
                    QuestionContent.Text("Xuân trên là texturl nhé"),

                    QuestionContent.TextRes(Res.string.mau_1),
                    QuestionContent.ImageRes(Res.drawable.anhmau),
                ),
                correctTextAnswers = listOf(
                    AnswerOption.Text("Thu"),
                    AnswerOption.TextRes(Res.string.mau_2),

                    ),
                detailedCorrectAnswers = listOf(
                    AnswerOption.TextRes(Res.string.mau_1),
                    AnswerOption.ImageRes(Res.drawable.anhmau),
                    AnswerOption.Text("Thu"),
                    AnswerOption.Text("Đông"),
                    AnswerOption.Text("Xuân"),
                )
            ),
            Question( // câu 4
                mainContent = QuestionContent.TextUrl("https://s3.ap-southeast-1.amazonaws.com/com.exam.quizapp/TXTTH/NH6C1.txt"),
                //correctAnswer = 3 // Đáp án đúng là D
                extraContents = listOf(
                    QuestionContent.TextUrl("https://s3.ap-southeast-1.amazonaws.com/com.exam.quizapp/TXTTH/NH6C1.txt"),
                    QuestionContent.Text("Xuân trên là texturl nhé"),

                    QuestionContent.TextRes(Res.string.mau_1),
                    QuestionContent.ImageRes(Res.drawable.anhmau),
                ),
                correctTextAnswers = listOf(
                    AnswerOption.Text("Thu"),
                    AnswerOption.TextRes(Res.string.mau_2),

                    ),
                detailedCorrectAnswers = listOf(
                    AnswerOption.TextRes(Res.string.mau_1),
                    AnswerOption.ImageRes(Res.drawable.anhmau),
                    AnswerOption.Text("Thu"),
                    AnswerOption.Text("Đông"),
                    AnswerOption.Text("Xuân"),
                )
            ),
            Question( // câu 5
                mainContent = QuestionContent.ClipUrl("https://s3.ap-southeast-1.amazonaws.com/com.exam.quizapp/I+%E1%BB%91t+th%C4%83ng+hoa.mp4"),
                answers = listOf(
                    AnswerOption.Text("Xuân trên là texturl nhé"),
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
            Question(// câu 6
                mainContent = QuestionContent.TextUrl("https://s3.ap-southeast-1.amazonaws.com/com.exam.quizapp/TXTTH/NH6C1.txt"),
                //correctAnswer = 3 // Đáp án đúng là D
                extraContents = listOf(
                    QuestionContent.AudioUrl("https://drive.google.com/uc?export=download&id=1fhUf0joDFAiGtg2ou3Mks0aD_6YQiHm4"),
                    QuestionContent.ImageRes(Res.drawable.anhmau),
                ),
                correctTextAnswers = listOf(
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
            Question( // câu 7
                mainContent = QuestionContent.TextUrl("https://s3.ap-southeast-1.amazonaws.com/com.exam.quizapp/TXTTH/NH6C2.txt"),
                answers = listOf(
                    AnswerOption.Text("Xuân"),
                    AnswerOption.TextRes(Res.string.mau_1),
                    AnswerOption.Text("Thu"),
                    AnswerOption.Text("Đông")
                ),
                correctAnswer = 1 // Đáp án đúng là B
            ),
            Question(//câu 9
                mainContent = QuestionContent.ClipUrl("https://drive.google.com/uc?export=download&id=1NV5HHPo4MYMiGf7fXcICKTuWSBX-msw7"),
                //correctAnswer = 3 // Đáp án đúng là D
                answers = listOf(
                    AnswerOption.Text("Nhiệt kế y tế"),
                    AnswerOption.Text("Nhiệt kế, lưỡng; kim"),
                    AnswerOption.Text("Nhiệt kế thủy ngân"),
                    AnswerOption.Text("Nhiệt kế rượu")
                ),
                correctAnswer = 1 // Đáp án đúng là B
            ),
            Question(//câu10
                mainContent = QuestionContent.AudioUrl("https://drive.google.com/uc?export=download&id=1fhUf0joDFAiGtg2ou3Mks0aD_6YQiHm4"),
                //correctAnswer = 3 // Đáp án đúng là D
                answers = listOf(
                    AnswerOption.Text("Nhiệt kế y tế"),
                    AnswerOption.Text("Nhiệt kế, lưỡng; kim"),
                    AnswerOption.Text("Nhiệt kế thủy ngân"),
                    AnswerOption.Text("Nhiệt kế rượu"),
                    AnswerOption.ImageUrl("https://lirizhang.github.io/C17.jpg")
                ),
                correctAnswer = 1 // Đáp án đúng là B
            ),
            Question( //câu 8
                mainContent = QuestionContent.TextRes(Res.string.mau_1),
                answers = listOf(
                    AnswerOption.Text("Nhiệt kế y tế"),
                    AnswerOption.ImageRes(Res.drawable.anhmau),
                    AnswerOption.ImageUrl("https://lirizhang.github.io/C17.jpg"),
                ),
                correctAnswer = 1 // Đáp án đúng là B
            ),
        ),
        "Thử nghiệm 02" to listOf(
            Question( // video ok nhé
                mainContent = QuestionContent.ClipUrl("https://s3.ap-southeast-1.amazonaws.com/com.exam.quizapp/I+%E1%BB%91t+th%C4%83ng+hoa.mp4"),
                answers = listOf(
                    AnswerOption.Text("Xuân trên là texturl nhé"),
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
            )
        ),
        "Thử nghiệm 03" to listOf( //audio ok nhé
            Question( // câu 4
                mainContent = QuestionContent.AudioUrl("https://drive.google.com/uc?export=download&id=1fhUf0joDFAiGtg2ou3Mks0aD_6YQiHm4"),
                answers = listOf(
                    AnswerOption.Text("Xuân trên là texturl nhé"),
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
            )
        ),
        "Thử nghiệm 04" to listOf(  // hình ảnh từ url gây ra lỗi nhé
            Question( // câu 4
                mainContent = QuestionContent.ImageUrl("https://lirizhang.github.io/C17.jpg"),
                answers = listOf(
                    AnswerOption.Text("Xuân trên là texturl nhé"),
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
            )
        ),
        "Thử nghiệm 05" to listOf(
            Question( // văn bản từ url gây ra lỗi nhé
                mainContent = QuestionContent.TextUrl("https://s3.ap-southeast-1.amazonaws.com/com.exam.quizapp/TXTTH/NH6C1.txt"),
                //correctAnswer = 3 // Đáp án đúng là D
                extraContents = listOf(
                    QuestionContent.TextUrl("https://s3.ap-southeast-1.amazonaws.com/com.exam.quizapp/TXTTH/NH6C1.txt"),
                    QuestionContent.Text("Xuân trên là texturl nhé"),

                    QuestionContent.TextRes(Res.string.mau_1),
                    QuestionContent.ImageRes(Res.drawable.anhmau),
                ),
                correctTextAnswers = listOf(
                    AnswerOption.Text("Thu"),
                    AnswerOption.TextRes(Res.string.mau_2),

                    ),
                detailedCorrectAnswers = listOf(
                    AnswerOption.TextRes(Res.string.mau_1),
                    AnswerOption.ImageRes(Res.drawable.anhmau),
                    AnswerOption.Text("Thu"),
                    AnswerOption.Text("Đông"),
                    AnswerOption.Text("Xuân"),
                )
            ),
        ),
    )
}
