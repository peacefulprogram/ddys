package com.jing.ddys.repository


data class VideoDetailInfo(
    val title: String,
    val coverUrl: String,
    val seasons: List<VideoSeason>,
    val episodes: List<VideoEpisode>,
    val relatedVideo: List<VideoCardInfo>,
    val rating: String,
    val description: String,
    val detailPageUrl: String
) : java.io.Serializable

data class VideoSeason(
    val seasonName: String,
    val seasonUrl: String?,
    val currentSeason: Boolean
) : java.io.Serializable

data class VideoEpisode(
    val id: String,
    val name: String,
    val subTitleUrl: String
) : java.io.Serializable