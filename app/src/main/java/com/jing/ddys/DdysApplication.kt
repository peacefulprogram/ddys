package com.jing.ddys

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import coil.ImageLoader
import com.jing.ddys.repository.HttpUtil

class DdysApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        _context = this
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        private lateinit var _context: Context

        val context: Context
            get() = _context

        val imageLoader by lazy {
            ImageLoader.Builder(context)
                .okHttpClient(HttpUtil.okHttpClient)
                .build()
        }
    }
}