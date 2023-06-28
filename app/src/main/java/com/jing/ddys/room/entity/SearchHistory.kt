package com.jing.ddys.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("search_history")
data class SearchHistory(
    @PrimaryKey
    val keyword: String,
    val searchTime: Long
)