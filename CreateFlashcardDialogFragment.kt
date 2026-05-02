package com.example.flashcardproto1

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID

class CreateFlashcardDialogFragment : DialogFragment(){

    private val folderListViewModel: FolderListViewModel by viewModels()
    private val args: CreateFlashcardDialogFragmentArgs by navArgs()


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val folderId= args.folderId

        val view = requireActivity().layoutInflater.inflate(R.layout.dialog_create_flashcard, null)
        val editFrontText = view.findViewById<EditText>(R.id.editFrontText)
        val editBackText = view.findViewById<EditText>(R.id.editBackText)

        return AlertDialog.Builder(requireContext())
            .setTitle("Create Flashcard")
            .setView(view)
            .setPositiveButton("Create") { _, _ ->
                val frontText = editFrontText.text.toString()
                val backText = editBackText.text.toString()

                if (frontText.isNotEmpty() && backText.isNotEmpty()) {
                    lifecycleScope.launch{
                            val flashcard = Flashcard(
                                id = UUID.randomUUID(),
                                folderId = folderId,
                                frontText = frontText,
                                backText = backText,
                                date = Date()
                            )
                        folderListViewModel.addFlashcard(flashcard)
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .create()
    }
}
