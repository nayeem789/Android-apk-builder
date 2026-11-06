package com.nior.music

import android.Manifest
import android.content.ComponentName
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import com.nior.music.media.AudioScanner
import com.nior.music.media.NiorPlaybackService
import com.nior.music.model.Playlist
import com.nior.music.model.Song
import com.nior.music.ui.components.LiquidGlassCard
import com.nior.music.ui.screens.*
import com.nior.music.ui.theme.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

enum class Screen {
    HOME, MUSIC, PLAYLIST, SETTINGS, SEARCH
}

class MainActivity : ComponentActivity() {

    private lateinit var controllerFuture: ListenableFuture<MediaController>
    private var mediaController: MediaController? = null

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            triggerLibraryScan()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sessionToken = SessionToken(this, ComponentName(this, NiorPlaybackService::class.java))
        controllerFuture = MediaController.Builder(this, sessionToken).buildAsync()
        controllerFuture.addListener({
            mediaController = controllerFuture.get()
        }, MoreExecutors.directExecutor())

        checkAndRequestAudioPermission()

        setContent {
            NiorTheme {
                val db = (application as NiorApplication).database
                val songs by db.songDao().getAllSongs().collectAsState(initial = emptyList())
                val recentlyPlayed by db.songDao().getRecentlyPlayed().collectAsState(initial = emptyList())
                val frequentlyPlayed by db.songDao().getFrequentlyPlayed().collectAsState(initial = emptyList())
                val playlists by db.playlistDao().getAllPlaylists().collectAsState(initial = emptyList())

                var currentScreen by remember { mutableStateOf(Screen.HOME) }
                var currentSong by remember { mutableStateOf<Song?>(null) }
                var isPlaying by remember { mutableStateOf(false) }
                var isFullPlayerOpen by remember { mutableStateOf(false) }
                var isEqualizerOpen by remember { mutableStateOf(false) }
                var currentPosMs by remember { mutableStateOf(0L) }
                var durationMs by remember { mutableStateOf(0L) }
                var isShuffle by remember { mutableStateOf(false) }
                var repeatMode by remember { mutableStateOf(0) }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    ObsidianBackground,
                                    Color(0xFF130E24),
                                    ObsidianBackground
                                )
                            )
                        )
                ) {
                    // Ambient Liquid Accent Glow backdrop
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(320.dp)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        NiorPurple.copy(alpha = 0.16f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )

                    // Active Screen Content
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = if (currentSong != null) 140.dp else 80.dp)
                    ) {
                        when (currentScreen) {
                            Screen.HOME -> HomeScreen(
                                songs = songs,
                                recentlyPlayed = recentlyPlayed,
                                frequentlyPlayed = frequentlyPlayed,
                                currentSong = currentSong,
                                isPlaying = isPlaying,
                                onPlaySong = { song -> playSong(song); currentSong = song; isPlaying = true },
                                onTogglePlay = { togglePlay() },
                                onNavigateToMusic = { currentScreen = Screen.MUSIC },
                                onNavigateToPlaylist = { currentScreen = Screen.PLAYLIST },
                                onNavigateToEqualizer = { isEqualizerOpen = true },
                                onRescanClick = { checkAndRequestAudioPermission() }
                            )
                            Screen.MUSIC -> MusicScreen(
                                songs = songs,
                                currentSong = currentSong,
                                isPlaying = isPlaying,
                                onPlaySong = { song -> playSong(song); currentSong = song; isPlaying = true },
                                onToggleFavorite = { song ->
                                    lifecycleScope.launch {
                                        db.songDao().setFavorite(song.id, !song.isFavorite)
                                    }
                                },
                                onAddToPlaylist = { /* Add to playlist */ },
                                onRescanClick = { checkAndRequestAudioPermission() }
                            )
                            Screen.PLAYLIST -> PlaylistScreen(
                                playlists = playlists,
                                onCreatePlaylist = { name ->
                                    lifecycleScope.launch {
                                        db.playlistDao().insertPlaylist(Playlist(name = name))
                                    }
                                },
                                onDeletePlaylist = { pl ->
                                    lifecycleScope.launch {
                                        db.playlistDao().deletePlaylist(pl)
                                    }
                                },
                                onOpenPlaylist = { /* Open playlist */ }
                            )
                            Screen.SETTINGS -> SettingsScreen(
                                songCount = songs.size,
                                playlistCount = playlists.size,
                                onRescanClick = { checkAndRequestAudioPermission() },
                                onOpenEqualizer = { isEqualizerOpen = true }
                            )
                            Screen.SEARCH -> SearchScreen(
                                songs = songs,
                                onPlaySong = { song -> playSong(song); currentSong = song; isPlaying = true }
                            )
                        }
                    }

                    // Mini-Player Floating Above Nav Bar
                    if (currentSong != null && !isFullPlayerOpen) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(horizontal = 16.dp, vertical = 84.dp)
                        ) {
                            MiniPlayerCard(
                                song = currentSong!!,
                                isPlaying = isPlaying,
                                onTogglePlay = { togglePlay() },
                                onExpand = { isFullPlayerOpen = true }
                            )
                        }
                    }

                    // Bottom Navigation Bar with Moveable Liquid Pill
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(horizontal = 16.dp, vertical = 18.dp)
                    ) {
                        LiquidGlassBottomNav(
                            selected = currentScreen,
                            onSelect = { currentScreen = it }
                        )
                    }

                    // Full Screen Player
                    if (isFullPlayerOpen && currentSong != null) {
                        FullPlayerScreen(
                            song = currentSong!!,
                            isPlaying = isPlaying,
                            currentPositionMs = currentPosMs,
                            totalDurationMs = durationMs,
                            isShuffle = isShuffle,
                            repeatMode = repeatMode,
                            onClose = { isFullPlayerOpen = false },
                            onTogglePlay = { togglePlay() },
                            onNext = { mediaController?.seekToNextMediaItem() },
                            onPrevious = { mediaController?.seekToPreviousMediaItem() },
                            onSeekTo = { mediaController?.seekTo(it) },
                            onToggleShuffle = {
                                isShuffle = !isShuffle
                                mediaController?.shuffleModeEnabled = isShuffle
                            },
                            onCycleRepeat = {
                                repeatMode = (repeatMode + 1) % 3
                                mediaController?.repeatMode = when (repeatMode) {
                                    1 -> Player.REPEAT_MODE_ALL
                                    2 -> Player.REPEAT_MODE_ONE
                                    else -> Player.REPEAT_MODE_OFF
                                }
                            },
                            onToggleFavorite = { song ->
                                lifecycleScope.launch {
                                    db.songDao().setFavorite(song.id, !song.isFavorite)
                                }
                            },
                            onOpenEqualizer = { isEqualizerOpen = true }
                        )
                    }

                    // Equalizer Screen Modal
                    if (isEqualizerOpen) {
                        EqualizerScreen(
                            equalizerManager = null,
                            onClose = { isEqualizerOpen = false }
                        )
                    }
                }
            }
        }
    }

    private fun checkAndRequestAudioPermission() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_AUDIO
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            triggerLibraryScan()
        } else {
            permissionLauncher.launch(permission)
        }
    }

    private fun triggerLibraryScan() {
        lifecycleScope.launch {
            val scanned = AudioScanner(this@MainActivity).scanLocalAudio()
            val db = (application as NiorApplication).database
            db.songDao().insertSongs(scanned)
        }
    }

    private fun playSong(song: Song) {
        val controller = mediaController ?: return
        val mediaItem = MediaItem.fromUri(song.contentUri)
        controller.setMediaItem(mediaItem)
        controller.prepare()
        controller.play()

        lifecycleScope.launch {
            (application as NiorApplication).database.songDao().recordPlayback(song.id, System.currentTimeMillis())
        }
    }

    private fun togglePlay() {
        val controller = mediaController ?: return
        if (controller.isPlaying) {
            controller.pause()
        } else {
            controller.play()
        }
    }

    override fun onDestroy() {
        MediaController.releaseFuture(controllerFuture)
        super.onDestroy()
    }
}

