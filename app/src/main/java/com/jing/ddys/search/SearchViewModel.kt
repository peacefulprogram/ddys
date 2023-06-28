package com.jing.ddys.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.jing.ddys.room.dao.SearchHistoryDao
import com.jing.ddys.room.entity.SearchHistory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchViewModel(
    private val searchHistoryDao: SearchHistoryDao
) : ViewModel() {

    val historyPager = Pager(
        config = PagingConfig(pageSize = 15, prefetchDistance = 15)
    ) {
        searchHistoryDao.queryPaging()
    }
        .flow


    suspend fun deleteSearchHistory(history: SearchHistory) {
        withContext(Dispatchers.IO) {
            searchHistoryDao.deleteHistory(history)
        }
    }

    suspend fun deleteAllHistory() {
        withContext(Dispatchers.IO) {
            searchHistoryDao.deleteAllHistory()
        }
    }

    fun saveHistory(keyword: String) {
        if (keyword.isBlank()) {
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            searchHistoryDao.saveHistory(
                history = SearchHistory(
                    keyword = keyword.trim(),
                    searchTime = System.currentTimeMillis()
                )
            )
        }
    }
}