package com.henrique.geoquiz

import android.util.Log
import androidx.lifecycle.ViewModel

private const val TAG = "QuizViewModel"

class QuizViewModel : ViewModel() {

    private val questionBank = listOf(
        Question(R.string.question_australia, true),
        Question(R.string.question_oceans, true),
        Question(R.string.question_mideast, false),
        Question(R.string.question_africa, false),
        Question(R.string.question_americas, true),
        Question(R.string.question_asia, true),
    )

    private val answeredQuestions = mutableSetOf<Int>()

    private val cheatedQuestions = mutableSetOf<Int>()

    val hasCheatsRemaining: Boolean
        get() = cheatedQuestions.size < 3

    var currentIndex = 0
    private var correctAnswersCount = 0

    val currentQuestionAnswer: Boolean
        get() = questionBank[currentIndex].answer

    val currentQuestionText: Int
        get() = questionBank[currentIndex].textResId

    val hasPendingQuestions: Boolean
        get() = answeredQuestions.size < questionBank.size

    val score: String
        get() = String.format(
            "%.2f",
            correctAnswersCount.toDouble() / answeredQuestions.size * 100
        )

    init {
        Log.d(TAG, "ViewModel instance created")
    }

    fun moveToNext() {
        currentIndex = (currentIndex + 1) % questionBank.size
    }

    fun moveToPrevious() {
        currentIndex = if (currentIndex > 0) {
            currentIndex - 1
        } else {
            questionBank.size - 1
        }
    }

    fun submitAnswer(answer: Boolean): Int {
        val correctAnswer = questionBank[currentIndex].answer

        answeredQuestions.add(currentIndex)

        val isAnswerCorrect = answer == correctAnswer
        val isCheater = cheatedQuestions.contains(currentIndex)

        return when {
            isCheater -> {
                correctAnswersCount += 1
                R.string.judgment_toast
            }
            isAnswerCorrect -> R.string.correct_toast
            else -> R.string.incorrect_toast
        }
    }

    fun notifyCheat(hasCheated: Boolean) {
        if (hasCheated) {
            cheatedQuestions.add(currentIndex)
        }
    }

    override fun onCleared() {
        super.onCleared()

        Log.d(TAG, "ViewModel instance about to be destroyed")
    }
}
