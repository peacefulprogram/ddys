package com.jing.ddys.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.jing.ddys.room.dao.EpisodeHistoryDao
import com.jing.ddys.room.dao.SearchHistoryDao
import com.jing.ddys.room.dao.VideoHistoryDao
import com.jing.ddys.room.entity.EpisodeHistory
import com.jing.ddys.room.entity.SearchHistory
import com.jing.ddys.room.entity.VideoHistory

@Database(
    entities = [
        SearchHistory::class,
        VideoHistory::class,
        EpisodeHistory::class
    ], version = 1
)
abstract class Dy555Database : RoomDatabase() {

    abstract fun searchHistoryDao(): SearchHistoryDao

    abstract fun videoHistoryDao(): VideoHistoryDao

    abstract fun episodeHistoryDao(): EpisodeHistoryDao
}