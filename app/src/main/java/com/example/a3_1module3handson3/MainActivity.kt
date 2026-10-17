package com.example.a3_1module3handson3

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.Lifecycle
import com.example.a3_1module3handson3.data.AnswerResult
import com.example.a3_1module3handson3.data.SourceItem
import com.example.a3_1module3handson3.viewmodel.ChatViewModel
import com.example.a3_1module3handson3.viewmodel.ChatUiState
import kotlinx.coroutines.launch
import java.util.Locale

class MainActivity : ComponentActivity() {

    private lateinit var tvStatus: TextView
    private lateinit var etQuestion: EditText
    private lateinit var btnCompare: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var tvDirectAnswer: TextView
    private lateinit var tvDirectTiming: TextView
    private lateinit var tvRagAnswer: TextView
    private lateinit var tvRagTiming: TextView
    private lateinit var tvSources: TextView
    private lateinit var tvError: TextView

    private lateinit var viewModel: ChatViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this)[ChatViewModel::class.java]

        tvStatus = findViewById(R.id.tvStatus)
        etQuestion = findViewById(R.id.etQuestion)
        btnCompare = findViewById(R.id.btnCompare)
        progressBar = findViewById(R.id.progressBar)
        tvDirectAnswer = findViewById(R.id.tvDirectAnswer)
        tvDirectTiming = findViewById(R.id.tvDirectTiming)
        tvRagAnswer = findViewById(R.id.tvRagAnswer)
        tvRagTiming = findViewById(R.id.tvRagTiming)
        tvSources = findViewById(R.id.tvSources)
        tvError = findViewById(R.id.tvError)

        progressBar.visibility = View.GONE
        tvError.visibility = View.GONE
        tvStatus.text = "Backend ready"

        btnCompare.setOnClickListener {
            val question = etQuestion.text.toString().trim()

            if (question.isEmpty()) {
                etQuestion.error = "Enter a question first"
                etQuestion.requestFocus()
            } else {
                viewModel.compareAnswers(question)
            }
        }

        observeUiState()
    }

    private fun observeUiState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    renderState(state)
                }
            }
        }
    }

    private fun renderState(state: ChatUiState) {
        progressBar.visibility =
            if (state.isLoading) View.VISIBLE else View.GONE

        btnCompare.isEnabled = !state.isLoading
        btnCompare.text =
            if (state.isLoading) "Comparing..." else "Compare Answers"

        tvError.visibility = View.GONE

        when {
            state.isLoading -> {
                tvStatus.text = "Processing question..."
                tvDirectAnswer.text = "Generating direct LLM answer..."
                tvDirectTiming.text = ""
                tvRagAnswer.text = "Retrieving documents and generating RAG answer..."
                tvRagTiming.text = ""
                tvSources.text = "Loading sources..."
            }

            state.error != null -> {
                tvStatus.text = "Request failed"
                tvError.text = state.error
                tvError.visibility = View.VISIBLE
                tvSources.text = "Sources unavailable."
            }

            state.result != null -> {
                val result = state.result

                displayAnswer(result.direct, tvDirectAnswer)
                displayTiming(result.direct, tvDirectTiming)
                displayAnswer(result.rag, tvRagAnswer)
                displayTiming(result.rag, tvRagTiming)
                displaySources(result.rag?.sources ?: emptyList())

                val directFailed = result.direct?.error != null
                val ragFailed = result.rag?.error != null

                when {
                    directFailed && ragFailed -> {
                        tvStatus.text = "Both requests failed"
                        tvError.text =
                            "Both answers failed. Check your Gemini API configuration and Flask logs."
                        tvError.visibility = View.VISIBLE
                    }

                    directFailed || ragFailed -> {
                        tvStatus.text = "Partially completed"
                    }

                    else -> {
                        tvStatus.text = "Comparison complete"
                    }
                }
            }

            else -> {
                tvStatus.text = "Ready"
            }
        }
    }

    private fun displayAnswer(
        result: AnswerResult?,
        target: TextView
    ) {
        target.text = when {
            result == null -> "No result returned."
            !result.answer.isNullOrBlank() -> result.answer
            !result.error.isNullOrBlank() -> result.error
            else -> "No answer returned."
        }
    }

    private fun displayTiming(
        result: AnswerResult?,
        target: TextView
    ) {
        val duration = result?.duration_ms

        target.text = if (duration != null) {
            String.format(Locale.US, "Time: %.2f ms", duration)
        } else {
            ""
        }
    }

    private fun displaySources(sources: List<SourceItem>) {
        if (sources.isEmpty()) {
            tvSources.text = "No source documents returned."
            return
        }

        tvSources.text = sources.mapIndexed { index, source ->
            buildString {
                append("${index + 1}. ${source.title ?: "Untitled source"}")
                append("\nID: ${source.id ?: "N/A"}")

                source.similarity?.let { score ->
                    append(
                        "\nSimilarity: " +
                                String.format(Locale.US, "%.4f", score)
                    )
                }

                source.content?.let { content ->
                    append("\n")
                    append(
                        if (content.length > 350) {
                            content.take(350) + "..."
                        } else {
                            content
                        }
                    )
                }
            }
        }.joinToString("\n\n")
    }
}