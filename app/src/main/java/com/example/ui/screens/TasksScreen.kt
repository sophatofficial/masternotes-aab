package com.example.ui.screens

import android.app.DatePickerDialog
import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.TaskEntity
import com.example.ui.MainViewModel
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(
    viewModel: MainViewModel,
    onNavigateToEditorTitle: (String) -> Unit
) {
    val context = LocalContext.current
    val tasks by viewModel.tasks.collectAsState()
    val timerTimeLeft by viewModel.timerTimeLeft.collectAsState()
    val isTimerRunning by viewModel.isTimerRunning.collectAsState()
    val completedCycles by viewModel.completedCycles.collectAsState()

    // Add / Edit Task Dialog States
    var showAddTaskDialog by remember { mutableStateOf(false) }
    var editingTask by remember { mutableStateOf<TaskEntity?>(null) }

    var taskTitleInput by remember { mutableStateOf("") }
    var taskDueDateInput by remember { mutableStateOf("Today") }
    var taskPriorityInput by remember { mutableStateOf("Medium") }
    var taskNotesInput by remember { mutableStateOf("") }
    var selectedCalendarDateDisplay by remember { mutableStateOf("") }

    // Real device week dates
    val realWeekDays = remember { getRealDeviceWeekDays() }
    val realCurrentMonthYear = remember {
        SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(Date())
    }
    var selectedFilterDay by remember { mutableStateOf<String?>(null) }

    val timerMinutes = timerTimeLeft / 60
    val timerSeconds = timerTimeLeft % 60
    val timerProgress by animateFloatAsState(
        targetValue = timerTimeLeft / (25 * 60f),
        label = "Timer Progress"
    )

    // Calculate real stats summary
    val totalTasks = tasks.size
    val completedTasks = tasks.count { it.isCompleted }
    val pendingTasks = totalTasks - completedTasks
    val completionPercentage = if (totalTasks > 0) ((completedTasks.toFloat() / totalTasks) * 100).toInt() else 0

    val highPriorityCount = tasks.count { it.priority.equals("High", ignoreCase = true) }
    val medPriorityCount = tasks.count { it.priority.equals("Medium", ignoreCase = true) }
    val lowPriorityCount = tasks.count { it.priority.equals("Low", ignoreCase = true) }

    val progressAnim by animateFloatAsState(
        targetValue = if (totalTasks > 0) completedTasks.toFloat() / totalTasks else 0f,
        label = "Task Stats Progress"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Task Management & Focus", color = OnSurfaceText, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = OxfordBlue)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    taskTitleInput = ""
                    taskDueDateInput = getFormattedDeviceDate(0) // Default to device real today date
                    taskPriorityInput = "Medium"
                    taskNotesInput = ""
                    selectedCalendarDateDisplay = ""
                    editingTask = null
                    showAddTaskDialog = true
                },
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
            // 1. Upcoming Week Calendar Strip (Real Device System Dates)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Device Calendar • This Week", color = OnSurfaceVariantText, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text(realCurrentMonthYear, color = ElectricIndigo, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }

                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(realWeekDays) { day ->
                        val isSelected = selectedFilterDay == day.fullDateString || (selectedFilterDay == null && day.isToday)
                        Box(
                            modifier = Modifier
                                .width(58.dp)
                                .height(74.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isSelected) ElectricIndigo else SurfaceContainer)
                                .border(
                                    1.dp,
                                    if (isSelected) ElectricIndigo else OutlineVariantBorder.copy(alpha = 0.3f),
                                    RoundedCornerShape(14.dp)
                                )
                                .clickable {
                                    selectedFilterDay = if (selectedFilterDay == day.fullDateString) null else day.fullDateString
                                }
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = day.dayName.uppercase(),
                                    color = if (isSelected) OxfordBlue else OnSurfaceVariantText,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = day.dayNum,
                                    color = if (isSelected) OxfordBlue else OnSurfaceText,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // 2. Focus Tasks Checklist Section
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.CheckCircle, contentDescription = null, tint = SkyBlue)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Active Tasks", color = OnSurfaceText, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }

                    Text("$completedTasks / $totalTasks Done", color = SlateSecondary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }

                if (tasks.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(SurfaceContainer)
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Outlined.Task, contentDescription = null, tint = SlateSecondary, modifier = Modifier.size(40.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("No tasks yet", color = OnSurfaceText, fontWeight = FontWeight.Bold)
                            Text("Tap 'Add Task' to schedule real dates & notes", color = OnSurfaceVariantText, fontSize = 12.sp)
                        }
                    }
                } else {
                    tasks.forEach { task ->
                        TaskRowItem(
                            task = task,
                            onToggle = { viewModel.toggleTaskCompletion(task) },
                            onEdit = {
                                editingTask = task
                                taskTitleInput = task.title
                                taskDueDateInput = task.dueDate
                                taskPriorityInput = task.priority
                                taskNotesInput = task.taskNotes ?: ""
                                showAddTaskDialog = true
                            },
                            onDelete = { viewModel.deleteTask(task) }
                        )
                    }
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
                        "FOCUS TIMER",
                        fontSize = 11.sp,
                        color = OnSurfaceVariantText,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )

                    // Timer Ring
                    Box(
                        modifier = Modifier.size(170.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val strokeWidth = 10.dp.toPx()
                            drawCircle(
                                color = SurfaceContainerHigh,
                                style = Stroke(width = strokeWidth)
                            )
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
                                fontSize = 34.sp,
                                fontWeight = FontWeight.Bold,
                                color = OnSurfaceText
                            )
                            Text("Pomodoro", fontSize = 12.sp, color = OnSurfaceVariantText)
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

            // 4. TASK SUMMARY & STATISTICS PERCENTAGE SECTION (AT BELOW TASK LIST)
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceContainer),
                shape = RoundedCornerShape(18.dp),
                border = CardDefaults.outlinedCardBorder(),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("task_summary_statistics_card")
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Outlined.Analytics, contentDescription = null, tint = SkyBlue)
                            Text("Task Statistics & Productivity", color = OnSurfaceText, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }

                        Surface(
                            color = ElectricIndigo.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "$completionPercentage% Done",
                                color = ElectricIndigo,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                                    .testTag("summary_completion_rate")
                            )
                        }
                    }

                    // Progress Gauge Bar
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Completion Rate", fontSize = 12.sp, color = OnSurfaceVariantText, fontWeight = FontWeight.Medium)
                            Text("$completedTasks of $totalTasks Tasks Completed", fontSize = 12.sp, color = SkyBlue, fontWeight = FontWeight.Bold)
                        }

                        LinearProgressIndicator(
                            progress = { progressAnim },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(12.dp)
                                .clip(RoundedCornerShape(6.dp)),
                            color = ElectricIndigo,
                            trackColor = SurfaceContainerHigh
                        )
                    }

                    // 4 Stat Cards Grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Total
                        StatBox(
                            label = "TOTAL",
                            value = "$totalTasks",
                            color = OnSurfaceText,
                            icon = Icons.Outlined.FormatListNumbered,
                            modifier = Modifier.weight(1f)
                        )
                        // Completed
                        StatBox(
                            label = "COMPLETED",
                            value = "$completedTasks",
                            color = Color(0xFF10B981),
                            icon = Icons.Outlined.TaskAlt,
                            modifier = Modifier.weight(1f)
                        )
                        // Pending
                        StatBox(
                            label = "PENDING",
                            value = "$pendingTasks",
                            color = Color(0xFFF59E0B),
                            icon = Icons.Outlined.HourglassEmpty,
                            modifier = Modifier.weight(1f)
                        )
                        // High Priority
                        StatBox(
                            label = "HIGH PRIO",
                            value = "$highPriorityCount",
                            color = Color(0xFFFF5252),
                            icon = Icons.Outlined.PriorityHigh,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Priority Breakdown Bars
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(SurfaceContainerHigh)
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("PRIORITY BREAKDOWN", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SlateSecondary, letterSpacing = 1.sp)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFFFF5252)))
                                Text("High Priority ($highPriorityCount)", fontSize = 12.sp, color = OnSurfaceText)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(SkyBlue))
                                Text("Medium ($medPriorityCount)", fontSize = 12.sp, color = OnSurfaceText)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(SlateSecondary))
                                Text("Low ($lowPriorityCount)", fontSize = 12.sp, color = OnSurfaceText)
                            }
                        }
                    }

                    // Productivity Insight Badge
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Brush.horizontalGradient(listOf(ElectricIndigo.copy(alpha = 0.15f), SkyBlue.copy(alpha = 0.15f))) )
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Outlined.AutoAwesome, contentDescription = null, tint = SkyBlue, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (completionPercentage >= 75) {
                                "🔥 Outstanding performance! You've completed $completionPercentage% of your task goals."
                            } else if (completionPercentage >= 50) {
                                "⚡ Great momentum! Over half of your tasks are complete."
                            } else {
                                "🎯 Keep going! You have $pendingTasks remaining tasks scheduled."
                            },
                            fontSize = 12.sp,
                            color = OnSurfaceText,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(64.dp))
        }

        // Add / Edit Task Dialog
        if (showAddTaskDialog) {
            AlertDialog(
                onDismissRequest = {
                    showAddTaskDialog = false
                    editingTask = null
                },
                title = {
                    Text(
                        text = if (editingTask != null) "Edit Task" else "Create New Task",
                        color = OnSurfaceText,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Title Input
                        OutlinedTextField(
                            value = taskTitleInput,
                            onValueChange = { taskTitleInput = it },
                            label = { Text("Task Description") },
                            placeholder = { Text("e.g. Prepare quarterly presentation slides") },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = SurfaceContainerHigh,
                                unfocusedContainerColor = SurfaceContainerHigh
                            ),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("new_task_input")
                        )

                        // Real Device Date Selection
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("REAL DEVICE DUE DATE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = OnSurfaceVariantText, letterSpacing = 1.sp)
                                TextButton(
                                    onClick = {
                                        showNativeDatePicker(context, taskDueDateInput) { chosenDate ->
                                            taskDueDateInput = chosenDate
                                            selectedCalendarDateDisplay = chosenDate
                                        }
                                    },
                                    modifier = Modifier.testTag("select_date_picker_button")
                                ) {
                                    Icon(Icons.Outlined.CalendarMonth, contentDescription = null, tint = SkyBlue, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Calendar Picker", fontSize = 11.sp, color = SkyBlue, fontWeight = FontWeight.Bold)
                                }
                            }

                            // Device Date Quick Chips
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                val todayStr = getFormattedDeviceDate(0)
                                val tomorrowStr = getFormattedDeviceDate(1)
                                val nextWeekStr = getFormattedDeviceDate(7)

                                val presetDates = listOf(
                                    "Today" to todayStr,
                                    "Tomorrow" to tomorrowStr,
                                    "Next Week" to nextWeekStr
                                )

                                presetDates.forEach { (label, actualDate) ->
                                    val isSelected = taskDueDateInput == actualDate || (label == "Today" && taskDueDateInput == "Today")
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = {
                                            taskDueDateInput = actualDate
                                            selectedCalendarDateDisplay = ""
                                        },
                                        label = { Text("$label ($actualDate)", fontSize = 11.sp) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = ElectricIndigo,
                                            selectedLabelColor = OxfordBlue
                                        )
                                    )
                                }
                            }

                            OutlinedTextField(
                                value = taskDueDateInput,
                                onValueChange = { taskDueDateInput = it },
                                label = { Text("Selected Date") },
                                singleLine = true,
                                trailingIcon = {
                                    IconButton(onClick = {
                                        showNativeDatePicker(context, taskDueDateInput) { chosen ->
                                            taskDueDateInput = chosen
                                        }
                                    }) {
                                        Icon(Icons.Outlined.Event, contentDescription = "Pick Date", tint = SkyBlue)
                                    }
                                },
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = SurfaceContainerHigh,
                                    unfocusedContainerColor = SurfaceContainerHigh
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("task_due_date_input")
                            )
                        }

                        // Priority Selector
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("PRIORITY", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = OnSurfaceVariantText, letterSpacing = 1.sp)
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                listOf("High", "Medium", "Low").forEach { priority ->
                                    val isSelected = taskPriorityInput == priority
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { taskPriorityInput = priority },
                                        label = { Text(priority, fontSize = 12.sp) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = when(priority) {
                                                "High" -> Color(0xFFFF5252)
                                                "Medium" -> SkyBlue
                                                else -> SlateSecondary
                                            },
                                            selectedLabelColor = OxfordBlue
                                        )
                                    )
                                }
                            }
                        }

                        // Task Notes Input
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("TASK NOTES & DETAILS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = OnSurfaceVariantText, letterSpacing = 1.sp)
                            OutlinedTextField(
                                value = taskNotesInput,
                                onValueChange = { taskNotesInput = it },
                                label = { Text("Add task notes, checklist points, links...") },
                                minLines = 2,
                                maxLines = 4,
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = SurfaceContainerHigh,
                                    unfocusedContainerColor = SurfaceContainerHigh
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("task_notes_input")
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (taskTitleInput.isNotBlank()) {
                                if (editingTask != null) {
                                    viewModel.updateTask(
                                        editingTask!!.copy(
                                            title = taskTitleInput,
                                            dueDate = taskDueDateInput,
                                            priority = taskPriorityInput,
                                            taskNotes = taskNotesInput.ifBlank { null }
                                        )
                                    )
                                } else {
                                    viewModel.addTask(
                                        title = taskTitleInput,
                                        dueDate = taskDueDateInput,
                                        priority = taskPriorityInput,
                                        taskNotes = taskNotesInput.ifBlank { null }
                                    )
                                }
                                showAddTaskDialog = false
                                editingTask = null
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricIndigo, contentColor = OxfordBlue),
                        modifier = Modifier.testTag("save_task_button")
                    ) {
                        Text(if (editingTask != null) "Save Changes" else "Add Task", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showAddTaskDialog = false
                        editingTask = null
                    }) {
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
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var isExpandedNotes by remember { mutableStateOf(false) }

    val priorityColor = when(task.priority) {
        "High" -> Color(0xFFFF5252)
        "Medium" -> SkyBlue
        else -> SlateSecondary
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceContainer)
            .border(1.dp, OutlineVariantBorder.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
            .padding(14.dp)
            .testTag("task_item_card_${task.id}")
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
                        colors = CheckboxDefaults.colors(checkedColor = ElectricIndigo),
                        modifier = Modifier.testTag("task_checkbox_${task.id}")
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = task.title,
                            color = if (task.isCompleted) OnSurfaceVariantText else OnSurfaceText,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Due date badge
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(SurfaceContainerHigh)
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                                    .testTag("task_due_date_tag")
                            ) {
                                Icon(Icons.Outlined.Event, contentDescription = null, tint = SkyBlue, modifier = Modifier.size(13.dp))
                                Text(
                                    text = task.noteSource?.let { "from $it" } ?: task.dueDate,
                                    color = SkyBlue,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // Priority Badge
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(priorityColor.copy(alpha = 0.2f))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = task.priority,
                                    color = priorityColor,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // Notes Badge toggle if present
                            if (!task.taskNotes.isNullOrBlank()) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(ElectricIndigo.copy(alpha = 0.15f))
                                        .clickable { isExpandedNotes = !isExpandedNotes }
                                        .padding(horizontal = 6.dp, vertical = 3.dp)
                                ) {
                                    Icon(Icons.Outlined.StickyNote2, contentDescription = null, tint = ElectricIndigo, modifier = Modifier.size(12.dp))
                                    Text("Notes", fontSize = 10.sp, color = ElectricIndigo, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("edit_task_button_${task.id}")
                    ) {
                        Icon(Icons.Outlined.Edit, contentDescription = "Edit Task", tint = SkyBlue, modifier = Modifier.size(18.dp))
                    }

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("delete_task_button_${task.id}")
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Task", tint = OnSurfaceVariantText.copy(alpha = 0.6f), modifier = Modifier.size(18.dp))
                    }
                }
            }

            // Expanded Task Notes Section
            if (!task.taskNotes.isNullOrBlank()) {
                AnimatedVisibility(visible = isExpandedNotes || !task.isCompleted) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(SurfaceContainerHigh)
                            .padding(10.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(Icons.Outlined.Notes, contentDescription = null, tint = SlateSecondary, modifier = Modifier.size(12.dp))
                                Text("TASK NOTES:", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = SlateSecondary)
                            }
                            Text(
                                text = task.taskNotes,
                                color = OnSurfaceText,
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatBox(
    label: String,
    value: String,
    color: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceContainerHigh)
            .padding(vertical = 10.dp, horizontal = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
            Text(value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = color)
            Text(label, fontSize = 8.sp, fontWeight = FontWeight.Bold, color = OnSurfaceVariantText)
        }
    }
}

// Helpers for Device Real Dates
data class RealDeviceDayItem(
    val dayName: String,
    val dayNum: String,
    val fullDateString: String,
    val isToday: Boolean
)

fun getRealDeviceWeekDays(): List<RealDeviceDayItem> {
    val list = mutableListOf<RealDeviceDayItem>()
    val cal = Calendar.getInstance()
    val dayNameFormat = SimpleDateFormat("EEE", Locale.getDefault())
    val dayNumFormat = SimpleDateFormat("dd", Locale.getDefault())
    val fullDateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    val todayCal = Calendar.getInstance()

    for (i in 0..6) {
        val currentCal = cal.clone() as Calendar
        currentCal.add(Calendar.DAY_OF_YEAR, i)

        val isToday = (i == 0) || (
                currentCal.get(Calendar.YEAR) == todayCal.get(Calendar.YEAR) &&
                        currentCal.get(Calendar.DAY_OF_YEAR) == todayCal.get(Calendar.DAY_OF_YEAR)
                )

        list.add(
            RealDeviceDayItem(
                dayName = dayNameFormat.format(currentCal.time),
                dayNum = dayNumFormat.format(currentCal.time),
                fullDateString = fullDateFormat.format(currentCal.time),
                isToday = isToday
            )
        )
    }
    return list
}

fun getFormattedDeviceDate(daysFromNow: Int): String {
    val cal = Calendar.getInstance()
    cal.add(Calendar.DAY_OF_YEAR, daysFromNow)
    return SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(cal.time)
}

fun showNativeDatePicker(
    context: Context,
    initialDateStr: String,
    onDateSelected: (String) -> Unit
) {
    val cal = Calendar.getInstance()
    try {
        val parsed = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).parse(initialDateStr)
        if (parsed != null) {
            cal.time = parsed
        }
    } catch (e: Exception) {
        // Fallback to today
    }

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val selectedCal = Calendar.getInstance().apply {
                set(year, month, dayOfMonth)
            }
            val formatted = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(selectedCal.time)
            onDateSelected(formatted)
        },
        cal.get(Calendar.YEAR),
        cal.get(Calendar.MONTH),
        cal.get(Calendar.DAY_OF_MONTH)
    )
    datePickerDialog.show()
}
