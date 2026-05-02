package com.example.flashcardproto1.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.flashcardproto1.Flashcard
import com.example.flashcardproto1.Folder

@Database(entities = [Folder::class, Flashcard::class], version = 1, exportSchema = false)
@TypeConverters(DateTypeConverters::class)
abstract class FlashcardDatabase : RoomDatabase() {
    abstract fun flashcardDao(): FlashcardDao
}