@Composable
fun MiniPlayerCard(
    song: Song,
    isPlaying: Boolean,
    onTogglePlay: () -> Unit,
    onExpand: () -> Unit
) {
    LiquidGlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp)
            .clickable { onExpand() },
        backgroundColor = Color(0xDD18142A)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF221A3A)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.MusicNote, contentDescription = null, tint = NiorPurple)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(song.title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(song.artist, fontSize = 11.sp, color = Color(0xFF9CA3AF), maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }

            IconButton(onClick = onTogglePlay) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = NiorPurple
                )
            }
        }
    }
}

@Composable
fun LiquidGlassBottomNav(
    selected: Screen,
    onSelect: (Screen) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .clip(RoundedCornerShape(32.dp)),
        color = Color(0xDD18142A),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x33D8B4FE)),
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            listOf(
                Screen.HOME to Icons.Default.Home to "Home",
                Screen.MUSIC to Icons.Default.MusicNote to "Music",
                Screen.PLAYLIST to Icons.Default.QueueMusic to "Playlists",
                Screen.SETTINGS to Icons.Default.Settings to "Settings",
                Screen.SEARCH to Icons.Default.Search to "Search"
            ).forEach { (pair, label) ->
                val (screen, icon) = pair
                val isCurrent = selected == screen

                IconButton(
                    onClick = { onSelect(screen) },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isCurrent) NiorPurple.copy(alpha = 0.28f) else Color.Transparent)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = if (isCurrent) Color.White else Color(0xFF9CA3AF)
                    )
                }
            }
        }
    }
}
