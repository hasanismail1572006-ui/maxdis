package com.example.ui.components

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AisleItem
import com.example.model.ComplianceStatus
import com.example.ui.theme.SoftBlueOutline
import com.example.ui.theme.SoftBluePrimary
import com.example.ui.theme.SoftBluePrimaryContainer
import com.example.ui.theme.StatusSuccessGreen
import com.example.ui.theme.StatusSuccessGreenBorder
import com.example.ui.theme.StatusSuccessGreenContainer
import com.example.ui.theme.StatusSuccessGreenText
import com.example.ui.theme.StatusWarningRed
import com.example.ui.theme.StatusWarningRedBorder
import com.example.ui.theme.StatusWarningRedContainer
import com.example.ui.theme.StatusWarningRedText

@Composable
fun AisleItemCard(
    aisle: AisleItem,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Dynamic border and background based on compliance status
    val borderColor by animateColorAsState(
        targetValue = when (aisle.complianceStatus) {
            ComplianceStatus.WARNING_EARLY -> StatusWarningRedBorder
            ComplianceStatus.COMPLIANT_ON_TIME -> StatusSuccessGreenBorder
            ComplianceStatus.PENDING -> if (aisle.isRunning) SoftBluePrimary else SoftBlueOutline
        },
        label = "borderColor"
    )

    val cardBgColor by animateColorAsState(
        targetValue = when (aisle.complianceStatus) {
            ComplianceStatus.WARNING_EARLY -> StatusWarningRedContainer.copy(alpha = 0.35f)
            ComplianceStatus.COMPLIANT_ON_TIME -> StatusSuccessGreenContainer.copy(alpha = 0.35f)
            ComplianceStatus.PENDING -> if (aisle.isRunning) SoftBluePrimaryContainer.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surface
        },
        label = "bgColor"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = if (aisle.complianceStatus != ComplianceStatus.PENDING || aisle.isRunning) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(16.dp)
            )
            .testTag("aisle_card_${aisle.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardBgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Top Row: Aisle Name, PLU Count, and Selesai (Save) Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(
                                when (aisle.complianceStatus) {
                                    ComplianceStatus.WARNING_EARLY -> StatusWarningRed
                                    ComplianceStatus.COMPLIANT_ON_TIME -> StatusSuccessGreen
                                    ComplianceStatus.PENDING -> if (aisle.isRunning) SoftBluePrimary else SoftBlueOutline
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${aisle.id}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = aisle.name,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.testTag("aisle_name_${aisle.id}")
                        )
                        Text(
                            text = "${aisle.pluCount} PLU • Standar: ${aisle.formattedDuration}",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Selesai (Save) Button (Requirement 6)
                Button(
                    onClick = onSaveClick,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = when (aisle.complianceStatus) {
                            ComplianceStatus.WARNING_EARLY -> StatusWarningRed
                            ComplianceStatus.COMPLIANT_ON_TIME -> StatusSuccessGreen
                            ComplianceStatus.PENDING -> SoftBluePrimary
                        },
                        contentColor = Color.White
                    ),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(
                        horizontal = 12.dp,
                        vertical = 8.dp
                    ),
                    modifier = Modifier
                        .height(38.dp)
                        .testTag("save_button_${aisle.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = "Simpan",
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Selesai (Save)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Middle Row: Timer Countdown & Running Indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Countdown Digits
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = aisle.formattedRemaining,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.Monospace,
                        color = when {
                            aisle.complianceStatus == ComplianceStatus.WARNING_EARLY -> StatusWarningRed
                            aisle.remainingSeconds == 0L -> StatusSuccessGreen
                            aisle.isRunning -> SoftBluePrimary
                            else -> MaterialTheme.colorScheme.onSurface
                        },
                        modifier = Modifier.testTag("timer_text_${aisle.id}")
                    )

                    if (aisle.isRunning) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = CircleShape,
                            color = SoftBluePrimary.copy(alpha = 0.15f),
                            modifier = Modifier.padding(2.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Aktif",
                                    tint = SoftBluePrimary,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = "Berjalan",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SoftBluePrimary
                                )
                            }
                        }
                    }
                }

                // Status chip / feedback
                when (aisle.complianceStatus) {
                    ComplianceStatus.WARNING_EARLY -> {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = StatusWarningRedContainer,
                            border = androidx.compose.foundation.BorderStroke(1.dp, StatusWarningRedBorder)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ErrorOutline,
                                    contentDescription = "Peringatan",
                                    tint = StatusWarningRed,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Terlalu Cepat!",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = StatusWarningRedText
                                )
                            }
                        }
                    }
                    ComplianceStatus.COMPLIANT_ON_TIME -> {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = StatusSuccessGreenContainer,
                            border = androidx.compose.foundation.BorderStroke(1.dp, StatusSuccessGreenBorder)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Sesuai",
                                    tint = StatusSuccessGreen,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Sesuai Waktu",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = StatusSuccessGreenText
                                )
                            }
                        }
                    }
                    ComplianceStatus.PENDING -> {
                        if (aisle.remainingSeconds == 0L) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = StatusSuccessGreenContainer
                            ) {
                                Text(
                                    text = "Waktu Habis (Siap Save)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = StatusSuccessGreenText,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        } else if (!aisle.isRunning) {
                            Text(
                                text = "Menunggu",
                                fontSize = 11.sp,
                                color = Color.Gray,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Progress Bar
            LinearProgressIndicator(
                progress = { aisle.progressFraction },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(CircleShape),
                color = when (aisle.complianceStatus) {
                    ComplianceStatus.WARNING_EARLY -> StatusWarningRed
                    ComplianceStatus.COMPLIANT_ON_TIME -> StatusSuccessGreen
                    ComplianceStatus.PENDING -> SoftBluePrimary
                },
                trackColor = SoftBlueOutline.copy(alpha = 0.4f)
            )

            // Prominent Compliance Status Message (Requirement 6)
            if (aisle.complianceStatus == ComplianceStatus.WARNING_EARLY) {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(StatusWarningRed)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("compliance_warning_banner_${aisle.id}")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.ErrorOutline,
                            contentDescription = "Indikasi",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "INDIKASI TIDAK MENGGUNAKAN MAX DISPLAY",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }
                }
            } else if (aisle.complianceStatus == ComplianceStatus.COMPLIANT_ON_TIME) {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(StatusSuccessGreen)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("compliance_success_banner_${aisle.id}")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Sesuai",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "DISPLAY SESUAI WAKTU",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
