package com.jing.ddys.repository

import android.net.Uri

data class VideoUrl(
    val type: VideoUrlType,
    var url: Uri = Uri.EMPTY,
    val m3u8Text: String = "",
    val subtitleUrl: Uri? = null
)

enum class VideoUrlType {
    URL,
    M3U8_TEXT
}
