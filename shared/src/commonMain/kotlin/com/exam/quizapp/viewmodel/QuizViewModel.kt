package com.exam.quizapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.exam.quizapp.model.* //không chỉ là thư viện
import com.exam.quizapp.data.*




class QuizViewModel : ViewModel() {
    private val quizQuestions = quizQuestions3 + quizQuestions2 + quizQuestions1

    var selectedChapter by mutableStateOf<String?>(null)
    var selectedLesson by mutableStateOf<String?>(null)
    var currentQuestion by mutableIntStateOf(0)
    var score by mutableIntStateOf(0)
    var showResult by mutableStateOf(false)
    var history by mutableStateOf<List<QuestionResult>>(emptyList())

    val questions: List<Question>
        get() = quizQuestions[selectedLesson] ?: emptyList()

    fun selectChapter(chapter: String) {
        selectedChapter = chapter
    }

    fun selectLesson(quiz: String) {
        selectedLesson = quiz
    }

    fun startChapter(chapter: String) {
        selectChapter(chapter)
    }

    fun startQuiz(quiz: String) {
        resetTest()
        selectLesson(quiz)
    }

    fun goToHome() {
        resetChapter()
    }

    fun selectAnswer(answerIndex: Int) {
        val question = questions.getOrNull(currentQuestion) ?: return
        val correctIndex = question.correctAnswer

        if (correctIndex == null || question.answers.isEmpty()) return

        val selected = question.answers.getOrNull(answerIndex)
        val correct = question.answers.getOrNull(correctIndex)

        val isCorrect = answerIndex == correctIndex
        if (isCorrect) score++

        history = history + QuestionResult(
            mainQuestion = question.mainContent,
            extraQuestions = question.extraContents,
            answers = question.answers,
            selectedAnswer = selected,
            correctAnswer = correct,
            isCorrect = isCorrect,
            detailedCorrectAnswers = question.detailedCorrectAnswers
        )

        if (currentQuestion < questions.size - 1) {
            currentQuestion++
        } else {
            showResult = true
        }
    }

    fun submitTextAnswer(userInput: String, isCorrect: Boolean) {
        val question = questions.getOrNull(currentQuestion) ?: return

        if (question.correctAnswer == null && question.correctTextAnswers.isNotEmpty()) {
            if (isCorrect) score++

            history = history + QuestionResult(
                mainQuestion = question.mainContent,
                extraQuestions = question.extraContents,
                isCorrect = isCorrect,
                userInputText = userInput,
                correctTextAnswers = question.correctTextAnswers,
                detailedCorrectAnswers = question.detailedCorrectAnswers
            )
            if (currentQuestion < questions.size - 1) {
                currentQuestion++
            } else {
                showResult = true
            }
        }
    }

    fun resetTest() {
        currentQuestion = 0
        score = 0
        showResult = false
        history = emptyList()
    }

    fun resetLesson() {
        selectedLesson = null
        resetTest()
    }

    fun resetChapter() {
        selectedChapter = null
        resetLesson()
    }

    fun onBackQuestion() {
        if (currentQuestion > 0) {
            currentQuestion--
        }
        history = history.dropLast(1)
        score = history.count { it.isCorrect }
    }
}
