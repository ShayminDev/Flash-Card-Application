package com.example.flashcardproto1


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.flashcardproto1.databinding.ListItemFolderBinding
import java.util.UUID

class FolderHolder(
    private val binding: ListItemFolderBinding
): RecyclerView.ViewHolder(binding.root) {
    fun bind(folder: Folder, onFolderClicked: (UUID) -> Unit) {
        binding.folderName.text = folder.name
        binding.folderDescription.text = folder.description
        binding.folderDate.text = folder.date.toString()

        binding.root.setOnClickListener {
            onFolderClicked(folder.id)
        }

        }
    }


class FolderListAdapter (
    private val folders: List<Folder>,
    private val onFolderClicked: (folderId: UUID) -> Unit
)   : RecyclerView.Adapter<FolderHolder>(){

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FolderHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemFolderBinding.inflate(inflater, parent, false)
        return FolderHolder(binding)
    }
    override fun onBindViewHolder(holder: FolderHolder, position: Int) {
        val folder = folders[position]
        holder.bind(folder, onFolderClicked)
    }
    override fun getItemCount() = folders.size

}
