package com.example.flashcardproto1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.flashcardproto1.databinding.FragmentTestBinding
import java.util.UUID

class TestDetailFragment : Fragment(), TestConfigDialogFragment.TestConfigListener {
    private var _binding: FragmentTestBinding? = null
    private val binding get() = checkNotNull(_binding) {
        "Cannot access binding because it is null. Is the view visible?"
    }

    private val args: TestDetailFragmentArgs by navArgs()  // Updated Args class name
    private val testViewModel: TestViewModel by viewModels {
        TestViewModelFactory(args.folderId)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTestBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showConfigDialog()
    }
    private fun showConfigDialog() {
        val dialog = TestConfigDialogFragment()
        dialog.setListener(this)
        dialog.show(parentFragmentManager, "TestConfigDialog")
    }
    override fun onTestConfigured(maxAttempts: Int, timeLimitMinutes: Int) {
        // Configure the test with user settings
        testViewModel.configureTest(maxAttempts, timeLimitMinutes)

        setupTest()
    }
    private fun setupTest() {
        setupClickListeners()
        setupTextWatcher()
        observeViewModel()

        testViewModel.startTest()
    }
    private fun setupClickListeners() {
        binding.btnSubmit.setOnClickListener {
            testViewModel.submitAnswer()
        }

        binding.btnSkip.setOnClickListener {
            testViewModel.skipCard()
        }

        binding.btnRestartTest.setOnClickListener {
            restartTest()
        }

        binding.btnExitTest.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.btnContinue.setOnClickListener {
            hideResultView()
            testViewModel.nextCard()
        }
    }

    private fun setupTextWatcher() {
        binding.etUserAnswer.addTextChangedListener { text ->
            testViewModel.updateUserAnswer(text.toString())
        }
    }

    private fun observeViewModel() {
        // Observe current card
        testViewModel.currentCardIndex.observe(viewLifecycleOwner) { index ->
            updateCardDisplay()
            updateProgress()
        }

        // Observe user answer
        testViewModel.userAnswer.observe(viewLifecycleOwner) { answer ->
            // Enable/disable submit button based on whether answer is empty
            binding.btnSubmit.isEnabled = answer?.trim()?.isNotEmpty()?: false
        }

        // Observe result state
        testViewModel.showResult.observe(viewLifecycleOwner) { showResult ->
            if (showResult==true) {
                showResultView()
            } else {
                hideResultView()
            }
        }

        // Observe if answer is correct
        testViewModel.isAnswerCorrect.observe(viewLifecycleOwner) { isCorrect ->
            if (isCorrect != null) {
                updateResultView(isCorrect)
            }
        }

        // Observe test score
        testViewModel.testScore.observe(viewLifecycleOwner) { score ->
            if (score != null) {
                updateScoreDisplay(score)
            }
        }

        // Observe test completion
        testViewModel.testComplete.observe(viewLifecycleOwner) { complete ->
            if (complete == true) {
                showTestCompleteView()
            }
        }

        testViewModel.testFailed.observe(viewLifecycleOwner) { failed ->

        }
        // Observe flashcards list
        testViewModel.attemptsRemaining.observe(viewLifecycleOwner) { attempts ->
            val maxAttempts = 3  // You might want to track this separately
            binding.attemptsRemainingText.text = "Attempts: $attempts/$maxAttempts"
        }

        testViewModel.timeRemaining.observe(viewLifecycleOwner) { time ->
            binding.timeRemainingText.text = time ?: "--:--"
        }
        testViewModel.timeProgress.observe(viewLifecycleOwner) { progress ->
            progress?.let {
                // Update progress bar (0-100 scale)
                binding.progressBarTime.progress = (it * 100).toInt()
            }
        }
        testViewModel.skippedCards.observe(viewLifecycleOwner) { skipped ->
        }
        testViewModel.flashcards.observe(viewLifecycleOwner) { flashcards ->
            if (flashcards.isNullOrEmpty()) {
                showEmptyState()
            } else {
                updateCardDisplay()
                updateProgress()
            }
        }
    }


    private fun updateCardDisplay() {
        val currentCard = testViewModel.getCurrentCard()
        currentCard?.let { flashcard ->
            binding.questionText.text = flashcard.frontText
            // Clear the answer input for new question
            binding.etUserAnswer.setText("")
            binding.etUserAnswer.requestFocus()
        } ?: run {
            binding.questionText.text = "No question available"
        }
    }

    private fun updateProgress() {
        val progressText = testViewModel.getProgressText()
        binding.progressText.text = "Question $progressText"
    }

    private fun updateScoreDisplay(score: Pair<Int, Int>) {
        val (correct, total) = score
        val percentage = testViewModel.getScorePercentage()

        binding.scoreText.text = "Score: $correct/$total"
    }

