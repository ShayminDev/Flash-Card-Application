package com.example.flashcardproto1

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class FolderDetailViewModel(folderId: UUID) : ViewModel() {
    private val flashcardRepository = FlashcardRepository.get()

    private val _folder: MutableStateFlow<Folder?> = MutableStateFlow(null)
    val folder: StateFlow<Folder?> = _folder.asStateFlow()

    private val _flashcards: MutableStateFlow<List<Flashcard>> = MutableStateFlow(emptyList())
    val flashcards: StateFlow<List<Flashcard>> = _flashcards.asStateFlow()

    init {
        viewModelScope.launch {
            _folder.value = flashcardRepository.getFolder(folderId)
        }

        viewModelScope.launch {
            flashcardRepository.getFlashcardsForFolder(folderId).collect { flashcards ->
                _flashcards.value = flashcards
            }
        }
    }

    fun updateFolder(onUpdate: (Folder) -> Folder) {
        _folder.update { oldFolder ->
            oldFolder?.let { onUpdate(it) }
        }
    }

    suspend fun addFlashcard(flashcard: Flashcard) {
        flashcardRepository.addFlashcard(flashcard)
    }


        override fun onCleared() {
            super.onCleared()
            folder.value?.let { flashcardRepository.updateFolder(it) }
        }
    class FolderDetailViewModelFactory(
        private val folderId: java.util.UUID
    ) : androidx.lifecycle.ViewModelProvider.Factory {

        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(FolderDetailViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return FolderDetailViewModel(folderId) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
    fun deleteFlashcard(flashcard: Flashcard) {
        viewModelScope.launch {
            flashcardRepository.deleteFlashcard(flashcard)
        }
    }
}


