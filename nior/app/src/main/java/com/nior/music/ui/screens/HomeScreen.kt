package com.nior.music.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.nior.music.model.Song
import com.nior.music.ui.components.LiquidGlassCard
import com.nior.music.ui.theme.NiorPurple

@Composable
fun HomeScreen(
    songs: List<Song>,
    recentlyPlayed: List<Song>,
    frequentlyPlayed: List<Song>,
    currentSong: Song?,
    isPlaying: Boolean,
    onPlaySong: (Song) -> Unit,
    onTogglePlay: () -> Unit,
    onNavigateToMusic: () -> Unit,
    onNavigateToPlaylist: () -> Unit,
    onNavigateToEqualizer: () -> Unit,
    onRescanClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 120.dp)
    ) {
        // Hero Liquid Glass Banner
        item {
            LiquidGlassCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = Color(0x9918142A)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "PURE OFFLINE AUDIO",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = NiorPurple,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "NIOR Music Player",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Text(
                        text = "High dynamic range playback directly from your local storage.",
                        fontSize = 12.sp,
                        color = Color(0xFF9CA3AF),
                        modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = onRescanClick,
                            colors = ButtonDefaults.buttonColors(containerColor = NiorPurple),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(Icons.Default.FolderOpen, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Scan Storage", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }

                        OutlinedButton(
                            onClick = onNavigateToMusic,
                            shape = RoundedCornerShape(14.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x33FFFFFF))
                        ) {
                            Text("All Songs (${songs.size})", fontSize = 12.sp, color = Color.White)
                        }
                    }
                }
            }
        }

        // Quick Access Matrix
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickCard(
                    modifier = Modifier.weight(1f),
                    title = "Library",
                    subtitle = "${songs.size} Songs",
                    icon = Icons.Default.MusicNote,
                    color = NiorPurple,
                    onClick = onNavigateToMusic
                )
                QuickCard(
                    modifier = Modifier.weight(1f),
                    title = "Playlists",
                    subtitle = "Organize",
                    icon = Icons.Default.QueueMusic,
                    color = Color(0xFF06B6D4),
                    onClick = onNavigateToPlaylist
                )
                QuickCard(
                    modifier = Modifier.weight(1f),
                    title = "Equalizer",
                    subtitle = "10 Bands",
                    icon = Icons.Default.GraphicEq,
                    color = Color(0xFFF43F5E),
                    onClick = onNavigateToEqualizer
                )
            }
        }

        // Empty State
        if (songs.isEmpty()) {
            item {
                LiquidGlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Album,
                            contentDescription = null,
                            tint = NiorPurple,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No Audio Files Found", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(
                            "Tap 'Scan Storage' to load music files stored on your Android device.",
                            color = Color(0xFF9CA3AF),
                            fontSize = 12.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                        )
                        Button(
                            onClick = onRescanClick,
                            colors = ButtonDefaults.buttonColors(containerColor = NiorPurple)
                        ) {
                            Text("Scan MediaStore")
                        }
                    }
                }
            }
        }

        // Recently Played
        if (recentlyPlayed.isNotEmpty()) {
            item {
                Text(
                    text = "Recently Played",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(recentlyPlayed) { song ->
                        RecentSongCard(song = song, onPlay = { onPlaySong(song) })
                    }
                }
            }
        }

        // Frequently Played
        if (frequentlyPlayed.isNotEmpty()) {
            item {
                Text(
                    text = "Frequently Played",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            items(frequentlyPlayed) { song ->
                FrequentSongRow(
                    song = song,
                    isCurrent = currentSong?.id == song.id,
                    isPlaying = isPlaying,
                    onPlay = { onPlaySong(song) },
                    onToggle = onTogglePlay
                )
            }
        }
    }
}

@Composable
fun QuickCard(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    LiquidGlassCard(
        modifier = modifier.clickable { onClick() },
        backgroundColor = Color(0x6618142A)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White, maxLines = 1)
            Text(subtitle, fontSize = 11.sp, color = Color(0xFF9CA3AF), maxLines = 1)
        }
    }
}

@Composable
fun RecentSongCard(song: Song, onPlay: () -> Unit) {
    LiquidGlassCard(
        modifier = Modifier
            .width(130.dp)
            .clickable { onPlay() },
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF201A38)),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = song.contentUri,
                    contentDescription = song.title,
                    modifier = Modifier.fillMaxSize()
                )
                Icon(Icons.Default.MusicNote, contentDescription = null, tint = Color(0x44FFFFFF), modifier = Modifier.size(32.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(song.title, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(song.artist, fontSize = 10.sp, color = Color(0xFF9CA3AF), maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
fun FrequentSongRow(
    song: Song,
    isCurrent: Boolean,
    isPlaying: Boolean,
    onPlay: () -> Unit,
    onToggle: () -> Unit
) {
    LiquidGlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onPlay() },
        backgroundColor = if (isCurrent) Color(0x44A855F7) else Color(0x4418142A)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF231C3D)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.MusicNote, contentDescription = null, tint = NiorPurple)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(song.title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = if (isCurrent) NiorPurple else Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text("${song.artist} • ${song.playCount} plays", fontSize = 11.sp, color = Color(0xFF9CA3AF), maxLines = 1)
            }
            IconButton(onClick = { if (isCurrent) onToggle() else onPlay() }) {
                Icon(
                    imageVector = if (isCurrent && isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = NiorPurple
                )
            }
        }
    }
}
