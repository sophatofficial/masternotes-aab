package com.example.data

data class ScannedDocument(
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String,
    val format: String, // "PDF" or "IMAGE"
    val dateScanned: String,
    val fileSize: String,
    val extractedText: String,
    val documentType: String = "Contract", // "Contract", "Invoice", "ID Card", "Receipt", "Article"
    val pageCount: Int = 1,
    val isFavorite: Boolean = false
)
