package com.jing.ddys.compose.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.tv.foundation.ExperimentalTvFoundationApi
import androidx.tv.foundation.lazy.list.TvLazyColumn
import androidx.tv.foundation.lazy.list.TvLazyRow
import androidx.tv.foundation.lazy.list.items
import androidx.tv.material3.Border
import androidx.tv.material3.CardScale
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.ClickableSurfaceScale
import androidx.tv.material3.CompactCard
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import coil.compose.AsyncImage
import com.jing.ddys.R
import com.jing.ddys.compose.common.ErrorTip
import com.jing.ddys.compose.common.FocusGroup
import com.jing.ddys.compose.common.Loading
import com.jing.ddys.compose.common.VideoCard
import com.jing.ddys.detail.DetailActivity
import com.jing.ddys.detail.DetailViewModel
import com.jing.ddys.ext.secondsToDuration
import com.jing.ddys.playback.VideoPlaybackActivity
import com.jing.ddys.repository.Resource
import com.jing.ddys.repository.VideoCardInfo
import com.jing.ddys.repository.VideoDetailInfo
import com.jing.ddys.repository.VideoEpisode
import com.jing.ddys.repository.VideoSeason
import com.jing.ddys.room.entity.VideoHistory
import kotlinx.coroutines.launch

@Composable
fun DetailScreen(viewModel: DetailViewModel) {
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                viewModel.fetchHistory()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    val videoDetailResource = viewModel.detailFlow.collectAsState().value
    if (videoDetailResource is Resource.Loading) {
        Loading()
        return
    }
    if (videoDetailResource is Resource.Error) {
        ErrorTip(message = videoDetailResource.message) {
            viewModel.queryDetail()
        }
        return
    }
    val videoDetail = (videoDetailResource as Resource.Success<VideoDetailInfo>).data
    val context = LocalContext.current
    TvLazyColumn(Modifier.fillMaxSize(), verticalArrangement = spacedBy(10.dp)) {
        item { VideoInfoRow(viewModel = viewModel, videoDetail = videoDetail) }
        if (videoDetail.seasons.isNotEmpty()) {
            item {
                VideoSeasonRow(videoDetail.seasons) { season ->
                    season.seasonUrl?.let {
                        DetailActivity.navigateTo(context, it)
                    }
                }
            }
        }
        if (videoDetail.episodes.isNotEmpty()) {
            item {
                VideoEpisodeRow(videoDetail.episodes) { _, episodeIndex ->
                    viewModel.saveHistory(
                        VideoHistory(
                            id = videoDetail.id,
                            title = videoDetail.title,
                            pic = videoDetail.coverUrl,
                        )
                    )

                    VideoPlaybackActivity.navigateTo(context, videoDetail, episodeIndex)
                }
            }
        }
        if (videoDetail.relatedVideo.isNotEmpty()) {
            item {
                RelatedVideoRow(videoDetail.relatedVideo) {
                    DetailActivity.navigateTo(context, it.url)
                }
            }
        }
    }


}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun VideoInfoRow(
    viewModel: DetailViewModel,
    videoDetail: VideoDetailInfo,
    imageWidth: Dp = dimensionResource(id = R.dimen.video_preview_card_width),
    imageHeight: Dp = dimensionResource(id = R.dimen.video_preview_card_height),
) {
    val imageFocusRequester = remember {
        FocusRequester()
    }
    var showDescriptionDialog by remember {
        mutableStateOf(false)
    }
    Row(
        Modifier
            .fillMaxWidth()
            .height(imageHeight)
    ) {
        CompactCard(
            onClick = {},
            image = {
                AsyncImage(
                    model = videoDetail.coverUrl,
                    contentDescription = videoDetail.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            },
            title = {},
            scale = CardScale.None,
            modifier = Modifier
                .size(imageWidth, imageHeight)
                .focusRequester(imageFocusRequester)
        )

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f), verticalArrangement = spacedBy(6.dp)) {
            TextLabel(
                text = videoDetail.title,
                maxLines = 2,
                textStyle = MaterialTheme.typography.titleLarge
            )
            val latestProgress = viewModel.latestProgress.collectAsState().value
            TvLazyColumn(
                content = {
                    if (latestProgress is Resource.Success) {
                        val his = latestProgress.data
                        item {
                            Text(
                                text = "上次播放到${his.name} ${(his.progress / 1000).secondsToDuration()}/${(his.duration / 1000).secondsToDuration()}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                    items(videoDetail.infoRows) {
                        Text(text = it, style = MaterialTheme.typography.bodyMedium)
                    }
                    if (videoDetail.description.isNotEmpty()) {
                        item {
                            TextLabel(
                                text = videoDetail.description,
                                maxLines = 2,
                                textStyle = MaterialTheme.typography.bodyMedium
                            ) {
                                showDescriptionDialog = true
                            }
                        }
                    }
                }, verticalArrangement = spacedBy(4.dp)
            )
        }
    }

    LaunchedEffect(Unit) {
        imageFocusRequester.requestFocus()
    }


    // 在Dialog中显示视频简介
    AnimatedVisibility(visible = showDescriptionDialog) {
        val scrollState = rememberScrollState()
        val coroutineScope = rememberCoroutineScope()
        val longDescFocusRequester = remember {
            FocusRequester()
        }
        AlertDialog(onDismissRequest = { showDescriptionDialog = false },
            confirmButton = {},
            properties = DialogProperties(usePlatformDefaultWidth = false),
            modifier = Modifier.fillMaxWidth(0.6f),
            title = {
                Text(
                    text = stringResource(R.string.video_description),
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(bottom = 10.dp)
                )
            },
            text = {
                Text(text = videoDetail.description.substringAfter(':').trimStart(),
                    modifier = Modifier
                        .verticalScroll(scrollState)
                        .focusRequester(longDescFocusRequester)
                        .focusable()
                        .onPreviewKeyEvent {
                            val step = 70f
                            when (it.key) {
                                Key.DirectionUp -> {
                                    if (it.type == KeyEventType.KeyDown) {
                                        coroutineScope.launch {
                                            scrollState.animateScrollBy(-step)
                                        }
                                        true
                                    } else {
                                        false
                                    }
                                }

                                Key.DirectionDown -> {
                                    if (it.type == KeyEventType.KeyDown) {
                                        coroutineScope.launch {
                                            scrollState.animateScrollBy(step)
                                        }
                                        true
                                    } else {
                                        false
                                    }

                                }

                                else -> false
                            }

                        })
                LaunchedEffect(Unit) {
                    longDescFocusRequester.requestFocus()
                }
            }

        )

    }

}

@OptIn(ExperimentalTvFoundationApi::class)
@Composable
fun VideoSeasonRow(seasons: List<VideoSeason>, onSeasonClick: (VideoSeason) -> Unit = {}) {
    Column(Modifier.fillMaxWidth()) {
        ContentWithTitle(title = "选季") {
            FocusGroup {
                TvLazyRow(horizontalArrangement = spacedBy(5.dp), content = {
                    items(seasons.size, key = { seasons[it].seasonName }) { seasonIndex ->
                        val season = seasons[seasonIndex]
                        TextLabel(
                            text = "第${season.seasonName}季",
                            enabled = !season.currentSeason,
                            modifier = if (seasonIndex == 0) Modifier.initiallyFocused() else Modifier.restorableFocus()
                        ) {
                            onSeasonClick(season)
                        }

                    }
                })
            }
        }
    }
}

@OptIn(ExperimentalTvFoundationApi::class)
@Composable
fun VideoEpisodeRow(
    episodes: List<VideoEpisode>,
    onEpisodeClick: (VideoEpisode, Int) -> Unit = { _, _ -> }
) {
    ContentWithTitle(title = "选集") {
        FocusGroup {
            TvLazyRow(
                content = {
                    items(episodes.size, key = { episodes[it].id }) { episodeIndex ->
                        val episode = episodes[episodeIndex]
                        TextLabel(
                            text = episode.name,
                            modifier = if (episodeIndex == 0) Modifier.initiallyFocused() else Modifier.restorableFocus()
                        ) {
                            onEpisodeClick(episode, episodeIndex)
                        }
                    }
                }, horizontalArrangement = spacedBy(5.dp)
            )
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun ContentWithTitle(
    title: String,
    space: Dp = 10.dp,
    textStyle: TextStyle = MaterialTheme.typography.titleMedium,
    content: @Composable () -> Unit
) {
    Column(Modifier.fillMaxWidth()) {
        Text(text = title, style = textStyle)
        Spacer(modifier = Modifier.height(space))
        content()
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun TextLabel(
    modifier: Modifier = Modifier,
    text: String,
    enabled: Boolean = true,
    maxLines: Int = 1,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    onClick: () -> Unit = {}
) {
    Surface(
        enabled = enabled,
        onClick = onClick,
        modifier = modifier,
        scale = ClickableSurfaceScale.None,
        border = ClickableSurfaceDefaults.border(
            focusedBorder = Border(BorderStroke(2.dp, MaterialTheme.colorScheme.border))
        ),
        colors = ClickableSurfaceDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface
        ),
        shape = ClickableSurfaceDefaults.shape(MaterialTheme.shapes.extraSmall)
    ) {
        Text(
            text = text,
            maxLines = maxLines,
            overflow = TextOverflow.Ellipsis,
            style = textStyle,
            modifier = Modifier.padding(6.dp, 3.dp)
        )
    }
}

@Composable
fun RelatedVideoRow(videos: List<VideoCardInfo>, onVideoClick: (VideoCardInfo) -> Unit = {}) {
    val videoWidth = dimensionResource(id = R.dimen.video_preview_card_width) * 0.6f
    val videoHeight = dimensionResource(id = R.dimen.video_preview_card_height) * 0.6f
    ContentWithTitle(title = "相关视频") {
        TvLazyRow(modifier = Modifier
//            .padding(videoWidth * 0.3f, videoHeight * 0.1f)
            .fillMaxWidth(), content = {
            item {
                Spacer(modifier = Modifier.width(videoWidth * 0.1f))
            }
            items(videos.size, key = { videos[it].url }) { videoIndex ->
                val video = videos[videoIndex]
                VideoCard(
                    width = videoWidth,
                    height = videoHeight,
                    video = video,
                    onVideoClick = onVideoClick
                )
            }
        })
    }
}