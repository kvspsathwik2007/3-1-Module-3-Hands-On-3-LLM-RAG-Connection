package com.example.a3_1module3handson3.data

data class AnswerResult(
    val answer: String? = null,
    val duration_ms: Double? = null,
    val error: String? = null,
    val sources: List<SourceItem>? = emptyList()
)

data class SourceItem(
    val id: String? = null,
    val title: String? = null,
    val content: String? = null,
    val similarity: Double? = null
)