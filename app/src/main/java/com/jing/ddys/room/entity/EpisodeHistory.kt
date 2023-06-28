package com.jing.ddys.room.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    "episode_history", indices = [
        Index("videoId")
    ]
)
data class EpisodeHistory(
    @PrimaryKey
    val id: String,
    val videoId: String,
    val name: String,
    val progress: Long,
    val duration: Long,
    val timestamp: Long
)