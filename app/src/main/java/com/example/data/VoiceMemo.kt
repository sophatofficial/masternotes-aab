package com.example.data

data class VoiceMemo(
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String,
    val durationSeconds: Int,
    val timestamp: String,
    val transcript: String,
    val tags: List<String> = listOf("Voice", "Memo"),
    val audioWaveform: List<Float> = listOf(0.2f, 0.5f, 0.8f, 0.3f, 0.9f, 0.6f, 0.4f, 0.7f, 0.2f, 0.5f, 0.9f, 0.3f),
    val isPlaying: Boolean = false,
    val currentProgress: Float = 0f
)
