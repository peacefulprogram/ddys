package com.jing.ddys.room.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.jing.ddys.room.VideoEpisodeHistory
import com.jing.ddys.room.entity.VideoHistory

@Dao
interface VideoHistoryDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun saveVideo(video: VideoHistory)

    @Query("update video_history set epId = :episodeId where id = :videoId")
    suspend fun updateLatestPlayedEpisode(videoId: String, episodeId: String)

    @Query(
        """
        select e.videoId,
               v.epId,
               v.title,
               v.pic,
               e.name epName,
               e.progress,
               e.duration
        from video_history v
        inner join episode_history e
            on v.epId = e.id
        order by e.timestamp desc
    """
    )
    fun queryAllHistory(): PagingSource<Int, VideoEpisodeHistory>


    @Query("delete from video_history where id = :id")
    suspend fun deleteVideo(id: String)

    @Query("delete from episode_history where videoId = :videoId")
    suspend fun deleteEpisodeHistoryOfVideo(videoId: String)

    @Query("delete from video_history")
    suspend fun deleteAllVideoHistory()


    @Query("delete from episode_history")
    suspend fun deleteAllEpisodeHistory()

    @Transaction
    suspend fun deleteHistoryById(id: String) {
        deleteVideo(id)
        deleteEpisodeHistoryOfVideo(id)
    }

    @Transaction
    suspend fun deleteAllHistory() {
        deleteAllVideoHistory()
        deleteAllEpisodeHistory()
    }


}