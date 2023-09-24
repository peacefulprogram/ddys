package com.jing.ddys.setting

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jing.ddys.DdysApplication
import com.jing.ddys.repository.HttpUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SettingsViewModel : ViewModel() {
    private val _sp = getSettingSharedPreference()


    private val _networkSettings =
        MutableStateFlow(NetworkProxySettings.loadFromSharedPreference(_sp))

    val networkProxySettings: StateFlow<NetworkProxySettings>
        get() = _networkSettings

    fun applySetting(newSettings: NetworkProxySettings) {
        val currentSettings = _networkSettings.value
        if (currentSettings == newSettings) {
            return
        }
        viewModelScope.launch(Dispatchers.Default) {
            _networkSettings.emit(newSettings)
            HttpUtil.resetOkhttpClientWithProxySettings(newSettings)
        }
        newSettings.flushToSharedPreference(_sp)
    }

    companion object {
        fun getSettingSharedPreference(): SharedPreferences =
            DdysApplication.context.getSharedPreferences("settings", Context.MODE_PRIVATE)
    }
}

data class NetworkProxySettings(
    val proxyEnabled: Boolean = false,
    val proxyHost: String = "",
    val proxyPort: Int
) {

    fun flushToSharedPreference(sp: SharedPreferences) {
        sp.edit().apply {
            putBoolean(proxyEnabledKey, proxyEnabled)
            putString(proxyHostKey, proxyHost)
            putInt(proxyPortKey, proxyPort)
        }.apply()
    }

    companion object {

        private const val proxyEnabledKey = "proxy.enable"
        private const val proxyHostKey = "proxy.host"
        private const val proxyPortKey = "proxy.port"

        fun loadFromSharedPreference(sp: SharedPreferences): NetworkProxySettings {
            return NetworkProxySettings(
                proxyEnabled = sp.getBoolean(proxyEnabledKey, false),
                proxyHost = sp.getString(proxyHostKey, "")!!,
                proxyPort = sp.getInt(proxyPortKey, 7890)
            )
        }
    }
}