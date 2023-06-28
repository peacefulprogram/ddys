package com.jing.ddys.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.jing.ddys.repository.BasicPagingSource
import com.jing.ddys.repository.HttpUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    @Volatile
    private var _category = "/"

    val refreshChannel = Channel<String>(onBufferOverflow = BufferOverflow.DROP_OLDEST)

    val pager = Pager(
        config = PagingConfig(
            pageSize = 24, prefetchDistance = 3
        )
    ) {
        BasicPagingSource {
            HttpUtil.queryVideoOfCategory(_category, it)
        }
    }.flow

    fun onCategoryChoose(category: String) {
        if (_category == category) {
            return
        }
        _category = category
        viewModelScope.launch(Dispatchers.Default) {
            refreshChannel.send(category)
        }
    }


}