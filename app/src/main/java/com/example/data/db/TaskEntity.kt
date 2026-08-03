package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val dueDate: String = "Today",
    val noteSource: String? = null,
    val isCompleted: Boolean = false,
    val priority: String = "Medium",
    val category: String = "Focus",
    val createdAt: Long = System.currentTimeMillis(),
    val taskNotes: String? = null
)
