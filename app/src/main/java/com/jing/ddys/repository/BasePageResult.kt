package com.jing.ddys.repository

data class BasePageResult<T>(
    val data: List<T>?,
    val page: Int,
    val hasNext: Boolean
)
