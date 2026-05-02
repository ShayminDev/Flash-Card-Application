package com.example.flashcardproto1


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.UUID


class FlashcardListAdapter(
    private var flashcards: List<Flashcard>,
    private val onFlashcardClicked: (flashcardId: UUID) -> Unit,
    private val onFlashcardDeleted: (flashcard: Flashcard) -> Unit
) : RecyclerView.Adapter<FlashcardListAdapter.FlashcardViewHolder>() {

    class FlashcardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val frontText: TextView = itemView.findViewById(R.id.flashcardFrontText)
        val backText: TextView = itemView.findViewById(R.id.flashcardBackText)
        val dateText: TextView = itemView.findViewById(R.id.flashcardDate)
        val deleteButton: ImageButton = itemView.findViewById(R.id.btnDeleteFlashcard)

    }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlashcardViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_flashcard, parent, false)
            return FlashcardViewHolder(view)
    }

    override fun onBindViewHolder(holder: FlashcardViewHolder, position: Int) {
        val flashcard = flashcards[position]

        holder.frontText.text = flashcard.frontText
        holder.backText.text = flashcard.backText
        holder.dateText.text = flashcard.date.toString()

        holder.itemView.setOnClickListener {
            onFlashcardClicked(flashcard.id)
        }
        holder.deleteButton.setOnClickListener {
            onFlashcardDeleted(flashcard)
        }
    }
    override fun getItemCount() = flashcards.size

    fun updateFlashcards(newFlashcards: List<Flashcard>) {
        flashcards = newFlashcards
        notifyDataSetChanged()
    }
}
