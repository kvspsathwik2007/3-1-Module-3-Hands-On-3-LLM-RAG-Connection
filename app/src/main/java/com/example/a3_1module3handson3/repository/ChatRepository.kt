
package com.example.a3_1module3handson3.repository

import com.example.a3_1module3handson3.api.RetrofitClient
import com.example.a3_1module3handson3.data.CompareRequest
import com.example.a3_1module3handson3.data.CompareResponse

class ChatRepository {

    suspend fun compareAnswers(
        question: String
    ): Result<CompareResponse> {
        return try {
            val response = RetrofitClient.apiService.compareAnswers(
                CompareRequest(question = question)
            )

            if (response.isSuccessful) {
                val body = response.body()

                if (body != null) {
                    Result.success(body)
                } else {
                    Result.failure(
                        Exception("Server returned an empty response")
                    )
                }
            } else {
                Result.failure(
                    Exception(
                        "HTTP ${response.code()}: ${
                            response.errorBody()?.string()
                                ?: response.message()
                        }"
                    )
                )
            }
        } catch (e: Exception) {
            Result.failure(
                Exception(
                    e.message ?: "Unable to connect to the server",
                    e
                )
            )
        }
    }
}
