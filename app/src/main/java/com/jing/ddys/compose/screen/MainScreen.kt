package com.jing.ddys.compose.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import androidx.tv.foundation.ExperimentalTvFoundationApi
import androidx.tv.foundation.lazy.grid.TvGridCells
import androidx.tv.foundation.lazy.grid.TvLazyVerticalGrid
import androidx.tv.foundation.lazy.grid.rememberTvLazyGridState
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.IconButton
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.jing.ddys.R
import com.jing.ddys.compose.common.CustomTabRow
import com.jing.ddys.compose.common.ErrorTip
import com.jing.ddys.compose.common.FocusGroup
import com.jing.ddys.compose.common.Loading
import com.jing.ddys.compose.common.VideoCard
import com.jing.ddys.compose.common.appendEnd
import com.jing.ddys.detail.DetailActivity
import com.jing.ddys.history.PlayHistoryActivity
import com.jing.ddys.main.MainViewModel
import com.jing.ddys.repository.VideoCardInfo
import com.jing.ddys.search.SearchActivity
import com.jing.ddys.setting.SettingsActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


private val categoryList = listOf(
    Pair("/", "首页"),
    Pair("/category/anime/new-bangumi/", "本季新番"),
    Pair("/category/airing/", "连载剧集"),
    Pair("/category/movie/", "电影"),
    Pair("/category/drama/kr-drama/", "韩剧"),
    Pair("/category/anime/", "动画"),
    Pair("/category/movie/western-movie/", "欧美电影"),
    Pair("/category/movie/asian-movie/", "日韩电影"),
    Pair("/category/movie/chinese-movie/", "华语电影"),
    Pair("/tag/douban-top250/", "豆瓣TOP250"),
    Pair("/category/drama/western-drama/", "欧美剧"),
    Pair("/category/drama/cn-drama/", "华语剧"),
    Pair("/category/drama/jp-drama/", "日剧"),
)

@Composable
fun MainScreen(viewModel: MainViewModel) {

    var selectedTabIndex by remember {
        mutableIntStateOf(0)
    }
    val tabNames = remember {
        categoryList.map { it.second }.toList()
    }
    val navFocusRequester = remember {
        FocusRequester()
    }

    val context = LocalContext.current
    LaunchedEffect(selectedTabIndex) {
        val url = categoryList[selectedTabIndex].first
        delay(200L)
        viewModel.onCategoryChoose(url)
    }

    var showAppNameRow by remember {
        mutableStateOf(true)
    }

    Column(Modifier.fillMaxSize()) {
        TopNav(Modifier.focusRequester(navFocusRequester),
            tabs = tabNames,
            selectedTabIndex = selectedTabIndex,
            showAppNameRow = showAppNameRow,
            onTabFocus = { selectedTabIndex = it })
        Spacer(modifier = Modifier.height(5.dp))
        VideoGrid(viewModel = viewModel,
            onVideoClick = { DetailActivity.navigateTo(context, it.url) },
            onScrollStateChanged = { showAppNameRow = !it }) {
            navFocusRequester.requestFocus()
        }
    }
    LaunchedEffect(Unit) {
        navFocusRequester.requestFocus()
    }

}

@OptIn(ExperimentalTvMaterial3Api::class, ExperimentalTvFoundationApi::class)
@Composable
fun TopNav(
    modifier: Modifier = Modifier,
    tabs: List<String>,
    selectedTabIndex: Int,
    showAppNameRow: Boolean = true,
    onTabFocus: (Int) -> Unit,
) {
    val context = LocalContext.current
    FocusGroup {
        Column {
            AnimatedVisibility(visible = showAppNameRow) {
                FocusGroup(modifier = Modifier.restorableFocus()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            IconButton(
                                onClick = {
                                    SearchActivity.navigateTo(context = context)
                                }, modifier = Modifier.initiallyFocused()
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "search"
                                )
                            }
                            IconButton(
                                onClick = {
                                    PlayHistoryActivity.navigateTo(context)
                                }, modifier = Modifier.restorableFocus()
                            ) {
                                Icon(
                                    imageVector = Icons.Default.History,
                                    contentDescription = "history"
                                )
                            }
                            IconButton(
                                onClick = {
                                    SettingsActivity.navigateTo(context)
                                }, modifier = Modifier.restorableFocus()
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = "settings"
                                )
                            }

                        }

                        Text(
                            text = stringResource(id = R.string.app_name),
                            style = MaterialTheme.typography.headlineMedium
                        )

                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
            }

            CustomTabRow(
                selectedTabIndex = selectedTabIndex,
                tabs = tabs,
                modifier = modifier.initiallyFocused(),
                onTabFocus = onTabFocus
            )
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun VideoGrid(
    viewModel: MainViewModel,
    onVideoClick: (VideoCardInfo) -> Unit = {},
    onScrollStateChanged: (scrolled: Boolean) -> Unit = {},
    onRequestTabFocus: () -> Unit = {}
) {
    val pagingItems = viewModel.pager.collectAsLazyPagingItems()
    if (pagingItems.loadState.refresh is LoadState.Loading) {
        Loading()
        return
    }
    if (pagingItems.loadState.refresh is LoadState.Error) {
        val error = (pagingItems.loadState.refresh as LoadState.Error).error
        ErrorTip(message = "加载失败:${error.message}") {
            pagingItems.retry()
        }
        return
    }
    LaunchedEffect(Unit) {
        withContext(Dispatchers.Default) {
            viewModel.refreshChannel.receiveAsFlow().collectLatest { pagingItems.refresh() }
        }
    }
    val cardWidth = dimensionResource(id = R.dimen.video_preview_card_width)
    val cardHeight = dimensionResource(id = R.dimen.video_preview_card_height)

    val videoCardContainerWidth = cardWidth * 1.1f
    val videoCardContainerHeight = cardHeight * 1.1f

    val gridState = rememberTvLazyGridState()
    val coroutineScope = rememberCoroutineScope()
    val notScrolled by remember {
        derivedStateOf {
            gridState.firstVisibleItemScrollOffset == 0 && gridState.firstVisibleItemIndex == 0
        }
    }

    LaunchedEffect(notScrolled) {
        onScrollStateChanged(!notScrolled)
    }

    TvLazyVerticalGrid(
        columns = TvGridCells.Adaptive(videoCardContainerWidth),
        state = gridState,
        content = {
            items(count = pagingItems.itemCount, key = pagingItems.itemKey { it.url }) {
                Box(
                    modifier = Modifier.size(videoCardContainerWidth, videoCardContainerHeight),
                    contentAlignment = Alignment.Center
                ) {
                    VideoCard(width = cardWidth,
                        height = cardHeight,
                        video = pagingItems[it]!!,
                        onVideoClick = onVideoClick,
                        onVideoKeyEvent = { _, event ->
                            when (event.key) {
                                Key.Back -> {
                                    coroutineScope.launch {
                                        gridState.scrollToItem(0)
                                    }
                                    onRequestTabFocus()
                                    true
                                }

                                Key.Menu -> {
                                    pagingItems.refresh()
                                    onRequestTabFocus()
                                    true
                                }

                                else -> {
                                    false
                                }
                            }

                        })
                }
            }

            appendEnd(pagingItems.loadState.append, pagingItems.itemCount > 0)

        })
}