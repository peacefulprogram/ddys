package com.jing.ddys.detail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jing.ddys.repository.HttpUtil
import com.jing.ddys.repository.Resource
import com.jing.ddys.repository.VideoDetailInfo
import com.jing.ddys.room.dao.EpisodeHistoryDao
import com.jing.ddys.room.dao.VideoHistoryDao
import com.jing.ddys.room.entity.EpisodeHistory
import com.jing.ddys.room.entity.VideoHistory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.net.URL

class DetailViewModel(
    private val videoUrl: String,
    private val episodeHistoryDao: EpisodeHistoryDao,
    private val videoHistoryDao: VideoHistoryDao
) : ViewModel() {

    private val videoId = URL(videoUrl).path

    private val TAG = DetailViewModel::class.java.simpleName

    private val _detail: MutableStateFlow<Resource<VideoDetailInfo>> =
        MutableStateFlow(Resource.Loading)

    val detailFlow: StateFlow<Resource<VideoDetailInfo>>
        get() = _detail

    private val _latestProgress: MutableStateFlow<Resource<EpisodeHistory>> =
        MutableStateFlow(Resource.Loading)

    val latestProgress: StateFlow<Resource<EpisodeHistory>>
        get() = _latestProgress

    init {
        queryDetail()
    }

    fun fetchHistory() {
        viewModelScope.launch(Dispatchers.Default) {
            episodeHistoryDao.queryLatestProgress(videoId)?.let {
                _latestProgress.emit(Resource.Success(it))
            }
        }
    }

    fun queryDetail() {
        viewModelScope.launch(Dispatchers.IO) {
            _detail.emit(Resource.Loading)
            try {
                _detail.emit(Resource.Success(HttpUtil.queryDetailPage(videoUrl)))
            } catch (ex: Exception) {
                Log.e(TAG, "查询详情页失败,url:$videoUrl", ex)
                _detail.emit(Resource.Error("加载详情失败:${ex.message}", ex))
            }
        }
    }

    fun saveHistory(videoHistory: VideoHistory) {
        viewModelScope.launch(Dispatchers.IO) {
            videoHistoryDao.saveVideo(videoHistory)
        }
    }

}