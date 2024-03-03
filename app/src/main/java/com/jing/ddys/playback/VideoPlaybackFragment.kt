package com.jing.ddys.playback

import TrafficSpeedCalculatorBandwidthMeter
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.core.graphics.drawable.toDrawable
import androidx.leanback.app.ProgressBarManager
import androidx.leanback.app.VideoSupportFragment
import androidx.leanback.app.VideoSupportFragmentGlueHost
import androidx.leanback.widget.Action
import androidx.leanback.widget.PlaybackControlsRow.SkipNextAction
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.text.CueGroup
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.okhttp.OkHttpDataSource
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.source.MergingMediaSource
import androidx.media3.exoplayer.source.SingleSampleMediaSource
import androidx.media3.exoplayer.upstream.DefaultBandwidthMeter
import androidx.media3.ui.SubtitleView
import androidx.media3.ui.leanback.LeanbackPlayerAdapter
import com.google.common.net.HttpHeaders
import com.jing.bilibilitv.playback.GlueActionCallback
import com.jing.bilibilitv.playback.PlayListAction
import com.jing.bilibilitv.playback.ReplayAction
import com.jing.ddys.BuildConfig
import com.jing.ddys.R
import com.jing.ddys.databinding.PlayerProgressBarLayoutBinding
import com.jing.ddys.ext.secondsToDuration
import com.jing.ddys.ext.showLongToast
import com.jing.ddys.ext.showShortToast
import com.jing.ddys.repository.HttpUtil
import com.jing.ddys.repository.Resource
import com.jing.ddys.repository.VideoDetailInfo
import com.jing.ddys.setting.NetworkProxySettings
import com.jing.ddys.setting.SettingsViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.androidx.viewmodel.ext.android.getActivityViewModel
import org.koin.core.parameter.parametersOf
import java.net.InetSocketAddress
import java.net.Proxy


@UnstableApi
class VideoPlaybackFragment : VideoSupportFragment() {


    private lateinit var videoDetail: VideoDetailInfo
    private var playEpIndex: Int = 0

    private val TAG = VideoPlaybackFragment::class.java.simpleName

    private lateinit var viewModel: PlaybackViewModel

    private var exoplayer: ExoPlayer? = null

    private var glue: ProgressTransportControlGlue<LeanbackPlayerAdapter>? = null

    private var backPressed = false

    private lateinit var mSubtitleView: SubtitleView

    private lateinit var mProgressBarManager: ProgressBarManager

