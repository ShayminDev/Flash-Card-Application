package com.example.flashcardproto1

import android.content.Context
import androidx.room.Room
import com.example.flashcardproto1.database.FlashcardDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.UUID

private const val DATABASE_NAME = "flashcard-database"

class FlashcardRepository private constructor(
    context: Context,
    private val coroutineScope: CoroutineScope = GlobalScope
) {
    private val database: FlashcardDatabase = Room.databaseBuilder(
        context.applicationContext,
        FlashcardDatabase::class.java,
        DATABASE_NAME
    ).build()

    private val flashcardDao = database.flashcardDao()
    fun getFolders(): Flow<List<Folder>> = flashcardDao.getFolders()

    suspend fun getFolder(id: UUID): Folder? = flashcardDao.getFolder(id)
    suspend fun addFolder(folder: Folder) = flashcardDao.insertFolder(folder)

    fun updateFolder(folder: Folder) {
        coroutineScope.launch {
            flashcardDao.updateFolder(folder)
        }
    }
    fun deleteFolder(folder: Folder) {
        coroutineScope.launch {
            flashcardDao.deleteFlashcardsByFolder(folder.id)
            flashcardDao.deleteFolder(folder)
        }
    }

    fun getFlashcardsForFolder(folderId: UUID): Flow<List<Flashcard>> =
        flashcardDao.getFlashcardsForFolder(folderId)

    suspend fun getFlashcard(id: UUID): Flashcard? = flashcardDao.getFlashcard(id)
    suspend fun addFlashcard(flashcard: Flashcard) = flashcardDao.insertFlashcard(flashcard)

    fun updateFlashcard(flashcard: Flashcard) {
        coroutineScope.launch {
            flashcardDao.updateFlashcard(flashcard)
        }
    }
    fun deleteFlashcard(flashcard: Flashcard) {
        coroutineScope.launch {
            flashcardDao.deleteFlashcard(flashcard)
        }
    }

    companion object {
        private var INSTANCE: FlashcardRepository? = null

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = FlashcardRepository(context)
            }
        }

        fun get(): FlashcardRepository {
            return INSTANCE
                ?: throw IllegalStateException("FlashcardRepository must be initialized")
        }
    }
}