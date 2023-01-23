package com.jing.ddys.playback

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import androidx.leanback.app.VideoSupportFragment
import androidx.leanback.app.VideoSupportFragmentGlueHost
import androidx.leanback.widget.Action
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.MediaItem.SubtitleConfiguration
import com.google.android.exoplayer2.ext.leanback.LeanbackPlayerAdapter
import com.google.android.exoplayer2.util.MimeTypes
import com.jing.bilibilitv.playback.GlueActionCallback
import com.jing.bilibilitv.playback.PlayListAction
import com.jing.bilibilitv.playback.ReplayAction
import com.jing.ddys.ext.dpToPx
import com.jing.ddys.ext.showLongToast
import com.jing.ddys.repository.Resource
import com.jing.ddys.repository.VideoDetailInfo
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class VideoPlaybackFragment(
    private val videoDetail: VideoDetailInfo,
    private val playEpIndex: Int
) : VideoSupportFragment() {

    private lateinit var viewModel: PlaybackViewModel

    private var exoplayer: ExoPlayer? = null

    private var glue: ProgressTransportControlGlue<LeanbackPlayerAdapter>? = null

    private var resumeFrom = -1L

    private var backPressed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[PlaybackViewModel::class.java].apply {
            init(videoDetail, playEpIndex)
        }
        isControlsOverlayAutoHideEnabled = true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.videoIndex.collectLatest {
                    glue?.subtitle = videoDetail.episodes[it].name
                }
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.videoUrl.collectLatest {
                    when (it) {
                        is Resource.Success -> {
                            val (_, url, _, subtitleUrl) = it.data
                            exoplayer?.apply {
                                val mediaItemBuilder = MediaItem.Builder()
                                    .setUri(url)
                                if (subtitleUrl != null) {
                                    SubtitleConfiguration.Builder(subtitleUrl)
                                        .setMimeType(MimeTypes.TEXT_VTT)
                                        .setSelectionFlags(C.SELECTION_FLAG_DEFAULT)
                                        .setLanguage("zh")
                                        .build()
                                        .run {
                                            mediaItemBuilder.setSubtitleConfigurations(
                                                listOf(
                                                    this
                                                )
                                            )
                                        }
                                }
                                exoplayer!!.setMediaItem(mediaItemBuilder.build())
                                prepare()
                                if (resumeFrom > 0) {
                                    seekTo(resumeFrom)
                                    resumeFrom = -1
                                }
                                play()
                            }
                        }
                        is Resource.Error -> requireContext().showLongToast(it.message)
                        else -> {}
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        buildPlayer()
    }

    override fun onStop() {
        super.onStop()
        resumeFrom = exoplayer!!.currentPosition
        destroyPlayer()
    }

    private fun buildPlayer() {
        exoplayer = ExoPlayer.Builder(requireContext())
            .build().apply {
                prepareGlue(this)
                playWhenReady = true
            }

    }


    private fun destroyPlayer() {
        exoplayer?.let {
            // Pause the player to notify listeners before it is released.
            it.pause()
            it.release()
            exoplayer = null
        }
    }

    private fun prepareGlue(localExoplayer: ExoPlayer) {
        glue = ProgressTransportControlGlue(
            context = requireContext(),
            playerAdapter = LeanbackPlayerAdapter(
                requireContext(),
                localExoplayer,
                200
            ),
            onCreatePrimaryAction = {
                it.add(PlayListAction(requireContext()))
                it.add(ReplayAction(requireContext()))
            },
            updateProgress = {}
        ).apply {
            host = VideoSupportFragmentGlueHost(this@VideoPlaybackFragment)
            title = videoDetail.title
            // Enable seek manually since PlaybackTransportControlGlue.getSeekProvider() is null,
            // so that PlayerAdapter.seekTo(long) will be called during user seeking.
            isSeekEnabled = true
            isControlsOverlayAutoHideEnabled = true
            addActionCallback(replayActionCallback)
            addActionCallback(changePlayVideoActionCallback)
            setKeyEventInterceptor { onKeyEvent(it) }
        }
    }


    private val replayActionCallback =
        object : GlueActionCallback {
            override fun support(action: Action): Boolean = action is ReplayAction

            override fun onAction(action: Action) {
                exoplayer?.seekTo(0L)
                exoplayer?.play()
                hideControlsOverlay(true)
            }

        }

    private val changePlayVideoActionCallback =
        object : GlueActionCallback {
            override fun support(action: Action): Boolean = action is PlayListAction

            override fun onAction(action: Action) {
                openPlayListDialogAndChoose()
            }

        }


    fun onKeyEvent(keyEvent: KeyEvent): Boolean {
        if (keyEvent.keyCode == KeyEvent.KEYCODE_BACK) {
            if (isControlsOverlayVisible) {
                return false
            }
            if (exoplayer?.isPlaying != true) {
                backPressed = false
                return false
            }
            if (backPressed) {
                return false
            }
            backPressed = true
            Toast.makeText(requireContext(), "再按一次退出播放", Toast.LENGTH_SHORT).show()
            lifecycleScope.launch {
                delay(2000)
                backPressed = false
            }
            return true
        }
        if (keyEvent.keyCode == KeyEvent.KEYCODE_DPAD_CENTER && !isControlsOverlayVisible) {
            if (exoplayer?.isPlaying == true) {
                exoplayer?.pause()
            } else {
                exoplayer?.play()
            }
            return true
        }

        if (keyEvent.keyCode == KeyEvent.KEYCODE_MENU) {
            openPlayListDialogAndChoose()
            return true
        }
        return false
    }

    private fun openPlayListDialogAndChoose() {
        val fragmentManager = requireActivity().supportFragmentManager
        ChooseEpisodeDialog(
            dataList = videoDetail.episodes,
            defaultSelectIndex = viewModel.videoIndex.value,
            viewWidth = 60.dpToPx.toInt(),
            getText = { _, item -> item.name }
        ) { index, _ ->
            viewModel.changePlayVideoIndex(index)
        }.apply {
            showNow(fragmentManager, "")
        }
    }


}