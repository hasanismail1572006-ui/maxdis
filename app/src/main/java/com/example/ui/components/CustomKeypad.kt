package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.KeypadButtonActionBg
import com.example.ui.theme.KeypadButtonBg
import com.example.ui.theme.KeypadEnterBg
import com.example.ui.theme.SoftBlueOutline
import com.example.ui.theme.SoftBluePrimary
import com.example.ui.theme.SoftBluePrimaryContainer

@Composable
fun CustomKeypad(
    inputFormula: String,
    onKeyPress: (String) -> Unit,
    onBackspace: () -> Unit,
    onClear: () -> Unit,
    onEnter: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isKeypadExpanded by remember { mutableStateOf(true) }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Formula Guide Text (Requirement 3)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(SoftBluePrimaryContainer.copy(alpha = 0.6f))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Calculate,
                        contentDescription = "Rumus",
                        tint = SoftBluePrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Rumus: PLU x 5 : 60 (12 PLU = 1 Menit)",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.testTag("formula_guide_text")
                    )
                }

                // Toggle keypad view
                IconButton(
                    onClick = { isKeypadExpanded = !isKeypadExpanded },
                    modifier = Modifier
                        .size(24.dp)
                        .testTag("toggle_keypad_button")
                ) {
                    Icon(
                        imageVector = if (isKeypadExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = if (isKeypadExpanded) "Sembunyikan Keypad" else "Tampilkan Keypad",
                        tint = SoftBluePrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Readonly Display Field (Requirement 2)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF6FAFD))
                    .border(1.5.dp, SoftBlueOutline, RoundedCornerShape(12.dp))
                    .padding(horizontal = 14.dp, vertical = 10.dp)
                    .testTag("formula_display_box"),
                contentAlignment = Alignment.CenterEnd
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Input PLU:",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (inputFormula.isEmpty()) "0" else inputFormula,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = if (inputFormula.isEmpty()) Color.Gray else SoftBluePrimary,
                            textAlign = TextAlign.End,
                            modifier = Modifier.testTag("formula_input_text")
                        )

                        if (inputFormula.isNotEmpty()) {
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(
                                onClick = onClear,
                                modifier = Modifier
                                    .size(28.dp)
                                    .testTag("clear_input_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Hapus Semua",
                                    tint = Color.Gray,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Quick Preset Suggestions
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Contoh:",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
                PresetChip(text = "5+10+6", onClick = {
                    onClear()
                    "5+10+6".forEach { onKeyPress(it.toString()) }
                })
                PresetChip(text = "12+24+36", onClick = {
                    onClear()
                    "12+24+36".forEach { onKeyPress(it.toString()) }
                })
                PresetChip(text = "8+15+20", onClick = {
                    onClear()
                    "8+15+20".forEach { onKeyPress(it.toString()) }
                })
            }

            // Custom Keypad Grid (Animated Visibility)
            AnimatedVisibility(
                visible = isKeypadExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                ) {
                    // Keypad Rows
                    val row1 = listOf("1", "2", "3")
                    val row2 = listOf("4", "5", "6")
                    val row3 = listOf("7", "8", "9")

                    KeypadRow(items = row1, onKeyPress = onKeyPress)
                    Spacer(modifier = Modifier.height(6.dp))
                    KeypadRow(items = row2, onKeyPress = onKeyPress)
                    Spacer(modifier = Modifier.height(6.dp))
                    KeypadRow(items = row3, onKeyPress = onKeyPress)
                    Spacer(modifier = Modifier.height(6.dp))

                    // Row 4: [+] [0] [⌫]
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        KeypadButton(
                            text = "+",
                            isAccent = true,
                            onClick = { onKeyPress("+") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("keypad_plus_button")
                        )
                        KeypadButton(
                            text = "0",
                            onClick = { onKeyPress("0") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("keypad_0_button")
                        )
                        KeypadIconButton(
                            onClick = onBackspace,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("keypad_backspace_button")
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // ENTER button (Requirement 2)
                    Button(
                        onClick = onEnter,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("keypad_enter_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = KeypadEnterBg,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Enter",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "ENTER (PROSES & URUTKAN LORONG)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PresetChip(text: String, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = KeypadButtonBg,
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .clickable(onClick = onClick)
    ) {
        Text(
            text = text,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = SoftBluePrimary,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}

@Composable
private fun KeypadRow(
    items: List<String>,
    onKeyPress: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        items.forEach { key ->
            KeypadButton(
                text = key,
                onClick = { onKeyPress(key) },
                modifier = Modifier
                    .weight(1f)
                    .testTag("keypad_${key}_button")
            )
        }
    }
}

@Composable
private fun KeypadButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isAccent: Boolean = false
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        color = if (isAccent) KeypadButtonActionBg else KeypadButtonBg,
        shadowElevation = 1.dp,
        modifier = modifier.height(46.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = text,
                fontSize = if (isAccent) 24.sp else 20.sp,
                fontWeight = FontWeight.Bold,
                color = if (isAccent) SoftBluePrimary else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun KeypadIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        color = KeypadButtonActionBg,
        shadowElevation = 1.dp,
        modifier = modifier.height(46.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Backspace,
                contentDescription = "Backspace Hapus",
                tint = SoftBluePrimary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
