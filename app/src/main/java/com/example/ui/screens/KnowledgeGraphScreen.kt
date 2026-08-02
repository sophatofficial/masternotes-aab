package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.theme.*

data class GraphNode(
    val id: String,
    val title: String,
    val xRatio: Float,
    val yRatio: Float,
    val color: Color,
    val sizeDp: Int = 16
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KnowledgeGraphScreen(
    viewModel: MainViewModel,
    onNavigateToEditorTitle: (String) -> Unit
) {
    var selectedFilter by remember { mutableStateOf("All") }
    var zoomLevel by remember { mutableFloatStateOf(1.0f) }
    var showSuggestions by remember { mutableStateOf(true) }

    val nodes = listOf(
        GraphNode("1", "Project Phoenix", 0.40f, 0.30f, ElectricIndigo, 20),
        GraphNode("2", "Second Brain Logic", 0.55f, 0.45f, SkyBlue, 28),
        GraphNode("3", "Reading List", 0.70f, 0.40f, SlateSecondary, 16),
        GraphNode("4", "Deep Work Strategy", 0.50f, 0.65f, ElectricIndigo, 22),
        GraphNode("5", "Neural Networks", 0.35f, 0.55f, SkyBlue, 18),
        GraphNode("6", "Archives 2026", 0.25f, 0.35f, SlateSecondary, 14)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Knowledge Base Graph", color = OnSurfaceText, fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { viewModel.runAiBrainstorm("Note linkages and semantic graph") }) {
                        Icon(Icons.Outlined.SmartToy, contentDescription = "AI Links", tint = SkyBlue)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = OxfordBlue)
            )
        },
        containerColor = OxfordBlue
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Background Canvas links
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                val node1 = Offset(w * 0.40f, h * 0.30f)
                val node2 = Offset(w * 0.55f, h * 0.45f)
                val node3 = Offset(w * 0.70f, h * 0.40f)
                val node4 = Offset(w * 0.50f, h * 0.65f)
                val node5 = Offset(w * 0.35f, h * 0.55f)
                val node6 = Offset(w * 0.25f, h * 0.35f)

                val lineStyle = OutlineVariantBorder.copy(alpha = 0.5f)

                drawLine(lineStyle, node1, node2, strokeWidth = 2f)
                drawLine(lineStyle, node2, node3, strokeWidth = 2f)
                drawLine(lineStyle, node2, node4, strokeWidth = 2f)
                drawLine(lineStyle, node4, node5, strokeWidth = 2f)
                drawLine(lineStyle, node1, node6, strokeWidth = 2f)
            }

            // Interactive Nodes Overlay
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val containerWidth = maxWidth
                val containerHeight = maxHeight

                nodes.forEach { node ->
                    var isHovered by remember { mutableStateOf(false) }

                    Box(
                        modifier = Modifier
                            .offset(
                                x = containerWidth * node.xRatio - (node.sizeDp / 2).dp,
                                y = containerHeight * node.yRatio - (node.sizeDp / 2).dp
                            )
                            .size((node.sizeDp * zoomLevel).dp)
                            .clip(CircleShape)
                            .background(node.color)
                            .border(2.dp, node.color.copy(alpha = 0.4f), CircleShape)
                            .clickable { onNavigateToEditorTitle(node.title) }
                    )

                    // Title Label tag next to node
                    Box(
                        modifier = Modifier
                            .offset(
                                x = containerWidth * node.xRatio + (node.sizeDp / 2 + 4).dp,
                                y = containerHeight * node.yRatio - 10.dp
                            )
                            .clip(RoundedCornerShape(6.dp))
                            .background(SurfaceContainer.copy(alpha = 0.85f))
                            .border(1.dp, OutlineVariantBorder.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .clickable { onNavigateToEditorTitle(node.title) }
                    ) {
                        Text(
                            text = node.title,
                            color = OnSurfaceText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // Top Search & Filter Bar
            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    placeholder = { Text("Search your second brain graph...", color = OnSurfaceVariantText) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = ElectricIndigo) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = SurfaceContainerLow.copy(alpha = 0.9f),
                        unfocusedContainerColor = SurfaceContainerLow.copy(alpha = 0.9f),
                        focusedBorderColor = ElectricIndigo,
                        unfocusedBorderColor = OutlineVariantBorder
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("graph_search_input")
                )

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val filters = listOf("All", "Recent", "By Tag", "By Folder")
                    items(filters.size) { index ->
                        val filter = filters[index]
                        FilterChip(
                            selected = selectedFilter == filter,
                            onClick = { selectedFilter = filter },
                            label = { Text(filter) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ElectricIndigo,
                                selectedLabelColor = OxfordBlue,
                                containerColor = SurfaceContainerLow,
                                labelColor = OnSurfaceVariantText
                            )
                        )
                    }
                }
            }

            // Side Controls: Zoom & Pan
            Column(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow.copy(alpha = 0.9f)),
                    shape = RoundedCornerShape(12.dp),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column {
                        IconButton(onClick = { zoomLevel = (zoomLevel + 0.2f).coerceAtMost(2.0f) }) {
                            Icon(Icons.Default.Add, contentDescription = "Zoom In", tint = OnSurfaceText)
                        }
                        HorizontalDivider(color = OutlineVariantBorder.copy(alpha = 0.3f))
                        IconButton(onClick = { zoomLevel = (zoomLevel - 0.2f).coerceAtLeast(0.6f) }) {
                            Icon(Icons.Default.Remove, contentDescription = "Zoom Out", tint = OnSurfaceText)
                        }
                        HorizontalDivider(color = OutlineVariantBorder.copy(alpha = 0.3f))
                        IconButton(onClick = { zoomLevel = 1.0f }) {
                            Icon(Icons.Default.FilterCenterFocus, contentDescription = "Reset Zoom", tint = OnSurfaceText)
                        }
                    }
                }
            }

            // Bottom Suggested Connections Panel
            if (showSuggestions) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp))
                        .background(SurfaceContainer.copy(alpha = 0.95f))
                        .border(1.dp, OutlineVariantBorder.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = SkyBlue)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("SUGGESTED AI CONNECTIONS", fontSize = 12.sp, color = ElectricIndigo, fontWeight = FontWeight.Bold)
                            }
                            IconButton(onClick = { showSuggestions = false }, modifier = Modifier.size(24.dp)) {
                                Icon(Icons.Default.Close, contentDescription = "Close", tint = OnSurfaceVariantText)
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(OxfordBlue)
                                    .border(1.dp, ElectricIndigo.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                    .clickable { onNavigateToEditorTitle("Deep Work Strategy") }
                                    .padding(12.dp)
                            ) {
                                Column {
                                    Text("Deep Work Strategy", color = OnSurfaceText, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("Relates to 'Neural Networks' via focus methods", color = OnSurfaceVariantText, fontSize = 11.sp)
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(OxfordBlue)
                                    .border(1.dp, SkyBlue.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                    .clickable { onNavigateToEditorTitle("Weekly Review") }
                                    .padding(12.dp)
                            ) {
                                Column {
                                    Text("Weekly Review", color = OnSurfaceText, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("80% semantic similarity to 'Reading List'", color = OnSurfaceVariantText, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

class FloatStateOption(val initial: Float) {
    var value by mutableFloatStateOf(initial)
}
