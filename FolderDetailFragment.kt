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
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.flashcardproto1.databinding.FragmentFolderDetailBinding
import kotlinx.coroutines.launch

class FolderDetailFragment : Fragment(){
    private  var _binding: FragmentFolderDetailBinding? = null
    private val binding get()= _binding!!

    private val args: FolderDetailFragmentArgs by navArgs()
    private val folderDetailViewModel: FolderDetailViewModel by viewModels {
        FolderDetailViewModel.FolderDetailViewModelFactory(args.folderId)
    }

    private lateinit var flashcardAdapter: FlashcardListAdapter



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFolderDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupFlashcardRecyclerView()

        binding.apply {
            folderName.doOnTextChanged { text, _, _, _ ->
                folderDetailViewModel.updateFolder { oldFolder ->
                    oldFolder.copy(name = text.toString())
                }
            }

            folderDescription.doOnTextChanged { text, _, _, _ ->
                folderDetailViewModel.updateFolder { oldFolder ->
                    oldFolder.copy(description = text.toString())
                }
            }

            fabAddFlashcard.setOnClickListener {
                val action = FolderDetailFragmentDirections.actionCreateFlashcard(args.folderId)
                findNavController().navigate(action)
            }
            binding.fabStudy.setOnClickListener {
                findNavController().navigate(
                    FolderDetailFragmentDirections.startStudySession(args.folderId)
                )
            }
            binding.btnStartTest.setOnClickListener {
                findNavController().navigate(
                    FolderDetailFragmentDirections.startTest(args.folderId.toString())
                )
            }
        }
        observeFolderData()
        observeFlashcardData()
    }

    private fun setupFlashcardRecyclerView() {
        flashcardAdapter = FlashcardListAdapter(
            flashcards = emptyList(),
            onFlashcardClicked = { flashcardId ->
                findNavController().navigate(
                    FolderDetailFragmentDirections.showFlashcardDetail(flashcardId, args.folderId)
                )
            },
            onFlashcardDeleted = { flashcard ->
                showDeleteConfirmationDialog(flashcard)
            }
        )
        binding.flashcardsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = flashcardAdapter
        }
    }

    private fun showDeleteConfirmationDialog(flashcard: Flashcard) {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Delete Flashcard")
            .setMessage("Are you sure you want to delete this flashcard?")
            .setPositiveButton("Delete") { _, _ ->
                viewLifecycleOwner.lifecycleScope.launch {
                    folderDetailViewModel.deleteFlashcard(flashcard)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    private fun observeFolderData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                folderDetailViewModel.folder.collect { folder ->
                    folder?.let { updateFolderUi(it) }
                }
            }
        }
    }

    private fun observeFlashcardData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                folderDetailViewModel.flashcards.collect { flashcards ->
                    flashcardAdapter.updateFlashcards(flashcards)
                }

            }
        }
    }
private fun updateFolderUi(folder: Folder) {
    binding.apply{
        if(folderName.text.toString() != folder.name){
            folderName.setText(folder.name)

            }
        if (folderDescription.text.toString() != folder.description){
            folderDescription.setText(folder.description)
            }
        folderDate.text= folder.date.toString()
        }
    }
    override fun onDestroyView(){
        super.onDestroyView()
        _binding = null
    }
}
