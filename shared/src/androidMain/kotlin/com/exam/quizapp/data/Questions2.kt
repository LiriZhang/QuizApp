package com.exam.quizapp.data

import com.exam.quizapp.R
import com.exam.quizapp.model.*


actual val quizQuestions2: Map<String, List<Question>> by lazy {
    mapOf(
        /*"Sẽ cập nhật sau" to listOf ( // https://s3.ap-southeast-1.amazonaws.com/com.exam.quizapp/I+%E1%BB%91t+th%C4%83ng+hoa.mp4
            Question(
                mainContent = QuestionContent.ImageUrl("https://s3.ap-southeast-1.amazonaws.com/com.exam.quizapp/long_long.jpg"), //https://lirizhang.github.io/C17.jpg
                answers = listOf(
                    AnswerOption.TextRes(R.string.mau_2),//https://lirizhang.github.io/Vbch.txt
                    //AnswerOption.TextUrl("https://lirizhang.github.io/vbch.txt"),//https://lirizhang.github.io/app-ads.txt
                    //AnswerOption.TextUrl("https://lirizhang.github.io/tl.txt"),//https://lirizhang.github.io/tl.txt
                ),
                correctAnswer = 0 //
            ),
            Question(
                mainContent = QuestionContent.ImageUrl("https://s3.ap-southeast-1.amazonaws.com/com.exam.quizapp/long_long.jpg"),
                answers = listOf(
                    AnswerOption.Text("。。。。"),
                    AnswerOption.Text("。。。"),
                    AnswerOption.ImageUrl("https://lirizhang.github.io/C17.jpg"),
                    AnswerOption.TextUrl("https://s3.ap-southeast-1.amazonaws.com/com.exam.quizapp/I_01/C19.1.txt"),
                ),
                correctAnswer = 0
            ),
            Question(
                mainContent = QuestionContent.ImageResBig(R.drawable.long_long), //Key là Que1, value là  htlongtps://lirizhang.github.io/C17.jpg
                answers = listOf(
                    AnswerOption.Text("。。。"),
                    AnswerOption.Text("。。。"),
                    AnswerOption.Text("。。。"),// Key là Ans1.0 value là https://lirizhang.github.io/Vbch.txt
                    //AnswerOption.TextUrl("https://lirizhang.github.io/vbch.txt"),// Key là Ans1.1, https://lirizhang.github.io/app-ads.txt
                    //AnswerOption.TextUrl("https://lirizhang.github.io/tl.txt"),// Key là Ans1.2, https://lirizhang.github.io/tl.txt
                ),
                correctAnswer = 0 // Key là CorrecAns1 - Value là 0
            ),
            Question(
                mainContent = QuestionContent.ImageResBig(R.drawable.long_long),
                answers = listOf(
                    AnswerOption.Text("Đúng"),
                    AnswerOption.Text("Sai")
                ),
                correctAnswer = 1 // Đáp án đúng là B. Sai
            ),
            Question(
                mainContent = QuestionContent.ImageUrl("https://s3.ap-southeast-1.amazonaws.com/com.exam.quizapp/long_long.jpg"), //ImageUrl("https://s3.ap-southeast-1.amazonaws.com/com.exam.quizapp/I_01/C1.jpg"),
                answers = listOf(
                    AnswerOption.ImageUrl("https://s3.ap-southeast-1.amazonaws.com/com.exam.quizapp/long_long.jpg"),
                    AnswerOption.ImageUrl("https://s3.ap-southeast-1.amazonaws.com/com.exam.quizapp/long_long.jpg"),
                    AnswerOption.ImageUrl("https://s3.ap-southeast-1.amazonaws.com/com.exam.quizapp/long_long.jpg"),
                    AnswerOption.TextUrl("https://s3.ap-southeast-1.amazonaws.com/com.exam.quizapp/I_01/C1.0.txt"),
                    AnswerOption.TextUrl("https://s3.ap-southeast-1.amazonaws.com/com.exam.quizapp/I_01/C1.1.txt"),
                    AnswerOption.TextUrl("https://s3.ap-southeast-1.amazonaws.com/com.exam.quizapp/I_01/C1.2.txt"),
                    AnswerOption.TextUrl("https://s3.ap-southeast-1.amazonaws.com/com.exam.quizapp/I_01/C1.3.txt")
                ),
                correctAnswer = 2 // Đáp án
            ),
        ),
        "Tương tác từ, từ trường, từ phổ và đường sức từ" to listOf (
            Question(
                mainContent = QuestionContent.TextUrl("https://s3.ap-southeast-1.amazonaws.com/com.exam.quizapp/III.01/C1.txt"),
                answers = listOf(
                    AnswerOption.TextUrl("https://s3.ap-southeast-1.amazonaws.com/com.exam.quizapp/III.01/C1.0.txt"),
                    AnswerOption.TextUrl("https://s3.ap-southeast-1.amazonaws.com/com.exam.quizapp/III.01/C1.1.txt"),
                    AnswerOption.TextUrl("https://s3.ap-southeast-1.amazonaws.com/com.exam.quizapp/III.01/C1.2.txt"),
                    AnswerOption.TextUrl("https://s3.ap-southeast-1.amazonaws.com/com.exam.quizapp/III.01/C1.3.txt")
                ),
                correctAnswer = 1
            ),
            Question(
                mainContent = QuestionContent.TextUrl("https://s3.ap-southeast-1.amazonaws.com/com.exam.quizapp/III.01/C2.txt"),
                answers = listOf(
                    AnswerOption.TextUrl("https://s3.ap-southeast-1.amazonaws.com/com.exam.quizapp/III.01/C2.0.txt"),
                    AnswerOption.TextUrl("https://s3.ap-southeast-1.amazonaws.com/com.exam.quizapp/III.01/C2.1.txt"),
                    AnswerOption.TextUrl("https://s3.ap-southeast-1.amazonaws.com/com.exam.quizapp/III.01/C2.2.txt"),
                    AnswerOption.TextUrl("https://s3.ap-southeast-1.amazonaws.com/com.exam.quizapp/III.01/C2.3.txt")
                ),
                correctAnswer = 2
            ),
            Question(
                mainContent = QuestionContent.TextUrl("https://s3.ap-southeast-1.amazonaws.com/com.exam.quizapp/III.01/C3.txt"),
                answers = listOf(
                    AnswerOption.TextUrl("https://s3.ap-southeast-1.amazonaws.com/com.exam.quizapp/III.01/C3.0.txt"),
                    AnswerOption.TextUrl("https://s3.ap-southeast-1.amazonaws.com/com.exam.quizapp/III.01/C3.1.txt"),
                    AnswerOption.TextUrl("https://s3.ap-southeast-1.amazonaws.com/com.exam.quizapp/III.01/C3.2.txt"),
                    AnswerOption.TextUrl("https://s3.ap-southeast-1.amazonaws.com/com.exam.quizapp/III.01/C3.3.txt")
                ),
                correctAnswer = 3
            ),
            Question(
                mainContent = QuestionContent.TextUrl("https://s3.ap-southeast-1.amazonaws.com/com.exam.quizapp/III.01/C4.txt"),
                answers = listOf(
                    AnswerOption.TextUrl("https://s3.ap-southeast-1.amazonaws.com/com.exam.quizapp/III.01/C4.0.txt"),
                    AnswerOption.TextUrl("https://s3.ap-southeast-1.amazonaws.com/com.exam.quizapp/III.01/C4.1.txt"),
                    AnswerOption.TextUrl("https://s3.ap-southeast-1.amazonaws.com/com.exam.quizapp/III.01/C4.2.txt"),
                    AnswerOption.TextUrl("https://s3.ap-southeast-1.amazonaws.com/com.exam.quizapp/III.01/C4.3.txt")
                ),
                correctAnswer = 0
            ),
            Question(
                mainContent = QuestionContent.TextUrl("https://s3.ap-southeast-1.amazonaws.com/com.exam.quizapp/III.01/C5.txt"),
                answers = listOf(
                    AnswerOption.TextUrl("https://s3.ap-southeast-1.amazonaws.com/com.exam.quizapp/III.01/C5.0.txt"),
                    AnswerOption.TextUrl("https://s3.ap-southeast-1.amazonaws.com/com.exam.quizapp/III.01/C5.1.txt"),
                    AnswerOption.TextUrl("https://s3.ap-southeast-1.amazonaws.com/com.exam.quizapp/III.01/C5.2.txt"),
                    AnswerOption.TextUrl("https://s3.ap-southeast-1.amazonaws.com/com.exam.quizapp/III.01/C5.3.txt")
                ),
                correctAnswer = 0
            ),
            Question(
                mainContent = QuestionContent.TextUrl("https://s3.ap-southeast-1.amazonaws.com/com.exam.quizapp/III.01/C6.txt"),
                answers = listOf(
                    AnswerOption.TextUrl("https://s3.ap-southeast-1.amazonaws.com/com.exam.quizapp/III.01/C6.0.txt"),
                    AnswerOption.TextUrl("https://s3.ap-southeast-1.amazonaws.com/com.exam.quizapp/III.01/C6.1.txt"),
                    AnswerOption.TextUrl("https://s3.ap-southeast-1.amazonaws.com/com.exam.quizapp/III.01/C6.2.txt"),
                    AnswerOption.TextUrl("https://s3.ap-southeast-1.amazonaws.com/com.exam.quizapp/III.01/C6.3.txt")
                ),
                correctAnswer = 3
            ),
            Question(
                mainContent = QuestionContent.TextUrl("https://s3.ap-southeast-1.amazonaws.com/com.exam.quizapp/III.01/C7.txt"),
                answers = listOf(
                    AnswerOption.TextUrl("https://s3.ap-southeast-1.amazonaws.com/com.exam.quizapp/III.01/C7.0.txt"),
                    AnswerOption.TextUrl("https://s3.ap-southeast-1.amazonaws.com/com.exam.quizapp/III.01/C7.1.txt"),
                    AnswerOption.TextUrl("https://s3.ap-southeast-1.amazonaws.com/com.exam.quizapp/III.01/C7.2.txt"),
                    AnswerOption.TextUrl("https://s3.ap-southeast-1.amazonaws.com/com.exam.quizapp/III.01/C7.3.txt")
                ),
                correctAnswer = 0
            ),
            Question(
                mainContent = QuestionContent.TextUrl("https://s3.ap-southeast-1.amazonaws.com/com.exam.quizapp/III.01/C8.txt"),
                answers = listOf(
                    AnswerOption.ImageRes(R.drawable.nc_hut_sai),
                    AnswerOption.ImageRes(R.drawable.nc_day_sai),
                    AnswerOption.ImageRes(R.drawable.nc_hut),
                    AnswerOption.ImageRes(R.drawable.nc_day)
                ),
                correctAnswer = 2
            ),
            Question(
                mainContent = QuestionContent.TextRes(R.string.TT_C9),
                answers = listOf(
                    AnswerOption.TextRes(R.string.TT_C9_0),
                    AnswerOption.TextRes(R.string.TT_C9_1),
                    AnswerOption.TextRes(R.string.TT_C9_2),
                    AnswerOption.TextRes(R.string.TT_C9_3)
                ),
                correctAnswer = 1
            ),
            Question(
                mainContent = QuestionContent.TextRes(R.string.TT_C10),
                answers = listOf(
                    AnswerOption.TextRes(R.string.TT_C10_0),
                    AnswerOption.TextRes(R.string.TT_C10_1),
                    AnswerOption.TextRes(R.string.TT_C10_2),
                    AnswerOption.TextRes(R.string.TT_C10_3)
                ),
                correctAnswer = 1
            ),
            Question(
                mainContent = QuestionContent.TextRes(R.string.TT_C11),
                answers = listOf(
                    AnswerOption.TextRes(R.string.TT_C11_0),
                    AnswerOption.TextRes(R.string.TT_C11_1),
                    AnswerOption.TextRes(R.string.TT_C11_2),
                    AnswerOption.TextRes(R.string.TT_C11_3)
                ),
                correctAnswer = 3
            ),
            Question(
                mainContent = QuestionContent.TextRes(R.string.TT_C12),
                answers = listOf(
                    AnswerOption.TextRes(R.string.TT_C12_0),
                    AnswerOption.TextRes(R.string.TT_C12_1),
                    AnswerOption.TextRes(R.string.TT_C12_2),
                    AnswerOption.TextRes(R.string.TT_C12_3)
                ),
                correctAnswer = 2
            ),
            Question(
                mainContent = QuestionContent.TextRes(R.string.TT_C13),
                answers = listOf(
                    AnswerOption.TextRes(R.string.TT_C13_0),
                    AnswerOption.TextRes(R.string.TT_C13_1),
                    AnswerOption.TextRes(R.string.TT_C13_2),
                    AnswerOption.TextRes(R.string.TT_C13_3)
                ),
                correctAnswer = 3
            ),
            Question(
                mainContent = QuestionContent.TextRes(R.string.TT_C14),
                answers = listOf(
                    AnswerOption.TextRes(R.string.TT_C14_0),
                    AnswerOption.TextRes(R.string.TT_C14_1),
                    AnswerOption.TextRes(R.string.TT_C14_2),
                    AnswerOption.TextRes(R.string.TT_C14_3)
                ),
                correctAnswer = 0
            ),
            Question(
                mainContent = QuestionContent.TextRes(R.string.TT_C15),
                answers = listOf(
                    AnswerOption.ImageRes(R.drawable.tu_pho_day),
                    AnswerOption.ImageRes(R.drawable.tu_pho_hut),
                    AnswerOption.ImageRes(R.drawable.nc_hut),
                    AnswerOption.ImageRes(R.drawable.nc_day)
                ),
                correctAnswer = 1
            ),
            Question(
                mainContent = QuestionContent.TextRes(R.string.TT_C16),
                answers = listOf(
                    AnswerOption.TextRes(R.string.TT_C16_0),
                    AnswerOption.TextRes(R.string.TT_C16_1),
                    AnswerOption.TextRes(R.string.TT_C16_2),
                    AnswerOption.TextRes(R.string.TT_C16_3)
                ),
                correctAnswer = 2
            ),
            Question(
                mainContent = QuestionContent.ImageRes(R.drawable.tt_c17),
                answers = listOf(
                    AnswerOption.TextRes(R.string.TT_C17_0),
                    AnswerOption.TextRes(R.string.TT_C17_1),
                    AnswerOption.TextRes(R.string.TT_C17_2),
                    AnswerOption.TextRes(R.string.TT_C17_3)
                ),
                correctAnswer = 2
            ),
            Question(
                mainContent = QuestionContent.ImageRes(R.drawable.tt_c18),
                answers = listOf(
                    AnswerOption.TextRes(R.string.TT_C18_0),
                    AnswerOption.TextRes(R.string.TT_C18_1),
                    AnswerOption.TextRes(R.string.TT_C18_2),
                    AnswerOption.TextRes(R.string.TT_C18_3)
                ),
                correctAnswer = 0
            ),
            Question(
                mainContent = QuestionContent.ImageResBig(R.drawable.tt_c19),
                answers = listOf(
                    AnswerOption.TextRes(R.string.TT_C19_0),
                    AnswerOption.TextRes(R.string.TT_C19_1),
                ),
                correctAnswer = 1
            ),
            Question(
                mainContent = QuestionContent.ImageRes(R.drawable.tt_c20),
                answers = listOf(
                    AnswerOption.TextRes(R.string.TT_C19_0),
                    AnswerOption.TextRes(R.string.TT_C19_1),
                ),
                correctAnswer = 1
            ),

            Question(
                mainContent = QuestionContent.ImageRes(R.drawable.tt_c21),
                answers = listOf(
                    AnswerOption.TextRes(R.string.TT_C19_0),
                    AnswerOption.TextRes(R.string.TT_C19_1),
                ),
                correctAnswer = 1
            ),
            Question(
                mainContent = QuestionContent.ImageRes(R.drawable.tt_c22),
                answers = listOf(
                    AnswerOption.TextRes(R.string.TT_C19_0),
                    AnswerOption.TextRes(R.string.TT_C19_1),
                ),
                correctAnswer = 1
            ),
            Question(
                mainContent = QuestionContent.TextRes(R.string.TT_C23),
                answers = listOf(
                    AnswerOption.TextRes(R.string.TT_C19_0),
                    AnswerOption.TextRes(R.string.TT_C19_1),
                ),
                correctAnswer = 0
            ),
            Question(
                mainContent = QuestionContent.TextRes(R.string.TT_C24),
                answers = listOf(
                    AnswerOption.TextRes(R.string.TT_C19_0),
                    AnswerOption.TextRes(R.string.TT_C19_1),
                ),
                correctAnswer = 0
            ),
            Question(
                mainContent = QuestionContent.TextRes(R.string.TT_C25),
                answers = listOf(
                    AnswerOption.TextRes(R.string.TT_C19_0),
                    AnswerOption.TextRes(R.string.TT_C19_1),
                ),
                correctAnswer = 1
            ),
            Question(
                mainContent = QuestionContent.TextRes(R.string.TT_C26),
                answers = listOf(
                    AnswerOption.TextRes(R.string.TT_C19_0),
                    AnswerOption.TextRes(R.string.TT_C19_1),
                ),
                correctAnswer = 0
            ),
            Question(
                mainContent = QuestionContent.TextRes(R.string.TT_C27),
                answers = listOf(
                    AnswerOption.TextRes(R.string.TT_C19_0),
                    AnswerOption.TextRes(R.string.TT_C19_1),
                ),
                correctAnswer = 0
            ),
            Question(
                mainContent = QuestionContent.TextRes(R.string.TT_C28),
                answers = listOf(
                    AnswerOption.TextRes(R.string.TT_C19_0),
                    AnswerOption.TextRes(R.string.TT_C19_1),
                ),
                correctAnswer = 0
            ),
            Question(
                mainContent = QuestionContent.Text("Nếu chiều chuyển động của electron trong dây dẫn thẳng được minh họa bằng hình vẽ có ký hiệu là ⨂ thì đường sức từ gây ra bởi dòng điện trong dây dẫn đó sẽ có chiều cùng chiều kim đồng hồ ?"),
                answers = listOf(
                    AnswerOption.TextRes(R.string.TT_C19_0),
                    AnswerOption.TextRes(R.string.TT_C19_1),
                ),
                correctAnswer = 0
            ),
            Question(
                mainContent = QuestionContent.Text("Nếu đường sức từ trong lòng ống dây được minh họa bằng hình vẽ có ký hiệu là ⊙ thì chiều dòng điện trong ống dây sẽ có chiều cùng chiều kim đồng hồ ?"),
                answers = listOf(
                    AnswerOption.TextRes(R.string.TT_C19_0),
                    AnswerOption.TextRes(R.string.TT_C19_1),
                ),
                correctAnswer = 0
            ),
        ),
        "Lực từ tác dụng lên dây dẫn mang dòng điện (lý thuyết)" to listOf(
            Question(
                mainContent = QuestionContent.TextUrl("https://s3.ap-southeast-1.amazonaws.com/com.exam.quizapp/LTDD/C1.txt"),
                answers = listOf(
                    AnswerOption.TextUrl("https://s3.ap-southeast-1.amazonaws.com/com.exam.quizapp/LTDD/C1.0.txt"),
                    AnswerOption.TextUrl("https://s3.ap-southeast-1.amazonaws.com/com.exam.quizapp/LTDD/C1.1.txt"),
                    AnswerOption.TextUrl("https://s3.ap-southeast-1.amazonaws.com/com.exam.quizapp/LTDD/C1.2.txt"),
                    AnswerOption.TextUrl("https://s3.ap-southeast-1.amazonaws.com/com.exam.quizapp/LTDD/C1.3.txt")
                ),
                correctAnswer = 3
            ),

            Question(
                mainContent = QuestionContent.ImageUrl("https://s3.ap-southeast-1.amazonaws.com/com.exam.quizapp/LTDD/C2.jpg"),
                answers = listOf(
                    AnswerOption.TextUrl("https://s3.ap-southeast-1.amazonaws.com/com.exam.quizapp/LTDD/C2.0.txt"),
                    AnswerOption.TextUrl("https://s3.ap-southeast-1.amazonaws.com/com.exam.quizapp/LTDD/C2.1.txt"),
                    AnswerOption.TextUrl("https://s3.ap-southeast-1.amazonaws.com/com.exam.quizapp/LTDD/C2.2.txt"),
                    AnswerOption.TextUrl("https://s3.ap-southeast-1.amazonaws.com/com.exam.quizapp/LTDD/C2.3.txt")
                ),
                correctAnswer = 1
            ),

            Question(
                mainContent = QuestionContent.TextUrl("https://s3.ap-southeast-1.amazonaws.com/com.exam.quizapp/LTDD/C3.txt"),
                answers = listOf(
                    AnswerOption.ImageUrl("https://s3.ap-southeast-1.amazonaws.com/com.exam.quizapp/LTDD/C3_0.jpg"),
                    AnswerOption.ImageUrl("https://s3.ap-southeast-1.amazonaws.com/com.exam.quizapp/LTDD/C3_1.jpg"),
                    AnswerOption.ImageUrl("https://s3.ap-southeast-1.amazonaws.com/com.exam.quizapp/LTDD/C3_2.jpg"),
                    AnswerOption.ImageUrl("https://s3.ap-southeast-1.amazonaws.com/com.exam.quizapp/LTDD/C3_3.jpg")
                ),
                correctAnswer = 0
            ),

            Question(
                mainContent = QuestionContent.TextUrl("https://s3.ap-southeast-1.amazonaws.com/com.exam.quizapp/LTDD/C4.txt"),
                answers = listOf(
                    AnswerOption.TextUrl("https://s3.ap-southeast-1.amazonaws.com/com.exam.quizapp/LTDD/C4.0.txt"),
                    AnswerOption.TextUrl("https://s3.ap-southeast-1.amazonaws.com/com.exam.quizapp/LTDD/C4.1.txt"),
                    AnswerOption.TextUrl("https://s3.ap-southeast-1.amazonaws.com/com.exam.quizapp/LTDD/C4.2.txt"),
                    AnswerOption.TextUrl("https://s3.ap-southeast-1.amazonaws.com/com.exam.quizapp/LTDD/C4.3.txt")
                ),
                correctAnswer = 1
            ),

            Question(
                mainContent = QuestionContent.TextUrl("https://s3.ap-southeast-1.amazonaws.com/com.exam.quizapp/LTDD/C5.txt"),
                answers = listOf(
                    AnswerOption.TextUrl("https://s3.ap-southeast-1.amazonaws.com/com.exam.quizapp/LTDD/C5.0.txt"),
                    AnswerOption.TextUrl("https://s3.ap-southeast-1.amazonaws.com/com.exam.quizapp/LTDD/C5.1.txt"),
                    AnswerOption.TextUrl("https://s3.ap-southeast-1.amazonaws.com/com.exam.quizapp/LTDD/C5.2.txt"),
                    AnswerOption.TextUrl("https://s3.ap-southeast-1.amazonaws.com/com.exam.quizapp/LTDD/C5.3.txt")
                ),
                correctAnswer = 3
            ),

            Question(
                mainContent = QuestionContent.TextUrl("https://s3.ap-southeast-1.amazonaws.com/com.exam.quizapp/LTDD/C6.txt"),
                answers = listOf(
                    AnswerOption.TextUrl("https://s3.ap-southeast-1.amazonaws.com/com.exam.quizapp/LTDD/C6.0.txt"),
                    AnswerOption.TextUrl("https://s3.ap-southeast-1.amazonaws.com/com.exam.quizapp/LTDD/C6.1.txt"),
                    AnswerOption.TextUrl("https://s3.ap-southeast-1.amazonaws.com/com.exam.quizapp/LTDD/C6.2.txt"),
                    AnswerOption.TextUrl("https://s3.ap-southeast-1.amazonaws.com/com.exam.quizapp/LTDD/C6.3.txt")
                ),
                correctAnswer = 2
            ),


            Question(
                mainContent = QuestionContent.TextRes(R.string.LTDD_C7),
                answers = listOf(
                    AnswerOption.TextRes(R.string.LTDD_C7_0),
                    AnswerOption.TextRes(R.string.LTDD_C7_1),
                    AnswerOption.TextRes(R.string.LTDD_C7_2),
                    AnswerOption.TextRes(R.string.LTDD_C7_3)
                ),
                correctAnswer = 1
            ),
            Question(
                mainContent = QuestionContent.TextRes(R.string.LTDD_C8),
                answers = listOf(
                    AnswerOption.TextRes(R.string.LTDD_C8_0),
                    AnswerOption.TextRes(R.string.LTDD_C8_1),
                    AnswerOption.TextRes(R.string.LTDD_C8_2),
                    AnswerOption.TextRes(R.string.LTDD_C8_3)
                ),
                correctAnswer = 2
            ),
            Question(
                mainContent = QuestionContent.TextRes(R.string.LTDD_C9),
                answers = listOf(
                    AnswerOption.TextRes(R.string.LTDD_C9_0),
                    AnswerOption.TextRes(R.string.LTDD_C9_1),
                    AnswerOption.TextRes(R.string.LTDD_C9_2),
                    AnswerOption.TextRes(R.string.LTDD_C9_3)
                ),
                correctAnswer = 3
            ),
            Question(
                mainContent = QuestionContent.TextRes(R.string.LTDD_C10),
                answers = listOf(
                    AnswerOption.TextRes(R.string.LTDD_C10_0),
                    AnswerOption.TextRes(R.string.LTDD_C10_1),
                    AnswerOption.TextRes(R.string.LTDD_C10_2),
                    AnswerOption.TextRes(R.string.LTDD_C10_3)
                ),
                correctAnswer = 3
            ),

            Question(
                mainContent = QuestionContent.TextRes(R.string.LTDD_C11),
                answers = listOf(
                    AnswerOption.TextRes(R.string.LTDD_C11_0),
                    AnswerOption.TextRes(R.string.LTDD_C11_1),
                    AnswerOption.TextRes(R.string.LTDD_C11_2),
                    AnswerOption.TextRes(R.string.LTDD_C11_3)
                ),
                correctAnswer = 0
            ),
            Question(
                mainContent = QuestionContent.TextRes(R.string.LTDD_C12),
                answers = listOf(
                    AnswerOption.TextRes(R.string.LTDD_C12_0),
                    AnswerOption.TextRes(R.string.LTDD_C12_1),
                    AnswerOption.TextRes(R.string.LTDD_C12_2),
                    AnswerOption.TextRes(R.string.LTDD_C12_3)
                ),
                correctAnswer = 0
            ),

            Question(
                mainContent = QuestionContent.TextRes(R.string.LTDD_C13),
                answers = listOf(
                    AnswerOption.ImageRes(R.drawable.ban_tay_trai_sai1),
                    AnswerOption.ImageRes(R.drawable.ban_tay_trai_sai2),
                    AnswerOption.ImageRes(R.drawable.ban_tay_trai_dung),
                    AnswerOption.ImageRes(R.drawable.ban_tay_trai_sai3)
                ),
                correctAnswer = 2
            ),

            Question(
                mainContent = QuestionContent.TextRes(R.string.LTDD_C14),
                answers = listOf(
                    AnswerOption.TextRes(R.string.LTDD_C14_0),
                    AnswerOption.TextRes(R.string.LTDD_C14_1),
                    AnswerOption.TextRes(R.string.LTDD_C14_2),
                    AnswerOption.TextRes(R.string.LTDD_C14_3)
                ),
                correctAnswer = 2
            ),

            Question(
                mainContent = QuestionContent.TextRes(R.string.LTDD_C15),
                answers = listOf(
                    AnswerOption.TextRes(R.string.LTDD_C15_0),
                    AnswerOption.TextRes(R.string.LTDD_C15_1),
                    AnswerOption.TextRes(R.string.LTDD_C15_2),
                    AnswerOption.TextRes(R.string.LTDD_C15_3)
                ),
                correctAnswer = 0
            ),

            Question(
                mainContent = QuestionContent.TextRes(R.string.LTDD_C16),
                answers = listOf(
                    AnswerOption.TextRes(R.string.LTDD_C16_0),
                    AnswerOption.TextRes(R.string.LTDD_C16_1),
                    AnswerOption.TextRes(R.string.LTDD_C10_2),
                    AnswerOption.TextRes(R.string.LTDD_C16_3)
                ),
                correctAnswer = 3
            ),

            Question(
                mainContent = QuestionContent.TextRes(R.string.LTDD_C17),
                answers = listOf(
                    AnswerOption.TextRes(R.string.LTDD_C17_0),
                    AnswerOption.TextRes(R.string.LTDD_C17_1),
                    AnswerOption.TextRes(R.string.LTDD_C17_2),
                    AnswerOption.TextRes(R.string.LTDD_C17_3)
                ),
                correctAnswer = 2
            ),

            Question(
                mainContent = QuestionContent.ImageUrl("https://s3.ap-southeast-1.amazonaws.com/com.exam.quizapp/LTDD/C18_long_big.jpg"),
                answers = listOf(
                    AnswerOption.TextRes(R.string.LTDD_C18_0),
                    AnswerOption.TextRes(R.string.LTDD_C18_1),
                    AnswerOption.TextRes(R.string.LTDD_C18_2),
                    AnswerOption.TextRes(R.string.LTDD_C18_3)
                ),
                correctAnswer = 0
            )
        ),
        "Bài kiểm tra 1.1" to listOf (
            Question(
                mainContent = QuestionContent.Text("Nội dung nào dưới đây là sai khi nói về mô hình đồng động học phân tử"),
                answers = listOf(
                    AnswerOption.Text("Các phân tử chuyển động nhiệt không ngừng"),
                    AnswerOption.Text("Giữa các phân tử có tương tác đẩy và tương tác hút"),
                    AnswerOption.Text("Độ lớn lực tương tác giữa các phân tử phụ thuộc vào khoảng cách giữa các phân tử"),
                    AnswerOption.Text("Tốc độ chuyển động của phân tử tỉ lệ thuận với nhiệt độ")
                ),
                correctAnswer = 3
            ),

            Question(
                mainContent = QuestionContent.TextRes(R.string.KT1_1_C2),
                answers = listOf(
                    AnswerOption.Text("3 hiện tượng"),
                    AnswerOption.Text("4 hiện tượng"),
                    AnswerOption.Text("2 hiện tượng"),
                    AnswerOption.Text("5 hiện tượng")
                ),
                correctAnswer = 1
            ),

            Question(
                mainContent = QuestionContent.Text("Thành phố Yakutsk (ở phía đông Siberia của Nga) được coi" +
                        " là nơi lạnh nhất thế giới với nhiệt độ có thể xuống đến -70ºC vào mùa đông. " +
                        "Vậy để đo được nhiệt độ thời tiết vào mùa đông của thành phố này thì " +
                        "người ta dùng loại nhiệt kế gì?"),
                answers = listOf(
                    AnswerOption.Text("Nhiệt kế thủy ngân"),
                    AnswerOption.Text("Nhiệt kế y tế"),
                    AnswerOption.Text("Nhiệt kế rượu"),
                    AnswerOption.Text("Nhiệt kế hồng ngoại điện tử")
                ),
                correctAnswer = 2
            ),

            Question(
                mainContent = QuestionContent.Text("“Nhiệt độ thời tiết đo được tại một địa điểm ở vùng ... " +
                        "vào một ngày mùa đông là 38 ... và vào một ngày mùa hè là 34 ...”. Vậy trong dấu “...” " +
                        "còn thiếu các từ theo thứ tự là"),
                answers = listOf(
                    AnswerOption.TextRes(R.string.KT1_1_C4_0),
                    AnswerOption.TextRes(R.string.KT1_1_C4_1),
                    AnswerOption.TextRes(R.string.KT1_1_C4_2),
                    AnswerOption.TextRes(R.string.KT1_1_C4_3)
                ),
                correctAnswer = 0
            ),

            Question(
                mainContent = QuestionContent.Text("Một quả bóng bay được bơm không quá căng rồi thả bay lên cao" +
                        " trong tiết trời râm mát làm cho nhiệt độ quả bóng giảm dần theo độ cao trong khi quả " +
                        "bóng nở dần và nổ. Quá trình bong bóng bay lên cao cho đến trước khi bị nổ thì khối khí " +
                        "trong quả bóng bay sẽ có thay đổi (coi như khí thất thoát ra khỏi quả bóng không đáng kể)."),
                answers = listOf(
                    AnswerOption.Text("Nhiệt năng và thế năng tương tác giảm"),
                    AnswerOption.Text("Nhiệt năng và thế năng tương tác tăng"),
                    AnswerOption.Text("Nhiệt năng tăng và thế năng tương tác giảm"),
                    AnswerOption.Text("Nhiệt năng giảm và thế năng tương tác tăng")
                ),
                correctAnswer = 0
            ),

            Question(
                mainContent = QuestionContent.TextRes(R.string.KT1_1_C6),
                answers = listOf(
                    AnswerOption.Text("Quang năng"),
                    AnswerOption.Text("Cơ năng"),
                    AnswerOption.Text("Nhiệt năng"),
                    AnswerOption.Text("Hóa năng")
                ),
                correctAnswer = 2
            ),

            Question(
                mainContent = QuestionContent.TextRes(R.string.KT1_1_C7),
                answers = listOf(
                    AnswerOption.Text("Nước đá trong tủ lạnh và nhiệt kế"),
                    AnswerOption.Text("Vạch chia thể tích nước và phần mềm bấm giờ trên điện thoại"),
                    AnswerOption.Text("Nước đá trong tủ lạnh và nước trong vòi nước sinh hoạt"),
                    AnswerOption.Text("Oát kế và nhiệt kế")
                ),
                correctAnswer = 3
            ),

            Question(
                mainContent = QuestionContent.TextRes(R.string.KT1_1_C8),
                answers = listOf(
                    AnswerOption.Text("Nước đá nhận nhiệt lượng nhiều hơn nước lỏng"),
                    AnswerOption.Text("Nhiệt dung riêng của nước không phụ thuộc vào cấu trúc của nước"),
                    AnswerOption.Text("Phần lớn nhiệt lượng mà nước nhận được dùng để phá vỡ cấu trúc tinh thể nước đá"),
                    AnswerOption.Text("Quá trình đun nước chỉ xảy ra hiện tượng chuyển thể là nóng chảy")
                ),
                correctAnswer = 2
            ),

            Question(
                mainContent = QuestionContent.Text("Người ta gọi nhiệt nóng chảy riêng và nhiệt hóa hơi riêng" +
                        " là “ẩn nhiệt riêng” vì truyền nhiệt trong quá trình nóng chảy của chất rắn kết " +
                        "tinh và trong quá trình hóa hơi của chất lỏng"),
                answers = listOf(
                    AnswerOption.Text("Được biểu hiện bằng sự thay đổi nhiệt độ."),
                    AnswerOption.Text("Không được biểu hiện bằng sự thay đổi nhiệt độ."),
                    AnswerOption.Text("Không được biểu hiện bằng sự thay đổi nhiệt lượng."),
                    AnswerOption.Text("Được biểu hiện bằng sự thay đổi nhiệt lượng.")
                ),
                correctAnswer = 1
            ),
            Question(
                mainContent = QuestionContent.TextRes(R.string.KT1_1_C10),
                answers = listOf(
                    AnswerOption.Text("Đúng"),
                    AnswerOption.Text("Sai")
                ),
                correctAnswer = 0
            ),
            Question(
                mainContent = QuestionContent.TextRes(R.string.KT1_1_C11),
                answers = listOf(
                    AnswerOption.Text("Đúng"),
                    AnswerOption.Text("Sai")
                ),
                correctAnswer = 0
            ),
            Question(
                mainContent = QuestionContent.TextRes(R.string.KT1_1_C12),
                answers = listOf(
                    AnswerOption.Text("Đúng"),
                    AnswerOption.Text("Sai")
                ),
                correctAnswer = 1
            ),
            Question(
                mainContent = QuestionContent.TextRes(R.string.KT1_1_C13),
                answers = listOf(
                    AnswerOption.Text("Đúng"),
                    AnswerOption.Text("Sai")
                ),
                correctAnswer = 0
            ),
            Question(
                mainContent = QuestionContent.Text("Bỏ 1 kg nước đá có nhiệt độ 0°C và 3 lít nước có nhiệt độ 20°C " +
                        "vào chung một cái ca để cho đá tan dần nhằm mục đích thu được nước mát để sử dụng. Nếu chỉ có nước" +
                        " đá và nước lỏng trao đổi nhiệt thì nhiệt độ của toàn khối nước khi cân bằng nhiệt là 15°C ?"),
                answers = listOf(
                    AnswerOption.Text("Đúng"),
                    AnswerOption.Text("Sai")
                ),
                correctAnswer = 1
            ),
            Question(
                mainContent = QuestionContent.Text("Bỏ 1 kg nước đá có nhiệt độ 0°C vào 3 lít nước có nhiệt độ 20°C " +
                        "để cho đá tan dần nhằm mục đích thu được nước mát để sử dụng. Nếu chỉ có nước đá và nước (lỏng) " +
                        "trao đổi nhiệt thì đá không thể tan hết ?"),
                answers = listOf(
                    AnswerOption.Text("Đúng"),
                    AnswerOption.Text("Sai")
                ),
                correctAnswer = 0
            ),
            Question(
                mainContent = QuestionContent.Text("Bỏ 1 kg nước đá có nhiệt độ 0°C và 3 lít nước có nhiệt độ 20°C " +
                        "vào chung một cái ca để cho đá tan dần nhằm mục đích thu được nước mát để sử dụng. Nếu nước" +
                        " đá và nước (lỏng) trao đổi nhiệt với nhau và trao đổi nhiệt với môi trường trong thời gian " +
                        "đủ dài mà nước đá vẫn không tan hết thì môi trường đang rất lạnh ?"),
                answers = listOf(
                    AnswerOption.Text("Đúng"),
                    AnswerOption.Text("Sai")
                ),
                correctAnswer = 0
            ),
            Question(
                mainContent = QuestionContent.TextRes(R.string.KT1_1_C17),
                answers = listOf(
                    AnswerOption.Text("Đúng"),
                    AnswerOption.Text("Sai")
                ),
                correctAnswer = 0
            ),
            Question(
                mainContent = QuestionContent.Text("Một kg nước ở nhiệt độ 5°C hòa tan với 2 kg nước ở nhiệt độ" +
                        " 65°C thì lúc nước hòa tan hoàn toàn (nhiệt lượng thất thoát do trao đổi nhiệt với bên ngoài không đáng kể) thì " +
                        "nhiệt độ của nước sẽ đã hòa tan là …. ?"),
                answers = listOf(
                    AnswerOption.Text("70°C"),
                    AnswerOption.Text("25°C"),
                    AnswerOption.Text("45°C"),
                    AnswerOption.Text("30°C")
                ),
                correctAnswer = 2
            ),

            Question(
                mainContent = QuestionContent.Text("Khi đo nhiệt độ môi trường thì học sinh thứ nhất đo được kết quả" +
                        " là xºC bằng nhiệt kế rượu có số chỉ theo nhiệt giai Celsius. Học sinh thứ hai đo được " +
                        "kết quả là xºF bằng nhiệt kế rượu có số chỉ theo nhiệt giai Kelvin. Vậy x có giá trị là bao nhiêu ?"),
                answers = listOf(
                    AnswerOption.Text("x = –40"),
                    AnswerOption.Text("x = 72"),
                    AnswerOption.TextRes(R.string.KT1_1_C19_2),
                    AnswerOption.Text("x = –72")
                ),
                correctAnswer = 0
            ),
            Question(
                mainContent = QuestionContent.TextRes(R.string.KT1_1_C20),
                answers = listOf(
                    AnswerOption.Text("75%"),
                    AnswerOption.Text("90%"),
                    AnswerOption.Text("85%"),
                    AnswerOption.Text("80%")
                ),
                correctAnswer = 3
            )

        ),
        "Bài kiểm tra 1.2" to listOf (
            Question(
                mainContent = QuestionContent.Text("Hiện tượng nào dưới đây thì thuyết động học phân tử không thể giải thích được"),
                answers = listOf(
                    AnswerOption.Text("Hiện tượng chuyển thể"),
                    AnswerOption.Text("Hiện tượng vật tăng nhiệt độ khi nhận nhiệt từ bên ngoài"),
                    AnswerOption.Text("Hiện tượng ly nước nóng đóng băng trước ly nước lạnh khi cùng bỏ vào tủ đông"),
                    AnswerOption.Text("Hiện tượng lốp xe đã bơm căng thì lâu ngày vẫn bị xẹp")
                ),
                correctAnswer = 2 // Đáp án C
            ),
            Question(
                mainContent = QuestionContent.Text("Hiện tượng chất chuyển thể từ thể hơi sang thể rắn là hiện tượng"),
                answers = listOf(
                    AnswerOption.Text("Thăng hoa"),
                    AnswerOption.Text("Ngưng tụ"),
                    AnswerOption.Text("Đông đặc"),
                    AnswerOption.Text("Ngưng kết")
                ),
                correctAnswer = 3 // Đáp án D
            ),
            Question(
                mainContent = QuestionContent.Text("Thân nhiệt của con người lúc bình thường là khoảng 98º nếu " +
                        "đo thân nhiệt theo thang nhiệt độ"),
                answers = listOf(
                    AnswerOption.Text("Celsius"),
                    AnswerOption.Text("Fahrenheit"),
                    AnswerOption.Text("Kelvin"),
                    AnswerOption.Text("Newton")
                ),
                correctAnswer = 1 // Đáp án B
            ),
            Question(
                mainContent = QuestionContent.TextRes(R.string.KT1_2_C4),
                answers = listOf(
                    AnswerOption.Text("Từ 35ºF đến 42ºF"),
                    AnswerOption.Text("Từ 42ºF đến 94ºF"),
                    AnswerOption.Text("Từ 94ºF đến 108ºF"),
                    AnswerOption.Text("Từ 108ºF đến 125ºF")
                ),
                correctAnswer = 2 // Đáp án C
            ),
            Question(
                mainContent = QuestionContent.Text("Nội năng của một vật KHÔNG có sự góp phần của"),
                answers = listOf(
                    AnswerOption.Text("Thế năng và động năng của vật"),
                    AnswerOption.Text("Nhiệt năng và thế năng của vật"),
                    AnswerOption.Text("Thế năng tương tác của các phân tử cấu tạo nên vật và cơ năng của vật"),
                    AnswerOption.Text("Nhiệt năng và thế năng tương tác của các phân tử cấu tạo nên vật")
                ),
                correctAnswer = 0 // Đáp án A
            ),
            Question(
                mainContent = QuestionContent.Text("Động cơ nào sau đây chắc chắn là động cơ nhiệt chứ không thể là động cơ điện ?"),
                answers = listOf(
                    AnswerOption.Text("Động cơ dùng để chạy xe máy"),
                    AnswerOption.Text("Động cơ dùng để chạy ô tô"),
                    AnswerOption.Text("Động cơ dùng để bay flycam"),
                    AnswerOption.Text("Động cơ dùng để phóng tàu vũ trụ")
                ),
                correctAnswer = 3 // Đáp án D
            ),
            Question(
                mainContent = QuestionContent.Text("Khu vực địa lý nào thường có biên độ nhiệt ngày và " +
                        "đêm thấp hơn các khu vực còn lại ?"),
                answers = listOf(
                    AnswerOption.Text("Sa mạc"),
                    AnswerOption.Text("Đảo ngoài khơi"),
                    AnswerOption.Text("Nội lục địa"),
                    AnswerOption.Text("Núi cao")
                ),
                correctAnswer = 1 // Đáp án B
            ),
            Question(
                mainContent = QuestionContent.TextRes(R.string.KT1_2_C8),
                answers = listOf(
                    AnswerOption.Text("0ºC"),
                    AnswerOption.TextRes(R.string.KT1_2_C8_1),
                    AnswerOption.TextRes(R.string.KT1_2_C8_2),
                    AnswerOption.Text("80ºC")
                ),
                correctAnswer = 3 // Đáp án D
            ),
            Question(
                mainContent = QuestionContent.TextRes(R.string.KT1_2_C9),
                answers = listOf(
                    AnswerOption.Text("Nhiệt hóa hơi riêng của nước lớn hơn nhiệt nóng chảy riêng của nước"),
                    AnswerOption.Text("Nhiệt nóng chảy riêng của nước đá lớn hơn nhiệt dung riêng của nước"),
                    AnswerOption.Text("Nhiệt dung riêng của nước đá lớn hơn nhiệt dung riêng của nước"),
                    AnswerOption.Text("Nhiệt nóng chảy riêng của nước lớn hơn nhiệt dung riêng của nước")
                ),
                correctAnswer = 0 // Đáp án A
            ),
            Question(
                mainContent = QuestionContent.TextRes(R.string.KT1_2_C10),
                answers = listOf(
                    AnswerOption.Text("Đúng"),
                    AnswerOption.Text("Sai")
                ),
                correctAnswer = 0 // Đáp án đúng là A. Đúng
            ),

            Question(
                mainContent = QuestionContent.TextRes(R.string.KT1_2_C11),
                answers = listOf(
                    AnswerOption.Text("Đúng"),
                    AnswerOption.Text("Sai")
                ),
                correctAnswer = 1 // Đáp án đúng là B. Sai
            ),

            Question(
                mainContent = QuestionContent.TextRes(R.string.KT1_2_C12),
                answers = listOf(
                    AnswerOption.Text("Đúng"),
                    AnswerOption.Text("Sai")
                ),
                correctAnswer = 0 // Đáp án đúng là A. Đúng
            ),

            Question(
                mainContent = QuestionContent.TextRes(R.string.KT1_2_C13),
                answers = listOf(
                    AnswerOption.Text("Đúng"),
                    AnswerOption.Text("Sai")
                ),
                correctAnswer = 0 // Đáp án đúng là A. Đúng
            ),

            Question(
                mainContent = QuestionContent.TextRes(R.string.KT1_2_C14),
                answers = listOf(
                    AnswerOption.Text("Đúng"),
                    AnswerOption.Text("Sai")
                ),
                correctAnswer = 1 // Đáp án đúng là B. Sai
            ),

            Question(
                mainContent = QuestionContent.TextRes(R.string.KT1_2_C15),
                answers = listOf(
                    AnswerOption.Text("Đúng"),
                    AnswerOption.Text("Sai")
                ),
                correctAnswer = 1 // Đáp án đúng là B. Sai
            ),

            Question(
                mainContent = QuestionContent.TextRes(R.string.KT1_2_C16),
                answers = listOf(
                    AnswerOption.Text("Đúng"),
                    AnswerOption.Text("Sai")
                ),
                correctAnswer = 0 // Đáp án đúng là A. Đúng
            ),

            Question(
                mainContent = QuestionContent.TextRes(R.string.KT1_2_C17),
                answers = listOf(
                    AnswerOption.Text("Đúng"),
                    AnswerOption.Text("Sai")
                ),
                correctAnswer = 0 // Đáp án đúng là A. Đúng
            ),
            Question(
                mainContent = QuestionContent.TextRes(R.string.KT1_2_C18),
                answers = listOf(
                    AnswerOption.Text("1372 m/s"),
                    AnswerOption.Text("970.15 m/s"),
                    AnswerOption.Text("343 m/s"),
                    AnswerOption.Text("171.5 m/s")
                ),
                correctAnswer = 2 // Đáp án đúng là C. 343 m/s
            ),

            Question(
                mainContent = QuestionContent.Text("Cho biết nhiệt dung riêng của nước (lỏng) là 4180 J/kg⋅K và nhiệt nóng chảy riêng của " +
                        "nước là 334 kJ/kg. Một kg nước lúc ban đầu có nhiệt độ là 10ºC được truyền một nhiệt lượng là 334 kJ. Nhiệt độ " +
                        "mà nước tăng thêm là …. ?"),
                answers = listOf(
                    AnswerOption.Text("80ºC"),
                    AnswerOption.Text("90ºC"),
                    AnswerOption.Text("90K"),
                    AnswerOption.Text("353K")
                ),
                correctAnswer = 0 // Đáp án đúng là A. 80ºC
            ),

            Question(
                mainContent = QuestionContent.TextRes(R.string.KT1_2_C20),
                answers = listOf(
                    AnswerOption.Text("2 loại khí"),
                    AnswerOption.Text("5 loại khí"),
                    AnswerOption.Text("3 loại khí"),
                    AnswerOption.Text("4 loại khí")
                ),
                correctAnswer = 1 // Đáp án đúng là B. 5 loại khí
            )
        ),
        "Bài kiểm tra 1.3" to listOf (
            Question(
                mainContent = QuestionContent.Text("Khi các phân tử rất gần nhau hoặc va chạm thì ..."),
                answers = listOf(
                    AnswerOption.Text("Tương tác hút lớn hơn tương tác đẩy"),
                    AnswerOption.TextRes(R.string.KT1_3_C1_1),
                    AnswerOption.Text("Tương tác đẩy lớn hơn tương tác hút"),
                    AnswerOption.TextRes(R.string.KT1_3_C1_3)
                ),
                correctAnswer = 2
            ),

            Question(
                mainContent = QuestionContent.TextRes(R.string.KT1_3_C2),
                answers = listOf(
                    AnswerOption.Text("Chất rắn dạng polime"),
                    AnswerOption.Text("Chất rắn đơn chất"),
                    AnswerOption.Text("Chất rắn kim loại"),
                    AnswerOption.Text("Chất rắn hợp chất")
                ),
                correctAnswer = 0
            ),

            Question(
                mainContent = QuestionContent.Text("Thực nghiệm cho thấy cứ 100 cm³ nước lỏng ở 0ºC khi đóng băng sẽ cho " +
                        "109 cm³ nước đá. Vậy nước khi đông đặc thì"),
                answers = listOf(
                    AnswerOption.Text("Giảm thể tích"),
                    AnswerOption.Text("Độ rỗng của nước tăng"),
                    AnswerOption.Text("Lực tương tác giữa các phân tử nước giảm"),
                    AnswerOption.Text("Mật độ khối của các phân tử nước tăng")
                ),
                correctAnswer = 1
            ),

            Question(
                mainContent = QuestionContent.Text("ĐIỀN VÀO DẤU “...” VỚI TỪ THÍCH HỢP. “...các chất rắn đều nóng " +
                        "chảy ở nhiệt độ nhất định. Những chất nóng chảy ở nhiệt độ nhất định là ...”"),
                answers = listOf(
                    AnswerOption.Text("Phần lớn … chất rắn kết tinh"),
                    AnswerOption.Text("Tất cả … chất rắn vô định hình"),
                    AnswerOption.Text("Phần lớn … chất rắn vô định hình"),
                    AnswerOption.Text("Tất cả … Chất rắn kết tinh")
                ),
                correctAnswer = 0
            ),

            Question(
                mainContent = QuestionContent.Text("Ở Nhiệt học Vật lí 8 chúng ta đã được học “Tổng động năng của " +
                        "các phân tử cấu thành nên vật gọi là nhiệt năng của vật”. Vậy khi nhiệt năng của một vật " +
                        "biến thiên thì giá trị nào sau đây không đổi."),
                answers = listOf(
                    AnswerOption.Text("Nội năng của vật"),
                    AnswerOption.Text("Động năng (trung bình) của các phân tử"),
                    AnswerOption.Text("Tốc độ chuyển động (trung bình) của các phân tử"),
                    AnswerOption.Text("Khối lượng mol của chất cấu thành nên vật")
                ),
                correctAnswer = 3
            ),

            Question(
                mainContent = QuestionContent.Text("CHỌN CÂU SAI. Một vật không thể giảm nhiệt độ tới 0K nên vật đó luôn luôn có"),
                answers = listOf(
                    AnswerOption.Text("Nội năng"),
                    AnswerOption.Text("Nhiệt năng"),
                    AnswerOption.Text("Động năng"),
                    AnswerOption.Text("Các phân tử chuyển động")
                ),
                correctAnswer = 2
            ),

            Question(
                mainContent = QuestionContent.Text("Đơn vị nào sau đây là đơn vị của năng lượng"),
                answers = listOf(
                    AnswerOption.Text("HP"),
                    AnswerOption.Text("atm"),
                    AnswerOption.Text("kWh"),
                    AnswerOption.Text("calo/m²")
                ),
                correctAnswer = 2
            ),

            Question(
                mainContent = QuestionContent.Text("Nguyên lý I nhiệt động lực học phát biểu như sau: “Độ biến thiên … " +
                        "của vật bằng tổng công và … mà vật nhận được”. Từ còn thiếu trong dấu “...” theo thứ tự là"),
                answers = listOf(
                    AnswerOption.TextRes(R.string.KT1_3_C8_0), // KHÔNG ĐƯỢC THAY BẰNG DẤU ; NHÉ
                    AnswerOption.TextRes(R.string.KT1_3_C8_1),
                    AnswerOption.TextRes(R.string.KT1_3_C8_2),
                    AnswerOption.TextRes(R.string.KT1_3_C8_3)
                ),
                correctAnswer = 0
            ),

            Question(
                mainContent = QuestionContent.Text("Một quả bóng bay được phơi dưới nắng rồi dần phình to ra. " +
                        "Độ biến thiên nội năng của khí trong quả bóng bay được xác định theo hệ thức ΔU = A + Q.  " +
                        "Xác định dấu của A và Q trong biểu thức của nguyên lí I Nhiệt động lực học"),
                answers = listOf(
                    AnswerOption.Text("A > 0 và Q > 0"),
                    AnswerOption.Text("A < 0 và Q < 0"),
                    AnswerOption.Text("A > 0 và Q < 0"),
                    AnswerOption.Text("A < 0 và Q > 0")
                ),
                correctAnswer = 3
            ),

            Question(
                mainContent = QuestionContent.TextRes(R.string.KT1_3_C10),
                answers = listOf(
                    AnswerOption.TextRes(R.string.KT1_3_C10_0),
                    AnswerOption.TextRes(R.string.KT1_3_C10_1),
                    AnswerOption.TextRes(R.string.KT1_3_C10_2),
                    AnswerOption.TextRes(R.string.KT1_3_C10_3)
                ),
                correctAnswer = 3
            ),

            Question(
                mainContent = QuestionContent.TextRes(R.string.KT1_3_C11),
                answers = listOf(
                    AnswerOption.TextRes(R.string.KT1_3_C11_0),
                    AnswerOption.TextRes(R.string.KT1_3_C11_1),
                    AnswerOption.Text("1 calo ≈ 39 J"),
                    AnswerOption.TextRes(R.string.KT1_3_C11_3)
                ),
                correctAnswer = 1
            ),
            Question(
                mainContent = QuestionContent.TextRes(R.string.KT1_3_C12),
                answers = listOf(
                    AnswerOption.Text("Đúng"),
                    AnswerOption.Text("Sai")
                ),
                correctAnswer = 1 // Sai
            ),

            Question(//Máy hơi nước không phải động cơ đốt trong?
                mainContent = QuestionContent.TextRes(R.string.KT1_3_C13),
                answers = listOf(
                    AnswerOption.Text("Đúng"),
                    AnswerOption.Text("Sai")
                ),
                correctAnswer = 0 // Đúng
            ),

            Question(//Toàn bộ chu trình thì nước có ba quá trình chuyển thể là sôi, bay hơi và ngưng tụ?
                mainContent = QuestionContent.TextRes(R.string.KT1_3_C14),
                answers = listOf(
                    AnswerOption.Text("Đúng"),
                    AnswerOption.Text("Sai")
                ),
                correctAnswer = 0 // Đúng
            ),

            Question(//Nước trong máy hơi nước có giai đoạn ngưng tụ và tái sử dụng nước nên không cần phải châm thêm nước vào nồi hơi?
                mainContent = QuestionContent.TextRes(R.string.KT1_3_C15),
                answers = listOf(
                    AnswerOption.Text("Đúng"),
                    AnswerOption.Text("Sai")
                ),
                correctAnswer = 1 // Sai
            ),

            Question(//Hơi nước được ngưng tụ nên hiệu suất chuyển hóa năng lượng của máy hơi nước rất cao?
                mainContent = QuestionContent.TextRes(R.string.KT1_3_C16),
                answers = listOf(
                    AnswerOption.Text("Đúng"),
                    AnswerOption.Text("Sai")
                ),
                correctAnswer = 1 // Sai
            ),
            Question(
                mainContent = QuestionContent.Text("Một bóng đèn sưởi ấm có công suất tiêu thụ là 500W được dùng để sưởi ấm cho đàn gà con vào mùa đông. Mỗi đêm thắp đèn 10 tiếng và thắp liên tục trong 20 ngày cho đến khi gà con đủ lớn thì ngưng thắp. Vậy điện năng tiêu thụ cho việc sưởi ấm của đàn gà là …. ?"),
                answers = listOf(
                    AnswerOption.Text("100 kJ"),
                    AnswerOption.Text("20000 J"),
                    AnswerOption.Text("25000 calo"),
                    AnswerOption.Text("50 kWh")
                ),
                correctAnswer = 3 // Đáp án đúng là D. 50 kWh
            ),
            Question(
                mainContent = QuestionContent.Text("Một ấm nước siêu tốc chứa đầy nước vừa sôi xong thì được hòa vào nước lạnh ở trong một xô nước. Biết lúc ban đầu nước lạnh ở trong xô là 6 lít và có nhiệt độ là 25ºC. Khi hòa nước xong thì nước ấm có nhiệt độ là 40ºC. Vậy vạch max trên ấm siêu tốc có chỉ số là"),
                answers = listOf(
                    AnswerOption.Text("1 lít"),
                    AnswerOption.TextRes(R.string.KT1_3_C18_1),
                    AnswerOption.TextRes(R.string.KT1_3_C18_2),
                    AnswerOption.TextRes(R.string.KT1_3_C18_3)
                ),
                correctAnswer = 2 // Đáp án đúng là C. 1.5 lít
            ),
            Question(
                mainContent = QuestionContent.Text("Một bóng đèn dây tóc có công suất tiêu thụ là 100W và hiệu suất phát sáng là 4%. Vậy nhiệt lượng mà bóng tỏa ra khi thắp trong một giờ là"),
                answers = listOf(
                    AnswerOption.TextRes(R.string.KT1_3_C19_0),
                    AnswerOption.Text("3456 kJ"),
                    AnswerOption.TextRes(R.string.KT1_3_C19_2),
                    AnswerOption.Text("144 kWh")
                ),
                correctAnswer = 1 // Đáp án đúng là B. 3456 kJ
            ),
            Question(
                mainContent = QuestionContent.TextRes(R.string.KT1_3_C20),
                answers = listOf(
                    AnswerOption.Text("72 %"),
                    AnswerOption.Text("77 %"),
                    AnswerOption.Text("83 %"),
                    AnswerOption.Text("91 %")
                ),
                correctAnswer = 0 // Đáp án đúng là A. 72 %
            )
        ),
        "Bài kiểm tra 1.4" to listOf (
            Question(
                mainContent = QuestionContent.Text("Lực liên kết hydro (tồn tại giữa các phân tử nước và phân tử amoniac… khi" +
                        " tồn tại ở trạng thái rắn với trạng thái lỏng) làm cho các chất có lực liên kết này"),
                answers = listOf(
                    AnswerOption.Text("Có nhiệt độ nóng chảy và nhiệt độ bay hơi thấp hơn nhiều so với các chất có khối lượng mol"),
                    AnswerOption.Text("Có nhiệt độ nóng chảy và nhiệt độ sôi thấp hơn nhiều so với các chất có cùng khối lượng mol"),
                    AnswerOption.Text("Có nhiệt độ đông đặc và nhiệt độ ngưng tụ hơi thấp hơn nhiều so với các chất có khối lượng mol"),
                    AnswerOption.Text("Có nhiệt độ đông đặc và nhiệt độ sôi cao hơn nhiều so với các chất có cùng khối lượng mol")
                ),
                correctAnswer = 1
            ),

            Question(
                mainContent = QuestionContent.TextRes(R.string.KT1_4_C2),
                answers = listOf(
                    AnswerOption.Text("Chất rắn không phải là chất lưu (chất có thể chuyển động thành dòng)"),
                    AnswerOption.Text("Không có chuyển động Brown trong chất rắn"),
                    AnswerOption.Text("Chất rắn chỉ có thể truyền nhiệt cho vật khác bằng dẫn nhiệt và " +
                            "bức xạ nhiệt，không thể truyền nhiệt bằng đối lưu"),
                    AnswerOption.Text("Chất rắn không thể nén được，không thể chuyển thể trực tiếp lên thể khí")
                ),
                correctAnswer = 3
            ),

            Question(
                mainContent = QuestionContent.Text("CHỌN CÂU SAI khi nói về vòng tuần hoàn của nước trên trái đất."),
                answers = listOf(
                    AnswerOption.Text("Mây hình thành là do nước bay hơi"),
                    AnswerOption.Text("Nước có thể đông đặc thành băng hoặc tuyết"),
                    AnswerOption.Text("Mưa đá hình thành là do hơi nước ngưng kết thành nước đá trên tầng cao của khí quyển"),
                    AnswerOption.Text("Dòng biển nóng thì nước dễ bay hơi lớn hơn dòng biển lạnh")
                ),
                correctAnswer = 2
            ),

            Question(
                mainContent = QuestionContent.Text("Ở điều kiện thường thì long não (camphor) là chất rắn tinh thể. Khi đun nóng nhẹ " +
                        "long não thì long não có thể chuyển thể trực tiếp từ thể rắn sang thể hơi mà không có biến đổi hóa học. Vậy khi đun nóng nhẹ thì long não đã có hiện tượng"),
                answers = listOf(
                    AnswerOption.Text("Thăng hoa"),
                    AnswerOption.Text("Hóa hơi"),
                    AnswerOption.Text("Nhiệt phân"),
                    AnswerOption.Text("Bay hơi")
                ),
                correctAnswer = 0
            ),

            Question(
                mainContent = QuestionContent.Text("Trong các công thức chuyển đổi thang nhiệt độ dưới đây thì công thức nào không đúng"),
                answers = listOf(
                    AnswerOption.Text("t(ºC) = T(K) + 273"),
                    AnswerOption.Text("t(K) = T(ºC) − 273"),
                    AnswerOption.TextRes(R.string.KT1_4_C5_2),
                    AnswerOption.TextRes(R.string.KT1_4_C5_3)
                ),
                correctAnswer = 2
            ),

            Question(
                mainContent = QuestionContent.Text("Nhiệt độ cần thiết để làm đông tụ hoàn toàn protein có trong trứng gà là 158ºF. " +
                        "Vậy để luộc chín trứng gà thì chỉ cần luộc đủ lâu và nhiệt độ nước luộc trứng lớn hơn"),
                answers = listOf(
                    AnswerOption.Text("65ºC"),
                    AnswerOption.Text("70ºC"),
                    AnswerOption.Text("80ºC"),
                    AnswerOption.Text("55ºC")
                ),
                correctAnswer = 1
            ),

            Question(
                mainContent = QuestionContent.TextRes(R.string.KT1_4_C7),
                answers = listOf(
                    AnswerOption.Text("Dây đốt nóng"),
                    AnswerOption.Text("Ruột nồi"),
                    AnswerOption.Text("Nước"),
                    AnswerOption.Text("Gạo")
                ),
                correctAnswer = 0
            ),

            Question(
                mainContent = QuestionContent.TextRes(R.string.KT1_4_C8),
                answers = listOf(
                    AnswerOption.Text("Cồn"),
                    AnswerOption.Text("Đường"),
                    AnswerOption.Text("Muối"),
                    AnswerOption.Text("Vôi sống")
                ),
                correctAnswer = 3
            ),
            Question(
                mainContent = QuestionContent.TextRes(R.string.KT1_4_C9),
                answers = listOf(
                    AnswerOption.Text("Nhiệt năng được sinh ra là do phản ứng hóa học"),
                    AnswerOption.Text("Lẩu tự sôi sẽ tiện lợi cho người dân ở những nơi cần cứu trợ sau thiên tai"),
                    AnswerOption.Text("Khi chế biến cần phải cẩn thận để đảm bảo an toàn"),
                    AnswerOption.Text("Gói tự sôi sau khi cho nước vào thì có thể phơi khô và tái sử dụng")
                ),
                correctAnswer = 3
            ),

            Question(
                mainContent = QuestionContent.Text("Trường hợp nào sau đây vật chắc chắn có nội năng giảm"),
                answers = listOf(
                    AnswerOption.Text("Vật nhận nhiệt lượng và nhận công"),
                    AnswerOption.Text("Vật tỏa nhiệt lượng và nhận công"),
                    AnswerOption.Text("Vật tỏa nhiệt lượng và sinh công"),
                    AnswerOption.Text("Vật nhận nhiệt lượng và sinh công")
                ),
                correctAnswer = 2
            ),

            Question(
                mainContent = QuestionContent.TextRes(R.string.KT1_4_C11),
                answers = listOf(
                    AnswerOption.Text("Ngay từ đầu nấu"),
                    AnswerOption.Text("Khi nước bắt đầu ấm"),
                    AnswerOption.Text("Khi nước bắt đầu nóng"),
                    AnswerOption.Text("Khi nước đang sôi")
                ),
                correctAnswer = 0
            ),

            Question(
                mainContent = QuestionContent.Text("Loại động cơ nhiệt sẽ không tạo ra khí thải độc hại khi hoạt động nếu động cơ nhiệt đó sử dụng chất đốt là"),
                answers = listOf(
                    AnswerOption.Text("Khí đốt"),
                    AnswerOption.Text("Hidro"),
                    AnswerOption.Text("Dầu diesel"),
                    AnswerOption.Text("Xăng")
                ),
                correctAnswer = 1
            ),

            Question(
                mainContent = QuestionContent.Text("Nhiệt lượng mà vật thu vào hay tỏa ra phụ thuộc vào?"),
                answers = listOf(
                    AnswerOption.Text("Khối lượng của vật，chất cấu tạo nên vật và độ thay đổi nhiệt độ của vật"),
                    AnswerOption.Text("Thể tích，nhiệt độ ban đầu và chất cấu tạo nên vật"),
                    AnswerOption.Text("Khối lượng，thể tích và độ thay đổi nhiệt độ của vật"),
                    AnswerOption.Text("Nhiệt độ ban đầu，nhiệt độ lúc sau và áp suất của môi trường")
                ),
                correctAnswer = 0
            ),

            Question(
                mainContent = QuestionContent.Text("Cho biết nhiệt dung riêng của nước (lỏng) là 4200 J/kg⋅K，nhiệt dung riêng của " +
                        "nước đá là 2100 J/kg⋅K và nhiệt nóng chảy riêng của nước là 334 kJ/kg. Dùng ấm đun một kg nước đá từ – 5°C đến " +
                        "lúc nước chớm sôi thì tắt bếp. Vậy phần lớn nhiệt lượng nhận được dùng để"),
                answers = listOf(
                    AnswerOption.Text("Phá vỡ tinh thể nước đá"),
                    AnswerOption.Text("Tăng nhiệt độ nước lỏng"),
                    AnswerOption.Text("Tăng nhiệt độ nước đá"),
                    AnswerOption.Text("Hóa hơi nước lỏng")
                ),
                correctAnswer = 1
            ),

            Question(
                mainContent = QuestionContent.Text("Trong các trường hợp sau trường hợp nào nước hóa hơi nhiều nhất"),
                answers = listOf(
                    AnswerOption.Text("Luộc rau muống cho hai người ăn"),
                    AnswerOption.Text("Hấp cá cho ba người ăn"),
                    AnswerOption.Text("Phơi khô một tạ lúa"),
                    AnswerOption.Text("Vận hành lò hơi nhà xưởng cho một ca làm việc")
                ),
                correctAnswer = 3
            ),

            Question(
                mainContent = QuestionContent.TextRes(R.string.KT1_4_C16),
                answers = listOf(
                    AnswerOption.Text("Nạp"),
                    AnswerOption.Text("Nén"),
                    AnswerOption.Text("Nổ"),
                    AnswerOption.Text("Xả")
                ),
                correctAnswer = 2
            )
        ),*/
    )
}
