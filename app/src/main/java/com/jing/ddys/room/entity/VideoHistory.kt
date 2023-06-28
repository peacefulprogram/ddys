package com.jing.ddys.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("video_history")
data class VideoHistory(
    @PrimaryKey
    val id: String,
    val title: String,
    val pic: String,
    val epId: String? = null
)