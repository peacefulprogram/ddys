package com.jing.ddys.room.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jing.ddys.room.entity.SearchHistory

@Dao
interface SearchHistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveHistory(history: SearchHistory)

    @Query("select * from search_history order by searchTime desc")
    fun queryPaging(): PagingSource<Int, SearchHistory>

    @Delete
    suspend fun deleteHistory(history: SearchHistory)

    @Query("delete from search_history")
    suspend fun deleteAllHistory()
}