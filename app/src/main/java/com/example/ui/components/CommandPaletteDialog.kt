package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.db.NoteEntity
import com.example.ui.MainViewModel
import com.example.ui.theme.*

@Composable
fun CommandPaletteDialog(
    viewModel: MainViewModel,
    onNavigateToEditor: (NoteEntity) -> Unit
) {
    val notes by viewModel.notes.collectAsState()
    var queryText by remember { mutableStateOf("") }

    val filteredNotes = remember(queryText, notes) {
        if (queryText.isEmpty()) notes else notes.filter {
            it.title.contains(queryText, ignoreCase = true) || it.content.contains(queryText, ignoreCase = true)
        }
    }

    Dialog(onDismissRequest = { viewModel.setCommandPaletteOpen(false) }) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(SurfaceContainer.copy(alpha = 0.98f))
                .border(1.dp, OutlineVariantBorder, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Search, contentDescription = null, tint = ElectricIndigo)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Command Palette", color = OnSurfaceText, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(SurfaceContainerHigh)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text("ESC", color = OnSurfaceVariantText, fontSize = 11.sp)
                    }
                }

                TextField(
                    value = queryText,
                    onValueChange = {
                        queryText = it
                        viewModel.setSearchQuery(it)
                    },
                    placeholder = { Text("Search notes or ask AI...", color = OnSurfaceVariantText) },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = OxfordBlue,
                        unfocusedContainerColor = OxfordBlue,
                        focusedIndicatorColor = ElectricIndigo
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("command_palette_input")
                )

                LazyColumn(
                    modifier = Modifier.heightIn(max = 240.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(filteredNotes) { note ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    viewModel.setCommandPaletteOpen(false)
                                    onNavigateToEditor(note)
                                }
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Outlined.Description, contentDescription = null, tint = SkyBlue, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(note.title, color = OnSurfaceText, fontSize = 14.sp)
                            }
                            Text("Shift + Enter to open", color = OnSurfaceVariantText, fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}
