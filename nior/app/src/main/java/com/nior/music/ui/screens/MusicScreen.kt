package com.nior.music.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nior.music.model.Song
import com.nior.music.ui.components.LiquidGlassCard
import com.nior.music.ui.theme.NiorPurple

@Composable
fun MusicScreen(
    songs: List<Song>,
    currentSong: Song?,
    isPlaying: Boolean,
    onPlaySong: (Song) -> Unit,
    onToggleFavorite: (Song) -> Unit,
    onAddToPlaylist: (Song) -> Unit,
    onRescanClick: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) } // 0: Songs, 1: Artists, 2: Albums
    var sortOrder by remember { mutableStateOf("Title") }

    val sortedSongs = remember(songs, sortOrder) {
        when (sortOrder) {
            "Artist" -> songs.sortedBy { it.artist.lowercase() }
            "Album" -> songs.sortedBy { it.album.lowercase() }
            "Duration" -> songs.sortedByDescending { it.durationMs }
            "Date" -> songs.sortedByDescending { it.dateAdded }
            else -> songs.sortedBy { it.title.lowercase() }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Header with Tabs
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Audio Library", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
            IconButton(onClick = onRescanClick) {
                Icon(Icons.Default.Refresh, contentDescription = "Rescan", tint = NiorPurple)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Tab Selector & Sort
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TabRow(
                selectedTabIndex = selectedTab,
                modifier = Modifier.width(220.dp),
                containerColor = Color.Transparent,
                contentColor = NiorPurple,
                divider = {}
            ) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("Songs", fontSize = 12.sp) })
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("Artists", fontSize = 12.sp) })
                Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }, text = { Text("Albums", fontSize = 12.sp) })
            }

            OutlinedButton(
                onClick = {
                    sortOrder = when (sortOrder) {
                        "Title" -> "Artist"
                        "Artist" -> "Album"
                        "Album" -> "Duration"
                        "Duration" -> "Date"
                        else -> "Title"
                    }
                },
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x33FFFFFF)),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Icon(Icons.Default.Sort, contentDescription = null, modifier = Modifier.size(14.dp), tint = NiorPurple)
                Spacer(modifier = Modifier.width(4.dp))
                Text(sortOrder, fontSize = 11.sp, color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Songs List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 120.dp)
        ) {
            items(sortedSongs) { song ->
                val isCurrent = currentSong?.id == song.id
                LiquidGlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onPlaySong(song) },
                    backgroundColor = if (isCurrent) Color(0x44A855F7) else Color(0x4418142A)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = song.title,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isCurrent) NiorPurple else Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${song.artist} • ${song.album}",
                                fontSize = 11.sp,
                                color = Color(0xFF9CA3AF),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        IconButton(onClick = { onToggleFavorite(song) }) {
                            Icon(
                                imageVector = if (song.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favorite",
                                tint = if (song.isFavorite) Color(0xFFF43F5E) else Color(0xFF9CA3AF),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        IconButton(onClick = { onAddToPlaylist(song) }) {
                            Icon(Icons.Default.PlaylistAdd, contentDescription = "Add to playlist", tint = Color(0xFF9CA3AF), modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }
        }
    }
}
