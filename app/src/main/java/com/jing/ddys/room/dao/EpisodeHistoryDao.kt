package com.jing.ddys.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jing.ddys.room.entity.EpisodeHistory

@Dao
interface EpisodeHistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(history: EpisodeHistory)

    @Query("select * from episode_history where id = :id")
    suspend fun queryHistoryByEpisodeId(id: String): EpisodeHistory?

    @Query(
        """
        select * 
        from episode_history 
        where videoId = :videoId
        order by timestamp desc
        limit 1
    """
    )
    fun queryLatestProgress(videoId: String): EpisodeHistory?
}