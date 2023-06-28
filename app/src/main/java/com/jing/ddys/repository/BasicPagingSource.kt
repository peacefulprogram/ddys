package com.jing.ddys.repository

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BasicPagingSource<T : Any>(
    private val dataProvider: (page: Int) -> BasePageResult<T>
) : PagingSource<Int, T>() {

    private val TAG = BasicPagingSource::class.java.simpleName

    override fun getRefreshKey(state: PagingState<Int, T>): Int? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        val page = params.key ?: 1
        return try {
            val result = withContext(Dispatchers.IO) {
                dataProvider(page)
            }
            LoadResult.Page(
                data = result.data,
                prevKey = if (page > 1) page - 1 else null,
                nextKey = if (result.hasNext) page + 1 else null
            )
        } catch (ex: Exception) {
            if (ex is CancellationException) {
                throw ex
            }
            Log.e(TAG, "load: ${ex.message}", ex)
            LoadResult.Error(ex)
        }
    }
}