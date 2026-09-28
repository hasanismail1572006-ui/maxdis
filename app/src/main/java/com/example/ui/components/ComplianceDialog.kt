package com.example.ui.components

import android.content.Intent
import android.net.Uri
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
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.example.viewmodel.ComplianceAlertState

@Composable
fun ComplianceDialog(
    alertState: ComplianceAlertState,
    onDismiss: () -> Unit
) {
    if (!alertState.isVisible) return

    val isWarning = alertState.isWarning

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("compliance_dialog"),
        shape = RoundedCornerShape(20.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        icon = {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(if (isWarning) StatusWarningRedContainer else StatusSuccessGreenContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isWarning) Icons.Default.Error else Icons.Default.CheckCircle,
                    contentDescription = if (isWarning) "Peringatan" else "Sesuai",
                    tint = if (isWarning) StatusWarningRed else StatusSuccessGreen,
                    modifier = Modifier.size(32.dp)
                )
            }
        },
        title = {
            Text(
                text = alertState.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
                color = if (isWarning) StatusWarningRedText else StatusSuccessGreenText,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = alertState.message,
                    fontSize = 13.5.sp,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )

                if (isWarning && alertState.remainingText.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(StatusWarningRedContainer)
                            .border(1.dp, StatusWarningRedBorder, RoundedCornerShape(10.dp))
                            .padding(10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "SISA WAKTU TERSISA:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = StatusWarningRedText
                            )
                            Text(
                                text = alertState.remainingText,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = FontFamily.Monospace,
                                color = StatusWarningRed
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Tunggu hingga 00:00 sebelum menekan Save di sistem utama!",
                                fontSize = 10.5.sp,
                                textAlign = TextAlign.Center,
                                color = StatusWarningRedText
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isWarning) StatusWarningRed else StatusSuccessGreen,
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("compliance_dialog_confirm_button")
            ) {
                Text(
                    text = if (isWarning) "Mengerti, Tunggu Waktu Habis" else "Lanjutkan Display",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }
    )
}

@Composable
fun InfoAboutDialog(
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(20.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        icon = {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(SoftBluePrimaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "MiftApp Info",
                    tint = SoftBluePrimary,
                    modifier = Modifier.size(28.dp)
                )
            }
        },
        title = {
            Text(
                text = "Kalkulator Maxdis (MiftApp)",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = SoftBluePrimary,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Aplikasi ini dirancang sebagai alat bantu kontrol kepatuhan waktu kerja display per lorong agar pekerja toko tidak menyimpan data terlalu cepat di sistem utama.",
                    fontSize = 12.5.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "• Standar Kepatuhan: 12 PLU = 1 Menit (5 detik / PLU)\n• Timer Anti-Lag: Berbasis Date.now() / System.currentTimeMillis() tanpa resiko lag saat background\n• Fitur Peringatan: Mencegah status merah 'Indikasi Tidak Menggunakan Max Display'\n• Audio Alarm: Notifikasi otomatis saat countdown selesai",
                    fontSize = 11.5.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SoftBluePrimary),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Tutup", fontWeight = FontWeight.Bold)
            }
        }
    )
}

@Composable
fun BrowserLaunchDialog(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(20.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        icon = {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(SoftBluePrimaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.OpenInBrowser,
                    contentDescription = "Buka di Browser",
                    tint = SoftBluePrimary,
                    modifier = Modifier.size(28.dp)
                )
            }
        },
        title = {
            Text(
                text = "Buka di Browser",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "Anda dapat membuka kalkulator ini di browser pilihan Anda (Chrome / Safari) untuk kemudahan akses multi-tab saat bekerja.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Aplikasi ini sepenuhnya mobile-first dan responsif di semua browser.",
                    fontSize = 11.5.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onDismiss()
                    try {
                        val browserIntent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://google.com")
                        )
                        context.startActivity(browserIntent)
                    } catch (e: Exception) {
                        // Fallback
                    }
                },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SoftBluePrimary),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Buka Browser", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Batal")
            }
        }
    )
}
