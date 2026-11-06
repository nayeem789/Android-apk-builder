package com.nior.music.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nior.music.ui.components.LiquidGlassCard
import com.nior.music.ui.theme.*

@Composable
fun SettingsScreen(
    songCount: Int,
    playlistCount: Int,
    onRescanClick: () -> Unit,
    onOpenEqualizer: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 120.dp)
    ) {
        item {
            Text("Settings", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
            Text("Appearance, library scan, and offline preferences", fontSize = 12.sp, color = Color(0xFF9CA3AF))
        }

        // Appearance Card
        item {
            LiquidGlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Palette, contentDescription = null, tint = NiorPurple)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Appearance & Accent", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    Spacer(modifier = Modifier.height(14.dp))

                    Text("Liquid Accent Color", fontSize = 12.sp, color = Color(0xFF9CA3AF))
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        listOf(NiorPurple, NiorCyan, NiorEmerald, NiorRose, NiorAmber).forEach { color ->
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(color)
                            )
                        }
                    }
                }
            }
        }

        // Library & Scanner Card
        item {
            LiquidGlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Sync, contentDescription = null, tint = NiorCyan)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Local Audio Library", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("$songCount Indexed Songs • $playlistCount Playlists", fontSize = 12.sp, color = Color(0xFF9CA3AF))
                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = onRescanClick,
                        colors = ButtonDefaults.buttonColors(containerColor = NiorPurple),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.FolderOpen, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Rescan Device Storage")
                    }
                }
            }
        }

        // Audio Effects Card
        item {
            LiquidGlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.GraphicEq, contentDescription = null, tint = NiorRose)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("DSP Audio Engine", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("10-Band Equalizer & Hardware Bass Boost", fontSize = 12.sp, color = Color(0xFF9CA3AF))
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = onOpenEqualizer,
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NiorPurple)
                    ) {
                        Text("Open Equalizer", color = NiorPurple)
                    }
                }
            }
        }

        // Offline Guarantee Card
        item {
            LiquidGlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Shield, contentDescription = null, tint = NiorEmerald)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Strict Offline & Privacy Guarantee", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "NIOR requires zero internet permissions. All audio decoding, database queries, and equalizer effects execute entirely on your device with zero data collection.",
                        fontSize = 11.sp,
                        color = Color(0xFF9CA3AF),
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // Version Info
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("NIOR Music Player v1.0.0", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Text("Native Android 7.0 - 17 Compatible", color = Color(0xFF6B7280), fontSize = 10.sp)
            }
        }
    }
}
