package com.jing.ddys.setting

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.tv.foundation.lazy.list.TvLazyColumn
import androidx.tv.material3.Button
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.ListItem
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.jing.ddys.R

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: SettingsViewModel) {
    val proxySettings by viewModel.networkProxySettings.collectAsState()
    val defaultFocusRequester = remember {
        FocusRequester()
    }
    var showProxySettingsDialog by remember {
        mutableStateOf(false)
    }
    Box(
        modifier = Modifier
            .padding(20.dp)
            .fillMaxSize()
    ) {

        TvLazyColumn(content = {
            item {
                Text(
                    text = stringResource(R.string.settings_title),
                    style = MaterialTheme.typography.headlineMedium
                )
            }
            item {
                Column {
                    val proxyText = if (proxySettings.proxyEnabled) {
                        "${proxySettings.proxyHost}:${proxySettings.proxyPort}"
                    } else {
                        stringResource(R.string.network_proxy_setting_none)
                    }
                    SettingsItem(
                        modifier = Modifier.focusRequester(defaultFocusRequester),
                        title = stringResource(R.string.network_proxy_setting_title),
                        supportText = proxyText
                    ) {
                        if (proxySettings.proxyEnabled) {
                            viewModel.applySetting(proxySettings.copy(proxyEnabled = false))
                        } else {
                            showProxySettingsDialog = true
                        }
                    }
                }
            }
        })
        LaunchedEffect(Unit) {
            defaultFocusRequester.requestFocus()
        }
    }
    if (showProxySettingsDialog) {
        ProxySettingsDialog(proxySettings = proxySettings) { newSettings ->
            showProxySettingsDialog = false
            if (newSettings != null) {
                viewModel.applySetting(newSettings)
            }
        }
    }
}

private fun isValidPort(port: String): Boolean {

    if (port.isEmpty()) {
        return false
    }
    val num = runCatching { port.toInt() }.getOrNull()
    return num != null && num >= 1 && num <= 65535
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun ProxySettingsDialog(
    modifier: Modifier = Modifier,
    proxySettings: NetworkProxySettings,
    onDialogClose: (newSettings: NetworkProxySettings?) -> Unit
) {
    var host by remember {
        mutableStateOf(proxySettings.proxyHost)
    }
    var port by remember {
        mutableStateOf(proxySettings.proxyPort.toString())
    }
    val focusRequesterList = remember {
        List(3) { FocusRequester() }
    }
    val hostError by remember(host) {
        mutableStateOf(host.isEmpty())
    }
    val portError by remember(port) {
        mutableStateOf(!isValidPort(port))
    }
    AlertDialog(modifier = modifier,
        onDismissRequest = { onDialogClose(null) },
        title = { Text(text = stringResource(id = R.string.network_proxy_setting_title)) },
        confirmButton = {
            Button(modifier = Modifier.focusRequester(focusRequester = focusRequesterList[2]),
                enabled = !hostError && !portError,
                onClick = {
                    val portNum = runCatching { port.toInt() }.getOrNull()
                    if (!hostError && !portError && portNum != null) {
                        onDialogClose(
                            NetworkProxySettings(
                                proxyEnabled = true,
                                proxyHost = host,
                                proxyPort = portNum
                            )
                        )
                    }
                }) {
                Text(text = stringResource(R.string.button_save))
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(modifier = Modifier.focusRequester(focusRequester = focusRequesterList[0]),
                    value = host,
                    label = { Text(text = stringResource(R.string.network_proxy_setting_host_title)) },
                    singleLine = true,
                    isError = hostError,
                    onValueChange = {
                        host = it.trim()
                    })
                OutlinedTextField(modifier = Modifier.focusRequester(focusRequester = focusRequesterList[1]),
                    value = port,
                    label = { Text(text = stringResource(R.string.network_proxy_setting_port_title)) },
                    singleLine = true,
                    isError = portError,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    onValueChange = {
                        port = it.trim()
                    })
            }
        })
    LaunchedEffect(Unit) {
        val defaultFocusRequesterIndex = if (host.isEmpty()) {
            0
        } else if (port.isEmpty()) {
            1
        } else {
            2
        }
        focusRequesterList[defaultFocusRequesterIndex].requestFocus()
    }
}


@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun SettingsItem(
    modifier: Modifier = Modifier, title: String, supportText: String, onClick: () -> Unit = {}
) {
    var focused by remember {
        mutableStateOf(false)
    }
    ListItem(modifier = modifier.onFocusChanged { focused = it.isFocused || it.hasFocus },
        selected = focused,
        onClick = onClick,
        headlineContent = { Text(text = title) },
        supportingContent = { Text(text = supportText) })

}