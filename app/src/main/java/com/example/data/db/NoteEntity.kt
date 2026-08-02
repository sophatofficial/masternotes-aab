package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val content: String,
    val folder: String = "Personal",
    val tags: String = "General",
    val isPinned: Boolean = false,
    val isStarred: Boolean = false,
    val isArchived: Boolean = false,
    val isLocked: Boolean = false,
    val wordCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val linkedNoteIds: String = ""
)
