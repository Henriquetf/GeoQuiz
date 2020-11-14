package com.henrique.geoquiz

import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.henrique.geoquiz.databinding.ActivityMainBinding

private const val TAG = "MainActivity"
private const val KEY_INDEX = "index"
private const val REQUEST_CODE_CHEAT = 0

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val quizViewModel: QuizViewModel by lazy {
        ViewModelProvider(this).get(QuizViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(TAG, "onCreate(Bundle?) called")

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        with(binding) {
            trueButton.onClick {
                checkAnswer(true)
            }

            falseButton.onClick {
                checkAnswer(false)
            }

            nextButton.onClick {
                nextQuestion()
            }

            previousButton.onClick {
                previousQuestion()
            }

            cheatButton.onClick { view ->
                openCheatScreen(view)
            }
        }

        Log.d(TAG, "Got a QuizViewModel: $quizViewModel")

        savedInstanceState?.run {
            quizViewModel.currentIndex = getInt(KEY_INDEX, 0)
        }

        checkRemainingCheats()
        updateQuestion()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != RESULT_OK) {
            return
        }

        if (requestCode == REQUEST_CODE_CHEAT) {
            val hasCheated = data?.getBooleanExtra(EXTRA_ANSWER_SHOWN, false) ?: false

            quizViewModel.notifyCheat(hasCheated)
        }

        checkRemainingCheats()
    }

    private fun checkRemainingCheats() {
        if (!quizViewModel.hasCheatsRemaining) {
            binding.cheatButton.apply {
                isEnabled = false
                isClickable = false
            }
        }
    }

    private fun openCheatScreen(view: View) {
        val answerIsTrue = quizViewModel.currentQuestionAnswer
        val intent = CheatActivity.newIntent(this, answerIsTrue)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val options = ActivityOptions
                .makeClipRevealAnimation(view, 0, 0, view.width, view.height)

            startActivityForResult(intent, REQUEST_CODE_CHEAT, options.toBundle())
        } else {
            startActivityForResult(intent, REQUEST_CODE_CHEAT)
        }
    }

    override fun onStart() {
        super.onStart()

        Log.d(TAG, "onStart() called")
    }

    override fun onResume() {
        super.onResume()

        Log.d(TAG, "onResume() called")
    }

    override fun onPause() {
        super.onPause()

        Log.d(TAG, "onPause() called")
    }

    override fun onStop() {
        super.onStop()

        Log.d(TAG, "onStop() called")
    }

    override fun onDestroy() {
        super.onDestroy()

        Log.d(TAG, isFinishing.toString())

        Log.d(TAG, "onDestroy() called")
    }

    private fun updateQuestion() {
        Log.d(TAG, "Updating question text", Exception())

        val questionTextResId = quizViewModel.currentQuestionText
        binding.questionTextView.setText(questionTextResId)

        val enableAnswerButtons = quizViewModel.hasPendingQuestions

        binding.trueButton.isEnabled = enableAnswerButtons
        binding.falseButton.isEnabled = enableAnswerButtons
    }

    private fun nextQuestion() {
        quizViewModel.moveToNext()

        updateQuestion()
    }

    private fun previousQuestion() {
        quizViewModel.moveToPrevious()

        updateQuestion()
    }

    private fun checkAnswer(userAnswer: Boolean) {
        val answerText = quizViewModel.submitAnswer(userAnswer)

        Toast.makeText(this, answerText, Toast.LENGTH_SHORT)
            .show()

        if (quizViewModel.hasPendingQuestions) {
            nextQuestion()
        } else {
            Toast.makeText(this, "Your score: ${quizViewModel.score}%", Toast.LENGTH_SHORT)
                .show()

            updateQuestion()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        Log.i(TAG, "onSaveInstanceState")

        outState.putInt(KEY_INDEX, quizViewModel.currentIndex)
    }
}