    private lateinit var progressBarBinding: PlayerProgressBarLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        videoDetail =
            requireActivity().intent.getSerializableExtra(VideoPlaybackActivity.VIDEO_KEY) as VideoDetailInfo
        playEpIndex = requireActivity().intent.getIntExtra(VideoPlaybackActivity.PLAY_INDEX, 0)
        viewModel = getActivityViewModel { parametersOf(videoDetail, playEpIndex) }
        isControlsOverlayAutoHideEnabled = true
    }

    private var _updateSpeedJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = super.onCreateView(inflater, container, savedInstanceState) as ViewGroup
        progressBarBinding =
            PlayerProgressBarLayoutBinding.inflate(inflater)
        val progressBarRoot = progressBarBinding.root
        val progressBarParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            gravity = Gravity.CENTER
        }
        root.addView(progressBarRoot, progressBarParams)
        progressBarManager.setProgressBarView(progressBarRoot)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mSubtitleView = requireActivity().findViewById(R.id.leanback_subtitles)
        mProgressBarManager = ProgressBarManager()
        mProgressBarManager.setRootView(view as ViewGroup?)
        mProgressBarManager.enableProgressBar()
        view.background = Color.BLACK.toDrawable()
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.episodeName.collectLatest {
                    glue?.subtitle = it
                }
            }
        }
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.videoIndex.collectLatest {
                    glue?.changeSkipNextVisibility(it < videoDetail.episodes.size - 1)
                }
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.videoUrl.collectLatest {
                    when (it) {
                        is Resource.Success -> {
                            mProgressBarManager.hide()
                            val history = it.data
                            val (_, url, _, subtitleUrl) = history.url
                            Log.d(TAG, "video url: $url")
                            exoplayer?.apply {
                                val videoMediaSource = mediaSourceFactory.createMediaSource(
                                    MediaItem.Builder().setUri(url).build()
                                )
                                if (subtitleUrl != null) {

                                    val subtitleConfiguration =
                                        MediaItem.SubtitleConfiguration.Builder(subtitleUrl)
                                            .setMimeType(MimeTypes.TEXT_VTT)
                                            .setSelectionFlags(C.SELECTION_FLAG_FORCED)
                                            .setLanguage("zh").build()
                                    val subtitleMediaSource =
                                        SingleSampleMediaSource.Factory(dataSourceFactory)
                                            .createMediaSource(subtitleConfiguration, C.TIME_UNSET)

                                    exoplayer!!.setMediaSource(
                                        MergingMediaSource(
                                            videoMediaSource,
                                            subtitleMediaSource
                                        )
                                    )
                                } else {
                                    exoplayer!!.setMediaSource(videoMediaSource)
                                }
                                prepare()
                                if (viewModel.resumePosition > 0) {
                                    seekTo(viewModel.resumePosition)
                                    viewModel.resumePosition = 0
                                } else if (history.lastPlayPosition > 0) {
                                    // 距离结束小于10秒,当作播放结束
                                    if (history.videoDuration > 0 && history.videoDuration - history.lastPlayPosition < 10_000) {
                                        requireContext().showShortToast("上次已播放完,将从头开始播放")
                                    } else {
                                        val seekTo = history.lastPlayPosition
                                        exoplayer?.seekTo(seekTo)
                                        requireContext().showShortToast("已定位到上次播放位置:${(seekTo / 1000).secondsToDuration()}")
                                    }
                                }
                                play()
                            }
                        }

                        is Resource.Error -> {
                            mProgressBarManager.hide()
                            requireContext().showLongToast(it.message)
                        }

                        Resource.Loading -> mProgressBarManager.show()
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (Build.VERSION.SDK_INT <= 23) {
            buildPlayer()
        }
    }

    private fun listenProgressBarVisibility() {
        progressBarBinding.root.setVisibilityListener { visibility ->
            if (visibility != View.VISIBLE) {
                progressBarBinding.speedIndicator.text = ""
                _updateSpeedJob?.cancel()
                _updateSpeedJob = null
            } else {
                if (_updateSpeedJob == null) {
                    _updateSpeedJob = viewLifecycleOwner.lifecycleScope.launch {
                        while (isActive) {
                            progressBarBinding.speedIndicator.text =
                                "${trafficSpeedCalculator.getNetworkSpeed()} kb/s"
                            delay(800L)
                        }
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (Build.VERSION.SDK_INT > 23) {
            buildPlayer()
            listenProgressBarVisibility()
        }
    }

    override fun onPause() {
        super.onPause()
        if (Build.VERSION.SDK_INT <= 23) {
            viewModel.resumePosition = exoplayer!!.currentPosition
            destroyPlayer()
            _updateSpeedJob?.cancel()
            _updateSpeedJob = null
            progressBarBinding.root.removeVisibilityListener()
        }
    }

    override fun onStop() {
        super.onStop()
        if (Build.VERSION.SDK_INT > 23) {
            viewModel.resumePosition = exoplayer!!.currentPosition
            destroyPlayer()
            _updateSpeedJob?.cancel()
            _updateSpeedJob = null
            progressBarBinding.root.removeVisibilityListener()
        }
    }

    private val okHttpClient = OkHttpClient.Builder()
        .sslSocketFactory(HttpUtil.buildSSLSocketFactory(), HttpUtil.trustManager)
        .hostnameVerifier { _, _ -> true }
        .apply {
            if (BuildConfig.DEBUG) {
                addNetworkInterceptor(HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.HEADERS
                })
            }
            val networkProxySettings =
                NetworkProxySettings.loadFromSharedPreference(SettingsViewModel.getSettingSharedPreference())
            if (networkProxySettings.proxyEnabled && networkProxySettings.proxyHost.isNotEmpty()) {
                proxy(
                    Proxy(
                        Proxy.Type.HTTP,
                        InetSocketAddress(
                            networkProxySettings.proxyHost,
                            networkProxySettings.proxyPort
                        )
                    )
                )
            }
        }
        .build()

    private val dataSourceFactory by lazy {
        DefaultDataSource.Factory(
            requireContext(),
            OkHttpDataSource.Factory { req ->
                val newReq = req.newBuilder()
                    .header(HttpHeaders.USER_AGENT, HttpUtil.USER_AGENT)
                    .header(HttpHeaders.REFERER, HttpUtil.BASE_URL + '/')
                    .build()
                okHttpClient.newCall(newReq)
            }
        )
    }

    private val mediaSourceFactory by lazy {
        DefaultMediaSourceFactory(dataSourceFactory)
    }

    private val trafficSpeedCalculator by lazy {
        TrafficSpeedCalculatorBandwidthMeter(
            DefaultBandwidthMeter.getSingletonInstance(
                requireContext()
            )
        )
    }

    private val playerListener = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            if (playbackState == ExoPlayer.STATE_ENDED) {
                viewModel.playNextEpisodeIfExists()
            }
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            if (isPlaying) {
                viewModel.startSaveHistory()
            } else {
                viewModel.saveHistory()
                viewModel.stopSaveHistory()
            }
        }

        override fun onPlayerError(error: PlaybackException) {
            this@VideoPlaybackFragment.requireContext()
                .showLongToast("播放失败:${error.cause?.message ?: error.message}")
        }

        override fun onCues(cueGroup: CueGroup) {
            mSubtitleView.setCues(cueGroup.cues)
        }
    }


    private fun buildPlayer() {
        exoplayer = ExoPlayer.Builder(requireContext())
            .setBandwidthMeter(trafficSpeedCalculator)
            .setLoadControl(
                DefaultLoadControl.Builder().setBufferDurationsMs(
                    20_000,
                    50_000,
                    1000,
                    1000
                ).build()
            )
            .build().apply {
                trackSelectionParameters = trackSelectionParameters.buildUpon()
                    .setPreferredTextLanguage("zh")
                    .build()
                prepareGlue(this)
                playWhenReady = true
                addListener(playerListener)
            }

    }


    private fun destroyPlayer() {
        exoplayer?.let {
            it.removeListener(playerListener)
            // Pause the player to notify listeners before it is released.
            it.pause()
            it.release()
            exoplayer = null
        }
    }

    private fun prepareGlue(localExoplayer: ExoPlayer) {
        glue = ProgressTransportControlGlue(context = requireContext(),
            playerAdapter = LeanbackPlayerAdapter(
                requireContext(), localExoplayer, 200
            ),
            onCreatePrimaryAction = {
                it.add(PlayListAction(requireContext()))
                it.add(ReplayAction(requireContext()))
            },
            updateProgress = {
                viewModel.currentPlayPosition = localExoplayer.currentPosition
                viewModel.videoDuration = localExoplayer.duration
            }).apply {
            host = VideoSupportFragmentGlueHost(this@VideoPlaybackFragment)
            title = videoDetail.title
            subtitle = viewModel.episodeName.value
            // Enable seek manually since PlaybackTransportControlGlue.getSeekProvider() is null,
            // so that PlayerAdapter.seekTo(long) will be called during user seeking.
            isSeekEnabled = true
            isControlsOverlayAutoHideEnabled = true
            addActionCallback(replayActionCallback)
            addActionCallback(changePlayVideoActionCallback)
            addActionCallback(object : GlueActionCallback {
                override fun support(action: Action): Boolean {
                    return action is SkipNextAction
                }

                override fun onAction(action: Action) {
                    val current = viewModel.videoIndex.value
                    if (current in 0 until videoDetail.episodes.size) {
                        exoplayer?.pause()
                        viewModel.changePlayVideoIndex(current + 1)
                    }
                }
            })
            changeSkipNextVisibility(viewModel.videoIndex.value < videoDetail.episodes.size - 1)
            setKeyEventInterceptor { onKeyEvent(it) }
        }
    }


    private val replayActionCallback = object : GlueActionCallback {
        override fun support(action: Action): Boolean = action is ReplayAction

        override fun onAction(action: Action) {
            exoplayer?.seekTo(0L)
            exoplayer?.play()
            hideControlsOverlay(true)
        }

    }

    private val changePlayVideoActionCallback = object : GlueActionCallback {
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
        ChooseEpisodeDialog(dataList = videoDetail.episodes,
            defaultSelectIndex = viewModel.videoIndex.value,
            getText = { _, item -> item.name }) { index, _ ->
            exoplayer?.pause()
            viewModel.changePlayVideoIndex(index)
        }.apply {
            showNow(fragmentManager, "")
        }
    }

    override fun onVideoSizeChanged(width: Int, height: Int) {
        // (see https://github.com/androidx/media/issues/617).
        if (width == 0 || height == 0) {
            return
        }
        super.onVideoSizeChanged(width, height)
    }


}