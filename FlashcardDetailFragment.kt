package com.example.flashcardproto1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.example.flashcardproto1.databinding.FragmentFlashcardDetailBinding
import kotlinx.coroutines.launch

class FlashcardDetailFragment : Fragment() {
    private var _binding: FragmentFlashcardDetailBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    private val args: FlashcardDetailFragmentArgs by navArgs()
    private val flashcardDetailViewModel: FlashcardDetailViewModel by viewModels {
        FlashcardDetailViewModelFactory(args.flashcardId)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFlashcardDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            flashcardFrontText.doOnTextChanged { text, _, _, _ ->
                flashcardDetailViewModel.updateFlashcard { oldFlashcard ->
                    oldFlashcard.copy(frontText = text.toString())
                }
            }

            flashcardBackText.doOnTextChanged { text, _, _, _ ->
                flashcardDetailViewModel.updateFlashcard { oldFlashcard ->
                    oldFlashcard.copy(backText = text.toString())
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                flashcardDetailViewModel.flashcard.collect { flashcard ->
                    flashcard?.let { updateUi(it) }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun updateUi(flashcard: Flashcard) {
        binding.apply {
            if (flashcardFrontText.text.toString() != flashcard.frontText) {
                flashcardFrontText.setText(flashcard.frontText)
            }
            if (flashcardBackText.text.toString() != flashcard.backText) {
                flashcardBackText.setText(flashcard.backText)
            }
            flashcardDate.text = flashcard.date.toString()
        }
    }
}