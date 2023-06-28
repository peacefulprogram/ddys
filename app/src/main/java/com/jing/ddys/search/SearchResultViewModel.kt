package com.jing.ddys.search

import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.jing.ddys.repository.BasicPagingSource
import com.jing.ddys.repository.HttpUtil

class SearchResultViewModel(private val keyword: String) : ViewModel() {

    val pager = Pager(
        config = PagingConfig(
            pageSize = 100,
            prefetchDistance = 40,
        )
    ) {
        BasicPagingSource {
            HttpUtil.searchVideo(it, keyword)
        }
    }
        .flow

}