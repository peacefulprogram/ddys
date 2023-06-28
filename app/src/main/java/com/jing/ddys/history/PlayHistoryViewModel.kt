package com.jing.ddys.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.jing.ddys.room.dao.VideoHistoryDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PlayHistoryViewModel(
    private val videoHistoryDao: VideoHistoryDao
) : ViewModel() {


    val pager = Pager(
        config = PagingConfig(
            pageSize = 20
        )
    ) {
        videoHistoryDao.queryAllHistory()
    }
        .flow

    fun deleteHistoryByVideoId(videoId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            videoHistoryDao.deleteHistoryById(videoId)
        }
    }

    fun deleteAllHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            videoHistoryDao.deleteAllHistory()
        }
    }

}