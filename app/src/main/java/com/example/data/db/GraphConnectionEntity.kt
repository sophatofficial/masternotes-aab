package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "graph_connections")
data class GraphConnectionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sourceTitle: String,
    val targetTitle: String,
    val relationReason: String,
    val semanticSimilarity: Int = 80
)
