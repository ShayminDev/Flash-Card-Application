package com.example.flashcardproto1

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date
import java.util.UUID

@Entity
data class Folder (
    @PrimaryKey val id: UUID = UUID.randomUUID(),
    val name:String,
    val description:String = "",
    val date:Date = Date()
    )


