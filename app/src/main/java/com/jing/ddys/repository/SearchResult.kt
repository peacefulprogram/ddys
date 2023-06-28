package com.jing.ddys.repository

data class SearchResult(
    val url: String,
    val title: String,
    val desc: String = "",
    val updateTime: String
)