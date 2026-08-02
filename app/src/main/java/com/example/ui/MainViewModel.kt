package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.GeminiAiService
import com.example.data.db.AppDatabase
import com.example.data.db.NoteEntity
import com.example.data.db.TaskEntity
import com.example.data.repository.NoteRepository
import com.example.ui.language.AppLanguage
import com.example.ui.language.AppStrings
import com.example.ui.language.LocalizedStrings
import com.example.ui.theme.AppStyleTheme
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: NoteRepository

    init {
        val database = AppDatabase.getDatabase(application)
        repository = NoteRepository(database.noteDao(), database.taskDao())
    }

    // App Visual Style Theme
    private val _appTheme = MutableStateFlow(AppStyleTheme.OXFORD_BLUE)
    val appTheme: StateFlow<AppStyleTheme> = _appTheme.asStateFlow()

    // App Language Localization
    private val _appLanguage = MutableStateFlow(AppLanguage.ENGLISH)
    val appLanguage: StateFlow<AppLanguage> = _appLanguage.asStateFlow()

    val strings: StateFlow<LocalizedStrings> = _appLanguage
        .map { AppStrings.get(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppStrings.get(AppLanguage.ENGLISH)
        )

    // Selected folder filter
    private val _selectedFolder = MutableStateFlow("All")
    val selectedFolder: StateFlow<String> = _selectedFolder.asStateFlow()

    // Voice Memos Library
    private val _voiceMemos = MutableStateFlow(
        listOf(
            com.example.data.VoiceMemo(
                title = "Product Strategy Brainstorm",
                durationSeconds = 84,
                timestamp = "Today, 10:15 AM",
                transcript = "Key takeaways from morning sync: 1. Launch offline vector graph database. 2. Implement end-to-end AES-256 encryption. 3. Add voice memo transcription with Gemini."
            ),
            com.example.data.VoiceMemo(
                title = "Architecture Refactoring Notes",
                durationSeconds = 142,
                timestamp = "Yesterday, 4:30 PM",
                transcript = "Migration plan for Jetpack Compose state isolation and Kotlin Coroutines flow debounce optimization."
            )
        )
    )
    val voiceMemos: StateFlow<List<com.example.data.VoiceMemo>> = _voiceMemos.asStateFlow()

    fun addVoiceMemo(title: String, durationSeconds: Int, transcript: String) {
        val newMemo = com.example.data.VoiceMemo(
            title = title,
            durationSeconds = durationSeconds,
            timestamp = "Just Now",
            transcript = transcript
        )
        _voiceMemos.value = listOf(newMemo) + _voiceMemos.value
    }

    fun deleteVoiceMemo(memoId: String) {
        _voiceMemos.value = _voiceMemos.value.filter { it.id != memoId }
    }

    // Scanned Documents & PDFs Library
    private val _scannedDocuments = MutableStateFlow(
        listOf(
            com.example.data.ScannedDocument(
                title = "Service_Agreement_2026.pdf",
                format = "PDF",
                dateScanned = "Today, 11:20 AM",
                fileSize = "1.4 MB",
                extractedText = "Contract Agreement for Mobile Application Development Services. Terms and Conditions included.",
                documentType = "Contract",
                pageCount = 3
            ),
            com.example.data.ScannedDocument(
                title = "Office_Supplies_Receipt.jpg",
                format = "IMAGE",
                dateScanned = "Yesterday, 2:15 PM",
                fileSize = "620 KB",
                extractedText = "Total Amount: $148.50. Items: Ergonomic Keyboard, LED Desk Lamp, Notebooks.",
                documentType = "Receipt",
                pageCount = 1
            )
        )
    )
    val scannedDocuments: StateFlow<List<com.example.data.ScannedDocument>> = _scannedDocuments.asStateFlow()

    fun addScannedDocument(title: String, format: String, extractedText: String, docType: String, fileSize: String = "850 KB") {
        val newDoc = com.example.data.ScannedDocument(
            title = if (format == "PDF") "${title.replace(" ", "_")}.pdf" else "${title.replace(" ", "_")}.jpg",
            format = format,
            dateScanned = "Just Now",
            fileSize = fileSize,
            extractedText = extractedText,
            documentType = docType
        )
        _scannedDocuments.value = listOf(newDoc) + _scannedDocuments.value
    }

    fun deleteScannedDocument(docId: String) {
        _scannedDocuments.value = _scannedDocuments.value.filter { it.id != docId }
    }

    // Search query
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Notes list combining DB notes with folder filter and search
    val notes: StateFlow<List<NoteEntity>> = combine(
        repository.allNotes,
        _selectedFolder,
        _searchQuery
    ) { allNotes, folder, query ->
        allNotes.filter { note ->
            val matchesFolder = (folder == "All" || note.folder.equals(folder, ignoreCase = true))
            val matchesQuery = query.isEmpty() ||
                    note.title.contains(query, ignoreCase = true) ||
                    note.content.contains(query, ignoreCase = true) ||
                    note.tags.contains(query, ignoreCase = true)
            matchesFolder && matchesQuery
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Pinned & Starred notes
    val pinnedAndStarredNotes: StateFlow<List<NoteEntity>> = repository.pinnedAndStarredNotes.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // All tasks
    val tasks: StateFlow<List<TaskEntity>> = repository.allTasks.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Current note in editor
    private val _currentEditingNote = MutableStateFlow<NoteEntity?>(null)
    val currentEditingNote: StateFlow<NoteEntity?> = _currentEditingNote.asStateFlow()

    // Pomodoro Timer State
    private val _timerTimeLeft = MutableStateFlow(25 * 60) // 25 minutes
    val timerTimeLeft: StateFlow<Int> = _timerTimeLeft.asStateFlow()

    private val _isTimerRunning = MutableStateFlow(false)
    val isTimerRunning: StateFlow<Boolean> = _isTimerRunning.asStateFlow()

    private val _completedCycles = MutableStateFlow(2)
    val completedCycles: StateFlow<Int> = _completedCycles.asStateFlow()

    private var timerJob: Job? = null

    // AI States
    private val _aiResponseText = MutableStateFlow<String?>(null)
    val aiResponseText: StateFlow<String?> = _aiResponseText.asStateFlow()

    private val _isAiLoading = MutableStateFlow(false)
    val isAiLoading: StateFlow<Boolean> = _isAiLoading.asStateFlow()

    // In-App Purchase state ($2.50 Lifetime)
    private val _isProUnlocked = MutableStateFlow(false)
    val isProUnlocked: StateFlow<Boolean> = _isProUnlocked.asStateFlow()

    // Command palette state
    private val _isCommandPaletteOpen = MutableStateFlow(false)
    val isCommandPaletteOpen: StateFlow<Boolean> = _isCommandPaletteOpen.asStateFlow()

    fun setAppTheme(theme: AppStyleTheme) {
        _appTheme.value = theme
    }

    fun setAppLanguage(language: AppLanguage) {
        _appLanguage.value = language
    }

    fun setSelectedFolder(folder: String) {
        _selectedFolder.value = folder
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setCommandPaletteOpen(open: Boolean) {
        _isCommandPaletteOpen.value = open
    }

    fun selectNoteForEditing(note: NoteEntity) {
        _currentEditingNote.value = note
    }

    fun createNewNote(title: String = "Untitled Note", folder: String = "Personal") {
        val newNote = NoteEntity(
            title = title,
            content = "",
            folder = folder,
            tags = "Draft"
        )
        viewModelScope.launch {
            val id = repository.insertNote(newNote)
            _currentEditingNote.value = newNote.copy(id = id)
        }
    }

    fun saveCurrentNote(title: String, content: String, tags: String = "General") {
        val current = _currentEditingNote.value ?: return
        val wordCount = content.trim().split("\\s+".toRegex()).filter { it.isNotBlank() }.size
        val updated = current.copy(
            title = title,
            content = content,
            tags = tags,
            wordCount = wordCount,
            updatedAt = System.currentTimeMillis()
        )
        _currentEditingNote.value = updated
        viewModelScope.launch {
            repository.updateNote(updated)
        }
    }

    fun togglePinNote(note: NoteEntity) {
        viewModelScope.launch {
            repository.updateNote(note.copy(isPinned = !note.isPinned))
        }
    }

    fun toggleStarNote(note: NoteEntity) {
        viewModelScope.launch {
            repository.updateNote(note.copy(isStarred = !note.isStarred))
        }
    }

    fun deleteNote(note: NoteEntity) {
        viewModelScope.launch {
            repository.deleteNote(note)
            if (_currentEditingNote.value?.id == note.id) {
                _currentEditingNote.value = null
            }
        }
    }

    // Task Actions
    fun addTask(title: String, dueDate: String = "Today", noteSource: String? = null) {
        viewModelScope.launch {
            repository.insertTask(
                TaskEntity(
                    title = title,
                    dueDate = dueDate,
                    noteSource = noteSource,
                    category = "Focus"
                )
            )
        }
    }

    fun toggleTaskCompletion(task: TaskEntity) {
        viewModelScope.launch {
            repository.updateTask(task.copy(isCompleted = !task.isCompleted))
        }
    }

    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    // Pomodoro Timer Logic
    fun toggleTimer() {
        if (_isTimerRunning.value) {
            timerJob?.cancel()
            _isTimerRunning.value = false
        } else {
            _isTimerRunning.value = true
            timerJob = viewModelScope.launch {
                while (_timerTimeLeft.value > 0) {
                    delay(1000)
                    _timerTimeLeft.value -= 1
                }
                _isTimerRunning.value = false
                _completedCycles.value += 1
                _timerTimeLeft.value = 25 * 60
            }
        }
    }

    fun resetTimer() {
        timerJob?.cancel()
        _isTimerRunning.value = false
        _timerTimeLeft.value = 25 * 60
    }

    // AI Actions
    fun runAiSummarize(title: String, content: String) {
        viewModelScope.launch {
            _isAiLoading.value = true
            val response = GeminiAiService.summarizeNote(title, content)
            _aiResponseText.value = response
            _isAiLoading.value = false
        }
    }

    fun runAiFixGrammar(content: String) {
        viewModelScope.launch {
            _isAiLoading.value = true
            val response = GeminiAiService.fixGrammar(content)
            _aiResponseText.value = response
            _isAiLoading.value = false
        }
    }

    fun runAiBrainstorm(topic: String) {
        viewModelScope.launch {
            _isAiLoading.value = true
            val response = GeminiAiService.generateBrainstorm(topic)
            _aiResponseText.value = response
            _isAiLoading.value = false
        }
    }

    fun runAiExtractTasks(content: String) {
        viewModelScope.launch {
            _isAiLoading.value = true
            val response = GeminiAiService.extractTasks(content)
            _aiResponseText.value = response
            _isAiLoading.value = false
        }
    }

    fun clearAiResponse() {
        _aiResponseText.value = null
    }

    fun unlockProVersion() {
        _isProUnlocked.value = true
    }
}
