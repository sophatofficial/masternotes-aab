package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.NoteEntity
import com.example.ui.MainViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditorScreen(
    viewModel: MainViewModel,
    note: NoteEntity?,
    onBack: () -> Unit
) {
    var titleText by remember(note?.id) { mutableStateOf(note?.title ?: "Executive Summary") }
    var contentText by remember(note?.id) { mutableStateOf(note?.content ?: "Project Phoenix represents our shift towards an autonomous, AI-driven knowledge ecosystem.\n\nAction Items:\n- [x] Define core architecture for Graph Node API\n- [ ] Onboard generative design specialists") }
    var tagsText by remember(note?.id) { mutableStateOf(note?.tags ?: "Strategy,Design") }

    val aiResponseText by viewModel.aiResponseText.collectAsState()
    val isAiLoading by viewModel.isAiLoading.collectAsState()

    var zenMode by remember { mutableStateOf(false) }

    // Auto-save when title or content changes
    LaunchedEffect(titleText, contentText) {
        if (note != null) {
            viewModel.saveCurrentNote(titleText, contentText, tagsText)
        }
    }

    val wordCount = remember(contentText) {
        contentText.trim().split("\\s+".toRegex()).filter { it.isNotBlank() }.size
    }

    val readTimeMinutes = remember(wordCount) {
        maxOf(1, (wordCount / 150))
    }

    Scaffold(
        topBar = {
            AnimatedVisibility(visible = !zenMode) {
                TopAppBar(
                    navigationIcon = {
                        IconButton(onClick = onBack, modifier = Modifier.testTag("back_button")) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = ElectricIndigo)
                        }
                    },
                    title = {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("WORKSPACES > PROJECTS > ", fontSize = 10.sp, color = OnSurfaceVariantText, letterSpacing = 1.sp)
                                Text(note?.folder?.uppercase() ?: "NOTES", fontSize = 10.sp, color = ElectricIndigo, letterSpacing = 1.sp)
                            }
                            Text(titleText, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = OnSurfaceText)
                        }
                    },
                    actions = {
                        IconButton(onClick = { viewModel.runAiSummarize(titleText, contentText) }) {
                            Icon(Icons.Outlined.SmartToy, contentDescription = "AI Assistant", tint = SkyBlue)
                        }
                        IconButton(onClick = { /* Share */ }) {
                            Icon(Icons.Outlined.Share, contentDescription = "Share", tint = OnSurfaceVariantText)
                        }
                        IconButton(onClick = { /* History */ }) {
                            Icon(Icons.Outlined.History, contentDescription = "History", tint = OnSurfaceVariantText)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = OxfordBlue)
                )
            }
        },
        bottomBar = {
            AnimatedVisibility(visible = !zenMode) {
                Surface(
                    color = SurfaceContainerLow,
                    tonalElevation = 4.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp)
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Text("$wordCount WORDS", fontSize = 11.sp, color = OnSurfaceVariantText, fontWeight = FontWeight.SemiBold)
                            Text("${readTimeMinutes}M READ", fontSize = 11.sp, color = OnSurfaceVariantText, fontWeight = FontWeight.SemiBold)
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CloudDone, contentDescription = null, tint = ElectricIndigo, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("SYNCED", fontSize = 11.sp, color = OnSurfaceVariantText, fontWeight = FontWeight.SemiBold)
                            }

                            IconButton(
                                onClick = { zenMode = true },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(Icons.Default.Fullscreen, contentDescription = "Zen Mode", tint = OnSurfaceVariantText, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        },
        containerColor = OxfordBlue
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Zen mode exit button
                if (zenMode) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        IconButton(onClick = { zenMode = false }) {
                            Icon(Icons.Default.FullscreenExit, contentDescription = "Exit Zen Mode", tint = ElectricIndigo)
                        }
                    }
                }

                // Note Title Input
                TextField(
                    value = titleText,
                    onValueChange = { titleText = it },
                    textStyle = MaterialTheme.typography.headlineLarge.copy(
                        color = OnSurfaceText,
                        fontWeight = FontWeight.Bold
                    ),
                    placeholder = { Text("Note Title...", fontSize = 28.sp, color = OnSurfaceVariantText.copy(alpha = 0.4f)) },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = ElectricIndigo,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("note_title_input")
                )

                // Tags Input
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Outlined.Tag, contentDescription = null, tint = SlateSecondary, modifier = Modifier.size(18.dp))
                    TextField(
                        value = tagsText,
                        onValueChange = { tagsText = it },
                        textStyle = MaterialTheme.typography.bodyMedium.copy(color = SlateSecondary),
                        placeholder = { Text("Comma separated tags...", color = OnSurfaceVariantText.copy(alpha = 0.4f)) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                HorizontalDivider(color = OutlineVariantBorder.copy(alpha = 0.3f))

                // AI Action Quick Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = SkyBlue, modifier = Modifier.size(16.dp))
                        Text("GEMINI AI ASSISTANT", color = SkyBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp)
                    }

                    FilledTonalButton(
                        onClick = { viewModel.runAiSummarize(titleText, contentText) },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = SkyBlue.copy(alpha = 0.15f),
                            contentColor = SkyBlue
                        ),
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("summarize_button")
                    ) {
                        Icon(Icons.Outlined.Compress, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Summarize Note", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }

                // AI Response Banner if available
                if (isAiLoading) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth(), color = SkyBlue)
                }

                aiResponseText?.let { response ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(SurfaceContainer)
                            .border(1.dp, SkyBlue, RoundedCornerShape(12.dp))
                            .padding(16.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = SkyBlue)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("AI Copilot Response", color = SkyBlue, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }
                                IconButton(onClick = { viewModel.clearAiResponse() }, modifier = Modifier.size(24.dp)) {
                                    Icon(Icons.Default.Close, contentDescription = "Close", tint = OnSurfaceVariantText)
                                }
                            }
                            Text(response, color = OnSurfaceText, fontSize = 14.sp, lineHeight = 20.sp)
                        }
                    }
                }

                // Main Content TextField
                TextField(
                    value = contentText,
                    onValueChange = { contentText = it },
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        color = OnSurfaceText,
                        lineHeight = 26.sp
                    ),
                    placeholder = { Text("Write your thoughts or markdown...", color = OnSurfaceVariantText.copy(alpha = 0.4f)) },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 260.dp)
                        .testTag("note_content_input")
                )

                // Code block preview box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF020617))
                        .border(1.dp, OutlineVariantBorder.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .padding(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("typescript", fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = SlateSecondary)
                            Icon(Icons.Outlined.ContentCopy, contentDescription = "Copy", tint = OnSurfaceVariantText, modifier = Modifier.size(16.dp))
                        }
                        Text(
                            text = "interface StrategyNode {\n  id: string;\n  priority: 'high' | 'medium' | 'low';\n  connectedEntities: string[];\n}",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 13.sp,
                            color = SkyBlue
                        )
                    }
                }

                Spacer(modifier = Modifier.height(80.dp))
            }

            // Floating Rich Text / AI Toolbar
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 24.dp)
                    .clip(CircleShape)
                    .background(SurfaceContainer.copy(alpha = 0.95f))
                    .border(1.dp, OutlineVariantBorder, CircleShape)
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(onClick = { contentText += "\n# Heading\n" }, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.Title, contentDescription = "Heading", tint = OnSurfaceVariantText)
                    }
                    IconButton(onClick = { contentText += " **bold** " }, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.FormatBold, contentDescription = "Bold", tint = OnSurfaceVariantText)
                    }
                    IconButton(onClick = { contentText += " *italic* " }, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.FormatItalic, contentDescription = "Italic", tint = OnSurfaceVariantText)
                    }
                    IconButton(onClick = { contentText += "\n- [ ] " }, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.FormatListBulleted, contentDescription = "List", tint = OnSurfaceVariantText)
                    }
                    IconButton(onClick = { contentText += "\n```typescript\n\n```\n" }, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.Code, contentDescription = "Code", tint = OnSurfaceVariantText)
                    }

                    VerticalDivider(modifier = Modifier.height(20.dp), color = OutlineVariantBorder)

                    IconButton(
                        onClick = { viewModel.runAiFixGrammar(contentText) },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(ElectricIndigo.copy(alpha = 0.2f))
                    ) {
                        Icon(Icons.Outlined.AutoAwesome, contentDescription = "AI Fix", tint = ElectricIndigo)
                    }
                }
            }
        }
    }
}
