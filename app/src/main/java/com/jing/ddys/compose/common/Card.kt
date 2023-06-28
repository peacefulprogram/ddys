package com.jing.ddys.compose.common

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.CompactCard
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import coil.compose.AsyncImage
import com.jing.ddys.repository.VideoCardInfo


@OptIn(ExperimentalTvMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun VideoCard(
    width: Dp,
    height: Dp,
    video: VideoCardInfo,
    modifier: Modifier = Modifier,
    focusedScale: Float = 1.1f,
    onVideoLongClick: (VideoCardInfo) -> Unit = {},
    onVideoClick: (VideoCardInfo) -> Unit = {},
    onVideoKeyEvent: ((VideoCardInfo, KeyEvent) -> Boolean)? = null
) {
    var focused by rememberSaveable {
        mutableStateOf(false)
    }
    var actualModifier = modifier
        .size(width = width, height = height)
        .onFocusChanged {
            focused = it.isFocused || it.hasFocus
        }
    if (onVideoKeyEvent != null) {
        actualModifier = actualModifier.onPreviewKeyEvent {
            onVideoKeyEvent(video, it)
        }
    }
    CompactCard(
        modifier = actualModifier,
        onClick = { onVideoClick(video) },
        onLongClick = { onVideoLongClick(video) },
        image = {
            AsyncImage(
                model = video.imageUrl,
                contentDescription = video.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        },
        title = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 10.dp, bottom = 10.dp)
            ) {
                Text(
                    text = video.title,
                    maxLines = 1,
                    modifier = Modifier.run {
                        if (focused) {
                            basicMarquee()
                        } else {
                            this
                        }
                    }
                )
                Text(
                    text = video.subTitle ?: "",
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    modifier = Modifier.run {
                        if (focused) {
                            basicMarquee()
                        } else {
                            this
                        }
                    }
                )
            }
        },
        scale = CardDefaults.scale(focusedScale = focusedScale)
    )
}
