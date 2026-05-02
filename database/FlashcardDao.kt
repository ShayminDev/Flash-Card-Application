package com.example.flashcardproto1.database;

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.flashcardproto1.Flashcard
import com.example.flashcardproto1.Folder
import kotlinx.coroutines.flow.Flow
import java.util.UUID


@Dao
 interface FlashcardDao {
 @Query("SELECT * FROM folder ORDER BY date DESC")
 fun getFolders(): Flow<List<Folder>>

 @Query("SELECT * FROM folder WHERE id=(:id)")
 suspend fun getFolder(id: UUID): Folder?

 @Insert
 suspend fun insertFolder(folder: Folder)

 @Update
 suspend fun updateFolder(folder: Folder)

 @Delete
 suspend fun deleteFolder(folder: Folder)

 // Flashcard operations
 @Query("SELECT * FROM flashcard WHERE folderId=(:folderId) ORDER BY date DESC")
 fun getFlashcardsForFolder(folderId: UUID): Flow<List<Flashcard>>

 @Query("SELECT * FROM flashcard WHERE id=(:id)")
 suspend fun getFlashcard(id: UUID): Flashcard?

 @Insert
 suspend fun insertFlashcard(flashcard: Flashcard)

 @Update
 suspend fun updateFlashcard(flashcard: Flashcard)

 @Delete
 suspend fun deleteFlashcard(flashcard: Flashcard)

 @Query("DELETE FROM flashcard WHERE folderId = :folderId")
 suspend fun deleteFlashcardsByFolder(folderId: UUID)
}
