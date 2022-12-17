package com.jing.ddys.playback

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.jing.ddys.R
import com.jing.ddys.repository.VideoDetailInfo

class VideoPlaybackActivity : FragmentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playback)
        val videoDetail = intent.getSerializableExtra(VIDEO_KEY) as VideoDetailInfo
        val playEpisodeIndex = intent.getIntExtra(PLAY_INDEX, 0)
        supportFragmentManager.beginTransaction()
            .replace(R.id.playback_fragment, VideoPlaybackFragment(videoDetail, playEpisodeIndex))
            .commit()
    }

    companion object {
        private const val VIDEO_KEY = "video"
        private const val PLAY_INDEX = "idx"

        fun navigateTo(context: Context, videoDetailInfo: VideoDetailInfo, playEpisodeIndex: Int) {
            Intent(context, VideoPlaybackActivity::class.java).apply {
                putExtra(
                    VIDEO_KEY, videoDetailInfo.copy(
                        coverUrl = "",
                        seasons = emptyList(),
                        relatedVideo = emptyList(),
                        rating = "",
                        description = ""
                    )
                )
                putExtra(PLAY_INDEX, playEpisodeIndex)
                context.startActivity(this)
            }
        }
    }
}