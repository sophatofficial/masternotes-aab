package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.admob.AdMobConfig
import com.example.ui.MainViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: MainViewModel,
    onNavigateToInAppPurchase: () -> Unit
) {
    val isProUnlocked by viewModel.isProUnlocked.collectAsState()

    var e2eEnabled by remember { mutableStateOf(true) }
    var autoSyncEnabled by remember { mutableStateOf(true) }
    var darkThemeEnabled by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings & Account", color = OnSurfaceText, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = OxfordBlue)
            )
        },
        containerColor = OxfordBlue
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Lifetime Pro Banner ($2.50)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceContainer)
                    .border(1.dp, Brush.horizontalGradient(listOf(ElectricIndigo, SkyBlue)), RoundedCornerShape(16.dp))
                    .padding(20.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = ElectricIndigo)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Master Notes Lifetime Pro", color = OnSurfaceText, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }

                        if (isProUnlocked) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(ElectricIndigo)
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text("UNLOCKED", color = OxfordBlue, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                            }
                        } else {
                            Text("$2.50 Lifetime", color = SkyBlue, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }

                    Text(
                        "Unlock unlimited AI notes copilot, local LLM offline support, unlimited knowledge graph, and ad-free experience.",
                        color = OnSurfaceVariantText,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )

                    if (!isProUnlocked) {
                        Button(
                            onClick = { viewModel.unlockProVersion() },
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricIndigo, contentColor = OxfordBlue),
                            modifier = Modifier.align(Alignment.End).testTag("unlock_pro_button")
                        ) {
                            Text("Upgrade for $2.50")
                        }
                    }
                }
            }

            // Security & Encryption
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceContainer),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("SECURITY & ENCRYPTION", color = ElectricIndigo, fontSize = 11.sp, fontWeight = FontWeight.Bold)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("End-to-End Encryption", color = OnSurfaceText, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                            Text("Zero-knowledge AES-256 note vault", color = OnSurfaceVariantText, fontSize = 12.sp)
                        }
                        Switch(
                            checked = e2eEnabled,
                            onCheckedChange = { e2eEnabled = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = ElectricIndigo)
                        )
                    }

                    HorizontalDivider(color = OutlineVariantBorder.copy(alpha = 0.3f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Biometric / Passcode Lock", color = OnSurfaceText, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                            Text("Protect private folders with Fingerprint/Face ID", color = OnSurfaceVariantText, fontSize = 12.sp)
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = OnSurfaceVariantText)
                    }
                }
            }

            // Sync & Storage
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceContainer),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("SYNC & BACKUP", color = ElectricIndigo, fontSize = 11.sp, fontWeight = FontWeight.Bold)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Cloud Automatic Sync", color = OnSurfaceText, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                            Text("Multi-device instant background sync", color = OnSurfaceVariantText, fontSize = 12.sp)
                        }
                        Switch(
                            checked = autoSyncEnabled,
                            onCheckedChange = { autoSyncEnabled = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = ElectricIndigo)
                        )
                    }
                }
            }

            // AdMob Configuration Info
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("ADMOB MONETIZATION CONFIG", color = SkyBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text("App ID: ${AdMobConfig.APP_ID}", color = OnSurfaceText, fontSize = 11.sp)
                    Text("Banner ID: ${AdMobConfig.BANNER_AD_UNIT_ID}", color = OnSurfaceVariantText, fontSize = 11.sp)
                    Text("Interstitial ID: ${AdMobConfig.INTERSTITIAL_AD_UNIT_ID}", color = OnSurfaceVariantText, fontSize = 11.sp)
                    Text("Centralized in AdMobConfig.kt for safe publishing.", color = SlateSecondary, fontSize = 11.sp)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
