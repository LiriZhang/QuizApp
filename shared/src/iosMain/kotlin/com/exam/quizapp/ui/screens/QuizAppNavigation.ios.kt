package com.exam.quizapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.exam.quizapp.viewmodel.QuizViewModel
import com.exam.quizapp.viewmodel.ResultViewModel
import com.exam.quizapp.db.initQuizDatabase
import com.exam.quizapp.platform.*

sealed interface IosScreen {
    object Login : IosScreen
    object TheoryAndExercise : IosScreen
    object ChapterSelection : IosScreen
    data class LessonSelection(val chapter: String) : IosScreen
    data class Question(val quiz: String) : IosScreen
    data class ReviewQuiz(val quiz: String) : IosScreen
    data class AllQuestions(val quiz: String) : IosScreen
    data class Result(val score: Int, val totalQuestions: Int, val quizName: String) : IosScreen
    object ResultList : IosScreen
    data class ResultDetail(val resultId: Int) : IosScreen
    object StudentSubmissions : IosScreen
}

@Composable
actual fun QuizApp(
    viewModel: QuizViewModel,
    onQuizCompleted: () -> Unit
) {
    LaunchedEffect(Unit) {
        try {
            initQuizDatabase()
        } catch (e: Throwable) {
            println("Lỗi khởi tạo DB: ${e.message}")
        }
    }
    var currentScreen by remember {
        mutableStateOf<IosScreen>(if (isUserLoggedIn()) IosScreen.TheoryAndExercise else IosScreen.Login)
    }
    val resultViewModel = remember { ResultViewModel() }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        when (val screen = currentScreen) {
            is IosScreen.Login -> {
                LoginScreen(
                    onLoginSuccess = {
                        currentScreen = IosScreen.TheoryAndExercise
                    }
                )
            }
            is IosScreen.TheoryAndExercise -> {
                TheoryAndExerciseScreen(
                    onTheorySelected = {
                        playVoice(isCorrect = true)
                    },
                    onExerciseSelected = {
                        playVoice(isCorrect = true)
                        currentScreen = IosScreen.ChapterSelection
                    }
                )
            }
            is IosScreen.ChapterSelection -> {
                ChapterSelectionScreen(
                    onChapterSelected = { chapter ->
                        playVoice(isCorrect = true)
                        viewModel.startChapter(chapter)
                        currentScreen = IosScreen.LessonSelection(chapter)
                    },
                    onList = {
                        playVoice(isCorrect = true)
                        currentScreen = IosScreen.ResultList
                    },
                    onStudentSubmissions = {
                        playVoice(isCorrect = true)
                        currentScreen = IosScreen.StudentSubmissions
                    },
                    onBackApp = {
                        playVoice(isCorrect = false)
                        viewModel.resetTest()
                        currentScreen = IosScreen.TheoryAndExercise
                    }
                )
            }
            is IosScreen.StudentSubmissions -> {
                StudentSubmissionsScreen(
                    onBack = { currentScreen = IosScreen.ChapterSelection }
                )
            }
            is IosScreen.LessonSelection -> {
                LessonSelectionScreen(
                    chapter = screen.chapter,
                    onQuizSelected = { quiz ->
                        playVoice(isCorrect = true)
                        viewModel.startQuiz(quiz)
                        currentScreen = IosScreen.Question(quiz)
                    },
                    onQuizSelectedAllQuestions = { quiz ->
                        playVoice(isCorrect = true)
                        viewModel.startQuiz(quiz)
                        currentScreen = IosScreen.AllQuestions(quiz)
                    },
                    onQuizReviewSelected = { quiz ->
                        playVoice(isCorrect = true)
                        viewModel.selectLesson(quiz)
                        currentScreen = IosScreen.ReviewQuiz(quiz)
                    },
                    onBack = {
                        playVoice(isCorrect = false)
                        viewModel.resetChapter()
                        currentScreen = IosScreen.ChapterSelection
                    }
                )
            }
            is IosScreen.ReviewQuiz -> {
                ReviewQuizScreen(
                    lessonName = viewModel.selectedLesson ?: screen.quiz,
                    questions = viewModel.questions,
                    onStartQuiz = {
                        playVoice(isCorrect = true)
                        viewModel.resetTest()
                        currentScreen = IosScreen.Question(viewModel.selectedLesson ?: screen.quiz)
                    },
                    onHomePage = {
                        playVoice(isCorrect = false)
                        viewModel.goToHome()
                        currentScreen = IosScreen.ChapterSelection
                    },
                    onBack = {
                        playVoice(isCorrect = false)
                        currentScreen = IosScreen.LessonSelection(viewModel.selectedChapter ?: "")
                    }
                )
            }
            is IosScreen.AllQuestions -> {
                AllQuestionsScreen(
                    questions = viewModel.questions,
                    quizName = viewModel.selectedLesson ?: screen.quiz,
                    onSubmitQuiz = { calculatedScore, calculatedHistory ->
                        viewModel.score = calculatedScore
                        viewModel.history = calculatedHistory
                        viewModel.showResult = true
                        currentScreen = IosScreen.Result(
                            score = viewModel.score,
                            totalQuestions = viewModel.questions.size,
                            quizName = viewModel.selectedLesson ?: screen.quiz
                        )
                        onQuizCompleted()
                    },
                    onHomePage = {
                        playVoice(isCorrect = false)
                        viewModel.resetChapter()
                        currentScreen = IosScreen.ChapterSelection
                    },
                    onBackQuiz = {
                        playVoice(isCorrect = false)
                        viewModel.resetLesson()
                        currentScreen = IosScreen.LessonSelection(viewModel.selectedChapter ?: "")
                    }
                )
            }
            is IosScreen.Question -> {
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
                                currentScreen = IosScreen.Result(
                                    score = viewModel.score,
                                    totalQuestions = questions.size,
                                    quizName = viewModel.selectedLesson ?: screen.quiz
                                )
                                onQuizCompleted()
                            }
                        },
                        onTextAnswerSubmitted = { userInput, isCorrect ->
                            viewModel.submitTextAnswer(userInput, isCorrect)
                            playSound(isCorrect)
                            if (!isCorrect) {
                                triggerVibration()
                            }
                            if (viewModel.showResult) {
                                currentScreen = IosScreen.Result(
                                    score = viewModel.score,
                                    totalQuestions = questions.size,
                                    quizName = viewModel.selectedLesson ?: screen.quiz
                                )
                                onQuizCompleted()
                            }
                        },
                        onBackQuestion = {
                            playVoice(isCorrect = false)
                            viewModel.onBackQuestion()
                        },
                        onHomePage = {
                            playVoice(isCorrect = false)
                            viewModel.resetChapter()
                            currentScreen = IosScreen.ChapterSelection
                        },
                        onBackQuiz = {
                            playVoice(isCorrect = false)
                            viewModel.resetLesson()
                            currentScreen = IosScreen.LessonSelection(viewModel.selectedChapter ?: "")
                        }
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(text = "Chưa có dữ liệu câu hỏi cho bài học này.")
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = {
                                currentScreen = IosScreen.ChapterSelection
                            }) {
                                Text("Quay lại")
                            }
                        }
                    }
                }
            }
            is IosScreen.Result -> {
                ResultScreen(
                    score = screen.score,
                    totalQuestions = screen.totalQuestions,
                    history = viewModel.history,
                    quizName = screen.quizName,
                    onBackTest = {
                        playVoice(isCorrect = false)
                        viewModel.resetTest()
                        currentScreen = IosScreen.Question(screen.quizName)
                    },
                    onBackTest2 = {
                        playVoice(isCorrect = false)
                        viewModel.resetTest()
                        currentScreen = IosScreen.Question(screen.quizName)
                    },
                    onSaveResult = {
                        playVoice(isCorrect = true)
                        resultViewModel.saveResult(
                            quizName = screen.quizName,
                            score = screen.score,
                            totalQuestions = screen.totalQuestions,
                            history = viewModel.history
                        )
                        currentScreen = IosScreen.ResultList
                    },
                    onHomePage = {
                        playVoice(isCorrect = false)
                        currentScreen = IosScreen.ChapterSelection
                    },
                    onQuizReviewSelected = {
                        playVoice(isCorrect = true)
                        viewModel.selectLesson(screen.quizName)
                        currentScreen = IosScreen.ReviewQuiz(screen.quizName)
                    },
                    onQuizReviewSelected2 = {
                        playVoice(isCorrect = true)
                        viewModel.selectLesson(screen.quizName)
                        currentScreen = IosScreen.ReviewQuiz(screen.quizName)
                    },
                    onBackQuiz = {
                        playVoice(isCorrect = false)
                        viewModel.resetLesson()
                        currentScreen = IosScreen.LessonSelection(viewModel.selectedChapter ?: "")
                    }
                )
            }
            is IosScreen.ResultList -> {
                ResultListScreen(
                    resultViewModel = resultViewModel,
                    onResultClick = { resultId ->
                        playVoice(isCorrect = true)
                        currentScreen = IosScreen.ResultDetail(resultId)
                    },
                    onHomePage = {
                        playVoice(isCorrect = false)
                        viewModel.goToHome()
                        currentScreen = IosScreen.ChapterSelection
                    },
                    onBack = {
                        playVoice(isCorrect = false)
                        currentScreen = IosScreen.ChapterSelection
                    }
                )
            }
            is IosScreen.ResultDetail -> {
                ResultDetailScreen(
                    resultId = screen.resultId,
                    viewModel = resultViewModel,
                    onBackTest = { quizName ->
                        playVoice(isCorrect = true)
                        viewModel.startQuiz(quizName)
                        currentScreen = IosScreen.Question(quizName)
                    },
                    onHomePage = {
                        playVoice(isCorrect = false)
                        viewModel.goToHome()
                        currentScreen = IosScreen.ChapterSelection
                    },
                    onBack = {
                        playVoice(isCorrect = false)
                        currentScreen = IosScreen.ResultList
                        onQuizCompleted()
                    }
                )
            }
        }
    }
}
