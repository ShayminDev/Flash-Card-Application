package com.example.flashcardproto1

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.util.UUID


class StudyViewModel (folderId: UUID) : ViewModel() {
    private val flashcardRepository = FlashcardRepository.get()
    private val _flashcards: MutableLiveData<List<Flashcard>> = MutableLiveData(emptyList())
    val flashcards: LiveData<List<Flashcard>> = _flashcards

    private val _currentCardIndex: MutableLiveData<Int> = MutableLiveData(0)
    val currentCardIndex: LiveData<Int> = _currentCardIndex

    private val _showAnswer: MutableLiveData<Boolean> = MutableLiveData(false)
    val showAnswer: LiveData<Boolean> = _showAnswer

    init{
        viewModelScope.launch {
            flashcardRepository.getFlashcardsForFolder(folderId).collect { cards ->
                _flashcards.value = cards
                if (cards.isNotEmpty()) {
                    _currentCardIndex.value = 0
                    _showAnswer.value = false
                }
            }
        }
    }

    fun toggleAnswer() {
    val showAnswer = _showAnswer.value ?: false
    _showAnswer.value = !showAnswer
    }

    fun nextCard() {
        val currentIndex = _currentCardIndex.value ?: 0
        val cards = _flashcards.value ?: emptyList()
        val totalCards = cards.size

        if (totalCards == 0) return

        if (currentIndex < totalCards - 1) {
            _currentCardIndex.value = currentIndex + 1
        } else {
            _currentCardIndex.value = 0
        }
        _showAnswer.value = false
    }

    fun previousCard() {
        val currentIndex = _currentCardIndex.value ?: 0
        val cards = _flashcards.value ?: emptyList()
        val totalCards = cards.size


        if (totalCards == 0) return

        if (currentIndex > 0) {
            _currentCardIndex.value = currentIndex - 1
        } else {
            _currentCardIndex.value = totalCards - 1
        }
        _showAnswer.value = false
    }
    fun getCurrentCard(): Flashcard? {
        val index = _currentCardIndex.value ?: 0
        val cards = _flashcards.value ?: emptyList()

        return if (cards.isNotEmpty() && index < cards.size) cards[index] else null
    }

    fun getProgress(): String {
        val total = _flashcards.value?.size ?: 0
        val current = (_currentCardIndex.value ?: 0) + 1

        return if (total > 0) {
            "$current of $total"
        } else {
            "0 of 0"
        }
    }

    fun shuffleCards() {
        val currentList = _flashcards.value ?: return
        _flashcards.value = currentList.shuffled()
        _currentCardIndex.value = 0
        _showAnswer.value = false
    }
}