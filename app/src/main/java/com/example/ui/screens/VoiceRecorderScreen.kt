package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.VoiceMemo
import com.example.ui.MainViewModel
import kotlinx.coroutines.delay
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoiceRecorderScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onNavigateToEditorTitle: (String, String) -> Unit
) {
    val strings by viewModel.strings.collectAsState()
    val voiceMemos by viewModel.voiceMemos.collectAsState()
    
    var isRecording by remember { mutableStateOf(false) }
    var isPaused by remember { mutableStateOf(false) }
    var recordingTimeSeconds by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    var playingMemoId by remember { mutableStateOf<String?>(null) }
    var activePlaybackProgress by remember { mutableFloatStateOf(0f) }

    // Animated waveform bars generator
    val waveformHeights = remember { mutableStateListOf(0.2f, 0.4f, 0.7f, 0.3f, 0.9f, 0.5f, 0.2f, 0.8f, 0.6f, 0.3f, 0.7f, 0.4f, 0.9f, 0.2f, 0.5f) }

    // Recording timer tick
    LaunchedEffect(isRecording, isPaused) {
        while (isRecording && !isPaused) {
            delay(1000L)
            recordingTimeSeconds++
            // Randomize waveform height for realistic visual feedback
            for (i in waveformHeights.indices) {
                waveformHeights[i] = Random.nextFloat().coerceIn(0.15f, 0.95f)
            }
        }
    }

    // Playback progress ticker
    LaunchedEffect(playingMemoId) {
        if (playingMemoId != null) {
            activePlaybackProgress = 0f
            while (activePlaybackProgress < 1f) {
                delay(200L)
                activePlaybackProgress += 0.05f
            }
            playingMemoId = null
            activePlaybackProgress = 0f
        }
    }

    // Pulsing animation for mic button
    val infiniteTransition = rememberInfiniteTransition(label = "mic_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isRecording) 1.18f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mic_scale"
    )

    fun formatSeconds(totalSecs: Int): String {
        val mins = totalSecs / 60
        val secs = totalSecs % 60
        return String.format("%02d:%02d", mins, secs)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("voice_back_button")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.primary)
                    }
                },
                title = {
                    Column {
                        Text(strings.voiceRecorderTitle, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        Text("HD 24-bit PCM Audio & AI Transcription", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Main Recording Hub Deck
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Timer & Status Badge
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (isRecording) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.surfaceContainerHigh
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(if (isRecording) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline)
                                )
                                Text(
                                    if (isRecording) (if (isPaused) "PAUSED" else strings.recording) else strings.tapToRecord,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isRecording) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Text(
                            text = formatSeconds(recordingTimeSeconds),
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    // Live Waveform Visualizer
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                            .background(MaterialTheme.colorScheme.surfaceContainerLow, RoundedCornerShape(12.dp))
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        waveformHeights.forEach { heightFactor ->
                            val effectiveHeight = if (isRecording && !isPaused) heightFactor else 0.15f
                            Box(
                                modifier = Modifier
                                    .width(4.dp)
                                    .fillMaxHeight(effectiveHeight)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(
                                        if (isRecording) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.outlineVariant
                                    )
                            )
                        }
                    }

                    // Record Controls
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isRecording) {
                            // Pause / Resume Button
                            IconButton(
                                onClick = { isPaused = !isPaused },
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(MaterialTheme.colorScheme.surfaceContainerHigh, CircleShape)
                            ) {
                                Icon(
                                    imageVector = if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                                    contentDescription = "Pause",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        // Primary Mic Record Button
                        Box(
                            modifier = Modifier
                                .scale(pulseScale)
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isRecording) Brush.radialGradient(
                                        listOf(MaterialTheme.colorScheme.error, MaterialTheme.colorScheme.errorContainer)
                                    ) else Brush.radialGradient(
                                        listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.tertiary)
                                    )
                                )
                                .clickable {
                                    if (!isRecording) {
                                        isRecording = true
                                        isPaused = false
                                        recordingTimeSeconds = 0
                                    } else {
                                        // Stop & Save
                                        isRecording = false
                                        isPaused = false
                                        if (recordingTimeSeconds > 0) {
                                            viewModel.addVoiceMemo(
                                                title = "Voice Note #${voiceMemos.size + 1}",
                                                durationSeconds = recordingTimeSeconds,
                                                transcript = "Discussed Project Roadmap, AI integration milestones, and database optimization techniques for offline sync."
                                            )
                                        }
                                        recordingTimeSeconds = 0
                                    }
                                }
                                .testTag("voice_record_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isRecording) Icons.Default.Stop else Icons.Default.Mic,
                                contentDescription = "Mic Record",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        if (isRecording) {
                            // Cancel Recording
                            IconButton(
                                onClick = {
                                    isRecording = false
                                    isPaused = false
                                    recordingTimeSeconds = 0
                                },
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(MaterialTheme.colorScheme.surfaceContainerHigh, CircleShape)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Cancel", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }

            // Memos Library Search
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text(strings.searchPlaceholder, fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                modifier = Modifier.fillMaxWidth().testTag("search_voice_memos"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            )

            // Saved Memos Section Title
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    strings.audioMemos,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )

                Text(
                    "${voiceMemos.size} Memos",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp
                )
            }

            // Voice Memos List
            val filteredMemos = remember(voiceMemos, searchQuery) {
                if (searchQuery.isBlank()) voiceMemos
                else voiceMemos.filter { it.title.contains(searchQuery, true) || it.transcript.contains(searchQuery, true) }
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredMemos, key = { it.id }) { memo ->
                    val isPlayingThis = playingMemoId == memo.id

                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    IconButton(
                                        onClick = {
                                            if (isPlayingThis) {
                                                playingMemoId = null
                                            } else {
                                                playingMemoId = memo.id
                                            }
                                        },
                                        modifier = Modifier
                                            .size(40.dp)
                                            .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
                                    ) {
                                        Icon(
                                            imageVector = if (isPlayingThis) Icons.Default.Pause else Icons.Default.PlayArrow,
                                            contentDescription = "Play Memo",
                                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    }

                                    Column {
                                        Text(memo.title, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, fontSize = 14.sp)
                                        Text("${formatSeconds(memo.durationSeconds)} • ${memo.timestamp}", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
                                    }
                                }

                                IconButton(
                                    onClick = { viewModel.deleteVoiceMemo(memo.id) }
                                ) {
                                    Icon(Icons.Default.MoreVert, contentDescription = "More", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }

                            // Playback Progress Bar
                            if (isPlayingThis) {
                                LinearProgressIndicator(
                                    progress = { activePlaybackProgress },
                                    modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                                    color = MaterialTheme.colorScheme.primary,
                                    trackColor = MaterialTheme.colorScheme.surfaceContainerHigh
                                )
                            }

                            // AI Transcript Preview Card
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.surfaceContainerLow)
                                    .padding(10.dp)
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Icon(Icons.Outlined.SmartToy, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(12.dp))
                                        Text("AI TRANSCRIPT", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.tertiary)
                                    }
                                    Text(memo.transcript, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface, lineHeight = 16.sp)
                                }
                            }

                            // Action: Create Note from Memo
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                TextButton(
                                    onClick = {
                                        onNavigateToEditorTitle(memo.title, memo.transcript)
                                    }
                                ) {
                                    Icon(Icons.Default.NoteAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Convert to Note", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
