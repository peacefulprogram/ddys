package com.jing.ddys.detail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jing.ddys.repository.HttpUtil
import com.jing.ddys.repository.Resource
import com.jing.ddys.repository.VideoDetailInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DetailViewModel : ViewModel() {

    private val TAG = DetailViewModel::class.java.simpleName

    private val _detail: MutableStateFlow<Resource<VideoDetailInfo>> =
        MutableStateFlow(Resource.Loading)

    val detailFlow: StateFlow<Resource<VideoDetailInfo>>
        get() = _detail

    fun queryDetail(pageUrl: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _detail.emit(Resource.Loading)
            try {
                _detail.emit(Resource.Success(HttpUtil.queryDetailPage(pageUrl)))
            } catch (ex: Exception) {
                Log.e(TAG, "查询详情页失败,url:$pageUrl", ex)
                _detail.emit(Resource.Error("加载详情失败:${ex.message}", ex))
            }
        }
    }

}