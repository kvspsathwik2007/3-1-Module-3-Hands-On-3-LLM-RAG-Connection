
package com.example.a3_1module3handson3.api

import com.example.a3_1module3handson3.data.CompareRequest
import com.example.a3_1module3handson3.data.CompareResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("api/compare")
    suspend fun compareAnswers(
        @Body request: CompareRequest
    ): Response<CompareResponse>
}
