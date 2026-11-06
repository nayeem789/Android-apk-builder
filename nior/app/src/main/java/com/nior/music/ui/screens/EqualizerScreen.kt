package com.nior.music.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Sparkles
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nior.music.media.NiorEqualizerManager
import com.nior.music.ui.components.LiquidGlassCard
import com.nior.music.ui.theme.NiorCyan
import com.nior.music.ui.theme.NiorPurple

@Composable
fun EqualizerScreen(
    equalizerManager: NiorEqualizerManager?,
    onClose: () -> Unit
) {
    var isEnabled by remember { mutableStateOf(equalizerManager?.isEnabled ?: true) }
    var selectedPreset by remember { mutableStateOf("Normal") }
    var bassStrength by remember { mutableStateOf(200f) }

    val presets = listOf("Normal", "Bass Boost", "Treble", "Vocal", "Rock", "Jazz", "Electronic", "Acoustic")
    val bands = listOf("32Hz", "64Hz", "125Hz", "250Hz", "500Hz", "1kHz", "2kHz", "4kHz", "8kHz", "16kHz")
    var bandLevels by remember { mutableStateOf(List(10) { 0f }) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D0B14))
            .padding(16.dp)
    ) {
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Smart Equalizer", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text("10-Band Hardware AudioFX", fontSize = 11.sp, color = Color(0xFF9CA3AF))
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = {
                    isEnabled = !isEnabled
                    equalizerManager?.setEnabled(isEnabled)
                }) {
                    Icon(
                        imageVector = Icons.Default.PowerSettingsNew,
                        contentDescription = "Power",
                        tint = if (isEnabled) NiorPurple else Color(0xFF6B7280)
                    )
                }
                IconButton(onClick = onClose) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Smart recommendation
        LiquidGlassCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = Color(0x33A855F7)
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Sparkles, contentDescription = null, tint = NiorPurple, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text("Smart Sound Profile", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text("Auto-adjusted reference curve for optimum headroom", fontSize = 10.sp, color = Color(0xFFD1D5DB))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Presets
        Text("Presets", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(presets) { preset ->
                val isSelected = selectedPreset == preset
                Button(
                    onClick = {
                        selectedPreset = preset
                        // Apply preset levels
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSelected) NiorPurple else Color(0x3318142A)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(preset, fontSize = 11.sp, color = Color.White)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Sliders
        Text("Frequency Response (±12 dB)", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(bands.size) { idx ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(bands[idx], modifier = Modifier.width(55.dp), fontSize = 11.sp, color = Color(0xFF9CA3AF))
                    Slider(
                        value = bandLevels[idx],
                        onValueChange = { newVal ->
                            val updated = bandLevels.toMutableList()
                            updated[idx] = newVal
                            bandLevels = updated
                            equalizerManager?.setBandLevel(idx.toShort(), (newVal * 100).toInt().toShort())
                        },
                        valueRange = -12f..12f,
                        modifier = Modifier.weight(1f),
                        enabled = isEnabled,
                        colors = SliderDefaults.colors(
                            thumbColor = Color.White,
                            activeTrackColor = NiorPurple,
                            inactiveTrackColor = Color(0x33FFFFFF)
                        )
                    )
                    Text(
                        "${bandLevels[idx].toInt()}dB",
                        modifier = Modifier.width(45.dp),
                        fontSize = 11.sp,
                        color = Color.White,
                        textAlign = androidx.compose.ui.text.style.TextAlign.End
                    )
                }
            }

            // Bass Boost section
            item {
                Spacer(modifier = Modifier.height(14.dp))
                Text("Sub-Bass Boost", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = NiorCyan)
                Slider(
                    value = bassStrength,
                    onValueChange = {
                        bassStrength = it
                        equalizerManager?.setBassBoostStrength(it.toInt().toShort())
                    },
                    valueRange = 0f..1000f,
                    enabled = isEnabled,
                    colors = SliderDefaults.colors(
                        thumbColor = Color.White,
                        activeTrackColor = NiorCyan,
                        inactiveTrackColor = Color(0x33FFFFFF)
                    )
                )
            }
        }
    }
}
