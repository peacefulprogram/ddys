package com.jing.ddys.playback

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jing.ddys.repository.HttpUtil
import com.jing.ddys.repository.Resource
import com.jing.ddys.repository.VideoDetailInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PlaybackViewModel : ViewModel() {
    private val TAG = PlaybackViewModel::class.java.simpleName
    private lateinit var videoDetail: VideoDetailInfo
    private lateinit var _videoIndex: MutableStateFlow<Int>
    private val videoUrlCache = mutableMapOf<Int, String>()
    private val _videoUrl: MutableStateFlow<Resource<String>> = MutableStateFlow(Resource.Loading)

    val videoIndex: StateFlow<Int>
        get() = _videoIndex

    val videoUrl: StateFlow<Resource<String>>
        get() = _videoUrl

    fun init(video: VideoDetailInfo, playVideoIndex: Int) {
        videoDetail = video
        _videoIndex = MutableStateFlow(playVideoIndex)
        observeVideoIndex()
    }

    private fun observeVideoIndex() {
        viewModelScope.launch {
            _videoIndex.collectLatest {
                val cache = videoUrlCache[it]
                if (cache != null) {
                    _videoUrl.emit(Resource.Success(cache))
                } else {
                    queryVideoUrl(it)
                }
            }
        }
    }

    private fun queryVideoUrl(videoIndex: Int) = viewModelScope.launch(Dispatchers.IO) {
        val ep = videoDetail.episodes[videoIndex]
        _videoUrl.emit(Resource.Loading)
        try {
            val url = HttpUtil.queryVideoUrl(ep.id, videoDetail.detailPageUrl)
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