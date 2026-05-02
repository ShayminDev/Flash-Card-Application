package com.example.flashcardproto1

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID

class TestViewModel(folderId: UUID) : ViewModel() {
    private val flashcardRepository = FlashcardRepository.get()

    private var maxAttempts: Int = 3  // Default
    private var timeLimitMinutes: Int = 10  // Default in minutes
    private var timeRemainingSeconds: Int = 0
    private var timerJob: Job? = null

    private val _flashcards = MutableLiveData<List<Flashcard>>()
    val flashcards: LiveData<List<Flashcard>> get() = _flashcards

    private val _currentCardIndex = MutableLiveData<Int>()
    val currentCardIndex: LiveData<Int> get() = _currentCardIndex

    private val _userAnswer = MutableLiveData<String>()
    val userAnswer: LiveData<String> get() = _userAnswer

    private val _showResult = MutableLiveData<Boolean>()
    val showResult: LiveData<Boolean> get() = _showResult

    private val _isAnswerCorrect = MutableLiveData<Boolean>()
    val isAnswerCorrect: LiveData<Boolean> get() = _isAnswerCorrect

    private val _testScore = MutableLiveData<Pair<Int, Int>>()  // (correct, total)
    val testScore: LiveData<Pair<Int, Int>> get() = _testScore

    private val _testComplete = MutableLiveData<Boolean>()
    val testComplete: LiveData<Boolean> get() = _testComplete

    private val _testFailed = MutableLiveData<Boolean>()
    val testFailed: LiveData<Boolean> get() = _testFailed

    private val _attemptsRemaining = MutableLiveData<Int>()
    val attemptsRemaining: LiveData<Int> get() = _attemptsRemaining

    private val _timeRemaining = MutableLiveData<String>()
    val timeRemaining: LiveData<String> get() = _timeRemaining

    private val _timeProgress = MutableLiveData<Float>()
    val timeProgress: LiveData<Float> get() = _timeProgress

    private val _skippedCards = MutableLiveData<Int>()
    val skippedCards: LiveData<Int> get() = _skippedCards


    init {
        viewModelScope.launch {
            flashcardRepository.getFlashcardsForFolder(folderId).collect { flashcards ->
                _flashcards.value = flashcards
                if (flashcards.isNotEmpty()) {
                    resetTest()
                }
            }
        }
    }
    fun configureTest(maxAttempts: Int, timeLimitMinutes: Int) {
        this.maxAttempts = maxAttempts
        this.timeLimitMinutes = timeLimitMinutes
        this.timeRemainingSeconds = timeLimitMinutes * 60
        _attemptsRemaining.value = maxAttempts
    }

    fun startTest() {
        if (timeLimitMinutes > 0) {
            startTimer()
        }
    }

    private fun startTimer() {
        timerJob?.cancel()

        timerJob = viewModelScope.launch {
            val totalSeconds = timeLimitMinutes * 60

            while (timeRemainingSeconds > 0) {
                delay(1000)  // Update every second
                timeRemainingSeconds--

                // Update time display
                val minutes = timeRemainingSeconds / 60
                val seconds = timeRemainingSeconds % 60
                _timeRemaining.value = String.format("%02d:%02d", minutes, seconds)

                // Update progress (0.0 to 1.0)
                _timeProgress.value = timeRemainingSeconds.toFloat() / totalSeconds
            }

            // Time's up!
            _testFailed.value = true
            _testComplete.value = true
        }
    }

    fun resetTest(){
        _currentCardIndex.value = 0
        _userAnswer.value = ""
        _showResult.value = false
        _isAnswerCorrect.value = false
        _testScore.value = Pair(0, flashcards.value?.size ?: 0)
        _testComplete.value = false
        _testFailed.value = false
        _attemptsRemaining.value = maxAttempts
        _skippedCards.value = 0
        timeRemainingSeconds = timeLimitMinutes * 60

        val totalQuestions = _flashcards.value?.size ?: 0
        _testScore.value = Pair(0, totalQuestions)

        // Initialize time display
        val minutes = timeRemainingSeconds / 60
        val seconds = timeRemainingSeconds % 60
        _timeRemaining.value = String.format("%02d:%02d", minutes, seconds)
        _timeProgress.value = 1.0f
    }
    fun updateUserAnswer(answer: String) {
        _userAnswer.value = answer
    }
    fun submitAnswer() {
        val currentCard = getCurrentCard()
        val userAnswer = _userAnswer.value?.trim() ?: ""

        if (currentCard != null && userAnswer.isNotEmpty()) {
            val isCorrect = compareAnswers(userAnswer, currentCard.backText)

            _isAnswerCorrect.value = isCorrect
            _showResult.value = true


            val (correct, total) = _testScore.value ?: Pair(0, 0)
            if (isCorrect) {
                _testScore.value = Pair(correct + 1, total)
            }
            if (!isCorrect) {
                val attempts = _attemptsRemaining.value ?: maxAttempts
                _attemptsRemaining.value = attempts - 1

                // Check if out of attempts
                if (attempts - 1 <= 0) {
                    _testFailed.value = true
                    _testComplete.value = true
                }
            }
        }
    }
    private fun compareAnswers(userAnswer: String, correctAnswer: String): Boolean {
        return userAnswer.equals(correctAnswer, ignoreCase = true)
    }

    fun nextCard() {
        val currentIndex = _currentCardIndex.value ?: 0
        val totalCards = _flashcards.value?.size ?: 0

        if (totalCards == 0) return

        if (currentIndex < totalCards - 1) {
            _currentCardIndex.value = currentIndex + 1
        } else {
            // Loop back to first card
            _testComplete.value = true
            timerJob?.cancel()
            return
        }
        _userAnswer.value = ""
        _showResult.value = false
        _isAnswerCorrect.value = false
    }

    fun skipCard() {
        // Skip without affecting score
        val skipped = _skippedCards.value ?: 0
        _skippedCards.value = skipped + 1

        nextCard()
    }

    fun getCurrentCard(): Flashcard? {
        val index = _currentCardIndex.value ?: 0
        val cards = _flashcards.value ?: emptyList()
        return if (index < cards.size) cards[index] else null
    }


    fun getDisplayScore(): Pair<Int, Int> {
        val (correct, total) = _testScore.value ?: Pair(0, 0)
        val answered = correct + (_skippedCards.value ?: 0)
        return Pair(correct, total)  // Shows correct out of total questions
    }

    fun getProgressText(): String {
        val total = _flashcards.value?.size ?: 0
        val current = (_currentCardIndex.value ?: 0) + 1
        return "$current of $total"
    }

    fun getScoreText(): String {
        val (correct, total) = _testScore.value ?: Pair(0, 0)
        return "$correct / $total"
    }

    fun getScorePercentage(): Float {
        val (correct, total) = _testScore.value ?: Pair(0, 0)
        return if (total > 0) correct.toFloat() / total * 100 else 0f
    }

    fun shuffleCards() {
        val currentCards = _flashcards.value ?: emptyList()
        val shuffled = currentCards.shuffled()
        _flashcards.value = shuffled
        resetTest()
    }
    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
