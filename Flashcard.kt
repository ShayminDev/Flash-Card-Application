package com.example.flashcardproto1

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date
import java.util.UUID

@Entity
data class Flashcard (
    @PrimaryKey val id: UUID = UUID.randomUUID(),
    val folderId: UUID,
    val frontText : String,
    val backText:String,
    val date:Date = Date(),
    )
