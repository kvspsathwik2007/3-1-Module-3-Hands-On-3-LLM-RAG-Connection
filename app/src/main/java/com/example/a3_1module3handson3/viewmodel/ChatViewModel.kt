package com.example.a3_1module3handson3.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a3_1module3handson3.data.CompareResponse
import com.example.a3_1module3handson3.repository.ChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ChatUiState(
    val isLoading: Boolean = false,
    val result: CompareResponse? = null,
    val error: String? = null
)

class ChatViewModel : ViewModel() {

    private val repository = ChatRepository()

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    fun compareAnswers(question: String) {
        if (question.isBlank()) {
            _uiState.value = ChatUiState(
                error = "Please enter a question."
            )
            return
        }

        if (_uiState.value.isLoading) return

        viewModelScope.launch {
            _uiState.value = ChatUiState(isLoading = true)

            val result = repository.compareAnswers(question.trim())

            _uiState.value = result.fold(
                onSuccess = { response ->
                    ChatUiState(result = response)
                },
                onFailure = { error ->
                    ChatUiState(
                        error = error.message ?: "An unexpected error occurred."
                    )
                }
            )
        }
    }
}