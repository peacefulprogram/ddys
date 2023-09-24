package com.jing.ddys.setting

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
import androidx.tv.material3.MaterialTheme
import com.jing.ddys.R
import com.jing.ddys.compose.theme.DdysTheme
import org.koin.android.ext.android.get

class SettingsActivity:ComponentActivity() {

    @OptIn(ExperimentalTvMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel = get<SettingsViewModel>()
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
                    CompositionLocalProvider(
                        androidx.tv.material3.LocalContentColor provides MaterialTheme.colorScheme.onSurface,
                        androidx.compose.material3.LocalContentColor provides MaterialTheme.colorScheme.onSurface
                    ) {
                        SettingsScreen(viewModel = viewModel)
                    }
                }
            }
        }
    }

    companion object {
        fun navigateTo(context: Context) {
            Intent(context, SettingsActivity::class.java).apply {
                context.startActivity(this)
            }
        }
    }
}