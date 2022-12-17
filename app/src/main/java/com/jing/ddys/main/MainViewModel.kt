package com.jing.ddys.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.jing.ddys.repository.HttpUtil
import com.jing.ddys.repository.VideoCardInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel : ViewModel() {

    private var _category = MutableStateFlow("/")

    val category: String
        get() = _category.value

    val pager = Pager(
        config = PagingConfig(
            pageSize = 24,
            prefetchDistance = 3
        )
    ) {
        VideoCardPagingSource()
    }.flow

    fun onCategoryChoose(category: String) {
        viewModelScope.launch {
            _category.emit(category)
        }
    }

    private inner class VideoCardPagingSource : PagingSource<Int, VideoCardInfo>() {
        override fun getRefreshKey(state: PagingState<Int, VideoCardInfo>): Int? {
            return null
        }

        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, VideoCardInfo> {
            val page = params.key ?: 1
            if (_category.value.isEmpty()) {
                return LoadResult.Page(emptyList(), null, null)
            }
            return try {
                val pageResult = withContext(Dispatchers.IO) {
                    HttpUtil.queryVideoOfCategory(_category.value, page)
                }
                LoadResult.Page(
                    data = pageResult.data ?: emptyList(),
                    prevKey = if (page > 1) page - 1 else null,
                    nextKey = if (pageResult.hasNext) page + 1 else null
                )
            } catch (e: Exception) {
                LoadResult.Error(e)
            }
        }

    }

}