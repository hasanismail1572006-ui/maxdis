package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.SoftBlueOutline
import com.example.ui.theme.SoftBluePrimary
import com.example.ui.theme.SoftBluePrimaryContainer

@Composable
fun TimeStatusCard(
    currentTimeString: String,
    frozenEstimatedEndTimeString: String,
    isSessionRunning: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Left Box: Jam Saat Ini (Real-time live clock)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF7FAFD))
                    .border(1.dp, SoftBlueOutline, RoundedCornerShape(12.dp))
                    .padding(12.dp)
                    .testTag("current_time_box")
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = "Jam Saat Ini",
                            tint = SoftBluePrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Jam Saat Ini",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = if (currentTimeString.isEmpty()) "--:--:--" else currentTimeString,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.testTag("current_time_text")
                    )

                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Waktu Real-Time",
                        fontSize = 9.5.sp,
                        color = Color.Gray
                    )
                }
            }

            // Right Box: Estimasi Selesai (Frozen upon 'Mulai' click)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (isSessionRunning || frozenEstimatedEndTimeString != "--:--:--")
                            SoftBluePrimaryContainer.copy(alpha = 0.5f)
                        else
                            Color(0xFFF7FAFD)
                    )
                    .border(
                        1.dp,
                        if (isSessionRunning) SoftBluePrimary else SoftBlueOutline,
                        RoundedCornerShape(12.dp)
                    )
                    .padding(12.dp)
                    .testTag("estimated_finish_box")
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (frozenEstimatedEndTimeString != "--:--:--") Icons.Default.Lock else Icons.Default.LockOpen,
                                contentDescription = "Estimasi Selesai",
                                tint = SoftBluePrimary,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Estimasi Selesai",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = frozenEstimatedEndTimeString,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = if (frozenEstimatedEndTimeString == "--:--:--") Color.Gray else SoftBluePrimary,
                        modifier = Modifier.testTag("estimated_finish_text")
                    )

                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (frozenEstimatedEndTimeString == "--:--:--")
                            "Terkunci saat 'Mulai'"
                        else
                            "Terkunci (Jam Mulai + Total)",
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (frozenEstimatedEndTimeString == "--:--:--") Color.Gray else SoftBluePrimary
                    )
                }
            }
        }
    }
}
