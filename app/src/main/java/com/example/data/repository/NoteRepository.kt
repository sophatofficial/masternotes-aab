package com.example.data.repository

import android.util.Log
import com.example.data.db.NoteDao
import com.example.data.db.NoteEntity
import com.example.data.db.TaskDao
import com.example.data.db.TaskEntity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class NoteRepository(
    private val noteDao: NoteDao,
    private val taskDao: TaskDao
) {
    val allNotes: Flow<List<NoteEntity>> = noteDao.getAllNotes()
    val pinnedAndStarredNotes: Flow<List<NoteEntity>> = noteDao.getPinnedAndStarredNotes()
    val allTasks: Flow<List<TaskEntity>> = taskDao.getAllTasks()

    suspend fun getNoteById(id: Long): NoteEntity? = noteDao.getNoteById(id)

    suspend fun insertNote(note: NoteEntity): Long {
        val id = noteDao.insertNote(note)
        val savedNote = note.copy(id = id)
        syncNoteToFirestore(savedNote)
        return id
    }

    suspend fun updateNote(note: NoteEntity) {
        noteDao.updateNote(note)
        syncNoteToFirestore(note)
    }

    suspend fun deleteNote(note: NoteEntity) {
        noteDao.deleteNote(note)
        deleteNoteFromFirestore(note.id)
    }

    fun searchNotes(query: String): Flow<List<NoteEntity>> = noteDao.searchNotes(query)

    suspend fun insertTask(task: TaskEntity): Long = taskDao.insertTask(task)

    suspend fun updateTask(task: TaskEntity) = taskDao.updateTask(task)

    suspend fun deleteTask(task: TaskEntity) = taskDao.deleteTask(task)

    suspend fun syncWithFirestore() {
        withContext(Dispatchers.IO) {
            try {
                val auth = com.google.firebase.auth.FirebaseAuth.getInstance()
                val user = auth.currentUser
                val db = FirebaseFirestore.getInstance()
                val collectionRef = if (user != null) {
                    db.collection("users").document(user.uid).collection("notes")
                } else {
                    db.collection("notes")
                }
                val snapshot = collectionRef.get().await()
                for (doc in snapshot.documents) {
                    val title = doc.getString("title") ?: continue
                    val content = doc.getString("content") ?: ""
                    val folder = doc.getString("folder") ?: "Personal"
                    val tags = doc.getString("tags") ?: "General"
                    val isPinned = doc.getBoolean("isPinned") ?: false
                    val isStarred = doc.getBoolean("isStarred") ?: false
                    val remoteNote = NoteEntity(
                        title = title,
                        content = content,
                        folder = folder,
                        tags = tags,
                        isPinned = isPinned,
                        isStarred = isStarred,
                        isSynced = true,
                        updatedAt = System.currentTimeMillis()
                    )
                    noteDao.insertNote(remoteNote)
                }
            } catch (e: Throwable) {
                Log.w("NoteRepository", "Firestore download sync skipped/offline: ${e.message}")
            }
        }
    }

    private suspend fun syncNoteToFirestore(note: NoteEntity) {
        withContext(Dispatchers.IO) {
            try {
                val auth = com.google.firebase.auth.FirebaseAuth.getInstance()
                val user = auth.currentUser
                val db = FirebaseFirestore.getInstance()
                val collectionRef = if (user != null) {
                    db.collection("users").document(user.uid).collection("notes")
                } else {
                    db.collection("notes")
                }
                val data = hashMapOf(
                    "id" to note.id,
                    "title" to note.title,
                    "content" to note.content,
                    "folder" to note.folder,
                    "tags" to note.tags,
                    "isPinned" to note.isPinned,
                    "isStarred" to note.isStarred,
                    "updatedAt" to note.updatedAt,
                    "userId" to (user?.uid ?: "guest")
                )
                collectionRef.document(note.id.toString()).set(data).await()
                noteDao.updateNote(note.copy(isSynced = true))
            } catch (e: Throwable) {
                Log.w("NoteRepository", "Note #${note.id} saved locally in Room; Firestore upload pending connectivity: ${e.message}")
            }
        }
    }

    private suspend fun deleteNoteFromFirestore(noteId: Long) {
        withContext(Dispatchers.IO) {
            try {
                val auth = com.google.firebase.auth.FirebaseAuth.getInstance()
                val user = auth.currentUser
                val db = FirebaseFirestore.getInstance()
                val collectionRef = if (user != null) {
                    db.collection("users").document(user.uid).collection("notes")
                } else {
                    db.collection("notes")
                }
                collectionRef.document(noteId.toString()).delete().await()
            } catch (e: Throwable) {
                Log.w("NoteRepository", "Firestore delete skipped (offline): ${e.message}")
            }
        }
    }
}
