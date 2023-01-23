package com.jing.ddys.playback

import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jing.ddys.context.GlobalContext
import com.jing.ddys.repository.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import java.io.File

class PlaybackViewModel : ViewModel() {
    private val TAG = PlaybackViewModel::class.java.simpleName
    private lateinit var videoDetail: VideoDetailInfo
    private lateinit var _videoIndex: MutableStateFlow<Int>
    private val videoUrlCache = mutableMapOf<Int, VideoUrl>()
    private val _videoUrl: MutableStateFlow<Resource<VideoUrl>> = MutableStateFlow(Resource.Loading)

    val videoIndex: StateFlow<Int>
        get() = _videoIndex

    val videoUrl: StateFlow<Resource<VideoUrl>>
        get() = _videoUrl

    fun init(video: VideoDetailInfo, playVideoIndex: Int) {
        videoDetail = video
        _videoIndex = MutableStateFlow(playVideoIndex)
        observeVideoIndex()
    }

    private fun observeVideoIndex() {
        viewModelScope.launch {
            _videoIndex.collectLatest {
/*
                val cache = videoUrlCache[it]
                if (cache != null) {
                    _videoUrl.emit(Resource.Success(cache))
                } else {
                    queryVideoUrl(it)
                }
*/
                queryVideoUrl(it)
            }
        }
    }

    private fun queryVideoUrl(videoIndex: Int) = viewModelScope.launch(Dispatchers.IO) {
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
            var url = HttpUtil.queryVideoUrl(ep.id, videoDetail.detailPageUrl)
            if (url.type == VideoUrlType.M3U8_TEXT) {
                val cacheFileName =
                    Uri.parse(videoDetail.detailPageUrl).path!!.trimStart('/').replace(
                        '/',
                        '-'
                    ) + videoDetail.episodes[videoIndex].name + '-' + videoIndex + ".m3u8"
                val cacheFile = File(GlobalContext.context.cacheDir, cacheFileName)
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
                val cacheFile = File(GlobalContext.context.cacheDir, cacheFileName)
                cacheFile.writeText(subtitle)
                url = url.copy(subtitleUrl = cacheFile.toUri())
            }
            _videoUrl.emit(Resource.Success(url))
            withContext(Dispatchers.Main) {
                videoUrlCache[videoIndex] = url
            }
        } catch (e: Exception) {
            Log.e(TAG, "查询视频链接失败:${e.message}", e)
            _videoUrl.emit(Resource.Error("查询视频链接失败:${e.message}", e))
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
}