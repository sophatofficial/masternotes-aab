package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.TaskEntity
import com.example.ui.MainViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(
    viewModel: MainViewModel,
    onNavigateToEditorTitle: (String) -> Unit
) {
    val tasks by viewModel.tasks.collectAsState()
    val timerTimeLeft by viewModel.timerTimeLeft.collectAsState()
    val isTimerRunning by viewModel.isTimerRunning.collectAsState()
    val completedCycles by viewModel.completedCycles.collectAsState()

    var showAddTaskDialog by remember { mutableStateOf(false) }
    var newTaskTitle by remember { mutableStateOf("") }

    val timerMinutes = timerTimeLeft / 60
    val timerSeconds = timerTimeLeft % 60
    val timerProgress by animateFloatAsState(
        targetValue = timerTimeLeft / (25 * 60f),
        label = "Timer Progress"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Task Management & Pomodoro", color = OnSurfaceText, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = OxfordBlue)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddTaskDialog = true },
                containerColor = ElectricIndigo,
                contentColor = OxfordBlue,
                icon = { Icon(Icons.Default.Add, contentDescription = "Add Task") },
                text = { Text("Add Task", fontWeight = FontWeight.Bold) },
                modifier = Modifier.testTag("add_task_fab")
            )
        },
        containerColor = OxfordBlue
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // 1. Upcoming Week Calendar Strip
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Upcoming Week", color = OnSurfaceVariantText, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text("October 2026", color = ElectricIndigo, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }

                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    val days = listOf(
                        Triple("Mon", "23", false),
                        Triple("Tue", "24", true), // Active
                        Triple("Wed", "25", false),
                        Triple("Thu", "26", false),
                        Triple("Fri", "27", false),
                        Triple("Sat", "28", false),
                        Triple("Sun", "29", false)
                    )

                    items(days.size) { index ->
                        val (day, date, isActive) = days[index]
                        Box(
                            modifier = Modifier
                                .width(54.dp)
                                .height(72.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isActive) ElectricIndigo else SurfaceContainer)
                                .border(
                                    1.dp,
                                    if (isActive) ElectricIndigo else OutlineVariantBorder.copy(alpha = 0.3f),
                                    RoundedCornerShape(12.dp)
                                )
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(day, color = if (isActive) OxfordBlue else OnSurfaceVariantText, fontSize = 12.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(date, color = if (isActive) OxfordBlue else OnSurfaceText, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // 2. Today's Focus Checklist
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.RocketLaunch, contentDescription = null, tint = SkyBlue)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Today's Focus", color = OnSurfaceText, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }

                tasks.forEach { task ->
                    TaskRowItem(
                        task = task,
                        onToggle = { viewModel.toggleTaskCompletion(task) },
                        onDelete = { viewModel.deleteTask(task) }
                    )
                }
            }

            // 3. Focus Session Pomodoro Widget
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceContainer),
                shape = RoundedCornerShape(16.dp),
                border = CardDefaults.outlinedCardBorder(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        "FOCUS SESSION",
                        fontSize = 11.sp,
                        color = OnSurfaceVariantText,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )

                    // Timer Circular Ring
                    Box(
                        modifier = Modifier.size(180.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val strokeWidth = 10.dp.toPx()
                            // Background Ring
                            drawCircle(
                                color = SurfaceContainerHigh,
                                style = Stroke(width = strokeWidth)
                            )
                            // Progress Ring
                            drawArc(
                                color = ElectricIndigo,
                                startAngle = -90f,
                                sweepAngle = 360f * timerProgress,
                                useCenter = false,
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "%02d:%02d".format(timerMinutes, timerSeconds),
                                fontSize = 36.sp,
                                fontWeight = FontWeight.Bold,
                                color = OnSurfaceText
                            )
                            Text("Pomodoro", fontSize = 13.sp, color = OnSurfaceVariantText)
                        }
                    }

                    // Timer Controls
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        IconButton(
                            onClick = { viewModel.toggleTimer() },
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(ElectricIndigo)
                                .testTag("timer_play_pause_button")
                        ) {
                            Icon(
                                imageVector = if (isTimerRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = "Toggle Timer",
                                tint = OxfordBlue,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        IconButton(
                            onClick = { viewModel.resetTimer() },
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(SurfaceContainerHigh)
                                .testTag("timer_reset_button")
                        ) {
                            Icon(Icons.Default.Replay, contentDescription = "Reset Timer", tint = OnSurfaceText)
                        }
                    }

                    // Stats row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(SurfaceContainerHigh)
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("%02d".format(completedCycles), fontWeight = FontWeight.Bold, color = ElectricIndigo, fontSize = 14.sp)
                                Text("CYCLES", fontSize = 9.sp, color = OnSurfaceVariantText)
                            }
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(SurfaceContainerHigh)
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("50m", fontWeight = FontWeight.Bold, color = SkyBlue, fontSize = 14.sp)
                                Text("FOCUS", fontSize = 9.sp, color = OnSurfaceVariantText)
                            }
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(SurfaceContainerHigh)
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("10m", fontWeight = FontWeight.Bold, color = SlateSecondary, fontSize = 14.sp)
                                Text("BREAK", fontSize = 9.sp, color = OnSurfaceVariantText)
                            }
                        }
                    }
                }
            }

            // 4. AI Task Optimizer Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceContainer)
                    .border(1.dp, Brush.horizontalGradient(listOf(SkyBlue, ElectricIndigo)), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = SkyBlue)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("AI Task Optimizer", color = SkyBlue, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }

                    Text(
                        "You're most productive between 2:00 PM and 4:00 PM. I've scheduled your high-priority quarterly report during this peak focus window.",
                        color = OnSurfaceVariantText,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(64.dp))
        }

        // Add Task Dialog
        if (showAddTaskDialog) {
            AlertDialog(
                onDismissRequest = { showAddTaskDialog = false },
                title = { Text("Create New Task", color = OnSurfaceText) },
                text = {
                    TextField(
                        value = newTaskTitle,
                        onValueChange = { newTaskTitle = it },
                        placeholder = { Text("Task description...", color = OnSurfaceVariantText) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = SurfaceContainerHigh,
                            unfocusedContainerColor = SurfaceContainerHigh
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("new_task_input")
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (newTaskTitle.isNotBlank()) {
                                viewModel.addTask(newTaskTitle)
                                newTaskTitle = ""
                                showAddTaskDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricIndigo, contentColor = OxfordBlue)
                    ) {
                        Text("Add")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddTaskDialog = false }) {
                        Text("Cancel", color = OnSurfaceVariantText)
                    }
                },
                containerColor = SurfaceContainer
            )
        }
    }
}

@Composable
fun TaskRowItem(
    task: TaskEntity,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceContainer)
            .border(1.dp, OutlineVariantBorder.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Checkbox(
                    checked = task.isCompleted,
                    onCheckedChange = { onToggle() },
                    colors = CheckboxDefaults.colors(checkedColor = ElectricIndigo)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = task.title,
                        color = if (task.isCompleted) OnSurfaceVariantText else OnSurfaceText,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                    )
                    task.noteSource?.let { source ->
                        Text("from $source", color = SkyBlue, fontSize = 11.sp)
                    } ?: Text(task.dueDate, color = SkyBlue, fontSize = 11.sp)
                }
            }

            IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                Icon(Icons.Default.Delete, contentDescription = "Delete Task", tint = OnSurfaceVariantText.copy(alpha = 0.5f))
            }
        }
    }
}
