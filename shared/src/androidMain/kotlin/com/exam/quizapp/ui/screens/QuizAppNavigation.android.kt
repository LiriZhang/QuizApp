package com.exam.quizapp.ui.screens

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.exam.quizapp.db.*
import com.exam.quizapp.viewmodel.*
import com.exam.quizapp.platform.*

@Composable
actual fun QuizApp(
    viewModel: QuizViewModel,
    onQuizCompleted: () -> Unit
) {
    LaunchedEffect(Unit) {
        try {
            initQuizDatabase()
        } catch (e: Exception) {
            println("Lỗi khởi tạo DB: ${e.message}")
        }
    }
    val navController = rememberNavController()
    val resultViewModel: ResultViewModel = viewModel()
    val startDestination = if (isUserLoggedIn()) "theory_and_exercise" else "login"
    NavHost(navController = navController, startDestination = startDestination) {
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("theory_and_exercise") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }
        composable("theory_and_exercise") {
            TheoryAndExerciseScreen(
                onTheorySelected = {
                    playVoice(isCorrect = true)
                },
                onExerciseSelected = {
                    playVoice(isCorrect = true)
                    navController.navigate("chapter_selection")
                },
                onSavedResultsSelected = {
                    playVoice(isCorrect = true)
                    navController.navigate("resultList")
                },
                onStudentSubmissionsSelected = {
                    playVoice(isCorrect = true)
                    navController.navigate("student_submissions")
                }
            )
        }
        composable("chapter_selection") {
            ChapterSelectionScreen(
                onChapterSelected = { chapter ->
                    playVoice(isCorrect = true)
                    viewModel.startChapter(chapter)
                    navController.navigate("quiz_selection/$chapter")
                },
                onList = {
                    playVoice(isCorrect = true)
                    navController.navigate("resultList")
                },
                onStudentSubmissions = {
                    playVoice(isCorrect = true)
                    navController.navigate("student_submissions")
                },
                onBackApp = {
                    playVoice(isCorrect = false)
                    viewModel.resetTest()
                    navController.navigate("theory_and_exercise") {
                        popUpTo("theory_and_exercise") { inclusive = true }
                    }
                }
            )
        }
        composable("student_submissions") {
            StudentSubmissionsScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable("quiz_selection/{chapter}") { backStackEntry ->
            val chapter = backStackEntry.arguments?.getString("chapter") ?: ""
            viewModel.selectChapter(chapter)
            LessonSelectionScreen(
                chapter = chapter,
                onQuizSelected = { quiz ->
                    playVoice(isCorrect = true)
                    viewModel.startQuiz(quiz)
                    navController.navigate("question/$quiz")
                },
                onQuizSelectedAllQuestions = { quiz ->
                    playVoice(isCorrect = true)
                    viewModel.startQuiz(quiz)
                    navController.navigate("all_questions/$quiz")
                },
                onQuizReviewSelected = { quiz ->
                    playVoice(isCorrect = true)
                    viewModel.selectLesson(quiz)
                    navController.navigate("review_quiz/$quiz")
                },
                onBack = {
                    playVoice(isCorrect = false)
                    viewModel.resetChapter()
                    navController.navigate("chapter_selection")
                }
            )
        }
        composable("review_quiz/{quiz}") {
            ReviewQuizScreen(
                lessonName = viewModel.selectedLesson ?: "Bài thi",
                questions = viewModel.questions,
                onStartQuiz = {
                    playVoice(isCorrect = true)
                    viewModel.resetTest()
                    navController.navigate("question/${viewModel.selectedLesson}")
                },
                onHomePage = {
                    playVoice(isCorrect = false)
                    viewModel.goToHome()
                    navController.navigate("chapter_selection")
                },
                onBack = {
                    playVoice(isCorrect = false)
                    navController.navigateUp()
                }
            )
        }
        composable("review_quiz_2") {
            ReviewQuizScreen(
                lessonName = viewModel.selectedLesson ?: "Bài thi",
                questions = viewModel.questions,
                onStartQuiz = {
                    playVoice(isCorrect = true)
                    viewModel.resetTest()
                    navController.navigate("question/${viewModel.selectedLesson}")
                },
                onHomePage = {
                    playVoice(isCorrect = false)
                    viewModel.goToHome()
                    navController.navigate("chapter_selection")
                },
                onBack = {
                    playVoice(isCorrect = false)
                    navController.navigateUp()
                }
            )
        }
        composable("question/{quiz}") { backStackEntry ->
            val quiz = backStackEntry.arguments?.getString("quiz") ?: ""
            viewModel.selectLesson(quiz)

            val questions = viewModel.questions
            val currentIdx = viewModel.currentQuestion
            if (questions.isNotEmpty() && currentIdx in questions.indices) {
                QuestionScreen(
                    question = questions[currentIdx],
                    currentQuestionIndex = currentIdx,
                    totalQuestions = questions.size,
                    onAnswerSelected = { answerIndex ->
                        val isCorrect = questions.getOrNull(currentIdx)?.correctAnswer == answerIndex
                        playSound(isCorrect)

                        if (!isCorrect) {
                            triggerVibration()
                        }

                        viewModel.selectAnswer(answerIndex)
                        if (viewModel.showResult) {
                            navController.navigate("result/${viewModel.score}/${questions.size}/${viewModel.selectedLesson}")
                            //onQuizCompleted()
                        }
                    },
                    onTextAnswerSubmitted = { userInput, isCorrect ->
                        viewModel.submitTextAnswer(userInput, isCorrect)
                        playSound(isCorrect)

                        if (!isCorrect) {
                            triggerVibration()
                        }

                        if (viewModel.showResult) {
                            navController.navigate("result/${viewModel.score}/${questions.size}/${viewModel.selectedLesson}")
                            //onQuizCompleted()
                        }
                    },
                    onBackQuestion = {
                        playVoice(isCorrect = false)
                        viewModel.onBackQuestion()
                    },
                    onHomePage = {
                        playVoice(isCorrect = false)
                        viewModel.resetChapter()
                        navController.navigate("chapter_selection")
                    },
                    onBackQuiz = {
                        playVoice(isCorrect = false)
                        viewModel.resetLesson()
                        navController.navigate("quiz_selection/${viewModel.selectedChapter}")
                    }
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Chưa có dữ liệu câu hỏi cho bài học này.")
                }
            }
        }
        composable("all_questions/{quiz}") { backStackEntry ->
            val quiz = backStackEntry.arguments?.getString("quiz") ?: ""
            viewModel.selectLesson(quiz)

            AllQuestionsScreen(
                questions = viewModel.questions,
                quizName = viewModel.selectedLesson ?: quiz,
                onSubmitQuiz = { calculatedScore, calculatedHistory ->
                    viewModel.score = calculatedScore
                    viewModel.history = calculatedHistory
                    viewModel.showResult = true
                    navController.navigate("result/${viewModel.score}/${viewModel.questions.size}/${viewModel.selectedLesson}")
                    onQuizCompleted()
                },
                onHomePage = {
                    playVoice(isCorrect = false)
                    viewModel.resetChapter()
                    navController.navigate("chapter_selection")
                },
                onBackQuiz = {
                    playVoice(isCorrect = false)
                    viewModel.resetLesson()
                    navController.navigate("quiz_selection/${viewModel.selectedChapter}")
                }
            )
        }
        composable("result/{score}/{totalQuestions}/{quizName}") { backStackEntry ->
            val score = backStackEntry.arguments?.getString("score")?.toInt() ?: 0
            val totalQuestions = backStackEntry.arguments?.getString("totalQuestions")?.toInt() ?: 0
            val quizName = backStackEntry.arguments?.getString("quizName") ?: ""
            ResultScreen(
                score = score,
                totalQuestions = totalQuestions,
                history = viewModel.history,
                quizName = quizName,
                onBackTest = {
                    playVoice(isCorrect = false)
                    viewModel.resetTest()
                    navController.navigate("question/${viewModel.selectedLesson}")
                },
                onBackTest2 = {
                    playVoice(isCorrect = false)
                    viewModel.resetTest()
                    navController.navigate("question/$quizName")
                },
                onSaveResult = {
                    playVoice(isCorrect = true)
                    resultViewModel.saveResult(
                        quizName = quizName,
                        score = score,
                        totalQuestions = totalQuestions,
                        history = viewModel.history
                    )
                    navController.navigate("resultList")
                },
                onHomePage = {
                    playVoice(isCorrect = false)
                    navController.navigate("chapter_selection")
                },
                onQuizReviewSelected = {
                    playVoice(isCorrect = true)
                    navController.navigate("review_quiz/$quizName")
                },
                onQuizReviewSelected2 = {
                    playVoice(isCorrect = true)
                    viewModel.selectLesson(quizName)
                    navController.navigate("review_quiz_2")
                },
                onBackQuiz = {
                    playVoice(isCorrect = false)
                    viewModel.resetLesson()
                    navController.navigate("quiz_selection/${viewModel.selectedChapter}")
                },
            )
        }
        composable("resultList") {
            ResultListScreen(
                onResultClick = { resultId ->
                    navController.navigate("resultDetail/$resultId")
                },
                onHomePage = {
                    playVoice(isCorrect = false)
                    viewModel.goToHome()
                    navController.navigate("theory_and_exercise")
                    //navController.navigate("chapter_selection")
                },
                onBack = {
                    playVoice(isCorrect = false)
                    navController.navigateUp()
                }
            )
        }
        composable("resultDetail/{resultId}") { backStackEntry ->
            val resultId = backStackEntry.arguments?.getString("resultId")?.toIntOrNull()
            if (resultId != null) {
                ResultDetailScreen(
                    resultId = resultId,
                    onBackTest = { quizName ->
                        playVoice(isCorrect = true)
                        viewModel.startQuiz(quizName)
                        navController.navigate("question/$quizName")
                    },
                    onHomePage = {
                        playVoice(isCorrect = false)
                        viewModel.goToHome()
                        navController.navigate("chapter_selection")
                    },
                    onBack = {
                        playVoice(isCorrect = false)
                        navController.navigate("resultList")
                        onQuizCompleted()
                    },
                )
            } else {
                Text(text = "Không tìm thấy kết quả")
            }
        }
    }
}
