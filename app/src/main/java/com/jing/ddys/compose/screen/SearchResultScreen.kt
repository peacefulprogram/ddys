package com.jing.ddys.compose.screen

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.tv.foundation.lazy.grid.TvGridCells
import androidx.tv.foundation.lazy.grid.TvGridItemSpan
import androidx.tv.foundation.lazy.grid.TvLazyVerticalGrid
import androidx.tv.foundation.lazy.grid.rememberTvLazyGridState
import androidx.tv.material3.Border
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.ClickableSurfaceScale
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Glow
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.ProvideTextStyle
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import com.jing.ddys.R
import com.jing.ddys.compose.common.ErrorTip
import com.jing.ddys.compose.common.Loading
import com.jing.ddys.detail.DetailActivity
import com.jing.ddys.repository.SearchResult
import com.jing.ddys.search.SearchResultViewModel
import kotlinx.coroutines.launch

@Composable
fun SearchResultScreen(viewModel: SearchResultViewModel) {
    val pagingItems = viewModel.pager.collectAsLazyPagingItems()
    val refreshState = pagingItems.loadState.refresh
    if (refreshState is LoadState.Loading) {
        Loading()
        return
    }
    if (refreshState is LoadState.Error) {
        ErrorTip(message = "加载错误:${refreshState.error.message}") {
            pagingItems.refresh()
        }
    }
    val context = LocalContext.current
    val gridState = rememberTvLazyGridState()
    val coroutineScope = rememberCoroutineScope()
    val titleFocusRequester = remember {
        FocusRequester()
    }
    val videoWidth = 300.dp
    val videoHeight = 170.dp
    val gap = 20.dp
    Box(modifier = Modifier.fillMaxSize()) {
        TvLazyVerticalGrid(columns = TvGridCells.Adaptive(videoWidth),
            modifier = Modifier.fillMaxSize(),
            state = gridState,
            verticalArrangement = spacedBy(gap),
            horizontalArrangement = spacedBy(gap),
            content = {
                item(span = { TvGridItemSpan(maxLineSpan) }) {
                    Text(
                        text = stringResource(R.string.title_search_result),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier
                            .focusRequester(titleFocusRequester)
                            .focusable()
                    )
                }
                items(count = pagingItems.itemCount) { videoIndex ->
                    val video = pagingItems[videoIndex]!!
                    SearchResultCard(modifier = Modifier.size(videoWidth, videoHeight),
                        searchResult = video,
                        onClick = { DetailActivity.navigateTo(context, video.url) }) { keyEvent ->
                        if (keyEvent.key == Key.Menu && keyEvent.type == KeyEventType.KeyUp) {
                            pagingItems.refresh()
                            coroutineScope.launch {
                                gridState.scrollToItem(0)
                                titleFocusRequester.requestFocus()
                            }
                            true
                        } else if (keyEvent.key == Key.Back) {
                            if (keyEvent.type == KeyEventType.KeyUp) {
                                coroutineScope.launch {
                                    gridState.scrollToItem(0)
                                    titleFocusRequester.requestFocus()
                                }
                            }
                            true
                        } else {
                            false
                        }
                    }
                }
            })

        if (pagingItems.itemCount == 0) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = stringResource(id = R.string.grid_no_data_tip),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }


    LaunchedEffect(Unit) {
        try {
            titleFocusRequester.requestFocus()
        } catch (e: Exception) {
            Log.w("SearchResultScreen", "request focus error: ${e.message}", e)
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun SearchResultCard(
    modifier: Modifier = Modifier,
    searchResult: SearchResult,
    onClick: () -> Unit = {},
    onKeyEvent: (KeyEvent) -> Boolean = { false }
) {
    Surface(
        onClick = onClick,
        modifier = modifier.onPreviewKeyEvent(onKeyEvent),
        scale = ClickableSurfaceScale.None,
        glow = ClickableSurfaceDefaults.glow(
            glow = Glow(
                colorResource(id = R.color.gray600).copy(alpha = 0.5f), 4.dp
            )
        ),
        colors = ClickableSurfaceDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface
        ),
        border = ClickableSurfaceDefaults.border(
            focusedBorder = Border(BorderStroke(2.dp, MaterialTheme.colorScheme.border))
        )
    ) {
        Column(
            Modifier
                .padding(20.dp)
                .fillMaxSize(), verticalArrangement = spacedBy(6.dp)
        ) {
            Text(
                text = searchResult.title, style = MaterialTheme.typography.titleLarge, maxLines = 2
            )
            ProvideTextStyle(
                value = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface.copy(
                        0.8f
                    )
                )
            ) {
                if (searchResult.desc.isNotBlank()) {
                    Text(text = searchResult.desc, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                if (searchResult.updateTime.isNotBlank()) {
                    Text(
                        text = "${searchResult.updateTime}更新",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

            }
        }
    }

}