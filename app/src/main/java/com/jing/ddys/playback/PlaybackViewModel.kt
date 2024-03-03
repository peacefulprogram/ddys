package com.jing.ddys.playback

import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jing.ddys.DdysApplication
import com.jing.ddys.repository.HttpUtil
import com.jing.ddys.repository.Resource
import com.jing.ddys.repository.VideoDetailInfo
import com.jing.ddys.repository.VideoUrl
import com.jing.ddys.repository.VideoUrlType
import com.jing.ddys.room.dao.EpisodeHistoryDao
import com.jing.ddys.room.dao.VideoHistoryDao
import com.jing.ddys.room.entity.EpisodeHistory
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class PlaybackViewModel(
    private val videoDetail: VideoDetailInfo,
    initEpisodeIndex: Int,
    private val episodeHistoryDao: EpisodeHistoryDao,
    private val videoHistoryDao: VideoHistoryDao
) : ViewModel() {
    private val TAG = PlaybackViewModel::class.java.simpleName
    private val _videoIndex: MutableStateFlow<Int> = MutableStateFlow(initEpisodeIndex)
    private val videoUrlCache = mutableMapOf<Int, VideoUrl>()
    private val _videoUrl: MutableStateFlow<Resource<VideoUrlWithHistory>> =
        MutableStateFlow(Resource.Loading)

    private val _episodeName =
        MutableStateFlow(videoDetail.episodes.getOrNull(initEpisodeIndex)?.name ?: "")

    val episodeName: StateFlow<String>
        get() = _episodeName

    private var requestVideoUrlJob: Job? = null

    var resumePosition = 0L

    var currentPlayPosition: Long = 0L

    var videoDuration: Long = 0L

    private var _saveHistoryJob: Job? = null

    val videoIndex: StateFlow<Int>
        get() = _videoIndex

    val videoUrl: StateFlow<Resource<VideoUrlWithHistory>>
        get() = _videoUrl

    init {
        viewModelScope.launch {
            _videoIndex.collectLatest {
                _episodeName.emit(videoDetail.episodes.getOrNull(it)?.name ?: "")
                queryVideoUrl(it)
            }
        }
    }


    private fun queryVideoUrl(videoIndex: Int) {
        requestVideoUrlJob?.cancel()
        requestVideoUrlJob = viewModelScope.launch(Dispatchers.IO) {
            val ep = videoDetail.episodes[videoIndex]
            _videoUrl.emit(Resource.Loading)
            try {
                val subtitleJob = async {
                    try {
                        HttpUtil.downloadSubtitles(videoDetail.episodes[videoIndex].subTitleUrl)
                    } catch (e: Exception) {
                        if (e is CancellationException) {
                            throw e
                        }
                        Log.e(TAG, "下载字幕出错: ${e.message}", e)
                        ""
                    }
                }
                var url = if (ep.src1.isNotEmpty()) {
                    HttpUtil.queryVideoUrl(ep.src1, videoDetail.detailPageUrl)
                } else {
                    VideoUrl(
                        type = VideoUrlType.URL,
                        url = Uri.parse(HttpUtil.VIDEO_BASE_URL + ep.src0)
                    )
                }
                if (url.type == VideoUrlType.M3U8_TEXT) {
                    val cacheFileName =
                        Uri.parse(videoDetail.detailPageUrl).path!!.trimStart('/').replace(
                            '/',
                            '-'
                        ) + videoDetail.episodes[videoIndex].name + '-' + videoIndex + ".m3u8"
                    val cacheFile = File(DdysApplication.context.cacheDir, cacheFileName)
                    cacheFile.writeText(url.m3u8Text)
                    url = url.copy(url = cacheFile.toUri())
                }
                val subtitle = subtitleJob.await()
                if (subtitle.isNotEmpty()) {
                    val cacheFileName =
                        Uri.parse(videoDetail.episodes[videoIndex].subTitleUrl).path!!.trimStart('/')
                            .replace(
                                '/',
                                '-'
                            ) + ".vtt"
                    val cacheFile = File(DdysApplication.context.cacheDir, cacheFileName)
                    cacheFile.writeText(subtitle)
                    url = url.copy(subtitleUrl = cacheFile.toUri())
                }
                val history = episodeHistoryDao.queryHistoryByEpisodeId(ep.id)
                _videoUrl.emit(
                    Resource.Success(
                        VideoUrlWithHistory(
                            url = url,
                            lastPlayPosition = history?.progress ?: 0L,
                            videoDuration = history?.duration ?: 0L
                        )
                    )
                )
                videoHistoryDao.updateLatestPlayedEpisode(videoDetail.id, ep.id)
                withContext(Dispatchers.Main) {
                    videoUrlCache[videoIndex] = url
                }
            } catch (e: Exception) {
                Log.e(TAG, "查询视频链接失败:${e.message}", e)
                _videoUrl.emit(Resource.Error("查询视频链接失败:${e.message}", e))
            }

        }
    }

    fun changePlayVideoIndex(index: Int) {
        if (index == _videoIndex.value) {
            return
        }
        viewModelScope.launch {
            _videoIndex.emit(index)
        }
    }

    fun playNextEpisodeIfExists() {
        val curr = _videoIndex.value
        if (curr < videoDetail.episodes.size - 1) {
            changePlayVideoIndex(curr + 1)
        }
    }


    fun startSaveHistory() {
        stopSaveHistory()
        val ep = videoDetail.episodes[_videoIndex.value]
        _saveHistoryJob = viewModelScope.launch(Dispatchers.IO) {
            while (isActive) {
                episodeHistoryDao.save(
                    EpisodeHistory(
                        id = ep.id,
                        videoId = videoDetail.id,
                        name = ep.name,
                        progress = currentPlayPosition,
                        duration = videoDuration,
                        timestamp = System.currentTimeMillis()
                    )
                )
                delay(5000L)
            }
        }
    }

    fun saveHistory() {
        val ep = videoDetail.episodes[_videoIndex.value]
        viewModelScope.launch {
            episodeHistoryDao.save(
                EpisodeHistory(
                    id = ep.id,
                    videoId = videoDetail.id,
                    name = ep.name,
                    progress = currentPlayPosition,
                    duration = videoDuration,
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }

    fun stopSaveHistory() {
        _saveHistoryJob?.cancel()
        _saveHistoryJob = null
    }
}


data class VideoUrlWithHistory(
    val url: VideoUrl,
    val lastPlayPosition: Long,
    val videoDuration: Long
)