    private fun restartTest() {

        binding.testCompleteContainer.visibility = View.GONE

        binding.progressText.visibility = View.VISIBLE
        binding.scoreText.visibility = View.VISIBLE
        binding.timeRemainingText.visibility = View.VISIBLE
        binding.attemptsRemainingText.visibility = View.VISIBLE
        binding.progressBarTime.visibility = View.VISIBLE
        binding.btnSubmit.visibility = View.VISIBLE
        binding.btnSkip.visibility = View.VISIBLE
        binding.etUserAnswer.visibility = View.VISIBLE
        binding.questionText.visibility = View.VISIBLE

        binding.resultContainer.visibility = View.GONE

        testViewModel.resetTest()

        binding.etUserAnswer.setText("")
        binding.etUserAnswer.isEnabled = true
        binding.etUserAnswer.requestFocus()
        binding.btnSubmit.isEnabled = false

        showConfigDialog()
    }

    private fun showResultView() {
        // Get current card data and user's answer
        val currentCard = testViewModel.getCurrentCard()
        val userAnswer = testViewModel.userAnswer.value ?: ""


        binding.etUserAnswer.setText(userAnswer)
        binding.etUserAnswer.isEnabled = false

        // If you have resultTitleText, use it. Otherwise, we'll handle it differently
        binding.resultTitleText.text = "Result"

        // Show result container and hide input buttons
        binding.resultContainer.visibility = View.VISIBLE
        binding.btnSubmit.visibility = View.GONE
        binding.btnSkip.visibility = View.GONE
       }

    private fun updateResultView(isCorrect: Boolean) {
        if (isCorrect) {
            binding.resultTitleText.text = "Correct! ✓"
            binding.resultTitleText.setTextColor(requireContext().getColor(android.R.color.holo_green_dark))
            binding.resultContainer.setBackgroundColor(requireContext().getColor(android.R.color.holo_green_light))
        } else {
            binding.resultTitleText.text = "Incorrect! ✗"
            binding.resultTitleText.setTextColor(requireContext().getColor(android.R.color.holo_red_dark))
            binding.resultContainer.setBackgroundColor(requireContext().getColor(android.R.color.holo_red_light))
        }
    }

    private fun hideResultView() {
        binding.resultContainer.visibility = View.GONE
        binding.btnSubmit.visibility = View.VISIBLE
        binding.btnSkip.visibility = View.VISIBLE
        binding.etUserAnswer.isEnabled = true
        binding.etUserAnswer.requestFocus()
    }

    private fun showTestCompleteView() {
        binding.testCompleteContainer.visibility = View.VISIBLE

        // Hide the main test UI
        binding.progressText.visibility = View.GONE
        binding.scoreText.visibility = View.GONE
        binding.timeRemainingText.visibility = View.GONE
        binding.attemptsRemainingText.visibility = View.GONE
        binding.progressBarTime.visibility = View.GONE
        binding.btnSubmit.visibility = View.GONE
        binding.btnSkip.visibility = View.GONE
        binding.etUserAnswer.visibility = View.GONE
        binding.questionText.visibility = View.GONE

        // Get test statistics
        val score = testViewModel.testScore.value ?: Pair(0, 0)
        val (correct, total) = score
        val skipped = testViewModel.skippedCards.value ?: 0
        val percentage = testViewModel.getScorePercentage()
        val isFailed = testViewModel.testFailed.value == true

        // Update final score display
        binding.finalScoreText.text = "Score: $correct/$total (${percentage.toInt()}%)"

        // Create detailed status message
        val statusMessage = StringBuilder()

        if (isFailed) {
            binding.testResultText.text = "Test Failed"
            binding.testResultText.setTextColor(
                requireContext().getColor(android.R.color.holo_red_dark)
            )
            statusMessage.append("You ran out of attempts or time!\n\n")
        } else {
            binding.testResultText.text = "Test Complete"
            binding.testResultText.setTextColor(
                requireContext().getColor(android.R.color.holo_green_dark)
            )
            statusMessage.append("Test completed successfully!\n\n")
        }

        // Add statistics
        statusMessage.append("Correct Answers: $correct\n")
        statusMessage.append("Total Questions: $total\n")
        statusMessage.append("Skipped Questions: $skipped\n")
        statusMessage.append("Accuracy: ${percentage.toInt()}%")

        binding.testStatusText.text = statusMessage.toString()

        // Style restart button based on result
        if (isFailed) {
            binding.btnRestartTest.setBackgroundColor(
                requireContext().getColor(android.R.color.holo_red_dark)
            )
            binding.btnRestartTest.text = "Try Again"
        } else {
            binding.btnRestartTest.setBackgroundColor(
                requireContext().getColor(android.R.color.holo_green_dark)
            )
            binding.btnRestartTest.text = "Restart Test"
        }
    }


    private fun showEmptyState() {
        binding.questionText.text = "No flashcards to test"
        binding.etUserAnswer.isEnabled = false
        binding.btnSubmit.isEnabled = false
        binding.btnSkip.isEnabled = false
        binding.progressText.text = "No questions"
        binding.scoreText.text = "Score: 0/0"
        binding.timeRemainingText.text = "--:--"
        binding.attemptsRemainingText.text = "Attempts: 0/0"
        binding.progressBarTime.progress = 0
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
class TestViewModelFactory(
    private val folderId: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return TestViewModel(UUID.fromString(folderId))as T
    }
}
