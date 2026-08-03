package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [NoteEntity::class, TaskEntity::class, GraphConnectionEntity::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
    abstract fun taskDao(): TaskDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "master_notes_db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateInitialData(database)
                    }
                }
            }

            private suspend fun populateInitialData(db: AppDatabase) {
                val noteDao = db.noteDao()
                val taskDao = db.taskDao()

                // Default Notes across diverse note types
                val note1 = NoteEntity(
                    title = "Product Roadmap 2026",
                    content = "Executive Summary\n\nProject Phoenix represents our shift towards an autonomous, AI-driven knowledge ecosystem. This strategy outlines the foundational pillars required to transition from static data storage to a dynamic, generative intelligence layer.\n\n```typescript\ninterface StrategyNode {\n  id: string;\n  priority: 'high' | 'medium' | 'low';\n  connectedEntities: string[];\n  isActive: boolean;\n}\n\nconst phoenixCore = async () => {\n  // Initialize the generative bridge\n  await IntelligenceLayer.connect();\n};\n```\n\nAction Items:\n- [x] Define core architecture for the Graph Node API\n- [ ] Onboard generative design specialists for UI overhaul\n- [ ] Secure Level 3 high-priority interaction popovers",
                    folder = "Work",
                    tags = "Strategy,Design,AI",
                    isPinned = true,
                    isStarred = true,
                    wordCount = 120
                )

                val note2 = NoteEntity(
                    title = "User Interview Notes",
                    content = "Feedback from Sarah from the Enterprise team regarding the new workspace switcher flow.\n- Wants faster shortcuts for fuzzy search\n- Appreciates offline AI summarization\n- Requested auto-generated meeting minutes",
                    folder = "Work",
                    tags = "Research,UX,Meeting",
                    isPinned = false,
                    isStarred = true,
                    wordCount = 45
                )

                val note3 = NoteEntity(
                    title = "Jetpack Compose Performance Spec",
                    content = "## Architecture Spec\n```kotlin\n@Composable\nfun FastNoteList(notes: List<NoteEntity>) {\n    LazyColumn {\n        items(notes, key = { it.id }) { note ->\n            NoteCard(note)\n        }\n    }\n}\n```\n- Use `derivedStateOf` to prevent redundant recompositions\n- Prefer Stable types for data models\n- Leverage Room Flow observe queries for instant UI updates",
                    folder = "Development",
                    tags = "Code,Kotlin,Architecture",
                    isPinned = true,
                    isStarred = true,
                    wordCount = 60
                )

                val note4 = NoteEntity(
                    title = "Cell Biology & Mitochondria Overview",
                    content = "## Key Concepts & Study Flashcards\n- **Mitochondria**: The powerhouse of the cell responsible for generating ATP through oxidative phosphorylation.\n- **ATP Synthase**: Enzyme that creates energy storage molecule ATP.\n\n## Flashcards\nQ: What is cellular respiration?\nA: The metabolic process converting nutrients into ATP.\n\nQ: What is the inner membrane fold called?\nA: Cristae.",
                    folder = "Education",
                    tags = "Study,Biology,Flashcards",
                    isPinned = false,
                    isStarred = false,
                    wordCount = 58
                )

                val note5 = NoteEntity(
                    title = "August Budget & Monthly Expenses",
                    content = "## Income Breakdown\n- Primary Salary: $5,200\n- Side Projects: $950\n\n## Fixed Monthly Expenses\n- Rent & Utilities: $1,850\n- Cloud Servers & Tools: $120\n- Health & Insurance: $340\n\n## Savings & Investment\n- Index Funds: $1,500\n- Emergency Fund: $500",
                    folder = "Finance",
                    tags = "Finance,Budget,Expenses",
                    isPinned = false,
                    isStarred = true,
                    wordCount = 50
                )

                val note6 = NoteEntity(
                    title = "Tokyo Travel Checklist",
                    content = "## High Priority Essentials\n- [x] Passport & JR Rail Pass\n- [x] Suica IC Card on Apple Wallet / Google Wallet\n- [ ] Pocket Wi-Fi reservation\n- [ ] Hotel booking confirmations\n\n## Places to Visit\n- Akihabara Electronics Town\n- Shinjuku Gyoen National Garden\n- Shibuya Sky Observation Deck",
                    folder = "Personal",
                    tags = "Checklist,Travel,Planner",
                    isPinned = true,
                    isStarred = false,
                    wordCount = 48
                )

                val note7 = NoteEntity(
                    title = "Notes on 'Building a Second Brain'",
                    content = "## CODE Framework Summary\n1. **Capture**: Keep what resonates.\n2. **Organize**: Organize for actionability using PARA (Projects, Areas, Resources, Archives).\n3. **Distill**: Extract the core essence with progressive summarization.\n4. **Express**: Show your work and share knowledge.\n\n> \"Your mind is for having ideas, not holding them.\"",
                    folder = "Books",
                    tags = "Reading,SecondBrain,Quotes",
                    isPinned = false,
                    isStarred = true,
                    wordCount = 62
                )

                val note8 = NoteEntity(
                    title = "Daily Gratitude & Reflection",
                    content = "## Today's Wins\n- Completed the Room database & Gemini AI integration.\n- Had a great 30-minute afternoon run in the park.\n\n## Gratitude\n1. Supportive team members.\n2. A quiet environment for deep focus.\n3. Fresh morning coffee.",
                    folder = "Journal",
                    tags = "Gratitude,Daily,Mindset",
                    isPinned = false,
                    isStarred = false,
                    wordCount = 42
                )

                val note9 = NoteEntity(
                    title = "Office Lease Agreement Summary",
                    content = "## OCR Scanned Document Highlights\n- **Landlord**: Apex Real Estate Group\n- **Term**: 24 Months starting Sept 1, 2026\n- **Monthly Rent**: $3,400\n- **Maintenance**: Landlord covers HVAC and structural repairs.\n- **Notice Period**: 60 days required prior to lease expiration.",
                    folder = "Research",
                    tags = "Scan,OCR,Contract",
                    isPinned = false,
                    isStarred = false,
                    wordCount = 45
                )

                noteDao.insertNote(note1)
                noteDao.insertNote(note2)
                noteDao.insertNote(note3)
                noteDao.insertNote(note4)
                noteDao.insertNote(note5)
                noteDao.insertNote(note6)
                noteDao.insertNote(note7)
                noteDao.insertNote(note8)
                noteDao.insertNote(note9)

                // Default Tasks
                taskDao.insertTask(
                    TaskEntity(
                        title = "Finalize quarterly projection report",
                        dueDate = "Due 2:00 PM",
                        priority = "High",
                        category = "Focus"
                    )
                )
                taskDao.insertTask(
                    TaskEntity(
                        title = "Review design system visual tokens",
                        dueDate = "Due 5:00 PM",
                        priority = "Medium",
                        category = "Focus"
                    )
                )
                taskDao.insertTask(
                    TaskEntity(
                        title = "Sync with mobile engineering team",
                        dueDate = "Due 6:30 PM",
                        priority = "Low",
                        category = "Focus"
                    )
                )
                taskDao.insertTask(
                    TaskEntity(
                        title = "Draft initial email to vendor",
                        dueDate = "Today",
                        noteSource = "Client Meeting Oct 24",
                        category = "Linked"
                    )
                )
                taskDao.insertTask(
                    TaskEntity(
                        title = "Research glassmorphism effects",
                        dueDate = "Tomorrow",
                        noteSource = "UI Inspiration Board",
                        category = "Linked"
                    )
                )
            }
        }
    }
}
