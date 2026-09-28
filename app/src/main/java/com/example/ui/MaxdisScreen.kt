package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AisleItemCard
import com.example.ui.components.BrowserLaunchDialog
import com.example.ui.components.ComplianceDialog
import com.example.ui.components.CustomKeypad
import com.example.ui.components.HeaderBar
import com.example.ui.components.InfoAboutDialog
import com.example.ui.components.TimeStatusCard
import com.example.ui.components.TotalSummaryCard
import com.example.ui.theme.SoftBlueBackground
import com.example.ui.theme.SoftBluePrimary
import com.example.ui.theme.SoftBluePrimaryContainer
import com.example.viewmodel.MaxdisViewModel

@Composable
fun MaxdisScreen(
    viewModel: MaxdisViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = SoftBlueBackground,
        topBar = {
            HeaderBar(
                isAudioEnabled = uiState.isAudioEnabled,
                onToggleAudio = { viewModel.toggleAudio() },
                onMiftAppClick = { viewModel.showInfoDialog(true) },
                onOpenBrowserClick = { viewModel.showBrowserDialog(true) }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 640.dp) // Mobile-first responsive constraint
                    .padding(horizontal = 14.dp),
                contentPadding = PaddingValues(top = 12.dp, bottom = 28.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // 1. Time Status Card (Jam Saat Ini & Estimasi Selesai Terkunci)
                item {
                    TimeStatusCard(
                        currentTimeString = uiState.currentTimeString,
                        frozenEstimatedEndTimeString = uiState.frozenEstimatedEndTimeString,
                        isSessionRunning = uiState.isSessionRunning
                    )
                }

                // 2. Custom Keypad & Formula Input
                item {
                    CustomKeypad(
                        inputFormula = uiState.inputFormula,
                        onKeyPress = { viewModel.onKeypadPress(it) },
                        onBackspace = { viewModel.onBackspace() },
                        onClear = { viewModel.onClearInput() },
                        onEnter = { viewModel.onEnterFormula() }
                    )
                }

                // 3. Total Keseluruhan Countdown & Master Controls
                item {
                    TotalSummaryCard(
                        totalDurationSeconds = uiState.totalDurationSeconds,
                        totalRemainingSeconds = uiState.totalRemainingSeconds,
                        isSessionRunning = uiState.isSessionRunning,
                        workMode = uiState.workMode,
                        onStartClick = { viewModel.startSession() },
                        onPauseClick = { viewModel.pauseSession() },
                        onResetClick = { viewModel.resetSession() },
                        onModeChange = { viewModel.setWorkMode(it) }
                    )
                }

                // 4. Section Header: Lorong List
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp, bottom = 2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.ListAlt,
                                contentDescription = "Daftar Lorong",
                                tint = SoftBluePrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Daftar Lorong Kerja",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Surface(
                            shape = CircleShape,
                            color = SoftBluePrimaryContainer.copy(alpha = 0.6f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SwapVert,
                                    contentDescription = "Urutan",
                                    tint = SoftBluePrimary,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "Urutan Terkecil ke Terbesar",
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = SoftBluePrimary
                                )
                            }
                        }
                    }
                }

                // 5. Individual Aisle Cards
                if (uiState.aisles.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Belum ada lorong diinput.\nMasukkan PLU di atas (contoh: 5+10+6) lalu tekan Enter.",
                                    fontSize = 13.sp,
                                    color = Color.Gray,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                } else {
                    items(
                        items = uiState.aisles,
                        key = { it.id }
                    ) { aisle ->
                        AisleItemCard(
                            aisle = aisle,
                            onSaveClick = { viewModel.saveAisle(aisle.id) }
                        )
                    }
                }
            }
        }
    }

    // Dialogs
    ComplianceDialog(
        alertState = uiState.complianceAlert,
        onDismiss = { viewModel.dismissComplianceAlert() }
    )

    if (uiState.showInfoDialog) {
        InfoAboutDialog(
            onDismiss = { viewModel.showInfoDialog(false) }
        )
    }

    if (uiState.showBrowserDialog) {
        BrowserLaunchDialog(
            onDismiss = { viewModel.showBrowserDialog(false) }
        )
    }
}
