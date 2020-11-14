package com.henrique.geoquiz

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.henrique.geoquiz.databinding.ActivityCheatBinding

const val EXTRA_ANSWER_SHOWN = "com.henrique.geoquiz.answer_shown"
private const val EXTRA_ANSWER_IS_TRUE = "com.henrique.geoquiz.answer_is_true"

private const val KEY_HAS_CHEATED = "has_cheated"

class CheatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCheatBinding

    private val cheatViewModel by lazy {
        ViewModelProvider(this).get(CheatViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCheatBinding.inflate(layoutInflater)

        binding.showAnswerButton.onClick {
            setImageShown()
        }

        binding.apiLevelText.text = "API Level " + Build.VERSION.SDK_INT

        val hasCheated = savedInstanceState?.getBoolean(KEY_HAS_CHEATED) ?: false

        if (cheatViewModel.answerShown || hasCheated) {
            setImageShown()
        }

        setContentView(binding.root)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putBoolean(KEY_HAS_CHEATED, cheatViewModel.answerShown)
    }

    private fun setImageShown() {
        val answerIsTrue = intent.getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false)

        val answerText = when {
            answerIsTrue -> R.string.true_button
            else -> R.string.false_button
        }

        binding.answerTextView.setText(answerText)
        cheatViewModel.answerShown = true
        setAnswerShownResult(true)
    }

    private fun setAnswerShownResult(isAnswerShown: Boolean) {
        val data = Intent().apply {
            putExtra(EXTRA_ANSWER_SHOWN, isAnswerShown)
        }

        setResult(RESULT_OK, data)
    }

    companion object {
        fun newIntent(packageContext: Context, answerIsTrue: Boolean): Intent {
            return Intent(packageContext, CheatActivity::class.java).apply {
                putExtra(EXTRA_ANSWER_IS_TRUE, answerIsTrue)
            }
        }
    }
}
