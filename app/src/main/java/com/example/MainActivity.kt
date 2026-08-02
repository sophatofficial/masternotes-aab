package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.admob.AdMobConfig
import com.example.ui.MainViewModel
import com.example.ui.components.AdMobBanner
import com.example.ui.components.CommandPaletteDialog
import com.example.ui.screens.*
import com.example.ui.theme.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize Google AdMob SDK
        AdMobConfig.initialize(this)

        setContent {
            MasterNotesTheme {
                val viewModel: MainViewModel = viewModel()
                val navController = rememberNavController()

                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route ?: "home"

                val isCommandPaletteOpen by viewModel.isCommandPaletteOpen.collectAsState()
                val currentEditingNote by viewModel.currentEditingNote.collectAsState()

                Scaffold(
                    bottomBar = {
                        Column {
                            // AdMob Banner Ad permanently anchored at bottom above navigation bar
                            AdMobBanner()

                            NavigationBar(
                                containerColor = SurfaceContainerLow,
                                tonalElevation = 8.dp
                            ) {
                                NavigationBarItem(
                                    selected = currentRoute == "home",
                                    onClick = { navController.navigate("home") },
                                    icon = { Icon(if (currentRoute == "home") Icons.Filled.Home else Icons.Outlined.Home, contentDescription = "Home") },
                                    label = { Text("Home") },
                                    colors = NavigationBarItemDefaults.colors(selectedIconColor = ElectricIndigo, indicatorColor = SurfaceContainerHigh),
                                    modifier = Modifier.testTag("nav_home")
                                )

                                NavigationBarItem(
                                    selected = currentRoute == "graph",
                                    onClick = { navController.navigate("graph") },
                                    icon = { Icon(if (currentRoute == "graph") Icons.Filled.Hub else Icons.Outlined.Hub, contentDescription = "Graph") },
                                    label = { Text("Graph") },
                                    colors = NavigationBarItemDefaults.colors(selectedIconColor = ElectricIndigo, indicatorColor = SurfaceContainerHigh),
                                    modifier = Modifier.testTag("nav_graph")
                                )

                                // Center FAB for quick note creation
                                Box(
                                    modifier = Modifier
                                        .size(52.dp)
                                        .clip(CircleShape)
                                        .background(ElectricIndigo)
                                        .clickable {
                                            viewModel.createNewNote("Untitled Note", "Personal")
                                            AdMobConfig.showInterstitialAd(this@MainActivity)
                                            navController.navigate("editor")
                                        }
                                        .testTag("create_note_center_fab"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = "Create Note", tint = OxfordBlue, modifier = Modifier.size(28.dp))
                                }

                                NavigationBarItem(
                                    selected = currentRoute == "tasks",
                                    onClick = { navController.navigate("tasks") },
                                    icon = { Icon(if (currentRoute == "tasks") Icons.Filled.CheckCircle else Icons.Outlined.CheckCircle, contentDescription = "Tasks") },
                                    label = { Text("Tasks") },
                                    colors = NavigationBarItemDefaults.colors(selectedIconColor = ElectricIndigo, indicatorColor = SurfaceContainerHigh),
                                    modifier = Modifier.testTag("nav_tasks")
                                )

                                NavigationBarItem(
                                    selected = currentRoute == "settings",
                                    onClick = { navController.navigate("settings") },
                                    icon = { Icon(if (currentRoute == "settings") Icons.Filled.Settings else Icons.Outlined.Settings, contentDescription = "Settings") },
                                    label = { Text("Settings") },
                                    colors = NavigationBarItemDefaults.colors(selectedIconColor = ElectricIndigo, indicatorColor = SurfaceContainerHigh),
                                    modifier = Modifier.testTag("nav_settings")
                                )
                            }
                        }
                    },
                    containerColor = OxfordBlue
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        NavHost(
                            navController = navController,
                            startDestination = "home"
                        ) {
                            composable("home") {
                                HomeScreen(
                                    viewModel = viewModel,
                                    onNavigateToEditor = { note ->
                                        viewModel.selectNoteForEditing(note)
                                        navController.navigate("editor")
                                    },
                                    onNavigateToScanner = { navController.navigate("scanner") },
                                    onNavigateToTasks = { navController.navigate("tasks") },
                                    onNavigateToGraph = { navController.navigate("graph") }
                                )
                            }

                            composable("editor") {
                                NoteEditorScreen(
                                    viewModel = viewModel,
                                    note = currentEditingNote,
                                    onBack = { navController.popBackStack() }
                                )
                            }

                            composable("graph") {
                                KnowledgeGraphScreen(
                                    viewModel = viewModel,
                                    onNavigateToEditorTitle = { title ->
                                        viewModel.createNewNote(title, "Work")
                                        navController.navigate("editor")
                                    }
                                )
                            }

                            composable("tasks") {
                                TasksScreen(
                                    viewModel = viewModel,
                                    onNavigateToEditorTitle = { title ->
                                        viewModel.createNewNote(title, "Tasks")
                                        navController.navigate("editor")
                                    }
                                )
                            }

                            composable("scanner") {
                                ScannerScreen(
                                    viewModel = viewModel,
                                    onBack = { navController.popBackStack() },
                                    onNavigateToEditorTitle = { title ->
                                        navController.navigate("editor")
                                    }
                                )
                            }

                            composable("settings") {
                                SettingsScreen(
                                    viewModel = viewModel,
                                    onNavigateToInAppPurchase = { /* Handle purchase dialog */ }
                                )
                            }
                        }

                        // Command Palette Overlay
                        if (isCommandPaletteOpen) {
                            CommandPaletteDialog(
                                viewModel = viewModel,
                                onNavigateToEditor = { note ->
                                    viewModel.selectNoteForEditing(note)
                                    navController.navigate("editor")
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
