package com.example.flashcardproto1


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID

class FolderListViewModel: ViewModel() {
    private val flashcardRepository = FlashcardRepository.get()
    private val _folders: MutableStateFlow<List<Folder>> = MutableStateFlow(emptyList())

    val folders: StateFlow<List<Folder>>
        get() = _folders.asStateFlow()


init {
        viewModelScope.launch {
            flashcardRepository.getFolders().collect {
                _folders.value = it
            }
        }
    }
    suspend fun createNewFolder(name: String, description: String = "") {
        val folder = Folder(
            id = UUID.randomUUID(),
            name = name,
            description = description,
            date = Date()
        )
        flashcardRepository.addFolder(folder)
    }
    suspend fun addFlashcard(flashcard: Flashcard) {
        flashcardRepository.addFlashcard(flashcard)
    }
}
