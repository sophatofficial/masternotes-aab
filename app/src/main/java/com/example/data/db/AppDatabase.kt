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
    version = 1,
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

                // Default Notes
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
                    tags = "Research,UX",
                    isPinned = false,
                    isStarred = true,
                    wordCount = 45
                )

                val note3 = NoteEntity(
                    title = "Personal Goals 2026",
                    content = "1. Master Jetpack Compose and Material 3 design\n2. Daily 25-minute Pomodoro focus sessions\n3. Read 12 books on knowledge management and Second Brain methodologies",
                    folder = "Personal",
                    tags = "Goals,Growth",
                    isPinned = true,
                    isStarred = false,
                    wordCount = 30
                )

                val note4 = NoteEntity(
                    title = "Deep Work Strategy",
                    content = "Relates to Neural Networks and Focus methods. Schedule high priority tasks between 2:00 PM and 4:00 PM.",
                    folder = "Personal",
                    tags = "Productivity",
                    isPinned = false,
                    isStarred = false,
                    wordCount = 22
                )

                noteDao.insertNote(note1)
                noteDao.insertNote(note2)
                noteDao.insertNote(note3)
                noteDao.insertNote(note4)

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
