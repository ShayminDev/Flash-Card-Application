package com.example.flashcardproto1

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class CreateFolderDialogFragment : DialogFragment(){
    private val folderListViewModel: FolderListViewModel by viewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = requireActivity().layoutInflater.inflate(R.layout.dialog_create_folder, null)
        val editFolderName = view.findViewById<EditText>(R.id.editFolderName)
        val editFolderDescription = view.findViewById<EditText>(R.id.editFolderDescription)

        return AlertDialog.Builder(requireContext())
            .setView(view)
            .setTitle("Create New Folder")
            .setPositiveButton("Create") { _, _ ->
                val name = editFolderName.text.toString()
                val description = editFolderDescription.text.toString()

                if (name.isNotEmpty()) {
                    lifecycleScope.launch {
                        folderListViewModel.createNewFolder(name, description)
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .create()
    }
}

