package com.example.a3_1module3handson3.data

data class CompareResponse(
    val question: String? = null,
    val direct: AnswerResult? = null,
    val rag: AnswerResult? = null
)