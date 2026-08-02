package com.example.data.repository

import com.example.data.db.NoteDao
import com.example.data.db.NoteEntity
import com.example.data.db.TaskDao
import com.example.data.db.TaskEntity
import kotlinx.coroutines.flow.Flow

class NoteRepository(
    private val noteDao: NoteDao,
    private val taskDao: TaskDao
) {
    val allNotes: Flow<List<NoteEntity>> = noteDao.getAllNotes()
    val pinnedAndStarredNotes: Flow<List<NoteEntity>> = noteDao.getPinnedAndStarredNotes()
    val allTasks: Flow<List<TaskEntity>> = taskDao.getAllTasks()

    suspend fun getNoteById(id: Long): NoteEntity? = noteDao.getNoteById(id)

    suspend fun insertNote(note: NoteEntity): Long = noteDao.insertNote(note)

    suspend fun updateNote(note: NoteEntity) = noteDao.updateNote(note)

    suspend fun deleteNote(note: NoteEntity) = noteDao.deleteNote(note)

    fun searchNotes(query: String): Flow<List<NoteEntity>> = noteDao.searchNotes(query)

    suspend fun insertTask(task: TaskEntity): Long = taskDao.insertTask(task)

    suspend fun updateTask(task: TaskEntity) = taskDao.updateTask(task)

    suspend fun deleteTask(task: TaskEntity) = taskDao.deleteTask(task)
}
