package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScannerScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onNavigateToEditorTitle: (String) -> Unit
) {
    var isScanning by remember { mutableStateOf(false) }
    var extractedText by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = ElectricIndigo)
                    }
                },
                title = { Text("OCR Document Scanner", color = OnSurfaceText, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = OxfordBlue)
            )
        },
        containerColor = OxfordBlue
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Viewfinder box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(380.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceContainerLow)
                    .border(2.dp, ElectricIndigo, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (extractedText == null) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.DocumentScanner,
                            contentDescription = null,
                            tint = SkyBlue,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Position document inside frame", color = OnSurfaceVariantText, fontSize = 14.sp)
                        Text("AI OCR automatically extracts printed text", color = SlateSecondary, fontSize = 12.sp)
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Text("Extracted OCR Text:", color = SkyBlue, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = extractedText ?: "",
                            color = OnSurfaceText,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            // Controls
            if (extractedText == null) {
                Button(
                    onClick = {
                        isScanning = true
                        extractedText = "Scanned Contract Summary\n\n1. Scope of work: Modular Architecture and Jetpack Compose Migration.\n2. Timeline: Oct 2026 completion.\n3. Cloud backup & E2E encryption verified."
                        isScanning = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricIndigo, contentColor = OxfordBlue),
                    modifier = Modifier
                        .height(50.dp)
                        .fillMaxWidth(0.7f)
                        .testTag("scan_capture_button")
                ) {
                    Icon(Icons.Default.Camera, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Capture & Scan OCR", fontWeight = FontWeight.Bold)
                }
            } else {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = {
                            viewModel.createNewNote("Scanned Contract Note", "Work")
                            onNavigateToEditorTitle("Scanned Contract Note")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricIndigo, contentColor = OxfordBlue)
                    ) {
                        Icon(Icons.Default.NoteAdd, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Save as Note")
                    }

                    OutlinedButton(
                        onClick = { extractedText = null },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = OnSurfaceText)
                    ) {
                        Text("Retake")
                    }
                }
            }
        }
    }
}
