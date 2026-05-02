package com.example.flashcardproto1

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class FlashcardDetailViewModel(flashcardId: UUID) : ViewModel() {
    private val flashcardRepository = FlashcardRepository.get()

    private val _flashcard: MutableStateFlow<Flashcard?> = MutableStateFlow(null)
    val flashcard: StateFlow<Flashcard?> = _flashcard.asStateFlow()

    init {
        viewModelScope.launch {
            _flashcard.value = flashcardRepository.getFlashcard(flashcardId)
        }
    }

    fun updateFlashcard(onUpdate: (Flashcard) -> Flashcard) {
        _flashcard.update { oldFlashcard ->
            oldFlashcard?.let { onUpdate(it) }
        }
    }

    override fun onCleared() {
        super.onCleared()
        flashcard.value?.let { flashcardRepository.updateFlashcard(it) }
    }
}

class FlashcardDetailViewModelFactory(
    private val flashcardId: UUID
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FlashcardDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FlashcardDetailViewModel(flashcardId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}