package com.jing.ddys

import android.app.Application
import com.jing.ddys.context.GlobalContext

class DdysApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        GlobalContext.context = this
    }
}