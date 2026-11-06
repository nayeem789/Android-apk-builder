package com.nior.music.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "songs")
data class Song(
    @PrimaryKey val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val durationMs: Long,
    val dataPath: String,
    val contentUri: String,
    val albumId: Long,
    val dateAdded: Long,
    val playCount: Int = 0,
    val lastPlayed: Long = 0L,
    val isFavorite: Boolean = false
)

@Entity(tableName = "playlists")
data class Playlist(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val createdAt: Long = System.currentTimeMillis(),
    val songIdsJson: String = "[]" // JSON string of song IDs
)
