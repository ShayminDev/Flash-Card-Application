package com.example.flashcardproto1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.flashcardproto1.databinding.FragmentFolderListBinding
import kotlinx.coroutines.launch
class FolderListFragment : Fragment() {
    private var _binding: FragmentFolderListBinding? = null
        private val binding
        get() = checkNotNull(_binding) {
            "cannot access binding because it is null."
            }
    private val folderListViewModel: FolderListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFolderListBinding.inflate(inflater, container, false)
        binding.folderRecyclerView.layoutManager = LinearLayoutManager(context)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.fabAddFolder.setOnClickListener {
            CreateFolderDialogFragment().show(parentFragmentManager, "CreateFolderDialog")
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                folderListViewModel.folders.collect { folders ->
                    binding.folderRecyclerView.adapter =
                        FolderListAdapter(folders) { folderId ->
                            findNavController().navigate(
                                FolderListFragmentDirections.showFolderDetail(folderId)
                            )
                        }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

