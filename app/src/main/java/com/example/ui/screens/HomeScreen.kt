package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.NoteEntity
import com.example.ui.MainViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onNavigateToEditor: (NoteEntity) -> Unit,
    onNavigateToScanner: () -> Unit,
    onNavigateToTasks: () -> Unit,
    onNavigateToGraph: () -> Unit,
    onNavigateToVoice: () -> Unit
) {
    val notes by viewModel.notes.collectAsState()
    val pinnedAndStarred by viewModel.pinnedAndStarredNotes.collectAsState()
    val selectedFolder by viewModel.selectedFolder.collectAsState()
    val strings by viewModel.strings.collectAsState()

    var showFolderMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                                .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                .clickable { showFolderMenu = true }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (selectedFolder == "All") strings.allNotes else selectedFolder,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Folder Menu",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            DropdownMenu(
                                expanded = showFolderMenu,
                                onDismissRequest = { showFolderMenu = false },
                                modifier = Modifier.background(MaterialTheme.colorScheme.surfaceContainer)
                            ) {
                                listOf("All", "Personal", "Work", "Development", "Education", "Finance", "Books", "Journal", "Research").forEach { folder ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                if (folder == "All") strings.allNotes else folder,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        },
                                        onClick = {
                                            viewModel.setSelectedFolder(folder)
                                            showFolderMenu = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = onNavigateToVoice,
                        modifier = Modifier.testTag("voice_recorder_top_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = strings.voiceRecorderTitle,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(
                        onClick = { viewModel.setCommandPaletteOpen(true) },
                        modifier = Modifier.testTag("search_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(
                        onClick = { viewModel.runAiBrainstorm("Daily Focus and Knowledge Connections") },
                        modifier = Modifier.testTag("ai_toy_button")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.SmartToy,
                            contentDescription = strings.brainstorm,
                            tint = MaterialTheme.colorScheme.tertiary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // 1. Daily Digest (AI Generated Card)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(
                        1.dp,
                        Brush.horizontalGradient(listOf(ElectricIndigo, SkyBlue)),
                        RoundedCornerShape(16.dp)
                    )
                    .background(SurfaceContainer)
                    .padding(20.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = SkyBlue
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Daily Digest",
                                color = SkyBlue,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        }
                        Text(
                            text = "AI GENERATED",
                            color = OnSurfaceVariantText.copy(alpha = 0.6f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 1.sp
                        )
                    }

                    Text(
                        text = "Good morning. You have 3 strategic meetings today. Based on your recent research, the \"Product Roadmap 2026\" note is highly relevant for today's focus.",
                        color = OnSurfaceText,
                        fontSize = 15.sp,
                        lineHeight = 22.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(OxfordBlue.copy(alpha = 0.6f))
                                .border(1.dp, OutlineVariantBorder.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.PriorityHigh,
                                    contentDescription = null,
                                    tint = ElectricIndigo,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Finalize Q3 Report", color = OnSurfaceText, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(OxfordBlue.copy(alpha = 0.6f))
                                .border(1.dp, OutlineVariantBorder.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Link,
                                    contentDescription = null,
                                    tint = SkyBlue,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Rev Sync", color = OnSurfaceText, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }
            }

            // 2. Note Types & Templates Creator Bar
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Note Types & Templates",
                    color = OnSurfaceText,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(end = 16.dp)
                ) {
                    val templateTypes = listOf(
                        TemplateItem("Meeting Notes", "Work", "Meeting, Minutes", Icons.Outlined.EventNote, Color(0xFF6366F1), "## Attendees\n- \n\n## Agenda\n1. \n2. \n\n## Discussion\n- \n\n## Action Items\n- [ ] "),
                        TemplateItem("Code Spec", "Development", "Code, Kotlin", Icons.Outlined.Code, Color(0xFF10B981), "## Architecture Spec\n\n```kotlin\nclass CoreEngine {\n    fun start() {\n    }\n}\n```\n\n- Performance target: < 100ms"),
                        TemplateItem("Study Flashcards", "Education", "Study, Flashcards", Icons.Outlined.School, Color(0xFFF59E0B), "## Key Concepts\n- Concept 1:\n- Concept 2:\n\n## Flashcards\nQ: \nA: "),
                        TemplateItem("Travel Checklist", "Personal", "Checklist, Planner", Icons.Outlined.TaskAlt, Color(0xFFEC4899), "## Essential Packing\n- [ ] Passport\n- [ ] Chargers\n- [ ] Clothes\n\n## Itinerary\n- Day 1: "),
                        TemplateItem("Monthly Budget", "Finance", "Finance, Budget", Icons.Outlined.AttachMoney, Color(0xFF14B8A6), "## Income\n- Primary: $0\n\n## Fixed Expenses\n- Rent: $0\n- Utilities: $0"),
                        TemplateItem("Book Summary", "Books", "Reading, Notes", Icons.Outlined.Bookmark, Color(0xFF8B5CF6), "## Book Info\n**Author**: \n**Rating**: ★★★★★\n\n## Core Key Takeaways\n1. "),
                        TemplateItem("Daily Reflection", "Journal", "Gratitude, Reflection", Icons.Outlined.RateReview, Color(0xFF3B82F6), "## Daily Wins\n- \n\n## Gratitude\n1. \n2. ")
                    )

                    items(templateTypes) { template ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(SurfaceContainer)
                                .border(1.dp, template.color.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                                .clickable {
                                    viewModel.createNewNote(template.title, template.folder)
                                }
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(template.color.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(template.icon, contentDescription = null, tint = template.color, modifier = Modifier.size(16.dp))
                                }
                                Column {
                                    Text(template.title, color = OnSurfaceText, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                    Text(template.folder, color = OnSurfaceVariantText, fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }
            }

            // 3. Recent Notes (Horizontal Scroll)
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent Notes",
                        color = OnSurfaceText,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    TextButton(onClick = { /* View All */ }) {
                        Text("View All", color = ElectricIndigo, fontWeight = FontWeight.SemiBold)
                    }
                }

                if (notes.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(SurfaceContainerLow)
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No notes found in this workspace. Create one below!", color = OnSurfaceVariantText, fontSize = 14.sp)
                    }
                } else {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(end = 16.dp)
                    ) {
                        items(notes) { note ->
                            NoteCard(note = note, onClick = { onNavigateToEditor(note) })
                        }
                    }
                }
            }

            // 3. Pinned & Starred + Quick Actions Section
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    text = "Pinned & Starred",
                    color = OnSurfaceText,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Card(
                    colors = CardDefaults.cardColors(containerColor = SurfaceContainer),
                    shape = RoundedCornerShape(16.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(OutlineVariantBorder, OutlineVariantBorder)))
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(0.dp)
                    ) {
                        if (pinnedAndStarred.isEmpty()) {
                            Box(modifier = Modifier.padding(16.dp)) {
                                Text("No pinned or starred notes yet.", color = OnSurfaceVariantText, fontSize = 14.sp)
                            }
                        } else {
                            pinnedAndStarred.take(4).forEachIndexed { index, note ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onNavigateToEditor(note) }
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = if (note.isStarred) Icons.Filled.Star else Icons.Filled.PushPin,
                                            contentDescription = null,
                                            tint = ElectricIndigo,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                            text = note.title,
                                            color = OnSurfaceText,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                    Icon(
                                        imageVector = Icons.Default.OpenInNew,
                                        contentDescription = "Open Note",
                                        tint = OnSurfaceVariantText,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                if (index < pinnedAndStarred.take(4).size - 1) {
                                    HorizontalDivider(color = OutlineVariantBorder.copy(alpha = 0.3f))
                                }
                            }
                        }
                    }
                }
            }

            // 4. Quick Actions
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Quick Actions",
                    color = OnSurfaceText,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickActionButton(
                        title = "Scan Document",
                        icon = Icons.Outlined.DocumentScanner,
                        iconTint = ElectricIndigo,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToScanner
                    )
                    QuickActionButton(
                        title = "Voice Note",
                        icon = Icons.Outlined.Mic,
                        iconTint = ElectricIndigo,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.createNewNote("Voice Note", "Personal") }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickActionButton(
                        title = "AI Brainstorm",
                        icon = Icons.Outlined.Psychology,
                        iconTint = SkyBlue,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.runAiBrainstorm("Next project roadmap and feature ideas") }
                    )
                    QuickActionButton(
                        title = "Knowledge Graph",
                        icon = Icons.Outlined.Hub,
                        iconTint = ElectricIndigo,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToGraph
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

data class TemplateItem(
    val title: String,
    val folder: String,
    val tags: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val color: Color,
    val defaultContent: String
)

@Composable
fun NoteCard(note: NoteEntity, onClick: () -> Unit) {
    val (badgeColor, badgeIcon) = when (note.folder.lowercase()) {
        "development" -> Pair(Color(0xFF10B981), Icons.Outlined.Code)
        "education" -> Pair(Color(0xFFF59E0B), Icons.Outlined.School)
        "finance" -> Pair(Color(0xFF14B8A6), Icons.Outlined.AttachMoney)
        "books" -> Pair(Color(0xFF8B5CF6), Icons.Outlined.Bookmark)
        "journal" -> Pair(Color(0xFF3B82F6), Icons.Outlined.RateReview)
        "work" -> Pair(Color(0xFF6366F1), Icons.Outlined.EventNote)
        "research" -> Pair(Color(0xFFEC4899), Icons.Outlined.DocumentScanner)
        else -> Pair(ElectricIndigo, Icons.Outlined.Folder)
    }

    Box(
        modifier = Modifier
            .width(260.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceContainer)
            .border(1.dp, OutlineVariantBorder.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(badgeIcon, contentDescription = null, tint = badgeColor, modifier = Modifier.size(14.dp))
                    Text(
                        text = note.folder.uppercase(),
                        color = badgeColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = null,
                    tint = OnSurfaceVariantText,
                    modifier = Modifier.size(18.dp)
                )
            }

            Text(
                text = note.title,
                color = OnSurfaceText,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = note.content.ifEmpty { "Empty note content..." },
                color = OnSurfaceVariantText,
                fontSize = 13.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 18.sp
            )

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                note.tags.split(",").take(3).forEach { tag ->
                    if (tag.isNotBlank()) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(SurfaceContainerHigh)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "#${tag.trim()}",
                                color = SlateSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QuickActionButton(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(90.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceContainerHigh)
            .border(1.dp, OutlineVariantBorder.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = iconTint,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                color = OnSurfaceText,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
