package com.jing.ddys.search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.LocalContentColor
import androidx.tv.material3.MaterialTheme
import com.jing.ddys.R
import com.jing.ddys.compose.screen.SearchResultScreen
import com.jing.ddys.compose.theme.DdysTheme
import org.koin.android.ext.android.get
import org.koin.core.parameter.parametersOf

class SearchResultActivity : ComponentActivity() {

    @OptIn(ExperimentalTvMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val keyword = intent.getStringExtra("k")!!
        val viewModel = get<SearchResultViewModel> { parametersOf(keyword) }
        setContent {
            DdysTheme {
                Box(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(
                            dimensionResource(id = R.dimen.screen_h_padding), dimensionResource(
                                id = R.dimen.screen_v_padding
                            )
                        )
                        .fillMaxWidth()
                ) {
                    CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurface) {
                        SearchResultScreen(viewModel = viewModel)
                    }
                }
            }
        }
    }

    companion object {
        fun navigateTo(context: Context, keyword: String) {
            Intent(context, SearchResultActivity::class.java).apply {
                putExtra("k", keyword)
                context.startActivity(this)
            }
        }
    }
}