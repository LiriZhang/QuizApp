package com.exam.quizapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exam.quizapp.db.QuizDatabase
import com.exam.quizapp.model.*
import com.exam.quizapp.shared.resources.Res
import com.exam.quizapp.shared.resources.allDrawableResources
import com.exam.quizapp.shared.resources.allStringResources
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class ResultViewModel : ViewModel() {
    private val quizDao: QuizResultDao by lazy {
        QuizDatabase.getDatabase().quizResultDao()
    }

    fun saveResult(
        quizName: String,
        score: Int,
        totalQuestions: Int,
        history: List<QuestionResult>,
        timestamp: Long = 0L
    ) {
        val historyString = convertHistoryToString(history)
        val quizResult = QuizResult(
            quizName = quizName,
            score = score,
            totalQuestions = totalQuestions,
            history = historyString,
            timestamp = timestamp
        )
        viewModelScope.launch {
            quizDao.insert(quizResult)
        }
    }

    private fun convertHistoryToString(history: List<QuestionResult>): String {
        return history.joinToString(separator = "。。") { result ->
            val question = result.mainQuestion.toStringRepresentation()
            val extraQuestions = result.extraQuestions.joinToString("，。") { it.toStringRepresentation() }
            val extraQuestionsEncoded = if (extraQuestions.isNotEmpty()) extraQuestions else "null"
            val answers = result.answers.joinToString("，。") { it.toStringRepresentation() }
            val answersEncoded = if (answers.isNotEmpty()) answers else "null"
            val selected = result.selectedAnswer?.toStringRepresentation() ?: "null"
            val correct = result.correctAnswer?.toStringRepresentation() ?: "null"
            val userInput = result.userInputText ?: "null"
            val correctTextAnswers = result.correctTextAnswers.joinToString("，。") { it.toStringRepresentation() }
            val correctTextAnswersEncoded = if (correctTextAnswers.isNotEmpty()) correctTextAnswers else "null"
            val isCorrect = if (result.isCorrect) "1" else "0"
            val detailedCorrectAnswers = result.detailedCorrectAnswers.joinToString("，。") { it.toStringRepresentation() }
            val detailedCorrectAnswersEncoded = if (detailedCorrectAnswers.isNotEmpty()) detailedCorrectAnswers else "null"

            listOf(question, extraQuestionsEncoded, answersEncoded, selected, correct, userInput, correctTextAnswersEncoded, isCorrect, detailedCorrectAnswersEncoded).joinToString("。，")
        }
    }

    private fun QuestionContent.toStringRepresentation(): String {
        return when (this) {
            is QuestionContent.AudioUrl -> "URLAUD${this.audioUrl}"
            is QuestionContent.Text -> "TEXTXT${this.text}"
            is QuestionContent.TextRes -> {
                val key = Res.allStringResources.entries.firstOrNull { it.value == this.textRes }?.key ?: ""
                "RESTEX$key"
            }
            is QuestionContent.ImageRes -> {
                val key = Res.allDrawableResources.entries.firstOrNull { it.value == this.imageRes }?.key ?: ""
                "RESIMA$key"
            }
            is QuestionContent.TextUrl -> "URLTEX${this.textUrl}"
            is QuestionContent.ImageUrl -> "URLIMA${this.imageUrl}"
            is QuestionContent.ClipUrl -> "URLCLI${this.clipUrl}"
        }
    }

    private fun AnswerOption.toStringRepresentation(): String {
        return when (this) {
            is AnswerOption.Text -> "TEXTXT${this.text}"
            is AnswerOption.TextRes -> {
                val key = Res.allStringResources.entries.firstOrNull { it.value == this.textRes }?.key ?: ""
                "RESTEX$key"
            }
            is AnswerOption.ImageRes -> {
                val key = Res.allDrawableResources.entries.firstOrNull { it.value == this.imageRes }?.key ?: ""
                "RESIMA$key"
            }
            is AnswerOption.TextUrl -> "URLTEX${this.textUrl}"
            is AnswerOption.ImageUrl -> "URLIMA${this.imageUrl}"
        }
    }

    private fun parseHistoryString(historyString: String): List<QuestionResult> {
        return historyString.split("。。").mapNotNull { entry ->
            val parts = entry.split("。，")
            if (parts.size < 9) return@mapNotNull null

            val question = parts[0].toQuestionContent()

            val extraQuestionsRaw = parts[1]
            val extraQuestions = if (extraQuestionsRaw != "null") {
                extraQuestionsRaw.split("，。").mapNotNull { it.toQuestionContent() }
            } else {
                emptyList()
            }
            val answersRaw = parts[2]
            val answers = if (answersRaw != "null") {
                answersRaw.split("，。").mapNotNull { it.toAnswerOption() }
            } else {
                emptyList()
            }

            val selectedAnswer = parts[3].takeIf { it != "null" }?.toAnswerOption()
            val correctAnswer = parts[4].takeIf { it != "null" }?.toAnswerOption()
            val userInput = parts[5].takeIf { it != "null" }

            val correctTextAnswersRaw = parts[6]
            val correctTextAnswers = if (correctTextAnswersRaw != "null") {
                correctTextAnswersRaw.split("，。").mapNotNull { it.toAnswerOption() }
            } else {
                emptyList()
            }

            val isCorrect = parts[7] == "1"
            val detailedCorrectAnswersRaw = parts[8]
            val detailedCorrectAnswers = if (detailedCorrectAnswersRaw != "null") {
                detailedCorrectAnswersRaw.split("，。").mapNotNull { it.toAnswerOption() }
            } else {
                emptyList()
            }

            QuestionResult(
                mainQuestion = question,
                extraQuestions = extraQuestions,
                answers = answers,
                selectedAnswer = selectedAnswer,
                correctAnswer = correctAnswer,
                userInputText = userInput,
                correctTextAnswers = correctTextAnswers,
                isCorrect = isCorrect,
                detailedCorrectAnswers = detailedCorrectAnswers
            )
        }
    }

    private fun String.toQuestionContent(): QuestionContent {
        return QuestionContent.Text(this)
    }

    private fun String.toAnswerOption(): AnswerOption {
        return AnswerOption.Text(this)
    }

    fun getAllResults(): List<QuizResult> {
        return runBlocking { quizDao.getAllResults() }
    }

    fun getResultById(id: Int): QuizResult? {
        return runBlocking { quizDao.getResultById(id) }
    }

    fun getQuestionResultsForResult(resultId: Int): List<QuestionResult> {
        val result = getResultById(resultId)
        return if (result != null) {
            parseHistoryString(result.history)
        } else {
            emptyList()
        }
    }

    fun deleteResultById(id: Int) {
        viewModelScope.launch {
            quizDao.deleteResultById(id)
        }
    }
}
