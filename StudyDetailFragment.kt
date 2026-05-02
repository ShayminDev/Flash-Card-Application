package com.example.flashcardproto1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.example.flashcardproto1.databinding.FragmentStudyDetailBinding
import java.util.UUID

class StudyDetailFragment : Fragment() {
    private var _binding: FragmentStudyDetailBinding? = null
    private val binding get() = _binding!!

    private val args: StudyDetailFragmentArgs by navArgs()
    private val studyViewModel: StudyViewModel by viewModels{
        StudyViewModelFactory(args.folderId)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStudyDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
        observeStudyData()
    }

    private fun setupClickListeners() {
        binding.btnShowAnswer.setOnClickListener {
        studyViewModel.toggleAnswer()
        }
        binding.btnNext.setOnClickListener {
            studyViewModel.nextCard()
        }
        binding.btnPrevious.setOnClickListener {
            studyViewModel.previousCard()
        }
        binding.btnShuffle.setOnClickListener {
            studyViewModel.shuffleCards()
        }
    }

    private fun observeStudyData() {
        studyViewModel.currentCardIndex.observe(viewLifecycleOwner) { _ ->
            updateCardDisplay()
            updateProgressDisplay()
        }
        studyViewModel.showAnswer.observe(viewLifecycleOwner) { showAnswer ->
            if(showAnswer){
                showAnswerView()
            }else{
                hideAnswerView()
            }
        }
        studyViewModel.flashcards.observe(viewLifecycleOwner) { flashcards ->
            if(flashcards.isEmpty()){
                showEmptyState()
            }else{
                binding.btnShowAnswer.isEnabled = true
                binding.btnNext.isEnabled = true
                binding.btnPrevious.isEnabled = true

                updateCardDisplay()
                updateProgressDisplay()
            }
        }
    }

    private fun updateCardDisplay() {
        val currentCard = studyViewModel.getCurrentCard()
        currentCard?.let {
            flashcard ->
            binding.QuestionText.text = flashcard.frontText
            binding.AnswerText.text = flashcard.backText
        } ?: run {
            binding.QuestionText.text = ""
            binding.AnswerText.text = ""
        }
    }
    private fun updateProgressDisplay() {
        binding.progressText.text = studyViewModel.getProgress()
    }

    private fun showAnswerView() {
        binding.btnShowAnswer.text = "Hide Answer"
        binding.cardAnswer.visibility = View.VISIBLE
    }
    private fun hideAnswerView() {
        binding.btnShowAnswer.text = "Show Answer"

        binding.btnShowAnswer.visibility = View.VISIBLE
        binding.cardAnswer.visibility = View.GONE
    }
    private fun showEmptyState(){
    binding.QuestionText.text = ""
    binding.AnswerText.text = ""
    binding.btnShowAnswer.isEnabled = false
    binding.btnNext.isEnabled = false
    binding.btnPrevious.isEnabled = false
    binding.progressText.text = ""
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
class StudyViewModelFactory(
    private val folderId: UUID
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return StudyViewModel(folderId) as T
    }
}
