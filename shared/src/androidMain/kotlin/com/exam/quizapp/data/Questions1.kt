package com.exam.quizapp.data

import com.exam.quizapp.R
import com.exam.quizapp.model.*

actual val quizQuestions1: Map<String, List<Question>> by lazy {
    mapOf(
        /*"Ôn lại Nhiệt học 6" to listOf (
            Question(
                mainContent = QuestionContent.TextUrl("https://s3.ap-southeast-1.amazonaws.com/com.exam.quizapp/TXTTH/NH6C1.txt"),
                answers = listOf(
                    AnswerOption.TextRes(R.string.NH06_C1_0),
                    AnswerOption.TextUrl("https://s3.ap-southeast-1.amazonaws.com/com.exam.quizapp/TXTTH/NH6C1_1.txt"),
                    AnswerOption.TextUrl("https://s3.ap-southeast-1.amazonaws.com/com.exam.quizapp/TXTTH/NH6C1_2.txt"),
                    AnswerOption.TextUrl("https://s3.ap-southeast-1.amazonaws.com/com.exam.quizapp/TXTTH/NH6C1_3.txt")
                ),
                correctAnswer = 3 // Đáp án đúng là D
            ),

            Question(
                mainContent = QuestionContent.TextUrl("https://s3.ap-southeast-1.amazonaws.com/com.exam.quizapp/TXTTH/NH6C2.txt"),
                answers = listOf(
                    AnswerOption.Text("Xuân"),
                    AnswerOption.Text("Hè"),
                    AnswerOption.Text("Thu"),
                    AnswerOption.Text("Đông")
                ),
                correctAnswer = 1 // Đáp án đúng là B
            ),
            Question(
                mainContent = QuestionContent.Text("Các chất rắn khác nhau nở vì nhiệt khác nhau được ứng dụng để tạo ra"),
                answers = listOf(
                    AnswerOption.Text("Nhiệt kế y tế"),
                    AnswerOption.Text("Nhiệt kế lưỡng kim"),
                    AnswerOption.Text("Nhiệt kế thủy ngân"),
                    AnswerOption.Text("Nhiệt kế rượu")
                ),
                correctAnswer = 1 // Đáp án đúng là B
            ),
            Question(
                mainContent = QuestionContent.TextRes(R.string.NH06_C4),
                answers = listOf(
                    AnswerOption.Text("Chỉ chất rắn"),
                    AnswerOption.Text("Chỉ chất lỏng"),
                    AnswerOption.Text("Chỉ chất khí"),
                    AnswerOption.TextRes(R.string.NH06_C4_3)
                ),
                correctAnswer = 3 // Đáp án đúng là D
            ),
            Question(
                mainContent = QuestionContent.TextRes(R.string.NH06_C5),
                answers = listOf(
                    AnswerOption.Text("Rắn"),
                    AnswerOption.Text("Lỏng"),
                    AnswerOption.Text("Khí"),
                    AnswerOption.Text("Lỏng và khí")
                ),
                correctAnswer = 2 // Đáp án đúng là C
            ),
            Question(
                mainContent = QuestionContent.TextRes(R.string.NH06_C6),
                answers = listOf(
                    AnswerOption.Text("Sắt"),
                    AnswerOption.Text("Thủy ngân"),
                    AnswerOption.Text("Nhôm"),
                    AnswerOption.Text("Đồng")
                ),
                correctAnswer = 1 // Đáp án đúng là B
            ),
            Question(
                mainContent = QuestionContent.Text("Nước ở bao nhiêu °C sẽ có khối lượng riêng lớn nhất?"),
                answers = listOf(
                    AnswerOption.Text("4°C"),
                    AnswerOption.Text("– 20°C"),
                    AnswerOption.Text("0°C"),
                    AnswerOption.Text("20°C")
                ),
                correctAnswer = 0 // Đáp án đúng là A
            ),
            Question(//7
                mainContent = QuestionContent.TextRes(R.string.NH06_C8),
                answers = listOf(
                    AnswerOption.Text("Ấm nấu nước siêu tốc"),
                    AnswerOption.Text("Bàn ủi than"),
                    AnswerOption.Text("Máy sấy tóc"),
                    AnswerOption.Text("Nhiệt kế lưỡng kim")
                ),
                correctAnswer = 1 // Đáp án đúng là B
            ),
            Question(
                mainContent = QuestionContent.Text("Hiện tượng chuyển thể nào sau đây luôn xảy ra ở một khoảng nhiệt độ chứ không phải một điểm nhiệt độ nhất định."),
                answers = listOf(
                    AnswerOption.Text("Nóng chảy"),
                    AnswerOption.Text("Đông đặc"),
                    AnswerOption.Text("Bay hơi và ngưng tụ"),
                    AnswerOption.Text("Sôi")
                ),
                correctAnswer = 2 // Đáp án đúng là C
            ),
            Question(
                mainContent = QuestionContent.Text("Ở điều kiện áp suất tiêu chuẩn 1 atm thì nước …. ở nhiệt độ 100°C."),
                answers = listOf(
                    AnswerOption.Text("Tan chảy"),
                    AnswerOption.Text("Đóng băng"),
                    AnswerOption.Text("Bay hơi"),
                    AnswerOption.Text("Sôi")
                ),
                correctAnswer = 3 // Đáp án đúng là D
            ),
            // Câu 11
            Question(
                mainContent = QuestionContent.TextRes(R.string.NH06_C11),
                answers = listOf(
                    AnswerOption.Text("Độ ẩm của môi trường"),
                    AnswerOption.Text("Nhiệt độ"),
                    AnswerOption.Text("Gió"),
                    AnswerOption.Text("Diện tích mặt thoáng của nước")
                ),
                correctAnswer = 0
            ),

            // Câu 12
            Question(
                mainContent = QuestionContent.Text("CHỌN CÂU SAI. Khi nấu các món hầm thì lúc nước sôi " +
                        "người ta thường nhỏ lửa lại để nước sôi liu riu với mục đích"),
                answers = listOf(
                    AnswerOption.Text("Tiết kiệm chất đốt"),
                    AnswerOption.Text("Hạn chế nước hóa hơi"),
                    AnswerOption.Text("Nước không bị trào ra ngoài"),
                    AnswerOption.Text("Giảm nhiệt độ sôi của nước")
                ),
                correctAnswer = 3
            ),

            // Câu 13
            Question(
                mainContent = QuestionContent.TextRes(R.string.NH06_C13),
                answers = listOf(
                    AnswerOption.Text("Nước tăng thể tích khi đông đặc"),
                    AnswerOption.Text("Nước tăng thể tích khi nóng chảy"),
                    AnswerOption.Text("Nước giảm thể tích khi đông đặc"),
                    AnswerOption.Text("Nước giảm thể tích khi nóng chảy")
                ),
                correctAnswer = 0
            ),

            // Câu 14
            Question(
                mainContent = QuestionContent.Text("Hiện tượng nước từ thể hơi chuyển sang thể lỏng gọi là hiện tượng"),
                answers = listOf(
                    AnswerOption.Text("Sôi"),
                    AnswerOption.Text("Bay hơi"),
                    AnswerOption.Text("Ngưng tụ"),
                    AnswerOption.Text("Hóa hơi")
                ),
                correctAnswer = 2
            ),

            // Câu 15
            Question(
                mainContent = QuestionContent.Text("Hiện tượng chuyển thể nào dưới đây của nước không có nhiệt độ xác định"),
                answers = listOf(
                    AnswerOption.Text("Tan chảy"),
                    AnswerOption.Text("Đóng băng"),
                    AnswerOption.Text("Sôi"),
                    AnswerOption.Text("Bay hơi và ngưng tụ")
                ),
                correctAnswer = 3
            ),

            // Câu 16
            Question(
                mainContent = QuestionContent.Text("Khi nấu nước bằng ấm siêu tốc thì ta thấy mặt nước sẽ " +
                        "dâng lên cao hơn sau khi nấu (sôi) xong là do"),
                answers = listOf(
                    AnswerOption.Text("Nước nở ra khi nóng lên còn ấm siêu tốc không nở ra khi nóng lên"),
                    AnswerOption.Text("Khi nước nóng lên sẽ làm hơi nước trong không khí ngưng tụ lại trong ấm siêu tốc"),
                    AnswerOption.Text("Nước và ấm đều nở ra khi nóng lên nhưng nước ở thể lỏng nở ra nhiều so với ấm ở thể rắn"),
                    AnswerOption.Text("Cả nước và ấm đều co lại khi nóng lên nhưng ấm co lại nhiều hơn nước")
                ),
                correctAnswer = 2
            ),

            // Câu 17
            Question(
                mainContent = QuestionContent.TextRes(R.string.NH06_C17),
                answers = listOf(
                    AnswerOption.TextRes(R.string.NH06_C17_0),
                    AnswerOption.TextRes(R.string.NH06_C17_1),
                    AnswerOption.TextRes(R.string.NH06_C17_2),
                    AnswerOption.TextRes(R.string.NH06_C17_3)
                ),
                correctAnswer = 2
            ),

            // Câu 18
            Question(
                mainContent = QuestionContent.Text("Sự nở vì nhiệt của chất khí được sử dụng trong việc"),
                answers = listOf(
                    AnswerOption.Text("Bơm bóng bay"),
                    AnswerOption.Text("Nâng khinh khí cầu"),
                    AnswerOption.Text("Hấp bánh bao"),
                    AnswerOption.Text("Luộc trứng")
                ),
                correctAnswer = 1
            ),

            // Câu 19
            Question(
                mainContent = QuestionContent.TextRes(R.string.NH06_C19),
                answers = listOf(
                    AnswerOption.Text("Băng phiến"),
                    AnswerOption.Text("Rượu"),
                    AnswerOption.Text("Thủy ngân"),
                    AnswerOption.Text("Nước")
                ),
                correctAnswer = 0
            ),
            Question(
                mainContent = QuestionContent.TextRes(R.string.NH06_C20),
                answers = listOf(
                    AnswerOption.TextRes(R.string.NH06_C20_0),
                    AnswerOption.TextRes(R.string.NH06_C20_1),
                    AnswerOption.TextRes(R.string.NH06_C20_2),
                    AnswerOption.TextRes(R.string.NH06_C20_3)
                ),
                correctAnswer = 0
            ),
            Question(
                mainContent = QuestionContent.TextRes(R.string.NH06_C21),
                answers = listOf(
                    AnswerOption.TextRes(R.string.NH06_C21_0),
                    AnswerOption.TextRes(R.string.NH06_C21_1),
                    AnswerOption.TextRes(R.string.NH06_C21_2),
                    AnswerOption.TextRes(R.string.NH06_C21_3)
                ),
                correctAnswer = 3
            ),
            Question(
                mainContent = QuestionContent.ImageRes(R.drawable.nh06_c22),
                answers = listOf(
                    AnswerOption.TextRes(R.string.NH06_C22_0),
                    AnswerOption.TextRes(R.string.NH06_C22_1),
                    AnswerOption.TextRes(R.string.NH06_C22_2),
                    AnswerOption.TextRes(R.string.NH06_C22_3)
                ),
                correctAnswer = 3
            ),
            Question(
                mainContent = QuestionContent.ImageResBig(R.drawable.nh06_c23),
                answers = listOf(
                    AnswerOption.TextRes(R.string.NH06_C23_0),
                    AnswerOption.TextRes(R.string.NH06_C23_1),
                    AnswerOption.TextRes(R.string.NH06_C23_2),
                    AnswerOption.TextRes(R.string.NH06_C23_3)
                ),
                correctAnswer = 2
            ),Question(
                mainContent = QuestionContent.TextRes(R.string.NH06_C24),
                answers = listOf(
                    AnswerOption.TextRes(R.string.NH06_C24_0),
                    AnswerOption.TextRes(R.string.NH06_C24_1),
                    AnswerOption.TextRes(R.string.NH06_C24_2),
                    AnswerOption.TextRes(R.string.NH06_C24_3)
                ),
                correctAnswer = 0
            ),Question(
                mainContent = QuestionContent.TextRes(R.string.NH06_C25),
                answers = listOf(
                    AnswerOption.TextRes(R.string.NH06_C25_0),
                    AnswerOption.TextRes(R.string.NH06_C25_1),
                    AnswerOption.TextRes(R.string.NH06_C25_2),
                    AnswerOption.TextRes(R.string.NH06_C25_3)
                ),
                correctAnswer = 1
            ),Question(
                mainContent = QuestionContent.TextRes(R.string.NH06_C26),
                answers = listOf(
                    AnswerOption.TextRes(R.string.NH06_C26_0),
                    AnswerOption.TextRes(R.string.NH06_C26_1),
                    AnswerOption.TextRes(R.string.NH06_C26_2),
                    AnswerOption.TextRes(R.string.NH06_C26_3)
                ),
                correctAnswer = 0
            ),
            Question(
                mainContent = QuestionContent.TextRes(R.string.NH06_C27),
                answers = listOf(
                    AnswerOption.TextRes(R.string.NH06_C27_0),
                    AnswerOption.TextRes(R.string.NH06_C27_1),
                ),
                correctAnswer = 1
            ),
            Question(
                mainContent = QuestionContent.TextRes(R.string.NH06_C28),
                answers = listOf(
                    AnswerOption.TextRes(R.string.NH06_C28_0),
                    AnswerOption.TextRes(R.string.NH06_C28_1),
                ),
                correctAnswer = 0
            ),
            Question(
                mainContent = QuestionContent.TextRes(R.string.NH06_C29),
                answers = listOf(
                    AnswerOption.TextRes(R.string.NH06_C29_0),
                    AnswerOption.TextRes(R.string.NH06_C29_1),
                ),
                correctAnswer = 1
            ),
            Question(
                mainContent = QuestionContent.TextRes(R.string.NH06_C30),
                answers = listOf(
                    AnswerOption.TextRes(R.string.NH06_C30_0),
                    AnswerOption.TextRes(R.string.NH06_C30_1),
                ),
                correctAnswer = 0
            ),
            Question(
                mainContent = QuestionContent.TextRes(R.string.NH06_C31),
                answers = listOf(
                    AnswerOption.TextRes(R.string.NH06_C31_0),
                    AnswerOption.TextRes(R.string.NH06_C31_1),
                ),
                correctAnswer = 1
            ),
            Question(
                mainContent = QuestionContent.TextRes(R.string.NH06_C32),
                answers = listOf(
                    AnswerOption.TextRes(R.string.NH06_C32_0),
                    AnswerOption.TextRes(R.string.NH06_C32_1),
                ),
                correctAnswer = 0
            ),
            Question(
                mainContent = QuestionContent.TextRes(R.string.NH06_C33),
                answers = listOf(
                    AnswerOption.TextRes(R.string.NH06_C33_0),
                    AnswerOption.TextRes(R.string.NH06_C33_1),
                ),
                correctAnswer = 1
            ),
            Question(
                mainContent = QuestionContent.TextRes(R.string.NH06_C34),
                answers = listOf(
                    AnswerOption.TextRes(R.string.NH06_C34_0),
                    AnswerOption.TextRes(R.string.NH06_C34_1),
                ),
                correctAnswer = 1
            ),
        ),*/
    )
}
